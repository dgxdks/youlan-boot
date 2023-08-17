package com.youlan.framework.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.entity.vo.PageVO;
import com.youlan.common.excel.helper.ExcelHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.youlan.common.core.restful.enums.ApiResultCode.B0001;

@Slf4j
public class BaseController {

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

    public ApiResult toSuccess(IPage<?> page) {
        return ApiResult.ok(new PageVO<>(page));
    }

    public ApiResult toSuccess(Object data, String status, String errorMsg) {
        return ApiResult.ok(data, status, errorMsg);
    }

    public ApiResult toSuccess(Object data, String status) {
        return ApiResult.ok(data, status);
    }

    public void toDownload(String fileName, byte[] data, String contentType, HttpServletResponse response) throws IOException {
        response.reset();
        response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, StrUtil.format("attachment; filename=\"{}\"", fileName));
        response.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
        IoUtil.write(response.getOutputStream(), true, data);
    }

    public void toDownload(String fileName, byte[] data, HttpServletResponse response) throws IOException {
        toDownload(fileName, data, MediaType.APPLICATION_OCTET_STREAM_VALUE, response);
    }

    public void toExcel(String fileName, Class<?> head, List<?> dataList, HttpServletResponse response) throws IOException {
        toExcel(fileName, null, head, dataList, response);
    }

    public void toExcel(String fileName, String sheetName, Class<?> head, List<?> dataList, HttpServletResponse response) throws IOException {
        try {
            response.reset();
            response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            // 这里URLEncoder.encode可以防止中文乱码
            String fileNameEncode = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.addHeader(HttpHeaders.CONTENT_DISPOSITION, StrUtil.format("attachment; filename=\"{}\"", fileNameEncode));
            ExcelWriterBuilder excelWriterBuilder = ExcelHelper.write(response.getOutputStream(), head)
                    .autoCloseStream(false);
            if (StrUtil.isNotBlank(sheetName)) {
                excelWriterBuilder.sheet(sheetName).doWrite(dataList);
                return;
            }
            excelWriterBuilder.sheet().doWrite(dataList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            toJson(ApiResult.error(B0001), response);
        }
    }

    public void toJson(Object object, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().println(JSONUtil.toJsonStr(object));
    }
}
