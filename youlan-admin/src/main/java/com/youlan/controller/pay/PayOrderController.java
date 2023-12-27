package com.youlan.controller.pay;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.core.servlet.helper.ServletHelper;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.controller.base.BaseController;
import com.youlan.plugin.pay.entity.PayOrder;
import com.youlan.plugin.pay.entity.dto.CreatePayOrderDTO;
import com.youlan.plugin.pay.entity.dto.PayOrderPageDTO;
import com.youlan.plugin.pay.entity.dto.SubmitPayOrderDTO;
import com.youlan.plugin.pay.service.PayOrderService;
import com.youlan.plugin.pay.service.biz.PayOrderBizService;
import com.youlan.system.anno.OperationLog;
import com.youlan.system.constant.OperationLogType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Tag(name = "支付订单")
@RestController
@RequestMapping("/pay/payOrder")
@AllArgsConstructor
public class PayOrderController extends BaseController {
    private final PayOrderService payOrderService;
    private final PayOrderBizService payOrderBizService;

    @SaCheckPermission("pay:payOrder:add")
    @Operation(summary = "支付订单创建")
    @PostMapping("/createPayOrder")
    public ApiResult createPayOrder(@Validated @RequestBody CreatePayOrderDTO dto) {
        return toSuccess(payOrderBizService.createPayOrder(dto));
    }

    @SaCheckPermission("pay:payOrder:update")
    @Operation(summary = "支付订单提交")
    @PostMapping("/submitPayOrder")
    public ApiResult submitPayOrder(@RequestBody SubmitPayOrderDTO dto) {
        if (StrUtil.isBlank(dto.getClientIp())) {
            dto.setClientIp(ServletHelper.getClientIp());
        }
        return toSuccess(payOrderBizService.submitPayOrder(dto));
    }

    @SaCheckPermission("pay:payOrder:update")
    @Operation(summary = "支付订单同步")
    @PostMapping("/syncPayOrder")
    @OperationLog(name = "支付订单", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult syncPayRefundOrder(@RequestParam Long id) {
        payOrderBizService.syncPayOrder(id);
        return toSuccess();
    }

    @SaCheckPermission("pay:payOrder:update")
    @Operation(summary = "支付订单修改")
    @PostMapping("/updatePayOrder")
    @OperationLog(name = "支付订单", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updatePayOrder(@Validated @RequestBody PayOrder payOrder) {
        if (ObjectUtil.isNull(payOrder.getId())) {
            return toError(ApiResultCode.C0001);
        }
        return toSuccess();
    }

    @SaCheckPermission("pay:payOrder:remove")
    @Operation(summary = "支付订单删除")
    @PostMapping("/removePayOrder")
    @OperationLog(name = "支付订单", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removePayOrder(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        payOrderBizService.removePayOrder(dto.getList());
        return toSuccess();
    }

    @SaCheckPermission("pay:payOrder:load")
    @Operation(summary = "支付订单详情")
    @PostMapping("/loadPayOrder")
    public ApiResult loadPayOrder(@RequestParam Long id) {
        return toSuccess(payOrderService.loadOne(id));
    }

    @SaCheckPermission("pay:payOrder:list")
    @Operation(summary = "支付订单分页")
    @PostMapping("/getPayOrderPageList")
    @OperationLog(name = "支付订单", type = OperationLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getPayOrderPageList(@RequestBody PayOrderPageDTO dto) {
        return toSuccess(payOrderService.getPayOrderPageList(dto));
    }

    @SaCheckPermission("pay:payOrder:export")
    @Operation(summary = "支付订单导出")
    @PostMapping("/exportPayOrderList")
    @OperationLog(name = "支付订单", type = OperationLogType.OPERATION_LOG_TYPE_EXPORT)
    public void exportPayOrderList(@RequestBody PayOrder payOrder, HttpServletResponse response) {
    }
}
