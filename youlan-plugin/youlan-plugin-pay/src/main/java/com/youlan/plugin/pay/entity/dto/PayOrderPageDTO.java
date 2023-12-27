package com.youlan.plugin.pay.entity.dto;

import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.plugin.pay.enums.PayStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PayOrderPageDTO extends PageDTO {

    @Schema(description = "支付订单ID")
    private Long id;

    @Schema(description = "商户订单号")
    private String mchOrderId;

    @Schema(description = "支付状态(1-待支付 2-已支付 3-已关闭 4-已退款)")
    private PayStatus payStatus;

    @Schema(description = "支付通道ID")
    private Long channelId;

    @Schema(description = "支付配置ID")
    private Long configId;

    @Schema(description = DBConstant.DESC_CREATE_TIME)
    private List<Date> createTimeRange;

}
