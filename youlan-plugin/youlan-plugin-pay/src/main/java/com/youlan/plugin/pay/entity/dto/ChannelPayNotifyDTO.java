package com.youlan.plugin.pay.entity.dto;

import com.youlan.plugin.pay.enums.PayStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ChannelPayNotifyDTO {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "商户订单号")
    private String mchOrderId;

    @Schema(description = "支付状态")
    private PayStatus payStatus;

}
