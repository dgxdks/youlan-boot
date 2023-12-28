package com.youlan.plugin.pay.entity.response;

import com.youlan.plugin.pay.enums.PayShowType;
import com.youlan.plugin.pay.enums.PayStatus;
import com.youlan.plugin.pay.enums.ResponseSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class PayResponse extends BaseResponse {

    @Schema(description = "支付状态")
    private PayStatus payStatus;

    @Schema(description = "外部交易订单号")
    private String outTradeNo;

    @Schema(description = "交易订单号")
    private String tradeNo;

    @Schema(description = "支付展示类型")
    private PayShowType payShowType;

    @Schema(description = "支付信息")
    private Object payInfo;

}
