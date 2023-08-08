package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaIgnore;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.entity.dto.UserLoginDTO;
import com.youlan.system.service.biz.LoginBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户登录")
@RestController
@AllArgsConstructor
public class LoginController extends BaseController {
    private final LoginBizService loginBizService;

    @SaIgnore
    @Operation(summary = "账号登录")
    @PostMapping("/login")
    public ApiResult userLogin(@Validated @RequestBody UserLoginDTO dto) {
        return toSuccess(loginBizService.login(dto));
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public ApiResult userLogout() {
        loginBizService.logout();
        return toSuccess();
    }
}
