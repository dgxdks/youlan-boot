package com.youlan.plugin.sms.enums;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.youlan.plugin.sms.provider.config.YunJiConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dromara.sms4j.aliyun.config.AlibabaConfig;
import org.dromara.sms4j.cloopen.config.CloopenConfig;
import org.dromara.sms4j.comm.constant.SupplierConstant;
import org.dromara.sms4j.ctyun.config.CtyunConfig;
import org.dromara.sms4j.emay.config.EmayConfig;
import org.dromara.sms4j.huawei.config.HuaweiConfig;
import org.dromara.sms4j.jdcloud.config.JdCloudConfig;
import org.dromara.sms4j.lianlu.config.LianLuConfig;
import org.dromara.sms4j.netease.config.NeteaseConfig;
import org.dromara.sms4j.provider.config.BaseConfig;
import org.dromara.sms4j.tencent.config.TencentConfig;
import org.dromara.sms4j.unisms.config.UniConfig;
import org.dromara.sms4j.yunpian.config.YunpianConfig;
import org.dromara.sms4j.zhutong.config.ZhutongConfig;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum SmsSupplierEnum {

    ALIBABA(SupplierConstant.ALIBABA, "阿里", AlibabaConfig.class),
    CLOOPEN(SupplierConstant.CLOOPEN, "容连云", CloopenConfig.class),
    CTYUN(SupplierConstant.CTYUN, "天翼云", CtyunConfig.class),
    EMAY(SupplierConstant.EMAY, "亿美软通", EmayConfig.class),
    HUAWEI(SupplierConstant.HUAWEI, "华为", HuaweiConfig.class),
    JDCLOUD(SupplierConstant.JDCLOUD, "京东", JdCloudConfig.class),
    NETEASE(SupplierConstant.NETEASE, "网易", NeteaseConfig.class),
    TENCENT(SupplierConstant.TENCENT, "腾讯", TencentConfig.class),
    UNISMS(SupplierConstant.UNISMS, "合一", UniConfig.class),
    YUNPIAN(SupplierConstant.YUNPIAN, "云片", YunpianConfig.class),
    ZHUTONG(SupplierConstant.ZHUTONG, "助通", ZhutongConfig.class),
    LIANLU(SupplierConstant.LIANLU, "联麓", LianLuConfig.class),
    YUNJI("yunji", "云极", YunJiConfig.class);

    private static final ConcurrentHashMap<String, SmsSupplierEnum> SMS_SUPPLIER_CACHE = createSmsSupplierCache();
    private final String code;
    private final String text;
    private final Class<? extends BaseConfig> configClass;

    public static ConcurrentHashMap<String, SmsSupplierEnum> createSmsSupplierCache() {
        ConcurrentHashMap<String, SmsSupplierEnum> smsSupplierCache = new ConcurrentHashMap<>();
        Arrays.stream(SmsSupplierEnum.values())
                .forEach(smsSupplierEnum -> {
                    smsSupplierCache.put(smsSupplierEnum.code, smsSupplierEnum);
                });
        return smsSupplierCache;
    }

    public static SmsSupplierEnum getSmsSupplierEnum(String supplier) {
        return SMS_SUPPLIER_CACHE.get(supplier);
    }

    public static String getExtraParamsJsonTemplate(String supplier) {
        SmsSupplierEnum smsSupplierEnum = getSmsSupplierEnum(supplier);
        if (smsSupplierEnum == null) {
            return null;
        }
        Class<? extends BaseConfig> configClass = smsSupplierEnum.configClass;
        // 获取当前配置类的属性名称集合
        Set<String> configFieldNames = Arrays.stream(configClass.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());
        // 获取配置类对应的JSON对象
        JSONObject configObj = JSONUtil.parseObj(ReflectUtil.newInstance(configClass), new JSONConfig().setIgnoreNullValue(false));
        Set<String> configObjKeySet = CollectionUtil.newHashSet(configObj.keySet());
        // 只保留当前配置类的属性
        configObjKeySet.removeAll(configFieldNames);
        for (String objKey : configObjKeySet) {
            if (!configFieldNames.contains(objKey)) {
                configObj.remove(objKey);
            }
        }
        return configObj.toStringPretty();
    }
}
