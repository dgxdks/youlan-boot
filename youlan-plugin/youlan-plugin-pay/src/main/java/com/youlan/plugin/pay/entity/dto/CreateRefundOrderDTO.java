package com.youlan.plugin.pay.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class CreateRefundOrderDTO {

    @NotNull(message = "商户订单号不能为空")
    @Schema(description = "商户订单号")
    private String mchOrderId;

    @NotBlank(message = "商户退款号不能为空")
    @Schema(description = "商户退款号")
    private String mchRefundId;

    @NotNull(message = "退款金额不能为空")
    @Schema(description = "退款金额")
    @DecimalMin(value = "0", inclusive = false, message = "退款金额必须大于零")
    private BigDecimal refundAmount;

    @Schema(description = "退款原因")
    private String refundReason;

}
