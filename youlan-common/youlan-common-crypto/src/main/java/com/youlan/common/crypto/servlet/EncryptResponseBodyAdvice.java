package com.youlan.common.crypto.servlet;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.crypto.anno.EncryptResponse;
import com.youlan.common.crypto.helper.EncryptorHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Slf4j
@RestControllerAdvice
@AllArgsConstructor
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(EncryptResponse.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        EncryptResponse encryptResponse = returnType.getMethodAnnotation(EncryptResponse.class);
        if (ObjectUtil.isNull(encryptResponse)) {
            return body;
        }
        //如果返回的是ApiResult则只处理data值
        if (body instanceof ApiResult) {
            ApiResult apiResult = (ApiResult) body;
            ApiResult retApiResult = new ApiResult().setStatus(apiResult.getStatus())
                    .setErrorMsg(apiResult.getErrorMsg());
            Object apiResultData = apiResult.getData();
            //如果数据是空的则不处理
            if (ObjectUtil.isNull(apiResultData)) {
                return retApiResult;
            }
            String plainText;
            try {
                plainText = JSONUtil.toJsonStr(apiResultData);
            } catch (Exception e) {
                //json序列化失败则可能是基础类型，直接使用toString()方法
                plainText = apiResultData.toString();
            }
            return retApiResult.setData(EncryptorHelper.encrypt(plainText, encryptResponse));
        }
        //默认对整个响应体进行加密
        try {
            return EncryptorHelper.encrypt(body.toString(), encryptResponse);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizRuntimeException(ApiResultCode.B0018);
        }
    }
}
