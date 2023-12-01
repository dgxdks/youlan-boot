package com.youlan.controller.pay;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
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
import com.youlan.plugin.pay.entity.dto.PayOrderDTO;
import com.youlan.plugin.pay.entity.dto.SubmitPayOrderDTO;
import com.youlan.plugin.pay.service.PayOrderService;
import com.youlan.plugin.pay.service.biz.PayBizService;
import com.youlan.system.anno.OperationLog;
import com.youlan.system.constant.OperationLogType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Tag(name = "支付订单")
@RestController
@RequestMapping("/pay/payOrder")
@AllArgsConstructor
public class PayOrderController extends BaseController {
    private final PayOrderService payOrderService;
    private final PayBizService payBizService;

    @SaIgnore
    // @SaCheckPermission("pay:payOrder:create")
    @Operation(summary = "支付订单创建")
    @PostMapping("/createPayOrder")
    public ApiResult createPayOrder(@Validated @RequestBody CreatePayOrderDTO dto) {
        return toSuccess(payBizService.createPayOrder(dto));
    }

    @SaIgnore
//    @SaCheckPermission("pay:payOrder:submit")
    @Operation(summary = "支付订单提交")
    @PostMapping("/submitPayOrder")
    public ApiResult submitPayOrder(@RequestBody SubmitPayOrderDTO dto) {
        if (StrUtil.isBlank(dto.getClientIp())) {
            dto.setClientIp(ServletHelper.getClientIp());
        }
        return toSuccess(payBizService.submitPayOrder(dto));
    }

    @SaCheckPermission("pay:payOrder:add")
    @Operation(summary = "支付订单新增")
    @OperationLog(name = "支付订单", type = OperationLogType.OPERATION_LOG_TYPE_ADD)
    @PostMapping("/addPayOrder")
    public ApiResult addPayOrder(@Validated @RequestBody PayOrder payOrder) {
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
        return toSuccess();
    }

    @SaCheckPermission("pay:payOrder:load")
    @Operation(summary = "支付订单详情")
    @PostMapping("/loadPayOrder")
    public ApiResult loadPayOrder(@RequestParam Long id) {
        return toSuccess();
    }

    @SaCheckPermission("pay:payOrder:list")
    @Operation(summary = "支付订单分页")
    @PostMapping("/getPayOrderPageList")
    @OperationLog(name = "支付订单", type = OperationLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getPayOrderPageList(@RequestBody PayOrderDTO dto) {
        return toSuccess(payOrderService.getPayOrderPageList(dto));
    }

    @SaCheckPermission("pay:payOrder:export")
    @Operation(summary = "支付订单导出")
    @PostMapping("/exportPayOrderList")
    @OperationLog(name = "支付订单", type = OperationLogType.OPERATION_LOG_TYPE_EXPORT)
    public void exportPayOrderList(@RequestBody PayOrder payOrder, HttpServletResponse response) throws IOException {
        return;
    }

    @SaCheckPermission("pay:payOrder:update")
    @Operation(summary = "支付订单状态修改")
    @PostMapping("/updatePayOrderStatus")
    @OperationLog(name = "支付订单", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updatePayOrderStatus(@RequestParam Long id, @RequestParam String status) {
        return toSuccess();
    }

    @SaCheckPermission("pay:payOrder:remove")
    @Operation(summary = "支付订单缓存刷新")
    @PostMapping("/refreshPayOrderCache")
    @OperationLog(name = "支付订单", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult refreshPayOrderCache() {
        return toSuccess();
    }
}
