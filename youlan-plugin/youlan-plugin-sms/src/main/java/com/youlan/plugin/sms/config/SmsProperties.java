package com.youlan.plugin.sms.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.sms4j.comm.enumerate.ConfigType;
import org.dromara.sms4j.provider.config.SmsConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "youlan.sms")
@EqualsAndHashCode(callSuper = true)
public class SmsProperties extends SmsConfig {
    /**
     * 是否保存短信记录
     */
    private Boolean saveRecord;

    /**
     * 最大保存短信响应结果长度
     */
    private Integer responseMaxLength = 200;

    @Override
    public ConfigType getConfigType() {
        return super.getConfigType();
    }

    @Override
    public Boolean getIsPrint() {
        return super.getIsPrint();
    }

    @Override
    public Boolean getRestricted() {
        return super.getRestricted();
    }

    @Override
    public Integer getAccountMax() {
        return super.getAccountMax();
    }

    @Override
    public Integer getMinuteMax() {
        return super.getMinuteMax();
    }

    @Override
    public Integer getCorePoolSize() {
        return super.getCorePoolSize();
    }

    @Override
    public Integer getMaxPoolSize() {
        return super.getMaxPoolSize();
    }

    @Override
    public Integer getQueueCapacity() {
        return super.getQueueCapacity();
    }

    @Override
    public Boolean getShutdownStrategy() {
        return super.getShutdownStrategy();
    }

    @Override
    public Boolean getHttpLog() {
        return super.getHttpLog();
    }

    @Override
    public void setConfigType(ConfigType configType) {
        super.setConfigType(configType);
    }

    @Override
    public void setIsPrint(Boolean isPrint) {
        super.setIsPrint(isPrint);
    }

    @Override
    public void setRestricted(Boolean restricted) {
        super.setRestricted(restricted);
    }

    @Override
    public void setAccountMax(Integer accountMax) {
        super.setAccountMax(accountMax);
    }

    @Override
    public void setMinuteMax(Integer minuteMax) {
        super.setMinuteMax(minuteMax);
    }

    @Override
    public void setCorePoolSize(Integer corePoolSize) {
        super.setCorePoolSize(corePoolSize);
    }

    @Override
    public void setMaxPoolSize(Integer maxPoolSize) {
        super.setMaxPoolSize(maxPoolSize);
    }

    @Override
    public void setQueueCapacity(Integer queueCapacity) {
        super.setQueueCapacity(queueCapacity);
    }

    @Override
    public void setShutdownStrategy(Boolean shutdownStrategy) {
        super.setShutdownStrategy(shutdownStrategy);
    }

    @Override
    public void setHttpLog(Boolean HttpLog) {
        super.setHttpLog(HttpLog);
    }
}
