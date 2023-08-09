package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.framework.anno.SystemLog;
import com.youlan.framework.constant.SystemLogType;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.entity.LoginLog;
import com.youlan.system.service.LoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Tag(name = "登录日志")
@RestController
@RequestMapping("/system/loginLog")
@AllArgsConstructor
public class LoginLogController extends BaseController {
    private final LoginLogService loginLogService;

    @SaCheckPermission("system:loginLog:remove")
    @Operation(summary = "登录日志删除")
    @PostMapping("/removeLoginLog")
    @SystemLog(name = "登录日志", type = SystemLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removeLoginLog(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        return toSuccess(loginLogService.removeBatchByIds(dto.getList()));
    }

    @SaCheckPermission("system:loginLog:load")
    @Operation(summary = "登录日志详情")
    @PostMapping("/loadLoginLog")
    public ApiResult loadLoginLog(@RequestParam Long id) {
        return toSuccess(loginLogService.loadOne(id));
    }

    @SaCheckPermission("system:loginLog:pageList")
    @Operation(summary = "登录日志分页")
    @PostMapping("/getLoginLogPageList")
    @SystemLog(name = "登录日志", type = SystemLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getLoginLogPageList(@RequestBody LoginLog loginLog) {
        return toSuccess(loginLogService.loadPage(loginLog, DBHelper.getQueryWrapper(loginLog)));
    }

    @SaCheckPermission("system:loginLog:export")
    @Operation(summary = "登录日志导出")
    @PostMapping("/exportLoginLogList")
    @SystemLog(name = "登录日志", type = SystemLogType.OPERATION_LOG_TYPE_EXPORT)
    public void exportLoginLogList(@RequestBody LoginLog loginLog, HttpServletResponse response) throws IOException {
        List<LoginLog> loginLogList = loginLogService.loadMore(DBHelper.getQueryWrapper(loginLog));
        toExcel("登录日志.xlsx", "登录日志", LoginLog.class, loginLogList, response);
    }

}
