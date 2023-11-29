package com.youlan.plugin.pay.entity.response;

import com.youlan.plugin.pay.enums.RefundStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class RefundResponse extends BaseResponse {

    @Schema(description = "退款状态")
    private RefundStatus refundStatus;

    @Schema(description = "外部退款订单号")
    private String outRefundNo;

    @Schema(description = "退款订单号")
    private String refundNo;

}
