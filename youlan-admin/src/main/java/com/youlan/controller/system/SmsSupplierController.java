package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.controller.base.BaseController;
import com.youlan.plugin.sms.entity.SmsSupplier;
import com.youlan.plugin.sms.service.SmsSupplierService;
import com.youlan.system.anno.OperationLog;
import com.youlan.system.constant.OperationLogType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Tag(name = "短信管理")
@RestController
@RequestMapping("/system/smsSupplier")
@AllArgsConstructor
public class SmsSupplierController extends BaseController {

    private final SmsSupplierService smsSupplierService;

    @SaCheckPermission("system:smsSupplier:add")
    @Operation(summary = "短信厂商新增")
    @OperationLog(name = "短信厂商", type = OperationLogType.OPERATION_LOG_TYPE_ADD)
    @PostMapping("/addSmsSupplier")
    public ApiResult addSmsSupplier(@Validated @RequestBody SmsSupplier smsSupplier) {
        smsSupplierService.addSmsSupplier(smsSupplier);
        return toSuccess();
    }

    @SaCheckPermission("system:smsSupplier:update")
    @Operation(summary = "短信厂商修改")
    @PostMapping("/updateSmsSupplier")
    @OperationLog(name = "短信厂商", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateSmsSupplier(@Validated @RequestBody SmsSupplier smsSupplier) {
        if (ObjectUtil.isNull(smsSupplier.getId())) {
            return toError(ApiResultCode.C0001);
        }
        smsSupplierService.updateSmsSupplier(smsSupplier);
        return toSuccess();
    }

    @SaCheckPermission("system:smsSupplier:remove")
    @Operation(summary = "短信厂商删除")
    @PostMapping("/removeSmsSupplier")
    @OperationLog(name = "短信厂商", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removeSmsSupplier(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        return toSuccess(smsSupplierService.removeBatchByIds(dto.getList()));
    }

    @SaCheckPermission("system:smsSupplier:load")
    @Operation(summary = "短信厂商详情")
    @PostMapping("/loadSmsSupplier")
    public ApiResult loadSmsSupplier(@RequestParam Long id) {
        return toSuccess(smsSupplierService.getById(id));
    }

    @SaCheckPermission("system:smsSupplier:list")
    @Operation(summary = "短信厂商分页")
    @PostMapping("/getSmsSupplierPageList")
    @OperationLog(name = "短信厂商", type = OperationLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getSmsSupplierPageList(@RequestBody SmsSupplier smsSupplier) {
        return toSuccess(smsSupplierService.loadPage(DBHelper.getPage(smsSupplier), DBHelper.getQueryWrapper(smsSupplier)));
    }

    @SaCheckPermission("system:smsSupplier:export")
    @Operation(summary = "短信厂商导出")
    @PostMapping("/exportSmsSupplierList")
    @OperationLog(name = "短信厂商", type = OperationLogType.OPERATION_LOG_TYPE_EXPORT)
    public void exportSmsSupplierList(@RequestBody SmsSupplier smsSupplier, HttpServletResponse response) throws IOException {
        List<SmsSupplier> smsSupplierList = smsSupplierService.loadMore(DBHelper.getQueryWrapper(smsSupplier));
        toExcel("短信厂商.xlsx", "短信厂商", SmsSupplier.class, smsSupplierList, response);
    }

    @SaCheckPermission("system:smsSupplier:update")
    @Operation(summary = "短信厂商状态修改")
    @PostMapping("/updateSmsSupplierStatus")
    @OperationLog(name = "短信厂商", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateSmsSupplierStatus(@RequestParam Long id, @RequestParam String status) {
        smsSupplierService.updateSmsSupplierStatus(id, status);
        return toSuccess();
    }

    @SaCheckPermission("system:smsSupplier:remove")
    @Operation(summary = "短信厂商缓存刷新")
    @PostMapping("/refreshSmsSupplierCache")
    @OperationLog(name = "短信厂商", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult refreshSmsSupplierCache() {
        smsSupplierService.refreshSmsSupplierCache();
        return toSuccess();
    }

    @Operation(summary = "短信厂商额外参数")
    @PostMapping("/getSmsSupplierExtraParams")
    public ApiResult getSmsSupplierExtraParams(@RequestParam String supplier) {
        return toSuccess(smsSupplierService.getSmsSupplierExtraParams(supplier));
    }
}
