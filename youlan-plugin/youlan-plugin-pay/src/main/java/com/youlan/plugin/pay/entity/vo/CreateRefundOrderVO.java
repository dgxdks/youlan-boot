package com.youlan.plugin.pay.entity.vo;

import com.youlan.plugin.pay.entity.PayRefundOrder;
import com.youlan.plugin.pay.enums.RefundStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateRefundOrderVO {

    /**
     * {@link PayRefundOrder#getId()}
     */
    @Schema(description = "退款订单ID")
    private Long orderId;

    @Schema(description = "退款状态")
    private RefundStatus refundStatus;

}
