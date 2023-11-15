package com.youlan.plugin.sms.service.biz;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.plugin.sms.entity.SmsRecord;
import com.youlan.plugin.sms.service.SmsRecordService;
import com.youlan.plugin.sms.service.SmsSupplierService;
import lombok.AllArgsConstructor;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.callback.CallBack;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.dromara.sms4j.provider.config.BaseConfig;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import static com.youlan.plugin.sms.constant.SmsConstant.*;

@Service
@AllArgsConstructor
public class SmsBizService {
    private final SmsSupplierService smsSupplierService;
    private final SmsRecordService smsRecordService;

    /**
     * 标准短信
     *
     * @param configId 配置标识
     * @param phone    手机号
     * @param message  消息内容
     * @return 短信记录
     */
    public SmsRecord sendMessage(String configId, String phone, String message) {
        SmsBlend smsBlend = getOrCreateSmsBlend(configId);
        SmsRecord smsRecord = smsRecordService.createSmsRecord(phone, null, message, SMS_TYPE_NORMAL, SEND_TYPE_SINGLE);
        SmsResponse smsResponse = smsBlend.sendMessage(phone, message);
        return smsRecordService.saveSmsRecord(smsRecord, smsResponse, new Date());
    }

    /**
     * 标准短信
     *
     * @param configId   配置标识
     * @param phone      手机号
     * @param templateId 模版ID
     * @param messages   消息内容
     * @return 短信记录
     */
    public SmsRecord sendMessage(String configId, String phone, String templateId, LinkedHashMap<String, String> messages) {
        SmsBlend smsBlend = getOrCreateSmsBlend(configId);
        SmsRecord smsRecord = smsRecordService.createSmsRecord(phone, templateId, JSONUtil.toJsonStr(messages), SMS_TYPE_NORMAL, SEND_TYPE_SINGLE);
        SmsResponse smsResponse = smsBlend.sendMessage(phone, templateId, messages);
        return smsRecordService.saveSmsRecord(smsRecord, smsResponse, new Date());
    }

    /**
     * 群发短信
     *
     * @param configId 配置标识
     * @param phones   手机号
     * @param message  消息内容
     * @return 短信记录列表
     */
    public List<SmsRecord> massTexting(String configId, List<String> phones, String message) {
        SmsBlend smsBlend = getOrCreateSmsBlend(configId);
        List<SmsRecord> smsRecords = smsRecordService.createSmsRecords(phones, null, message, SMS_TYPE_NORMAL, SEND_TYPE_BATCH);
        SmsResponse smsResponse = smsBlend.massTexting(phones, message);
        return smsRecordService.saveSmsRecords(smsRecords, smsResponse, new Date());
    }

    /**
     * 群发短信
     *
     * @param configId   配置标识
     * @param phones     手机号
     * @param templateId 模版ID
     * @param messages   消息内容
     * @return 短信记录列表
     */
    public List<SmsRecord> massTexting(String configId, List<String> phones, String templateId, LinkedHashMap<String, String> messages) {
        SmsBlend smsBlend = getOrCreateSmsBlend(configId);
        List<SmsRecord> smsRecords = smsRecordService.createSmsRecords(phones, templateId, JSONUtil.toJsonStr(messages), SMS_TYPE_NORMAL, SEND_TYPE_BATCH);
        SmsResponse smsResponse = smsBlend.massTexting(phones, templateId, messages);
        return smsRecordService.saveSmsRecords(smsRecords, smsResponse, new Date());
    }

    /**
     * 异步短信
     *
     * @param configId 配置标识
     * @param phone    手机号
     * @param message  消息内容
     * @param callBack 短信回调
     * @return 短信记录
     */
    public SmsRecord sendMessageAsync(String configId, String phone, String message, CallBack callBack) {
        SmsBlend smsBlend = getOrCreateSmsBlend(configId);
        SmsRecord smsRecord = smsRecordService.createSmsRecord(phone, null, message, SMS_TYPE_ASYNC, SEND_TYPE_SINGLE);
        smsBlend.sendMessageAsync(phone, message, smsResponse -> {
            smsRecordService.saveSmsRecord(smsRecord, smsResponse, new Date());
            callBack.callBack(smsResponse);
        });
        return smsRecord;
    }

    /**
     * 异步短信
     *
     * @param configId 配置标识
     * @param phone    手机号
     * @param message  消息内容
     * @return 短信记录
     */
    public SmsRecord sendMessageAsync(String configId, String phone, String message) {
        SmsBlend smsBlend = getOrCreateSmsBlend(configId);
        SmsRecord smsRecord = smsRecordService.createSmsRecord(phone, null, message, SMS_TYPE_ASYNC, SEND_TYPE_SINGLE);
        smsBlend.sendMessageAsync(phone, message);
        smsRecordService.saveSmsRecord(smsRecord, null, new Date());
        return smsRecord;
    }

