package com.youlan.plugin.pay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.plugin.pay.enums.TradeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("t_pay_channel_config")
@EqualsAndHashCode(callSuper = true)
public class PayChannelConfig extends PageDTO {

    @Schema(description = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "支付通道ID")
    private Long channelId;

    @Schema(description = "支付配置ID")
    private Long configId;

    @Schema(description = "交易类型")
    private TradeType tradeType;

}
