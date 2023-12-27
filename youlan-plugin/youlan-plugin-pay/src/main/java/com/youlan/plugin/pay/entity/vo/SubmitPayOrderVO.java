package com.youlan.plugin.pay.entity.vo;

import com.youlan.plugin.pay.enums.PayShowType;
import com.youlan.plugin.pay.enums.PayStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SubmitPayOrderVO {

    @Schema(description = "支付状态(1-待支付 2-已支付 3-已关闭 4-已退款)")
    private PayStatus payStatus;

    @Schema(description = "展示类型")
    private PayShowType payShowType;

    @Schema(description = "支付信息")
    private Object payInfo;

}
