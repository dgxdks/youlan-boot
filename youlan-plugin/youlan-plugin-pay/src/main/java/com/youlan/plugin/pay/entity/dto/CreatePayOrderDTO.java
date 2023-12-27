package com.youlan.plugin.pay.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class CreatePayOrderDTO {

    @NotNull(message = "支付通道ID不能为空")
    @Schema(description = "支付通道ID")
    private Long channelId;

    @NotBlank(message = "商户订单号不能为空")
    @Length(max = 64, message = "商户订单号不能超过64个长度")
    @Schema(description = "商户订单号")
    private String mchOrderId;

    @Length(max = 32, message = "标题信息不能超过32个长度")
    @Schema(description = "商品标题")
    private String subject;

    @Length(max = 128, message = "详情信息不能超过128个长度")
    @Schema(description = "商品详情")
    private String detail;

    @NotNull(message = "支付金额不能为空")
    @Schema(description = "支付金额")
    @DecimalMin(value = "0", inclusive = false, message = "支付金额必须大于零")
    private BigDecimal payAmount;

    @NotNull(message = "过期时间不能为空")
    @Schema(description = "过期时间")
    private Date expireTime;

}
