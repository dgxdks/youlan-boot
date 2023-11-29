package com.youlan.controller.pay;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.StrUtil;
import com.youlan.controller.base.BaseController;
import com.youlan.plugin.pay.enums.TradeType;
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
    @SaIgnore
    @PostMapping("/order/{configId}/{tradeType}")
    @Operation(summary = "支付回调")
    public String payNotify(@PathVariable("configId") String channelId,
                            @PathVariable("tradeType") TradeType tradeType,
                            @RequestParam(required = false) Map<String, String> params,
                            @RequestBody(required = false) String body) {
        return StrUtil.EMPTY;
    }

    @SaIgnore
    @PostMapping("/refund/{configId}/{tradeType}")
    @Operation(summary = "退款回调")
    public String refundNotify(@PathVariable("configId") String configId,
                               @PathVariable("tradeType") TradeType tradeType,
                               @RequestParam(required = false) Map<String, String> params,
                               @RequestBody(required = false) String body) {
        return StrUtil.EMPTY;
    }

}
