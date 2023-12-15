package com.youlan.controller.pay;

import cn.dev33.satoken.annotation.SaIgnore;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.controller.base.BaseController;
import com.youlan.plugin.pay.entity.dto.NotifyDTO;
import com.youlan.plugin.pay.enums.TradeType;
import com.youlan.plugin.pay.service.biz.PayOrderBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "支付回调")
@RestController
@RequestMapping("/pay/payNotify")
@AllArgsConstructor
public class PayNotifyController extends BaseController {
    private final PayOrderBizService payOrderBizService;

    @SaIgnore
    @PostMapping("/channel/pay")
    @Operation(summary = "通道支付回调")
    public ApiResult channelPayNotify(@RequestBody NotifyDTO notifyDTO) {
        return toSuccess();
    }

    @SaIgnore
    @PostMapping("/channel/refund")
    @Operation(summary = "通道退款回调")
    public ApiResult channelRefundNotify(@RequestBody NotifyDTO notifyDTO) {
        return toSuccess();
    }

    @SaIgnore
    @PostMapping("/pay/{configId}/{tradeType}")
    @Operation(summary = "支付回调")
    public String payNotify(@PathVariable("configId") Long configId,
                            @PathVariable("tradeType") TradeType tradeType,
                            @RequestParam(required = false) Map<String, String> params,
                            @RequestBody(required = false) String body) {
        payOrderBizService.payNotify(configId, tradeType, params, body);
        return "SUCCESS";
    }

    @SaIgnore
    @PostMapping("/refund/{configId}/{tradeType}")
    @Operation(summary = "退款回调")
    public String refundNotify(@PathVariable("configId") Long configId,
                               @PathVariable("tradeType") TradeType tradeType,
                               @RequestParam(required = false) Map<String, String> params,
                               @RequestBody(required = false) String body) {
        payOrderBizService.refundNotify(configId, tradeType, params, body);
        return "SUCCESS";
    }

}
