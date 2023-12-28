package com.youlan.plugin.pay.entity.dto;

import com.youlan.plugin.pay.enums.TradeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@Accessors(chain = true)
public class SubmitPayOrderDTO {

    @NotNull(message = "支付订单ID不能为空")
    @Schema(description = "支付订单ID")
    private Long orderId;

    @NotNull(message = "交易类型不能为空")
    @Schema(description = "交易类型")
    private TradeType tradeType;

    @URL(message = "返回地址必须是URL格式")
    @Schema(description = "返回地址")
    private String returnUrl;

    @NotBlank(message = "客户端IP不能为空")
    @Schema(description = "客户端IP")
    private String clientIp;

    @Schema(description = "额外参数")
    private Map<String, String> extraParams;

}
