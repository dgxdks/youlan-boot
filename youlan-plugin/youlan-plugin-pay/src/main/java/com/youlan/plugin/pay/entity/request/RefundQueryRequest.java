package com.youlan.plugin.pay.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class RefundQueryRequest {

    @NotBlank(message = "外部退款订单号不能为空")
    @Schema(description = "外部退款订单号")
    private String outRefundNo;

    @NotBlank(message = "外部交易订单号不能为空")
    @Schema(description = "外部交易订单号")
    private String outTradeNo;

}