    /**
     * 异步短信
     *
     * @param configId   配置标识
     * @param phone      手机号
     * @param templateId 模版ID
     * @param messages   消息内容
     * @param callBack   短信回调
     * @return 短信记录
     */
    public SmsRecord sendMessageAsync(String configId, String phone, String templateId, LinkedHashMap<String, String> messages, CallBack callBack) {
        SmsBlend smsBlend = getOrCreateSmsBlend(configId);
        SmsRecord smsRecord = smsRecordService.createSmsRecord(phone, templateId, JSONUtil.toJsonStr(messages), SMS_TYPE_ASYNC, SEND_TYPE_SINGLE);
        smsBlend.sendMessageAsync(phone, templateId, messages, smsResponse -> {
            smsRecordService.setSmsRecord(smsRecord, smsResponse, new Date());
            callBack.callBack(smsResponse);
        });
        return smsRecord;
    }

    /**
     * 异步短信
     *
     * @param configId   配置标识
     * @param phone      手机号
     * @param templateId 模版ID
     * @param messages   消息内容
     * @return 短信记录
     */
    public SmsRecord sendMessageAsync(String configId, String phone, String templateId, LinkedHashMap<String, String> messages) {
        SmsBlend smsBlend = getOrCreateSmsBlend(configId);
        SmsRecord smsRecord = smsRecordService.createSmsRecord(phone, templateId, JSONUtil.toJsonStr(messages), SMS_TYPE_ASYNC, SEND_TYPE_SINGLE);
        smsBlend.sendMessageAsync(phone, templateId, messages);
        smsRecordService.saveSmsRecord(smsRecord, null, new Date());
        return smsRecord;
    }

    /**
     * 延迟短信
     *
     * @param configId    配置标识
     * @param phone       手机号
     * @param message     消息内容
     * @param delayedTime 延迟时间
     * @return 短信记录
     */
    public SmsRecord delayedMessage(String configId, String phone, String message, Long delayedTime) {
        SmsBlend smsBlend = getOrCreateSmsBlend(configId);
        SmsRecord smsRecord = smsRecordService.createSmsRecord(phone, null, message, SMS_TYPE_DELAYED, SEND_TYPE_SINGLE);
        smsRecord.setDelayedTime(delayedTime);
        smsBlend.delayedMessage(phone, message, delayedTime);
        smsRecordService.saveSmsRecord(smsRecord, null, new Date());
        return smsRecord;
    }

    /**
     * 延迟短信
     *
     * @param configId    配置标识
     * @param phone       手机号
     * @param templateId  模版ID
     * @param messages    消息内容
     * @param delayedTime 延迟时间
     * @return 短信记录
     */
    public SmsRecord delayedMessage(String configId, String phone, String templateId, LinkedHashMap<String, String> messages, Long delayedTime) {
        SmsBlend smsBlend = getOrCreateSmsBlend(configId);
        SmsRecord smsRecord = smsRecordService.createSmsRecord(phone, templateId, JSONUtil.toJsonStr(messages), SMS_TYPE_DELAYED, SEND_TYPE_SINGLE);
        smsBlend.delayedMessage(phone, templateId, messages, delayedTime);
        smsRecordService.saveSmsRecord(smsRecord, null, new Date());
        return smsRecord;
    }

    /**
     * 延迟短信
     *
     * @param configId    配置标识
     * @param phones      手机号
     * @param message     消息内容
     * @param delayedTime 延迟时间
     * @return 短信记录列表
     */
    public List<SmsRecord> delayMassTexting(String configId, List<String> phones, String message, Long delayedTime) {
        SmsBlend smsBlend = getOrCreateSmsBlend(configId);
        List<SmsRecord> smsRecords = smsRecordService.createSmsRecords(phones, null, message, SMS_TYPE_DELAYED, SEND_TYPE_BATCH);
        smsRecords.forEach(smsRecord -> smsRecord.setDelayedTime(delayedTime));
        smsBlend.delayMassTexting(phones, message, delayedTime);
        return smsRecords;
    }

    /**
     * 延迟短信
     *
     * @param configId    配置标识
     * @param phones      手机号
     * @param templateId  模版ID
     * @param messages    消息内容
     * @param delayedTime 延迟时间
     * @return 短信记录列表
     */
    public List<SmsRecord> delayMassTexting(String configId, List<String> phones, String templateId, LinkedHashMap<String, String> messages, Long delayedTime) {
        SmsBlend smsBlend = getOrCreateSmsBlend(configId);
        List<SmsRecord> smsRecords = smsRecordService.createSmsRecords(phones, templateId, JSONUtil.toJsonStr(messages), SMS_TYPE_DELAYED, SEND_TYPE_BATCH);
        smsBlend.delayMassTexting(phones, templateId, messages, delayedTime);
        smsRecordService.saveSmsRecords(smsRecords, null, new Date());
        return smsRecords;
    }

    /**
     * 根据配置标识获取短信发送对象
     *
     * @param configId 配置标识
     */
    public SmsBlend getOrCreateSmsBlend(String configId) {
        //优先加载持久化层配置，为了能获取持久化层最新配置必须每次获取时重新创建短信发送对象
        BaseConfig supplierConfig = smsSupplierService.getSupplierConfig(configId);
        supplierConfig.setConfigId(configId);
        if (ObjectUtil.isNotNull(supplierConfig)) {
            SmsFactory.createSmsBlend(supplierConfig);
        }
        SmsBlend smsBlend = SmsFactory.getSmsBlend(configId);
        if (ObjectUtil.isNull(smsBlend)) {
            throw new BizRuntimeException("短信配置标识不存在");
        }
        return smsBlend;
    }
}
