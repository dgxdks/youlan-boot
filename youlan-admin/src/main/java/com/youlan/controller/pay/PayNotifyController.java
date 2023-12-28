package com.youlan.controller.pay;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.controller.base.BaseController;
import com.youlan.plugin.pay.entity.PayNotify;
import com.youlan.plugin.pay.entity.PayNotifyRecord;
import com.youlan.plugin.pay.entity.dto.ChannelPayNotifyDTO;
import com.youlan.plugin.pay.enums.TradeType;
import com.youlan.plugin.pay.service.PayNotifyRecordService;
import com.youlan.plugin.pay.service.PayNotifyService;
import com.youlan.plugin.pay.service.biz.PayOrderBizService;
import com.youlan.plugin.pay.service.biz.PayRefundOrderBizService;
import com.youlan.system.anno.OperationLog;
import com.youlan.system.constant.OperationLogType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "支付回调")
@RestController
@RequestMapping("/pay/payNotify")
@AllArgsConstructor
public class PayNotifyController extends BaseController {
    private final PayOrderBizService payOrderBizService;
    private final PayRefundOrderBizService payRefundOrderBizService;
    private final PayNotifyService payNotifyService;
    private final PayNotifyRecordService payNotifyRecordService;

    @SaIgnore
    @PostMapping("/channel/order")
    @Operation(summary = "通道支付回调")
    public ApiResult channelPayNotify(@RequestBody ChannelPayNotifyDTO dto) {
        return toSuccess();
    }

    @SaIgnore
    @PostMapping("/channel/refund")
    @Operation(summary = "通道退款回调")
    public ApiResult channelRefundNotify(@RequestBody ChannelPayNotifyDTO dto) {
        return toSuccess();
    }

    @SaIgnore
    @PostMapping("/order/{configId}/{tradeType}")
    @Operation(summary = "支付回调")
    public String payNotify(@PathVariable("configId") Long configId,
                            @PathVariable("tradeType") TradeType tradeType,
                            @RequestParam(required = false) Map<String, String> params,
                            @RequestBody(required = false) String body) {
        payOrderBizService.payNotify(configId, tradeType, params, body);
        return ApiResultCode.SUCCESS.getStatus();
    }

    @SaIgnore
    @PostMapping("/refund/{configId}/{tradeType}")
    @Operation(summary = "退款回调")
    public String refundNotify(@PathVariable("configId") Long configId,
                               @PathVariable("tradeType") TradeType tradeType,
                               @RequestParam(required = false) Map<String, String> params,
                               @RequestBody(required = false) String body) {
        payRefundOrderBizService.refundNotify(configId, tradeType, params, body);
        return ApiResultCode.SUCCESS.getStatus();
    }

    @SaCheckPermission("pay:payNotify:load")
    @Operation(summary = "支付回调详情")
    @PostMapping("/loadPayNotify")
    public ApiResult loadPayNotify(@RequestParam Long id) {
        return toSuccess(payNotifyService.loadOne(id));
    }

    @SaCheckPermission("pay:payNotify:list")
    @Operation(summary = "支付回调分页")
    @PostMapping("/getPayNotifyPageList")
    @OperationLog(name = "支付回调", type = OperationLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getPayNotifyPageList(@RequestBody PayNotify payNotify) {
        List<String> sortColumns = List.of("create_time");
        Page<PayNotify> page = DBHelper.getPage(payNotify, sortColumns);
        return toSuccess(payNotifyService.loadPage(page, DBHelper.getQueryWrapper(payNotify)));
    }

    @SaCheckPermission("pay:payNotify:load")
    @Operation(summary = "支付回调记录详情")
    @PostMapping("/loadPayNotifyRecord")
    public ApiResult loadPayNotifyRecord(@RequestParam Long id) {
        return toSuccess(payNotifyRecordService.loadOne(id));
    }

    @SaCheckPermission("pay:payNotify:load")
    @Operation(summary = "支付回调记录分页")
    @PostMapping("/getPayNotifyRecordPageList")
    public ApiResult getPayNotifyRecordPageList(@RequestBody PayNotifyRecord payNotifyRecord) {
        List<String> sortColumns = List.of("create_time");
        Page<PayNotifyRecord> page = DBHelper.getPage(payNotifyRecord, sortColumns);
        return toSuccess(payNotifyRecordService.loadPage(page, DBHelper.getQueryWrapper(payNotifyRecord)));
    }

}
