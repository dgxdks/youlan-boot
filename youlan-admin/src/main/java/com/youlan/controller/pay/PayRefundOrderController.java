package com.youlan.controller.pay;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.controller.base.BaseController;
import com.youlan.plugin.pay.entity.PayRefundOrder;
import com.youlan.plugin.pay.entity.dto.CreateRefundOrderDTO;
import com.youlan.plugin.pay.entity.dto.PayRefundOrderPageDTO;
import com.youlan.plugin.pay.service.PayRefundOrderService;
import com.youlan.plugin.pay.service.biz.PayRefundOrderBizService;
import com.youlan.system.anno.OperationLog;
import com.youlan.system.constant.OperationLogType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Tag(name = "退款订单")
@RestController
@RequestMapping("/pay/payRefundOrder")
@AllArgsConstructor
public class PayRefundOrderController extends BaseController {
    private final PayRefundOrderService payRefundOrderService;
    private final PayRefundOrderBizService payRefundOrderBizService;

    @SaCheckPermission("pay:payRefundOrder:add")
    @Operation(summary = "退款订单创建")
    @PostMapping("/createPayRefundOrder")
    @OperationLog(name = "退款订单", type = OperationLogType.OPERATION_LOG_TYPE_ADD)
    public ApiResult createPayRefundOrder(@Validated @RequestBody CreateRefundOrderDTO dto) {
        return toSuccess(payRefundOrderBizService.createRefundOrder(dto));
    }

    @SaCheckPermission("pay:payRefundOrder:update")
    @Operation(summary = "退款订单同步")
    @PostMapping("/syncPayRefundOrder")
    @OperationLog(name = "退款订单", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult syncPayRefundOrder(@RequestParam Long id) {
        PayRefundOrder payRefundOrder = payRefundOrderService.loadRefundOrderNotNull(id);
        payRefundOrderBizService.syncRefundOrder(payRefundOrder);
        return toSuccess();
    }

    @SaCheckPermission("pay:payRefundOrder:update")
    @Operation(summary = "退款订单修改")
    @PostMapping("/updatePayRefundOrder")
    @OperationLog(name = "退款订单", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updatePayRefundOrder(@Validated @RequestBody PayRefundOrder payOrder) {
        if (ObjectUtil.isNull(payOrder.getId())) {
            return toError(ApiResultCode.C0001);
        }
        return toSuccess();
    }

    @SaCheckPermission("pay:payRefundOrder:remove")
    @Operation(summary = "退款订单删除")
    @PostMapping("/removePayRefundOrder")
    @OperationLog(name = "退款订单", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removePayRefundOrder(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        payRefundOrderService.removeBatchByIds(dto.getList());
        return toSuccess();
    }

    @SaCheckPermission("pay:payRefundOrder:load")
    @Operation(summary = "退款订单详情")
    @PostMapping("/loadPayRefundOrder")
    public ApiResult loadPayRefundOrder(@RequestParam Long id) {
        return toSuccess(payRefundOrderService.loadOne(id));
    }

    @SaCheckPermission("pay:payRefundOrder:list")
    @Operation(summary = "退款订单分页")
    @PostMapping("/getPayRefundOrderPageList")
    @OperationLog(name = "退款订单", type = OperationLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getPayRefundOrderPageList(@RequestBody PayRefundOrderPageDTO dto) {
        return toSuccess(payRefundOrderService.getPayRefundOrderPageList(dto));
    }

    @SaCheckPermission("pay:payRefundOrder:export")
    @Operation(summary = "退款订单导出")
    @PostMapping("/exportPayRefundOrderList")
    @OperationLog(name = "退款订单", type = OperationLogType.OPERATION_LOG_TYPE_EXPORT)
    public void exportPayRefundOrderList(@RequestBody PayRefundOrder payOrder, HttpServletResponse response) {
    }

}
