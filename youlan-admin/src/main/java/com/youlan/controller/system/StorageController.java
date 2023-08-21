package com.youlan.controller.system;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.core.servlet.helper.ServletHelper;
import com.youlan.framework.anno.SystemLog;
import com.youlan.framework.constant.SystemLogType;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.entity.StorageRecord;
import com.youlan.system.service.biz.StorageBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Tag(name = "存储管理")
@RestController
@RequestMapping("/system/storage")
@AllArgsConstructor
public class StorageController extends BaseController {

    private final StorageBizService storageBizService;

    @Operation(summary = "文件上传")
    @SystemLog(name = "文件上传", type = SystemLogType.OPERATION_LOG_TYPE_UPLOAD)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResult upload(@RequestPart("file") MultipartFile file, @RequestPart(value = "platform", required = false) String platform) {
        if (ObjectUtil.isNull(file)) {
            return toError(ApiResultCode.B0005);
        }
        return toSuccess(storageBizService.upload(file, platform));
    }

    @Operation(summary = "文件下载(url)")
    @GetMapping(value = "/download/url/**")
    public void downloadUrl() throws IOException {
        String requestURI = ServletHelper.getRequestURI();
        String url = StrUtil.subAfter(requestURI, "/download/url/", true);
        File downloadFile = storageBizService.downloadByUrl(url);
        byte[] data = FileUtil.readBytes(downloadFile);
        String fileName = downloadFile.getName();
        toDownload(fileName, data);
    }

    @Operation(summary = "文件下载(fileName)")
    @GetMapping(value = "/download/fileName/{fileName}")
    public void downloadFileName(@PathVariable String fileName) throws IOException {
        if (StrUtil.isBlank(fileName)) {
            throw new BizRuntimeException(ApiResultCode.B0010);
        }
        StorageRecord storageRecord = storageBizService.downloadByFileName(fileName);
        toDownload(fileName, storageRecord.getData(), storageRecord.getContentType());
    }

    @Operation(summary = "文件下载(objectId)")
    @GetMapping(value = "/download/objectId/{objectId}")
    public void downloadObjectId(@PathVariable String objectId) throws IOException {
        if (StrUtil.isBlank(objectId)) {
            throw new BizRuntimeException(ApiResultCode.B0011);
        }
        StorageRecord storageRecord = storageBizService.downloadByObjectId(objectId);
        toDownload(storageRecord.getFileName(), storageRecord.getData(), storageRecord.getContentType());
    }
}
