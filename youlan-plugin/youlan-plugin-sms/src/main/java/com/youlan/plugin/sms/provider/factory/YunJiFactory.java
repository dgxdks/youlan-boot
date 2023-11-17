package com.youlan.plugin.sms.provider.factory;

import cn.hutool.core.lang.Assert;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.plugin.sms.enums.SmsSupplierEnum;
import com.youlan.plugin.sms.provider.config.YunJiConfig;
import com.youlan.plugin.sms.provider.service.YunJiSmsImpl;
import org.dromara.sms4j.provider.factory.AbstractProviderFactory;
import org.springframework.stereotype.Component;

@Component
public class YunJiFactory extends AbstractProviderFactory<YunJiSmsImpl, YunJiConfig> {
    private static final YunJiFactory INSTANCE = new YunJiFactory();

    /**
     * 获取建造者实例
     *
     * @return 建造者实例
     */
    public static YunJiFactory getInstance() {
        return INSTANCE;
    }


    /**
     * 创建短信实现对象
     *
     * @param config 短信配置对象
     * @return 短信实现对象
     */
    @Override
    public YunJiSmsImpl createSms(YunJiConfig config) {
        // 此参数为必填参数
        Assert.notBlank(config.getClassification(), () -> new BizRuntimeException("请指定[classification]套餐编码参数"));
        return new YunJiSmsImpl(config);
    }

    @Override
    public String getSupplier() {
        return SmsSupplierEnum.YUNJI.getCode();
    }
}
