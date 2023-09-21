package com.youlan.common.core.restful.controller;

import cn.hutool.json.JSONUtil;
import com.youlan.common.core.helper.FileHelper;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.core.servlet.helper.ServletHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class RestfulController {

    public ApiResult toError() {
        return ApiResult.error();
    }

    public ApiResult toError(ApiResultCode apiResultCode) {
        return ApiResult.error(apiResultCode);
    }

    public ApiResult toError(String status) {
        return ApiResult.error(status);
    }

    public ApiResult toError(String status, String errorMsg) {
        return ApiResult.error(status, errorMsg);
    }

    public ApiResult toSuccess(Object data) {
        return ApiResult.ok(data);
    }

    public ApiResult toSuccess() {
        return ApiResult.ok(true);
    }


    public ApiResult toSuccess(Object data, String status, String errorMsg) {
        return ApiResult.ok(data, status, errorMsg);
    }

    public ApiResult toSuccess(Object data, String status) {
        return ApiResult.ok(data, status);
    }

    public void toDownload(String fileName, byte[] data, String contentType, HttpServletResponse response) throws IOException {
        ServletHelper.download(fileName, data, contentType, response);
    }

    public void toDownload(String fileName, byte[] data, String contentType) throws IOException {
        toDownload(fileName, data, contentType, ServletHelper.getHttpServletResponse());
    }

    public void toDownload(String fileName, byte[] data) throws IOException {
        toDownload(fileName, data, ServletHelper.getHttpServletResponse());
    }

    public void toDownload(String fileName, byte[] data, HttpServletResponse response) throws IOException {
        toDownload(fileName, data, FileHelper.getContentTypeByFileName(fileName), response);
    }

    public void toObject(Object object, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().println(JSONUtil.toJsonStr(object));
    }
}
