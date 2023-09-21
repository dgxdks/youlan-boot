package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.StrUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.controller.base.BaseController;
import com.youlan.system.constant.SystemConstant;
import com.youlan.system.entity.dto.AccountRegistryDTO;
import com.youlan.system.entity.dto.UserDTO;
import com.youlan.system.helper.SystemConfigHelper;
import com.youlan.system.service.biz.LoginBizService;
import com.youlan.system.service.biz.UserBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户注册")
@RestController
@RequestMapping("/system/registry")
@AllArgsConstructor
public class RegistryController extends BaseController {
    private final LoginBizService loginBizService;
    private final UserBizService userBizService;

    @SaIgnore
    @Operation(summary = "账户注册")
    @PostMapping("/accountRegistry")
    public ApiResult accountRegistry(@RequestBody @Validated AccountRegistryDTO dto) {
        // 校验密码和确认密码
        if (!StrUtil.equalsAnyIgnoreCase(dto.getUserPassword(), dto.getConfirmPassword())) {
            throw new BizRuntimeException(ApiResultCode.A0023);
        }
        // 只有开启了用户注册才允许执行后续操作
        boolean accountRegistryEnabled = SystemConfigHelper.accountRegistryEnabled();
        if (!accountRegistryEnabled) {
            throw new BizRuntimeException(ApiResultCode.A0022);
        }
        loginBizService.doImageCaptchaCheck(null, dto.getCaptchaId(), dto.getCaptchaCode());
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName(dto.getUserName());
        userDTO.setNickName(dto.getUserName());
        userDTO.setUserPassword(dto.getUserPassword());
        //后台管理系统一般不会主动放开由C端用户进行注册，所以用户注册属于保留功能，如果要使用必须设置用户注册默认关联的机构ID
        //userDTO.setOrgId();
        return toSuccess(userBizService.addUser(userDTO));
    }

    @SaIgnore
    @Operation(summary = "账户注册开关")
    @PostMapping("/accountRegistryEnabled")
    public ApiResult accountRegistryEnabled() {
        return toSuccess(SystemConfigHelper.accountRegistryEnabled());
    }
}
