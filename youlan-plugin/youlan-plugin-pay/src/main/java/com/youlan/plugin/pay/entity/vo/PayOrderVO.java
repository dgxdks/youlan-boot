package com.youlan.plugin.pay.entity.vo;

import com.youlan.common.db.constant.DBConstant;
import com.youlan.plugin.pay.enums.PayStatus;
import com.youlan.plugin.pay.enums.TradeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class PayOrderVO {

    @Schema(description = "支付订单ID")
    private Long id;

    @Schema(description = "商户订单号")
    private String mchOrderId;

    @Schema(description = "外部交易订单号")
    private String outTradeNo;

    @Schema(description = "交易订单号")
    private String tradeNo;

    @Schema(description = "支付状态(1-待支付 2-已支付 3-已关闭 4-已退款)")
    private PayStatus payStatus;

    @Schema(description = "交易类型(数据字典[pay_trade_type])")
    private TradeType tradeType;

    @Schema(description = "支付金额")
    private BigDecimal payAmount;

    @Schema(description = "过期时间")
    private Date expireTime;

    @Schema(description = "成功时间")
    private Date successTime;

    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    @Schema(description = "支付通道ID")
    private Long channelId;

    @Schema(description = "支付配置ID")
    private Long configId;

    @Schema(description = "支付记录ID")
    private Long recordId;

    @Schema(description = "商品标题")
    private String subject;

    @Schema(description = "商品详情")
    private String detail;

    @Schema(description = "回调地址")
    private String notifyUrl;

    @Schema(description = "客户端IP")
    private String clientIp;

    @Schema(description = "客户端ID")
    private String clientId;

    @Schema(description = DBConstant.DESC_REMARK)
    private String remark;

    @Schema(description = DBConstant.DESC_CREATE_ID)
    private Long createId;

    @Schema(description = DBConstant.DESC_CREATE_BY)
    private String createBy;

    @Schema(description = DBConstant.DESC_UPDATE_ID)
    private Long updateId;

    @Schema(description = DBConstant.DESC_UPDATE_BY)
    private String updateBy;

    @Schema(description = DBConstant.DESC_CREATE_TIME)
    private Date createTime;

    @Schema(description = DBConstant.DESC_UPDATE_TIME)
    private Date updateTime;

    @Schema(description = "支付配置名称")
    private String configName;

    @Schema(description = "支付通道名称")
    private String channelName;

    @Schema(description = "回调原始数据")
    private Object notifyRawData;

    @Schema(description = "原始数据")
    private Object rawData;

}
