package com.youlan.plugin.sms.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.common.redis.helper.RedisHelper;
import com.youlan.plugin.sms.entity.SmsSupplier;
import com.youlan.plugin.sms.enums.SmsSupplierEnum;
import com.youlan.plugin.sms.mapper.SmsSupplierMapper;
import com.youlan.plugin.sms.utils.SmsUtil;
import lombok.AllArgsConstructor;
import org.dromara.sms4j.core.datainterface.SmsReadConfig;
import org.dromara.sms4j.provider.config.BaseConfig;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SmsSupplierService extends BaseServiceImpl<SmsSupplierMapper, SmsSupplier> implements SmsReadConfig {

    @Override
    public BaseConfig getSupplierConfig(String configId) {
        SmsSupplier smsSupplier = loadSmsSupplierCacheByConfigId(configId);
        if (ObjectUtil.isNull(smsSupplier)) {
            throw new BizRuntimeException("短信厂商不存在");
        }
        return smsSupplierToBaseConfig(smsSupplier);
    }

    @Override
    public List<BaseConfig> getSupplierConfigList() {
        return this.list()
                .stream()
                .map(this::smsSupplierToBaseConfig)
                .collect(Collectors.toList());
    }

    public BaseConfig smsSupplierToBaseConfig(SmsSupplier smsSupplier) {
        String supplier = smsSupplier.getSupplier();
        SmsSupplierEnum smsSupplierEnum = SmsSupplierEnum.getSmsSupplierEnum(supplier);
        BaseConfig baseConfig = BeanUtil.copyProperties(smsSupplier, smsSupplierEnum.getConfigClass());
        // 设置额外参数
        String extraParams = smsSupplier.getExtraParams();
        JSONObject extraParamsObj = JSONUtil.parseObj(extraParams);
        extraParamsObj.keySet().forEach(key -> {
            ReflectUtil.setFieldValue(baseConfig, key, extraParamsObj.getStr(key));
        });
        return baseConfig;
    }

    /**
     * 根据配置标识获取短信厂商
     */
    public SmsSupplier loadSmsSupplierByConfigId(String configId) {
        List<SmsSupplier> smsSupplierList = this.lambdaQuery()
                .eq(SmsSupplier::getConfigId, configId)
                .list();
        return CollectionUtil.getFirst(smsSupplierList);
    }

    /**
     * 根据配置标识获取短信厂商缓存
     */
    public SmsSupplier loadSmsSupplierCacheByConfigId(String configId) {
        String smsSupplierRedisKey = SmsUtil.getSmsSupplierRedisKey(configId);
        SmsSupplier smsSupplier = RedisHelper.getCacheObject(smsSupplierRedisKey);
        if (ObjectUtil.isNotNull(smsSupplier)) {
            return smsSupplier;
        }
        smsSupplier = this.loadSmsSupplierByConfigId(configId);
        if (ObjectUtil.isNotNull(smsSupplier)) {
            RedisHelper.setCacheObject(smsSupplierRedisKey, smsSupplier);
        }
        return smsSupplier;
    }

    /**
     * 根据配置标识重新加载短信厂商缓存
     */
    public void reloadSmsSupplierCacheByConfigId(String configId) {
        SmsSupplier smsSupplier = this.loadSmsSupplierByConfigId(configId);
        String smsSupplierRedisKey = SmsUtil.getSmsSupplierRedisKey(configId);
        RedisHelper.setCacheObject(smsSupplierRedisKey, smsSupplier);
    }

    /**
     * 获取短信厂商且不为空
     */
    public SmsSupplier loadSmsSupplierNotNull(Serializable id) {
        return Optional.ofNullable(getById(id)).orElseThrow(() -> new BizRuntimeException("短信厂商不存在"));
    }

    /**
     * 新增短信厂商
     */
    public void addSmsSupplier(SmsSupplier smsSupplier) {
        beforeAddOrUpdateSmsSupplier(smsSupplier);
        // 配置标识不为空则需要判断重复，为空则自动填充
        String configId = smsSupplier.getConfigId();
        if (StrUtil.isNotBlank(configId)) {
            SmsSupplier oldSmsSupplier = this.loadSmsSupplierByConfigId(configId);
            if (ObjectUtil.isNotNull(oldSmsSupplier)) {
                throw new BizRuntimeException("配置标识不能重复");
            }
        } else {
            smsSupplier.setConfigId(IdUtil.objectId());
        }
        this.save(smsSupplier);
    }

    /**
     * 修改短信厂商
     */
    public void updateSmsSupplier(SmsSupplier smsSupplier) {
        // 不允许修改配置标识configId
        SmsSupplier oldSmsSupplier = loadSmsSupplierNotNull(smsSupplier.getId());
        smsSupplier.setConfigId(oldSmsSupplier.getConfigId());
        beforeAddOrUpdateSmsSupplier(smsSupplier);
        this.updateById(smsSupplier);
        this.reloadSmsSupplierCacheByConfigId(smsSupplier.getConfigId());
    }

    /**
     * 修改短信厂商状态
     */
    public void updateSmsSupplierStatus(Long id, String status) {
        SmsSupplier oldSmsSupplier = loadSmsSupplierNotNull(id);
        this.updateStatus(id, status);
        this.reloadSmsSupplierCacheByConfigId(oldSmsSupplier.getConfigId());
    }

    /**
     * 刷新短信厂商缓存
     */
    public void refreshSmsSupplierCache() {
        String smsSupplierRedisKeyPattern = SmsUtil.getSmsSupplierRedisKey(StringPool.ASTERISK);
        RedisHelper.deleteKeysByPattern(smsSupplierRedisKeyPattern);
    }

    /**
     * 短信厂商额外参数
     */
    public String getSmsSupplierExtraParams(String supplier) {
        String extraParamsJsonTemplate = SmsSupplierEnum.getExtraParamsJsonTemplate(supplier);
        if (StrUtil.isBlank(extraParamsJsonTemplate)) {
            return new JSONObject().toStringPretty();
        }
        return extraParamsJsonTemplate;
    }

    /**
     * 新增或修改短信厂商前置操作
     */
    public void beforeAddOrUpdateSmsSupplier(SmsSupplier smsSupplier) {
        // 短信厂商必须存在于枚举中
        String supplier = smsSupplier.getSupplier();
        SmsSupplierEnum smsSupplierEnum = SmsSupplierEnum.getSmsSupplierEnum(supplier);
        if (ObjectUtil.isNull(smsSupplierEnum)) {
            throw new BizRuntimeException("短信厂商信息不存在");
        }
        // 校验短信厂商额外参数
        String extraParams = smsSupplier.getExtraParams();
        if (StrUtil.isNotBlank(extraParams)) {
            if (!JSONUtil.isTypeJSON(extraParams)) {
                throw new BizRuntimeException("短信厂商额外参数必须是JSON格式");
            }
        } else {
            smsSupplier.setExtraParams(new JSONObject().toStringPretty());
        }
    }
}
