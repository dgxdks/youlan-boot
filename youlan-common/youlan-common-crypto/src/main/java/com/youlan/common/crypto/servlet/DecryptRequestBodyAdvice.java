package com.youlan.common.crypto.servlet;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.crypto.anno.DecryptRequest;
import com.youlan.common.crypto.helper.EncryptorHelper;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@RestControllerAdvice
@AllArgsConstructor
public class DecryptRequestBodyAdvice extends RequestBodyAdviceAdapter {

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.hasMethodAnnotation(DecryptRequest.class);
    }

    @Override
    public HttpInputMessage beforeBodyRead(final HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        DecryptRequest decryptRequest = parameter.getMethodAnnotation(DecryptRequest.class);
        if (ObjectUtil.isNull(decryptRequest)) {
            return super.beforeBodyRead(inputMessage, parameter, targetType, converterType);
        }
        try {
            String encryptText = IoUtil.read(inputMessage.getBody(), StandardCharsets.UTF_8);
            String plainText = EncryptorHelper.decrypt(encryptText, decryptRequest);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(plainText.getBytes(StandardCharsets.UTF_8));
            return new HttpInputMessage() {
                @Override
                public InputStream getBody() {
                    return inputStream;
                }

                @Override
                public HttpHeaders getHeaders() {
                    return inputMessage.getHeaders();
                }
            };
        } catch (Exception e) {
            throw new BizRuntimeException(ApiResultCode.B0003);
        }
    }
}
