package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.entity.OperationLog;
import com.youlan.system.entity.dto.OperationLogPageDTO;
import com.youlan.system.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@Tag(name = "操作日志")
@RestController
@RequestMapping("/system/operationLog")
@AllArgsConstructor
public class OperationLogController extends BaseController {
    private final OperationLogService operationLogService;

    @SaCheckPermission("system:operationLog:remove")
    @Operation(summary = "操作日志删除")
    @PostMapping("/removeOperationLog")
    public ApiResult removeOperationLog(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        return toSuccess(operationLogService.removeBatchByIds(dto.getList()));
    }

    @SaCheckPermission("system:operationLog:load")
    @Operation(summary = "操作日志详情")
    @PostMapping("/loadOperationLog")
    public ApiResult loadOperationLog(@RequestParam Long id) {
        return toSuccess(operationLogService.loadOne(id));
    }

    @SaCheckPermission("system:operationLog:pageList")
    @Operation(summary = "操作日志分页")
    @PostMapping("/getOperationLogPageList")
    public ApiResult getOperationLogPageList(@RequestBody OperationLogPageDTO dto) {
        return toSuccess(operationLogService.loadPage(dto, DBHelper.getQueryWrapper(dto)));
    }

    @SaCheckPermission("system:operationLog:export")
    @Operation(summary = "操作日志导出")
    @PostMapping("/exportOperationLog")
    public void exportOperationLog(@RequestBody OperationLogPageDTO dto, HttpServletResponse response) throws IOException {
        List<OperationLog> operationLogList = operationLogService.loadMore(DBHelper.getQueryWrapper(dto));
        toExcel("操作日志.xlsx", "操作日志", OperationLog.class, operationLogList, response);
    }
}
