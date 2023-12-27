package com.youlan.plugin.pay.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.db.handler.JSONObjectTypeHandler;
import com.youlan.plugin.pay.enums.PayStatus;
import com.youlan.plugin.pay.enums.TradeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
@TableName("t_pay_record")
@EqualsAndHashCode(callSuper = true)
public class PayRecord extends PageDTO {

    @Schema(description = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "外部交易订单号")
    private String outTradeNo;

    @Schema(description = "交易订单号")
    private String tradeNo;

    @Schema(description = "支付订单ID")
    private Long orderId;

    @Schema(description = "支付配置ID")
    private Long configId;

    @Schema(description = "交易类型")
    private TradeType tradeType;

    @Schema(description = "客户端IP")
    private String clientIp;

    @Schema(description = "支付状态(1-待支付 2-已支付 3-已关闭 4-已退款)")
    private PayStatus payStatus;

    @Schema(description = "额外参数(JSON格式)")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, String> extraParams;

    @Schema(description = "回调地址")
    private String notifyUrl;

    @Schema(description = "回调原始数据")
    @TableField(typeHandler = JSONObjectTypeHandler.class)
    private Object notifyRawData;

    @Schema(description = "错误码")
    private String errorCode;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "原始数据")
    @TableField(typeHandler = JSONObjectTypeHandler.class)
    private Object rawData;

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

    @Schema(description = DBConstant.DESC_CREATE_TIME)
    @TableField(exist = false)
    private List<Date> createTimeRange;

    @Schema(description = "配置名称")
    @TableField(exist = false)
    private String configName;

}
