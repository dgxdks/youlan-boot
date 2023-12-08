package com.youlan.controller.pay;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.controller.base.BaseController;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.service.PayChannelConfigService;
import com.youlan.plugin.pay.service.PayConfigService;
import com.youlan.plugin.pay.service.biz.PayConfigBizService;
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

@Tag(name = "支付配置")
@RestController
@RequestMapping("/pay/payConfig")
@AllArgsConstructor
public class PayConfigController extends BaseController {

    private final PayConfigService payConfigService;
    private final PayConfigBizService payConfigBizService;

    @SaCheckPermission("pay:payConfig:add")
    @Operation(summary = "支付配置新增")
    @OperationLog(name = "支付配置", type = OperationLogType.OPERATION_LOG_TYPE_ADD)
    @PostMapping("/addPayConfig")
    public ApiResult addPayConfig(@Validated @RequestBody PayConfig payConfig) {
        payConfigBizService.addPayConfig(payConfig);
        return toSuccess();
    }

    @SaCheckPermission("pay:payConfig:update")
    @Operation(summary = "支付配置修改")
    @PostMapping("/updatePayConfig")
    @OperationLog(name = "支付配置", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updatePayConfig(@Validated @RequestBody PayConfig payConfig) {
        if (ObjectUtil.isNull(payConfig.getId())) {
            return toError(ApiResultCode.C0001);
        }
        payConfigBizService.updatePayConfig(payConfig);
        return toSuccess();
    }

    @SaCheckPermission("pay:payConfig:remove")
    @Operation(summary = "支付配置删除")
    @PostMapping("/removePayConfig")
    @OperationLog(name = "支付配置", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removePayConfig(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        payConfigBizService.removePayConfig(dto.getList());
        return toSuccess();
    }

    @SaCheckPermission("pay:payConfig:load")
    @Operation(summary = "支付配置详情")
    @PostMapping("/loadPayConfig")
    public ApiResult loadPayConfig(@RequestParam Long id) {
        return toSuccess(payConfigService.loadOne(id));
    }

    @SaCheckPermission("pay:payConfig:list")
    @Operation(summary = "支付配置分页")
    @PostMapping("/getPayConfigPageList")
    @OperationLog(name = "支付配置", type = OperationLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getPayConfigPageList(@RequestBody PayConfig PayConfig) {
        return toSuccess(payConfigService.loadPage(PayConfig, DBHelper.getQueryWrapper(PayConfig)));
    }

    @SaCheckPermission("pay:payConfig:list")
    @Operation(summary = "支付配置列表")
    @PostMapping("/getPayConfigList")
    @OperationLog(name = "支付配置", type = OperationLogType.OPERATION_LOG_TYPE_LIST)
    public ApiResult getPayConfigList(@RequestBody PayConfig PayConfig) {
        return toSuccess(payConfigService.loadMore(DBHelper.getQueryWrapper(PayConfig)));
    }

    @SaCheckPermission("pay:payConfig:export")
    @Operation(summary = "支付配置导出")
    @PostMapping("/exportPayConfigList")
    @OperationLog(name = "支付配置", type = OperationLogType.OPERATION_LOG_TYPE_EXPORT)
    public void exportPayConfigList(@RequestBody PayConfig PayConfig, HttpServletResponse response) throws IOException {
        List<PayConfig> payConfigs = payConfigService.loadMore(DBHelper.getQueryWrapper(PayConfig));
        toExcel("支付配置.xlsx", "支付配置", PayConfig.class, payConfigs, response);
    }

    @SaCheckPermission("pay:payConfig:update")
    @Operation(summary = "支付配置状态修改")
    @PostMapping("/updatePayConfigStatus")
    @OperationLog(name = "支付配置", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updatePayConfigStatus(@RequestParam Long id, @RequestParam String status) {
        payConfigService.updateStatus(id, status);
        return toSuccess();
    }

}
