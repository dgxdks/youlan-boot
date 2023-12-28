package com.youlan.plugin.pay.entity.vo;

import com.youlan.plugin.pay.entity.PayOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreatePayOrderVO {

    /**
     * {@link PayOrder#getId()}
     */
    @Schema(description = "支付订单ID")
    private Long orderId;

}
