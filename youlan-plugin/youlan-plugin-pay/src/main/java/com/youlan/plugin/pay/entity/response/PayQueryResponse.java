package com.youlan.plugin.pay.entity.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PayQueryResponse extends BaseResponse {

    @Schema(description = "支付状态")
    private String payStatus;

    @Schema(description = "交易订单号")
    private String tradeNo;

}
