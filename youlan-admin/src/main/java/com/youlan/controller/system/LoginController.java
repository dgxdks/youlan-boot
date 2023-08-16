package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaIgnore;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.entity.dto.AccountLoginDTO;
import com.youlan.system.service.biz.LoginBizService;
import com.youlan.system.service.biz.RoleBizService;
import com.youlan.system.helper.SystemAuthHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户登录")
@RestController
@RequestMapping("/system/login")
@AllArgsConstructor
public class LoginController extends BaseController {
    private final LoginBizService loginBizService;
    private final RoleBizService roleBizService;

    @SaIgnore
    @Operation(summary = "用户账号登录")
    @PostMapping("/accountLogin")
    public ApiResult accountLogin(@Validated @RequestBody AccountLoginDTO dto) {
        return toSuccess(loginBizService.login(dto));
    }

    @Operation(summary = "用户退出登录")
    @PostMapping("/logout")
    public ApiResult logout() {
        loginBizService.logout();
        return toSuccess();
    }

    @Operation(summary = "获取登录信息")
    @PostMapping("/getLoginInfo")
    public ApiResult getLoginInfo() {
        return toSuccess(loginBizService.getLoginInfo());
    }

    @Operation(summary = "获取用户菜单树列表")
    @PostMapping("/getMenuTreeList")
    public ApiResult getMenuTreeList() {
        return toSuccess(roleBizService.getMenuTreeList(SystemAuthHelper.getUserId()));
    }
}
