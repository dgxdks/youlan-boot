package com.youlan.plugin.sms.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.plugin.sms.config.SmsProperties;
import com.youlan.plugin.sms.entity.SmsRecord;
import com.youlan.plugin.sms.mapper.SmsRecordMapper;
import lombok.AllArgsConstructor;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.youlan.plugin.sms.constant.SmsConstant.booleanToSendStatus;

@Service
@AllArgsConstructor
public class SmsRecordService extends BaseServiceImpl<SmsRecordMapper, SmsRecord> {
    private final SmsProperties smsProperties;

    /**
     * 创建短信记录列表
     *
     * @param phones     手机号列表
     * @param templateId 模版ID
     * @param message    消息内容
     * @param smsType    短信类型
     * @param sendType   发送类型
     * @return 短信记录列表
     */
    public List<SmsRecord> createSmsRecords(List<String> phones, String templateId, String message, String smsType, String sendType) {
        Date sendTime = new Date();
        String sendBatch = IdUtil.objectId();
        return phones.stream()
                .map(phone -> {
                    SmsRecord smsRecord = new SmsRecord();
                    smsRecord.setPhone(phone);
                    smsRecord.setTemplateId(templateId);
                    smsRecord.setMessage(message);
                    smsRecord.setSmsType(smsType);
                    smsRecord.setSendType(sendType);
                    smsRecord.setSendTime(sendTime);
                    smsRecord.setSendBatch(sendBatch);
                    return smsRecord;
                }).collect(Collectors.toList());
    }

    /**
     * 创建短信记录
     *
     * @param phone      手机号
     * @param templateId 模版ID
     * @param message    消息内容
     * @param smsType    短信类型
     * @param sendType   发送类型
     * @return 短信记录
     */
    public SmsRecord createSmsRecord(String phone, String templateId, String message, String smsType, String sendType) {
        SmsRecord smsRecord = new SmsRecord();
        smsRecord.setPhone(phone);
        smsRecord.setTemplateId(templateId);
        smsRecord.setMessage(message);
        smsRecord.setSmsType(smsType);
        smsRecord.setSendType(sendType);
        smsRecord.setSendTime(new Date());
        return smsRecord;
    }

    /**
     * 保存短信记录
     *
     * @param smsRecord    短信记录
     * @param smsResponse  短信响应
     * @param responseTime 响应时间
     * @return 短信记录
     */
    public SmsRecord saveSmsRecord(SmsRecord smsRecord, SmsResponse smsResponse, Date responseTime) {
        setSmsRecord(smsRecord, smsResponse, responseTime);
        this.save(smsRecord);
        return smsRecord;
    }

    /**
     * 保存短信记录列表
     *
     * @param smsRecords   短信记录列表
     * @param smsResponse  短信响应
     * @param responseTime 响应时间
     * @return 短信响应列表
     */
    @Transactional(rollbackFor = Exception.class)
    public List<SmsRecord> saveSmsRecords(List<SmsRecord> smsRecords, SmsResponse smsResponse, Date responseTime) {
        smsRecords.forEach(smsRecord -> setSmsRecord(smsRecord, smsResponse, responseTime));
        this.saveBatch(smsRecords);
        return smsRecords;
    }

    /**
     * 设置短信记录
     *
     * @param smsRecord    短信记录
     * @param smsResponse  短信响应
     * @param responseTime 响应时间
     */
    public void setSmsRecord(SmsRecord smsRecord, SmsResponse smsResponse, Date responseTime) {
        if (ObjectUtil.isNotNull(smsResponse)) {
            smsRecord.setConfigId(smsResponse.getConfigId());
            smsRecord.setSendStatus(booleanToSendStatus(smsResponse.isSuccess()));
            Object data = smsResponse.getData();
            // 如果消息不为空则截取指定长度消息
            if (ObjectUtil.isNotNull(data)) {
                String responseData = data.toString();
                Integer responseMaxLength = smsProperties.getResponseMaxLength();
                responseData = StrUtil.sub(responseData, 0, responseMaxLength + 1);
                smsRecord.setResponseData(responseData);
            }
        }
        smsRecord.setResponseTime(responseTime);
    }
}
