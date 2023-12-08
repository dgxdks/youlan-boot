package com.youlan.plugin.pay.config;

import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "youlan.pay")
public class PayProperties {

    /**
     * 支付回调地址
     */
    @URL(message = "支付回调地址必须是URL格式")
    private String payNotifyUrl;

    /**
     * 退款回调地址
     */
    @URL(message = "退款回调地址必须是URL格式")
    private String refundNotifyUrl;

    /**
     * 支付订单前缀
     */
    private String payOrderPrefix = "P";

    /**
     * 退款订单前缀
     */
    private String refundOrderPrefix = "R";

    /**
     * 是否启用回调(只有接收回调的服务才需要开启)
     */
    public Boolean notifyEnabled = false;

    /**
     * 回调超时时间(s)
     */
    private int notifyTimeout = 120;

    /**
     * 回调调度Cron表达式
     */
    public String notifyScheduleCron = "0/1 * * * * ?";
}
