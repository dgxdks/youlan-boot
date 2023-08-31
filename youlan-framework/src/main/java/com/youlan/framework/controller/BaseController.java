package com.youlan.framework.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.common.core.helper.FileHelper;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.core.servlet.helper.ServletHelper;
import com.youlan.common.db.entity.vo.PageVO;
import com.youlan.common.excel.helper.ExcelHelper;
import com.youlan.system.helper.SystemAuthHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    public void toExcel(String fileName, Class<?> head, List<?> dataList, HttpServletResponse response) throws IOException {
        toExcel(fileName, null, head, dataList, response);
    }

    public void toExcel(String fileName, Class<?> head, List<?> dataList) throws IOException {
        toExcel(fileName, head, dataList, ServletHelper.getHttpServletResponse());
    }

    public void toExcel(String fileName, String sheetName, Class<?> head, List<?> dataList, HttpServletResponse response) throws IOException {
        try {
            ByteArrayOutputStream cacheBos = new ByteArrayOutputStream(1024);
            ExcelWriterBuilder excelWriterBuilder = ExcelHelper.write(cacheBos, head)
                    .autoCloseStream(true);
            if (StrUtil.isNotBlank(sheetName)) {
                excelWriterBuilder.sheet(sheetName).doWrite(dataList);
            } else {
                excelWriterBuilder.sheet().doWrite(dataList);
            }
            String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            ServletHelper.download(fileName, cacheBos.toByteArray(), contentType);
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

    public void checkUserNotAdmin(Long userId) {
        SystemAuthHelper.checkUserNotAdmin(userId);
    }

    public void checkRoleNotAdmin(Long roleId) {
        SystemAuthHelper.checkRoleNotAdmin(roleId);
    }

    public Long getUserId() {
        return SystemAuthHelper.getUserId();
    }
}
