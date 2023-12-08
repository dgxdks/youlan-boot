package com.youlan.plugin.pay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.plugin.pay.enums.NotifyStatus;
import com.youlan.plugin.pay.enums.NotifyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("t_pay_notify")
public class PayNotify {

    @Schema(description = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "回调类型(1-支付回调 2-退款回调)")
    private NotifyType notifyType;

    @Schema(description = "回调状态(1-等待回调 2-回调成功 3-回调失败 4-请求成功 5-请求失败)")
    private NotifyStatus notifyStatus;

    @Schema(description = "订单ID(支付回调时-支付订单ID 退款回调时-退款订单ID)")
    private Long orderId;

    @Schema(description = "商户订单号")
    private String mchOrderId;

    @Schema(description = "回调地址")
    private String notifyUrl;

    @Schema(description = "通知次数")
    private Integer notifyTimes;

    @Schema(description = "下次通知时间")
    private Date nextNotifyTime;

    @Schema(description = "最后通知时间")
    private Date lastNotifyTime;

}
