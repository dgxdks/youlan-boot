package com.youlan.plugin.pay.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotifyDTO {

    @Schema(description = "商户订单号")
    private String mchOrderId;

    @Schema(description = "订单ID")
    private Long orderId;

}
