package com.youlan.plugin.pay.entity.dto;

import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.plugin.pay.enums.PayStatus;
import com.youlan.plugin.pay.enums.RefundStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class PayRefundOrderPageDTO extends PageDTO {

    @Schema(description = "退款订单ID")
    private Long id;

    @Schema(description = "支付订单ID")
    private String orderId;

    @Schema(description = "商户订单号")
    private String mchOrderId;

    @Schema(description = "商户退款号")
    private String mchRefundId;

    @Schema(description = "退款状态(1-待退款 2-已退款 3-已失败)")
    private RefundStatus refundStatus;

    @Schema(description = "支付通道ID")
    private Long channelId;

    @Schema(description = "支付配置ID")
    private Long configId;

    @Schema(description = DBConstant.DESC_CREATE_TIME)
    private List<Date> createTimeRange;
}
