package com.youlan.controller.system;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.core.servlet.helper.ServletHelper;
import com.youlan.common.storage.entity.StorageRecord;
import com.youlan.common.storage.service.biz.StorageBizService;
import com.youlan.controller.base.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.dromara.x.file.storage.core.Downloader;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "存储管理")
@RestController
@RequestMapping("/system/storage")
@AllArgsConstructor
public class StorageController extends BaseController {

    private final StorageBizService storageBizService;

    @Operation(summary = "文件上传")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResult upload(@RequestPart("file") MultipartFile file,
                            @RequestPart(value = "platform", required = false) String platform) {
        if (ObjectUtil.isNull(file)) {
            return toError(ApiResultCode.B0005);
        }
        return toSuccess(storageBizService.upload(platform, file, null));
    }

    @Operation(summary = "文件下载(url)")
    @GetMapping(value = "/download/url/**")
    public void downloadUrl() throws IOException {
        String requestURI = ServletHelper.getRequestURI();
        String url = StrUtil.subAfter(requestURI, "/download/url/", true);
        StorageRecord storageRecord = storageBizService.downloadByUrl(url);
        Downloader downloader = storageRecord.getDownloader();
        String fileName = storageRecord.getFileName();
        byte[] data = downloader.bytes();
        toDownload(fileName, data, storageRecord.getContentType());
    }

    @Operation(summary = "缩略下载(url)")
    @GetMapping(value = "/download/th/url/**")
    public void downloadThUrl() throws IOException {
        String requestURI = ServletHelper.getRequestURI();
        String url = StrUtil.subAfter(requestURI, "/download/th/url/", true);
        StorageRecord storageRecord = storageBizService.downloadThByUrl(url);
        Downloader downloader = storageRecord.getThDownloader();
        String thFileName = storageRecord.getThFileName();
        byte[] data = downloader.bytes();
        toDownload(thFileName, data, storageRecord.getContentType());
    }

    @Operation(summary = "文件下载(fileName)")
    @GetMapping(value = "/download/fileName/{fileName}")
    public void downloadFileName(@PathVariable String fileName) throws IOException {
        if (StrUtil.isBlank(fileName)) {
            throw new BizRuntimeException(ApiResultCode.B0009);
        }
        StorageRecord storageRecord = storageBizService.downloadByFileName(fileName);
        Downloader downloader = storageRecord.getDownloader();
        byte[] data = downloader.bytes();
        toDownload(fileName, data, storageRecord.getContentType());
    }

    @Operation(summary = "缩略下载(fileName)")
    @GetMapping(value = "/download/th/fileName/{fileName}")
    public void downloadThFileName(@PathVariable String fileName) throws IOException {
        if (StrUtil.isBlank(fileName)) {
            throw new BizRuntimeException(ApiResultCode.B0009);
        }
        StorageRecord storageRecord = storageBizService.downloadThByFileName(fileName);
        Downloader thDownloader = storageRecord.getThDownloader();
        byte[] data = thDownloader.bytes();
        toDownload(fileName, data, storageRecord.getContentType());
    }

    @Operation(summary = "文件下载(objectId)")
    @GetMapping(value = "/download/objectId/{objectId}")
    public void downloadObjectId(@PathVariable String objectId) throws IOException {
        if (StrUtil.isBlank(objectId)) {
            throw new BizRuntimeException(ApiResultCode.B0009);
        }
        StorageRecord storageRecord = storageBizService.downloadByObjectId(objectId);
        Downloader downloader = storageRecord.getDownloader();
        String fileName = storageRecord.getFileName();
        byte[] data = downloader.bytes();
        toDownload(fileName, data, storageRecord.getContentType());
    }

    @Operation(summary = "缩略下载(objectId)")
    @GetMapping(value = "/download/th/objectId/{objectId}")
    public void downloadThObjectId(@PathVariable String objectId) throws IOException {
        if (StrUtil.isBlank(objectId)) {
            throw new BizRuntimeException(ApiResultCode.B0009);
        }
        StorageRecord storageRecord = storageBizService.downloadThByObjectId(objectId);
        Downloader thDownloader = storageRecord.getThDownloader();
        String fileName = storageRecord.getFileName();
        byte[] data = thDownloader.bytes();
        toDownload(fileName, data, storageRecord.getContentType());
    }
}
