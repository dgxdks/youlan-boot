package com.youlan.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.hutool.core.exceptions.ExceptionUtil;
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
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static cn.dev33.satoken.exception.NotLoginException.*;
import static com.youlan.common.core.restful.enums.ApiResultCode.*;

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
        return handleTargetExceptionIfExists(exception, request, () -> toApiResult(null, null, exception, request));

    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResult handleRuntimeException(RuntimeException exception, HttpServletRequest request) {
        return handleTargetExceptionIfExists(exception, request, () -> toApiResult(null, null, exception, request));

    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResult handleMissingServletRequestParameterException(MissingServletRequestParameterException exception, HttpServletRequest request) {
        return toApiResult(C0003.getStatus(), C0003.getErrorMsg(), exception, request);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ApiResult handleMissingServletRequestPartException(MissingServletRequestPartException exception, HttpServletRequest request) {
        return toApiResult(C0004.getStatus(), C0004.getErrorMsg(), exception, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResult handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception, HttpServletRequest request) {
        return toApiResult(B0006.getStatus(), B0006.getErrorMsg(), exception, request);
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
        return handleTargetExceptionIfExists(exception, request, () -> toApiResult(null, "数据格式异常", exception, request));
    }

    @ExceptionHandler(NotLoginException.class)
    public ApiResult notLoginException(NotLoginException exception, HttpServletRequest request) {
        String notLoginType = exception.getType();
        String logMsg = ApiResultCode.A0003.getErrorMsg();
        switch (notLoginType) {
            case NOT_TOKEN:
                logMsg = NOT_TOKEN_MESSAGE;
                break;
            case INVALID_TOKEN:
                logMsg = INVALID_TOKEN_MESSAGE;
                break;
            case TOKEN_TIMEOUT:
                logMsg = TOKEN_TIMEOUT;
                break;
            case BE_REPLACED:
                logMsg = BE_REPLACED_MESSAGE;
                break;
            case KICK_OUT:
                logMsg = KICK_OUT_MESSAGE;
                break;
            case TOKEN_FREEZE:
                logMsg = TOKEN_FREEZE_MESSAGE;
                break;
            case NO_PREFIX:
                logMsg = NO_PREFIX_MESSAGE;
                break;
            default:
                break;
        }
        log.info("用户未登录: {}", logMsg);
        return toApiResult(A0003.getStatus(), A0003.getErrorMsg(), exception, request);
    }

    @ExceptionHandler(SaTokenException.class)
    public ApiResult saTokenException(SaTokenException exception, HttpServletRequest request) {
        return toApiResult(ApiResultCode.A0004.getStatus(), ApiResultCode.A0004.getErrorMsg(), exception, request);
    }

    /**
     * 如果存在目标异常则优先处理
     */
    public ApiResult handleTargetExceptionIfExists(Throwable throwable, HttpServletRequest request, Supplier<ApiResult> defaultSupplier) {
        Throwable exception = ExceptionUtil.getCausedBy(throwable, BizException.class, BizRuntimeException.class, NotLoginException.class);
        // 拦截业务异常
        if (exception instanceof BizException) {
            BizException bizException = (BizException) exception;
            return toApiResult(bizException.getStatus(), bizException.getErrorMsg(), bizException, request);
        }
        // 拦截运行时业务异常
        if (exception instanceof BizRuntimeException) {
            BizRuntimeException bizRuntimeException = (BizRuntimeException) exception;
            return toApiResult(bizRuntimeException.getStatus(), bizRuntimeException.getErrorMsg(), bizRuntimeException, request);
        }
        // 拦截未登录异常
        if (exception instanceof NotLoginException) {
            NotLoginException notLoginException = (NotLoginException) exception;
            return notLoginException(notLoginException, request);
        }
        return defaultSupplier.get();
    }

    public ApiResult toApiResult(String status, String errorMsg, Exception exception, HttpServletRequest request) {
        status = StrUtil.isBlank(status) ? ApiResultCode.ERROR.getStatus() : status;
        errorMsg = StrUtil.isBlank(errorMsg) ? ApiResultCode.ERROR.getErrorMsg() : errorMsg;
        String requestUrl = request.getRequestURI();
        String queryString = request.getQueryString();
        String requestBody = ServletHelper.getBody();
        log.error("请求异常: {}, 请求地址: {}, 请求参数: {}, 请求数据: {}", errorMsg, requestUrl, queryString, StrUtil.sub(requestBody, 0, 512));
        log.error(exception.getMessage(), exception);
        return ApiResult.error(status, errorMsg);
    }
}
