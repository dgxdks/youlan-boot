package com.youlan.plugin.pay.entity.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class PayResponse extends BaseResponse {

    @Schema(description = "支付状态")
    private String payStatus;

    @Schema(description = "外部交易订单号")
    private String outTradeNo;



}
