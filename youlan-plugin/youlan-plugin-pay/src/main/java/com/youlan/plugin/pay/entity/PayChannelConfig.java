package com.youlan.plugin.pay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.plugin.pay.enums.PayType;
import com.youlan.plugin.pay.enums.TradeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@TableName("t_pay_channel_config")
@EqualsAndHashCode(callSuper = false)
public class PayChannelConfig {

    @EqualsAndHashCode.Exclude
    @Schema(description = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "支付通道ID不能为空")
    @Schema(description = "支付通道ID")
    private Long channelId;

    @NotNull(message = "支付配置ID不能为空")
    @Schema(description = "支付配置ID")
    private Long configId;

    @NotNull(message = "交易类型不能为空")
    @Schema(description = "交易类型")
    private TradeType tradeType;

    @EqualsAndHashCode.Exclude
    @Schema(description = "支付类型")
    @TableField(exist = false)
    private PayType payType;
}
