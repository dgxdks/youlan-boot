package com.youlan.plugin.pay.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class RefundRequest {

    @NotBlank(message = "外部订单号不能为空")
    @Schema(description = "外部交易订单号")
    private String outTradeNo;

    @NotBlank(message = "外部退款号不能为空")
    @Schema(description = "外部退款号")
    private String outRefundNo;

    @NotBlank(message = "退款原因不能为空")
    @Schema(description = "退款原因")
    private String refundReason;

    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0", inclusive = false, message = "支付金额必须大于0")
    @Schema(description = "支付金额")
    private BigDecimal payAmount;

    @NotNull(message = "退款金额不能为空")
    @DecimalMin(value = "0", inclusive = false, message = "退款金额必须大于0")
    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    @URL(message = "回调地址必须是URL格式")
    @NotBlank(message = "回调地址不能为空")
    @Schema(description = "回调地址")
    private String notifyUrl;
}
