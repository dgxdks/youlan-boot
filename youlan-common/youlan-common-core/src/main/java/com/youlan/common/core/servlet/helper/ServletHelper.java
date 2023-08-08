package com.youlan.common.core.servlet.helper;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ServletHelper {

    /**
     * 获取{@link HttpServletRequest}
     */
    public static HttpServletRequest getHttpServletRequest() {
        return getServletRequestAttributes().getRequest();
    }

    /**
     * 获取请求体
     */
    public static String getBody() {
        return StrUtil.str(getBodyBytes(), StandardCharsets.UTF_8);
    }

    /**
     * 获取请求IP
     */
    public static String getClientIp() {
        return ServletUtil.getClientIP(getHttpServletRequest());
    }

    /**
     * 获取UserAgent
     */
    public static String getUserAgent() {
        return getHttpServletRequest().getHeader(HttpHeaders.USER_AGENT);
    }

    /**
     * 获取请求体
     */
    public static byte[] getBodyBytes() {
        HttpServletRequest httpServletRequest = getHttpServletRequest();
        if (httpServletRequest instanceof ContentCachingRequestWrapper) {
            return ((ContentCachingRequestWrapper) httpServletRequest).getContentAsByteArray();
        }
        return ServletUtil.getBodyBytes(httpServletRequest);
    }

    /**
     * 获取{@link HttpServletResponse}
     */
    public static HttpServletResponse getHttpServletResponse() {
        return getServletRequestAttributes().getResponse();
    }

    /**
     * 获取{@link ServletRequestAttributes}
     */
    public static ServletRequestAttributes getServletRequestAttributes() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return (ServletRequestAttributes) requestAttributes;
        }
        throw new BizRuntimeException("无法获取org.springframework.web.context.request.ServletRequestAttributes");
    }
}
