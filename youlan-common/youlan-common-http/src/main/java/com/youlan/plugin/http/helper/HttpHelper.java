package com.youlan.plugin.http.helper;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.plugin.http.entity.CookieInfo;
import com.youlan.plugin.http.entity.HeaderInfo;
import com.youlan.plugin.http.entity.ProxyInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class HttpHelper {

    private static final CloseableHttpClient CLIENT;

    public static String postJsonEntityStr(String url, HeaderInfo headerInfo, String content, ProxyInfo proxyInfo, CookieInfo cookieInfo) {
        try (CloseableHttpResponse response = postJson(url, headerInfo, content, proxyInfo, cookieInfo)) {
            if (cookieInfo != null) {
                cookieInfo.putFormHeaders(response.getAllHeaders());
            }
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BizRuntimeException(e);
        }
    }

    public static CloseableHttpResponse postJson(String url, HeaderInfo headerInfo, String content, ProxyInfo proxyInfo, CookieInfo cookieInfo) {
        HttpPost post = new HttpPost(url);
        post.setConfig(getRequestConfig(proxyInfo));
        if (StrUtil.isNotBlank(content)) {
            post.setEntity(new StringEntity(content, ContentType.APPLICATION_JSON));
        } else {
            post.setHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
        }
        if (headerInfo != null) {
            post.setHeaders(headerInfo.toArray());
        }
        CloseableHttpResponse response = execute(post);
        if (cookieInfo != null) {
            cookieInfo.putFormHeaders(response.getAllHeaders());
        }
        return response;
    }

    public static String postFormEntityStr(String url, HeaderInfo headerInfo, Map<String, Object> content, ProxyInfo proxyInfo, CookieInfo cookieInfo) {
        try (CloseableHttpResponse response = postForm(url, headerInfo, content, proxyInfo, cookieInfo)) {
            if (cookieInfo != null) {
                cookieInfo.putFormHeaders(response.getAllHeaders());
            }
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BizRuntimeException(e);
        }
    }

    public static CloseableHttpResponse postForm(String url, HeaderInfo headerInfo, Map<String, Object> content, ProxyInfo proxy, CookieInfo cookieInfo) {
        HttpPost post = new HttpPost(url);
        post.setConfig(getRequestConfig(proxy));
        if (CollectionUtil.isNotEmpty(content)) {
            List<BasicNameValuePair> formData = content.entrySet()
                    .stream()
                    .map(entry -> new BasicNameValuePair(entry.getKey(), StrUtil.str(entry.getValue(), Charset.defaultCharset())))
                    .collect(Collectors.toList());
            post.setEntity(new UrlEncodedFormEntity(formData, StandardCharsets.UTF_8));
        }
        if (headerInfo != null) {
            post.setHeaders(headerInfo.toArray());
        }
        CloseableHttpResponse response = execute(post);
        if (cookieInfo != null) {
            cookieInfo.putFormHeaders(response.getAllHeaders());
        }
        return response;
    }


    public static CloseableHttpResponse checkResponse(CloseableHttpResponse response) {
        if (response.getStatusLine().getStatusCode() == 407) {
            throw new BizRuntimeException("网络异常,可能未设置代理白名单地址");
        }
        return response;
    }

    public static CloseableHttpResponse get(String url, HeaderInfo headerInfo, Map<String, Object> entity, ProxyInfo proxy, CookieInfo cookieInfo) {
        String requestUrl = urlEncodeParams(url, entity);
        HttpGet get = new HttpGet(requestUrl);
        get.setConfig(getRequestConfig(proxy));
        if (headerInfo != null) {
            get.setHeaders(headerInfo.toArray());
        }
        CloseableHttpResponse response = execute(get);
        if (cookieInfo != null) {
            cookieInfo.putFormHeaders(response.getAllHeaders());
        }
        return response;
    }

    public static String getEntityStr(String url, HeaderInfo headerInfo, Map<String, Object> params, ProxyInfo proxy, CookieInfo cookieInfo) {
        CloseableHttpResponse response = get(url, headerInfo, params, proxy, cookieInfo);
        try {
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BizRuntimeException(e);
        }
    }

    public static JSONObject getEntityObj(String url, HeaderInfo headerInfo, Map<String, Object> params, ProxyInfo proxy, CookieInfo cookieInfo) {
        String entity = getEntityStr(url, headerInfo, params, proxy, cookieInfo);
        return JSONUtil.parseObj(entity);
    }

    public static CloseableHttpResponse execute(HttpUriRequest request) {
        //重试机制
        int retryTimes = 10;
        for (int i = 1; i <= retryTimes; i++) {
            try {
                CloseableHttpResponse response = CLIENT.execute(request);
                return checkResponse(response);
            } catch (IOException e) {
                log.error("网络异常: {error: {}, retry: {}}", e.getMessage(), i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new BizRuntimeException(ex);
                }
            }
        }
        throw new RuntimeException(StrUtil.format("请求异常已超过重试次数: {}", retryTimes));
    }

    public static RequestConfig getRequestConfig(ProxyInfo proxyInfo) {
        int connectTimeout = proxyInfo == null ? 3 * 1000 : proxyInfo.getConnectTimeout();
        int connectionRequestTimeout = proxyInfo == null ? 10 * 1000 : proxyInfo.getConnectionRequestTimeout();
        int socketTimeout = proxyInfo == null ? 4 * 1000 : proxyInfo.getSocketTimeout();
        RequestConfig.Builder builder = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setSocketTimeout(socketTimeout)
                .setRedirectsEnabled(false);
        if (proxyInfo != null) {
            builder.setProxy(HttpHost.create(getProxyUrl(proxyInfo)));
        }
        return builder.build();
    }

    public static String getProxyUrl(ProxyInfo proxy) {
        String protocol = proxy.isSsl() ? "https" : "http";
        return StrUtil.format("{}://{}:{}", protocol, proxy.getIp(), proxy.getPort());
    }

    public static String urlEncodeParams(String url, Map<String, Object> params) {
        if (StrUtil.isBlank(url)) {
            return StrUtil.EMPTY;
        }
        String encodeParams = encodeParams(params);
        if (StrUtil.isBlank(encodeParams)) {
            return url;
        } else {
            return StrUtil.format("{}?{}", url, encodeParams);
        }
    }

    public static String encodeParams(Map<String, Object> params) {
        if (CollectionUtil.isEmpty(params)) {
            return StrUtil.EMPTY;
        }
        List<BasicNameValuePair> paramList = params.entrySet()
                .stream()
                .map(entry -> new BasicNameValuePair(entry.getKey(), StrUtil.str(entry.getValue(), Charset.defaultCharset())))
                .collect(Collectors.toList());
        return URLEncodedUtils.format(paramList, Charset.defaultCharset());
    }

    static {
        CLIENT = HttpClients.custom()
                .setRetryHandler((exception, exceptionCount, context) -> {
                    if (exceptionCount >= 2) {
                        // Do not retry if over max retry count
                        return false;
                    }
                    if (exception instanceof ConnectTimeoutException) {
                        // Timeout
                        return false;
                    }
                    if (exception instanceof InterruptedIOException) {
                        // Connection refused
                        return false;
                    }
                    if (exception instanceof UnknownHostException) {
                        // Unknown host
                        return false;
                    }
                    if (exception instanceof SSLException) {
                        // SSL handshake exception
                        return false;
                    }
                    HttpClientContext clientContext = HttpClientContext.adapt(context);
                    HttpRequest request = clientContext.getRequest();
                    // Retry if the request is considered idempotent
                    return !(request instanceof HttpEntityEnclosingRequest);
                })
                .setDefaultConnectionConfig(ConnectionConfig.custom().setBufferSize(1024).setCharset(StandardCharsets.UTF_8).build())
                .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).setConnectTimeout(3 * 1000).setConnectionRequestTimeout(10 * 1000).setSocketTimeout(4 * 1000).setRedirectsEnabled(false).build())
                .setDefaultSocketConfig(SocketConfig.copy(SocketConfig.DEFAULT).setTcpNoDelay(true).setSoKeepAlive(true).setSoTimeout(4 * 1000).build())
                .setMaxConnPerRoute(500)
                .setMaxConnTotal(1000)
                .build();
    }
}
