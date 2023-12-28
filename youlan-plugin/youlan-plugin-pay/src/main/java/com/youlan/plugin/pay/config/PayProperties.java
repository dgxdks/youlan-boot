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

    /**
     * 是否启用支付记录同步(启用后将会定期向支付平台同步支付记录和支付订单状态)
     */
    public Boolean recordSyncEnabled = false;

    /**
     * 支付记录同步Cron表达式
     */
    public String recordSyncCron = "0 0/1 * * * ?";

    /**
     * 支付记录同步多久时间内的订单(分钟)
     */
    public Integer recordSyncInMinutes = 10;

    /**
     * 是否启用支付订单过期(启用后将会定期关闭过期的支付订单)
     */
    public Boolean orderExpireEnabled = false;

    /**
     * 支付订单过期Cron表达式
     */
    private String orderExpireCron = "0 0/1 * * *?";

    /**
     * 是否启用退款订单同步(启用后将会定期向支付平台同步退款订单状态)
     */
    public Boolean refundOrderSyncEnabled = false;

    /**
     * 退款订单同步Cron表达式
     */
    public String refundOrderSyncCron = "0 0/1 * * * ?";

}
