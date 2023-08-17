package com.youlan.controller.system;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.helper.FileHelper;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.core.servlet.helper.ServletHelper;
import com.youlan.framework.anno.SystemLog;
import com.youlan.framework.constant.SystemLogType;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.service.biz.StorageBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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
    @GetMapping(value = "/download/url")
    public void downloadUrl(@RequestParam String url) throws IOException {
        File downloadFile = storageBizService.downloadUrl(url);
        byte[] data = FileUtil.readBytes(downloadFile);
        String fileName = downloadFile.getName();
        HttpServletResponse response = ServletHelper.getHttpServletResponse();
        String contentType = FileHelper.getContentTypeByFileName(fileName);
        toDownload(fileName, data, contentType, response);
    }

    @Operation(summary = "文件下载(fileName)")
    @GetMapping(value = "/download/fileName")
    public void downloadFileName(@RequestParam String fileName) {

    }

    @Operation(summary = "文件下载(objectId)")
    @GetMapping(value = "/download/objectId")
    public void downloadObjectId(@RequestParam String objectId) {

    }

    public static void main(String[] args) throws MalformedURLException {
        URL url = new URL("/aaa/abc.txt");
        System.out.println(url.getFile());
    }
}
