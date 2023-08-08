package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.entity.dto.ListDTO;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.utils.QueryWrapperUtil;
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

    @SaCheckPermission("system:loginLog:addLoginLog")
    @Operation(summary = "登录日志新增")
    @PostMapping("/addLoginLog")
    @SystemLog(name = "登录日志", type = SystemLogType.OPERATION_LOG_TYPE_ADD)
    public ApiResult addLoginLog(@Validated @RequestBody LoginLog loginLog) {
        return toSuccess(loginLogService.save(loginLog));
    }

    @SaCheckPermission("system:loginLog:updateLoginLog")
    @Operation(summary = "登录日志修改")
    @PostMapping("/updateLoginLog")
    @SystemLog(name = "登录日志", type = SystemLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateLoginLog(@Validated @RequestBody LoginLog loginLog) {
        if (ObjectUtil.isNull(loginLog.getId())) {
            return toError(ApiResultCode.C0001);
        }
        return toSuccess(loginLogService.updateById(loginLog));
    }

    @SaCheckPermission("system:loginLog:removeLoginLog")
    @Operation(summary = "登录日志删除")
    @PostMapping("/removeLoginLog")
    @SystemLog(name = "登录日志", type = SystemLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removeLoginLog(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        return toSuccess(loginLogService.removeBatchByIds(dto.getList()));
    }

    @SaCheckPermission("system:loginLog:loadLoginLog")
    @Operation(summary = "登录日志详情")
    @PostMapping("/loadLoginLog")
    public ApiResult loadLoginLog(@RequestParam Long id) {
        return toSuccess(loginLogService.loadOne(id));
    }

    @SaCheckPermission("system:loginLog:getLoginLogPageList")
    @Operation(summary = "登录日志分页")
    @PostMapping("/getLoginLogPageList")
    @SystemLog(name = "登录日志", type = SystemLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getLoginLogPageList(@RequestBody LoginLog loginLog) {
        return toSuccess(loginLogService.loadPage(loginLog, QueryWrapperUtil.getQueryWrapper(loginLog)));
    }

    @SaCheckPermission("system:loginLog:getLoginLogList")
    @Operation(summary = "登录日志列表")
    @PostMapping("/getLoginLogList")
    @SystemLog(name = "登录日志", type = SystemLogType.OPERATION_LOG_TYPE_LIST)
    public ApiResult getLoginLogList(@RequestBody LoginLog loginLog) {
        return toSuccess(loginLogService.loadMore(QueryWrapperUtil.getQueryWrapper(loginLog)));
    }

    @SaCheckPermission("system:loginLog:exportLoginLogList")
    @Operation(summary = "登录日志导出")
    @PostMapping("/exportLoginLogList")
    @SystemLog(name = "登录日志", type = SystemLogType.OPERATION_LOG_TYPE_EXPORT)
    public void exportLoginLogList(@RequestBody LoginLog loginLog, HttpServletResponse response) throws IOException {
        List<LoginLog> loginLogList = loginLogService.loadMore(QueryWrapperUtil.getQueryWrapper(loginLog));
        toExcel("登录日志.xlsx", "登录日志", LoginLog.class, loginLogList, response);
    }

}
