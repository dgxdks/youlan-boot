package com.youlan.plugin.pay.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.handler.JSONObjectTypeHandler;
import com.youlan.plugin.pay.enums.RefundStatus;
import com.youlan.plugin.pay.enums.TradeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("t_pay_refund_order")
public class PayRefundOrder {

    @Schema(description = "退款订单ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "商户订单号")
    private String mchOrderId;

    @Schema(description = "商户退款号")
    private String mchRefundId;

    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    @Schema(description = "退款状态(1-待退款 2-已退款 3-已失败)")
    private RefundStatus refundStatus;

    @Schema(description = "支付订单ID")
    private Long orderId;

    @Schema(description = "外部交易订单号")
    private String outTradeNo;

    @Schema(description = "交易订单号")
    private String tradeNo;

    @Schema(description = "交易类型(数据字典[pay_trade_type])")
    private TradeType tradeType;

    @Schema(description = "外部退款编号")
    private String outRefundNo;

    @Schema(description = "退款编号")
    private String refundNo;

    @Schema(description = "退款原因")
    private String refundReason;

    @Schema(description = "支付通道ID")
    private Long channelId;

    @Schema(description = "支付配置ID")
    private Long configId;

    @Schema(description = "成功时间")
    private Date successTime;

    @Schema(description = "错误码")
    private String errorCode;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "原始数据")
    @TableField(typeHandler = JSONObjectTypeHandler.class)
    private Object rawData;

    @Schema(description = "回调地址")
    private String notifyUrl;

    @Schema(description = "回调原始数据")
    @TableField(typeHandler = JSONObjectTypeHandler.class)
    private Object notifyRawData;

    @Schema(description = DBConstant.DESC_REMARK)
    private String remark;

    @Schema(description = DBConstant.DESC_CREATE_ID)
    @TableField(fill = FieldFill.INSERT)
    private Long createId;

    @Schema(description = DBConstant.DESC_CREATE_BY)
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @Schema(description = DBConstant.DESC_UPDATE_ID)
    @TableField(fill = FieldFill.UPDATE)
    private Long updateId;

    @Schema(description = DBConstant.DESC_UPDATE_BY)
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

    @Schema(description = DBConstant.DESC_CREATE_TIME)
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = DBConstant.DESC_UPDATE_TIME)
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

}
