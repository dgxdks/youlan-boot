package com.youlan.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.hutool.core.util.StrUtil;
import com.youlan.common.core.exception.BizException;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.core.servlet.helper.ServletHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

import static cn.dev33.satoken.exception.NotLoginException.*;

@Slf4j
@AllArgsConstructor
@RestControllerAdvice
public class ExceptionHandlerConfig {

    @ExceptionHandler(BizRuntimeException.class)
    public ApiResult handleBizRuntimeException(BizRuntimeException exception, HttpServletRequest request) {
        return toApiResult(exception.getStatus(), exception.getErrorMsg(), exception, request);
    }

    @ExceptionHandler(BizException.class)
    public ApiResult handleBizException(BizException exception, HttpServletRequest request) {
        return toApiResult(exception.getStatus(), exception.getErrorMsg(), exception, request);
    }

    @ExceptionHandler(Exception.class)
    public ApiResult handleException(Exception exception, HttpServletRequest request) {
        return toApiResult(null, null, exception, request);
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResult handleRuntimeException(RuntimeException exception, HttpServletRequest request) {
        return toApiResult(null, null, exception, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResult handleMissingServletRequestParameterException(MissingServletRequestParameterException exception, HttpServletRequest request) {
        return toApiResult(null, "缺少必填参数", exception, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResult handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception, HttpServletRequest request) {
        return toApiResult(null, "HTTP方法不支持", exception, request);
    }

    @ExceptionHandler(BindException.class)
    public ApiResult handleBindExceptionHandler(BindException exception, HttpServletRequest request) {
        String message = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("|"));
        return toApiResult(ApiResultCode.C0002.getStatus(), message, exception, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResult handleConstraintViolationExceptionHandler(ConstraintViolationException exception, HttpServletRequest request) {
        String message = exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("|"));
        return toApiResult(ApiResultCode.C0002.getStatus(), message, exception, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult handleMethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception, HttpServletRequest request) {
        String message = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("|"));
        return toApiResult(ApiResultCode.C0002.getStatus(), message, exception, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResult handleHttpMessageNotReadableException(HttpMessageNotReadableException exception, HttpServletRequest request) {
        return toApiResult(null, "数据格式异常", exception, request);
    }

    @ExceptionHandler(NotLoginException.class)
    public ApiResult notLoginException(NotLoginException exception, HttpServletRequest request) {
        String notLoginType = exception.getType();
        String errorMsg = ApiResultCode.A0003.getErrorMsg();
        switch (notLoginType) {
            case NOT_TOKEN:
                errorMsg = NOT_TOKEN_MESSAGE;
                break;
            case INVALID_TOKEN:
                errorMsg = INVALID_TOKEN_MESSAGE;
                break;
            case TOKEN_TIMEOUT:
                errorMsg = TOKEN_TIMEOUT;
                break;
            case BE_REPLACED:
                errorMsg = BE_REPLACED_MESSAGE;
                break;
            case KICK_OUT:
                errorMsg = KICK_OUT_MESSAGE;
                break;
            case TOKEN_FREEZE:
                errorMsg = TOKEN_FREEZE_MESSAGE;
                break;
            case NO_PREFIX:
                errorMsg = NO_PREFIX_MESSAGE;
                break;
            default:
                break;
        }
        return toApiResult(ApiResultCode.A0003.getStatus(), errorMsg, exception, request);
    }

    @ExceptionHandler(SaTokenException.class)
    public ApiResult saTokenException(SaTokenException exception, HttpServletRequest request) {
        return toApiResult(ApiResultCode.A0004.getStatus(), ApiResultCode.A0004.getErrorMsg(), exception, request);
    }

    public static ApiResult toApiResult(String status, String errorMsg, Exception exception, HttpServletRequest request) {
        status = StrUtil.isBlank(status) ? ApiResultCode.ERROR.getStatus() : status;
        errorMsg = StrUtil.isBlank(errorMsg) ? ApiResultCode.ERROR.getErrorMsg() : errorMsg;
        String requestUrl = request.getRequestURI();
        String queryString = request.getQueryString();
        String requestBody = ServletHelper.getBody();
        log.error("请求异常: {}, 请求地址: {}, 请求参数: {}, 请求数据: {}", errorMsg, requestUrl, queryString, requestBody);
        log.error(exception.getMessage(), exception);
        return ApiResult.error(status, errorMsg);
    }
}
