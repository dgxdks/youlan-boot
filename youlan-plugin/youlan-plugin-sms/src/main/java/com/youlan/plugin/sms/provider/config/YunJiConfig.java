package com.youlan.plugin.sms.provider.config;

import com.youlan.plugin.sms.enums.SmsSupplierEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.sms4j.provider.config.BaseConfig;

@Data
@EqualsAndHashCode(callSuper = true)
public class YunJiConfig extends BaseConfig {

    /**
     * 模板变量名称
     */
    private String templateName;

    /**
     * 套餐码
     */
    private String classification;

    /**
     * 获取供应商
     *
     * @since 3.0.0
     */
    @Override
    public String getSupplier() {
        return SmsSupplierEnum.YUNJI.getCode();
    }
}
