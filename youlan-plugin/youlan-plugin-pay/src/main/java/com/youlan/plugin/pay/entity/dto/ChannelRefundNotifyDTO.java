package com.youlan.plugin.pay.entity.dto;

import com.youlan.plugin.pay.enums.RefundStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ChannelRefundNotifyDTO {

    @Schema(description = "退款订单ID")
    private Long refundId;

    @Schema(description = "支付订单ID")
    private Long orderId;

    @Schema(description = "商户订单号")
    private String mchOrderId;

    @Schema(description = "商户退款号")
    private String mchRefundId;

    @Schema(description = "退款状态")
    private RefundStatus refundStatus;

}
