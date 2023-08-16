package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.framework.anno.SystemLog;
import com.youlan.framework.constant.SystemLogType;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.service.biz.StorageBizService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "存储管理")
@RestController
@RequestMapping("/system/storage")
@AllArgsConstructor
public class StorageController extends BaseController {

    private final StorageBizService storageBizService;

    @SaCheckPermission("system:storage:upload")
    @SystemLog(name = "文件上传", type = SystemLogType.OPERATION_LOG_TYPE_ADD)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResult upload(@RequestPart("file") MultipartFile file, @RequestPart(value = "platform", required = false) String platform) {
        if (ObjectUtil.isNull(file)) {
            return toError(ApiResultCode.B0005);
        }
        return toSuccess(storageBizService.upload(file, platform));
    }
}
