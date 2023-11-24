package com.youlan.controller.pay;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.StrUtil;
import com.youlan.controller.base.BaseController;
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
    @PostMapping("/order/{configId}")
    @Operation(summary = "支付回调")
    public String payNotify(@PathVariable String configId, @RequestParam(required = false) Map<String, String> params,
                            @RequestBody(required = false) String body) {
        return StrUtil.EMPTY;
    }

    @SaIgnore
    @PostMapping("/refund/{configId}")
    @Operation(summary = "退款回调")
    public String refundNotify(@PathVariable String configId, @RequestParam(required = false) Map<String, String> params,
                               @RequestBody(required = false) String body) {
        return StrUtil.EMPTY;
    }

}
