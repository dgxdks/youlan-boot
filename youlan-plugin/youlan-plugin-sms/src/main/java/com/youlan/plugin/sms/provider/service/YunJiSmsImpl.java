package com.youlan.plugin.sms.provider.service;

import com.youlan.plugin.sms.enums.SmsSupplierEnum;
import com.youlan.plugin.sms.provider.config.YunJiConfig;
import com.youlan.plugin.sms.provider.utils.YunJiUtil;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.comm.delayedTime.DelayedTime;
import org.dromara.sms4j.provider.service.AbstractSmsBlend;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executor;

public class YunJiSmsImpl extends AbstractSmsBlend<YunJiConfig> {

    public YunJiSmsImpl(YunJiConfig config, Executor pool, DelayedTime delayed) {
        super(config, pool, delayed);
    }

    public YunJiSmsImpl(YunJiConfig config) {
        super(config);
    }

    @Override
    public String getSupplier() {
        return SmsSupplierEnum.YUNJI.getCode();
    }

    @Override
    public SmsResponse sendMessage(String phone, String message) {
        LinkedHashMap<String, String> messages = new LinkedHashMap<>();
        messages.put(getConfig().getTemplateName(), message);
        return sendMessage(phone, getConfig().getTemplateId(), messages);
    }

    @Override
    public SmsResponse sendMessage(String phone, String templateId, LinkedHashMap<String, String> messages) {
        return YunJiUtil.getSmsResponse(getConfig(), phone, templateId, messages);
    }

    @Override
    public SmsResponse massTexting(List<String> phones, String templateId, LinkedHashMap<String, String> messages) {
        return YunJiUtil.getSmsResponse(getConfig(), phones, templateId, messages);
    }

    @Override
    public SmsResponse massTexting(List<String> phones, String message) {
        LinkedHashMap<String, String> messages = new LinkedHashMap<>();
        messages.put(getConfig().getTemplateName(), message);
        return massTexting(phones, getConfig().getTemplateId(), messages);
    }
}
