package com.youlan.controller.system;

import cn.hutool.core.util.ObjectUtil;
import cn.xuyanwu.spring.file.storage.UploadPretreatment;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.helper.FileHelper;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.framework.anno.OperationLog;
import com.youlan.framework.constant.OperationLogType;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.entity.StorageRecord;
import com.youlan.system.entity.dto.UserDTO;
import com.youlan.system.entity.dto.UserUpdatePasswdDTO;
import com.youlan.system.service.biz.StorageBizService;
import com.youlan.system.service.biz.UserBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "用户个人信息")
@RestController
@RequestMapping("/system/user/profile")
@AllArgsConstructor
public class UserProfileController extends BaseController {
    private final UserBizService userBizService;
    private final StorageBizService storageBizService;

    @Operation(summary = "个人信息详情")
    @GetMapping("loadUserProfile")
    public ApiResult loadUserProfile() {
        return toSuccess(userBizService.loadUserProfile());
    }

    @Operation(summary = "个人信息修改")
    @OperationLog(name = "个人信息", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    @PostMapping("/updateUserProfile")
    public ApiResult updateUserProfile(@RequestBody @Validated UserDTO dto) {
        if (ObjectUtil.isNull(dto.getId())) {
            return toError(ApiResultCode.C0001);
        }
        userBizService.updateUserProfile(dto);
        return toSuccess();
    }

    @Operation(summary = "个人密码修改")
    @OperationLog(name = "个人信息", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    @PostMapping("/updateUserPasswd")
    public ApiResult updateUserPasswd(@RequestBody @Validated UserUpdatePasswdDTO dto) {
        return toSuccess(userBizService.updateUserPasswd(dto));
    }

    @Operation(summary = "个人头像上传")
    @OperationLog(name = "个人信息", type = OperationLogType.OPERATION_LOG_TYPE_UPLOAD)
    @PostMapping("/uploadUserAvatar")
    public ApiResult uploadUserAvatar(@RequestPart("file") MultipartFile file) {
        if (!FileHelper.isImageExtName(file)) {
            throw new BizRuntimeException(ApiResultCode.B0013);
        }
        UploadPretreatment uploadPretreatment = storageBizService.createUploadPretreatment(file);
        uploadPretreatment.image(200, 200);
        StorageRecord storageRecord = storageBizService.doUploadPretreatment(uploadPretreatment);
        userBizService.updateUserAvatar(storageRecord.getUrl());
        return toSuccess(storageRecord.getUrl());
    }
}
