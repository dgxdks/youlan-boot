package com.youlan.plugin.sms.provider.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.youlan.plugin.sms.provider.config.YunJiConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.comm.utils.SmsHttpUtils;

import java.util.List;
import java.util.Map;

@Slf4j
public class YunJiUtil {
    private static final String URL = "https://market.juncdt.com/smartmarket/msgService/sendMessage";
    private static final String URL_BATCH = "https://market.juncdt.com/smartmarket/msgService/sendMessageToMulti";

    /**
     * 获取短信响应
     *
     * @param config     短信配置
     * @param phone      手机号
     * @param templateId 模版ID
     * @param messages   消息内容
     * @return 短信响应
     */
    public static SmsResponse getSmsResponse(YunJiConfig config, String phone, String templateId, Map<String, String> messages) {
        RequestBody requestBody = new RequestBody(config.getAccessKeyId(), config.getAccessKeySecret(),
                config.getSignature(), templateId, config.getClassification(), phone, messages);
        String requestBodyJsonStr = JSONUtil.toJsonStr(requestBody);
        JSONObject responseBodyJson = SmsHttpUtils.instance()
                .postJson(URL, null, requestBodyJsonStr);
        JSONObject businessData = responseBodyJson.getJSONObject("BusinessData");
        boolean success = ObjectUtil.isNotNull(businessData) && ObjectUtil.equal(businessData.getInt("code"), 100);
        SmsResponse smsResponse = new SmsResponse();
        smsResponse.setSuccess(success);
        smsResponse.setData(responseBodyJson);
        smsResponse.setConfigId(config.getConfigId());
        return smsResponse;
    }

    /**
     * 获取短信响应
     *
     * @param config     短信配置
     * @param phones     手机号列表
     * @param templateId 模版ID
     * @param messages   消息内容
     * @return 短信响应
     */
    public static SmsResponse getSmsResponse(YunJiConfig config, List<String> phones, String templateId, Map<String, String> messages) {
        RequestBatchBody requestBatchBody = new RequestBatchBody(config.getAccessKeyId(), config.getAccessKeySecret(),
                config.getSignature(), templateId, config.getClassification(), phones, messages, RandomUtil.randomString(6));
        String requestBodyJsonStr = JSONUtil.toJsonStr(requestBatchBody);
        JSONObject responseBodyJson = SmsHttpUtils.instance()
                .postJson(URL_BATCH, null, requestBodyJsonStr);
        JSONObject businessData = responseBodyJson.getJSONObject("BusinessData");
        boolean success = ObjectUtil.isNotNull(businessData) && CollectionUtil.isNotEmpty(businessData.getJSONArray("list"));
        SmsResponse smsResponse = new SmsResponse();
        smsResponse.setSuccess(success);
        smsResponse.setData(responseBodyJson);
        smsResponse.setConfigId(config.getConfigId());
        return smsResponse;
    }


    @Data
    @AllArgsConstructor
    static class RequestBody {
        private String accessKey;
        private String accessSecret;
        private String signCode;
        private String templateCode;
        private String classificationSecret;
        private String phone;
        private Map<String, String> params;
    }

    @Data
    @AllArgsConstructor
    static class RequestBatchBody {
        private String accessKey;
        private String accessSecret;
        private String signCode;
        private String templateCode;
        private String classificationSecret;
        private List<String> phones;
        private Map<String, String> params;
        private String taskId;
    }
}