package com.youlan.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.controller.base.BaseController;
import com.youlan.system.anno.OperationLog;
import com.youlan.system.constant.OperationLogType;
import com.youlan.system.entity.dto.OnlineUserPageDTO;
import com.youlan.system.helper.SystemAuthHelper;
import com.youlan.system.service.biz.LoginBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "在线用户")
@RestController
@RequestMapping("/monitor/online")
@AllArgsConstructor
public class OnlineController extends BaseController {

    private final LoginBizService loginBizService;

    @SaCheckPermission("monitor:onlineUser:list")
    @Operation(summary = "在线用户分页")
    @RequestMapping("/getOnlineUserPageList")
    public ApiResult getOnlineUserPageList(@RequestBody OnlineUserPageDTO dto) {
        return toSuccess(loginBizService.getOnlineUserPageList(dto));
    }

    @SaCheckPermission("monitor:onlineUser:kickout")
    @Operation(summary = "在线强退", description = "会将对应token标记为已下线")
    @OperationLog(name = "在线用户", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    @RequestMapping("/kickoutOnlineUser")
    public ApiResult kickoutOnlineUser(@RequestParam String tokenValue) {
        SystemAuthHelper.kickoutByTokenValue(tokenValue);
        return toSuccess();
    }
}
