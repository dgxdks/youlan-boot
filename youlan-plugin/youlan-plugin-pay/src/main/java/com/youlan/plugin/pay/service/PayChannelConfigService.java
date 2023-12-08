package com.youlan.plugin.pay.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.common.validator.helper.ValidatorHelper;
import com.youlan.plugin.pay.entity.PayChannelConfig;
import com.youlan.plugin.pay.mapper.PayChannelConfigMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PayChannelConfigService extends BaseServiceImpl<PayChannelConfigMapper, PayChannelConfig> {

    /**
     * 根据支付配置ID列表删除通道配置
     */
    public void removeByConfigIds(List<Long> configIds) {
        LambdaQueryWrapper<PayChannelConfig> queryWrapper = Wrappers.<PayChannelConfig>lambdaQuery()
                .in(PayChannelConfig::getConfigId, configIds);
        this.remove(queryWrapper);
    }

    /**
     * 根据通道ID列表删除通道配置
     */
    public void removeByChannelIds(List<Long> channelIds) {
        LambdaQueryWrapper<PayChannelConfig> queryWrapper = Wrappers.<PayChannelConfig>lambdaQuery()
                .in(PayChannelConfig::getChannelId, channelIds);
        this.remove(queryWrapper);
    }

    /**
     * 根据通道ID删除通道配置
     */
    public void removeByChannelId(Long channelId) {
        LambdaQueryWrapper<PayChannelConfig> queryWrapper = Wrappers.<PayChannelConfig>lambdaQuery()
                .eq(PayChannelConfig::getChannelId, channelId);
        this.remove(queryWrapper);
    }

    /**
     * 根据通道ID列表获取通道配置列表
     */
    public List<PayChannelConfig> getListByChannelIds(List<Long> channelIds) {
        return this.lambdaQuery()
                .in(PayChannelConfig::getChannelId, channelIds)
                .list();
    }

    /**
     * 修改支付通道配置
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePayChannelConfig(Long channelId, List<PayChannelConfig> payChannelConfigs) {
        if (CollectionUtil.isEmpty(payChannelConfigs)) {
            return;
        }
        // 设置支付通道ID
        payChannelConfigs.forEach(payChannelConfig -> payChannelConfig.setChannelId(channelId));
        // 通道配置校验并去重
        payChannelConfigs = payChannelConfigs.stream()
                .peek(ValidatorHelper::validateWithThrow)
                .distinct()
                .collect(Collectors.toList());
        // 删除原有支付通道配置
        this.removeByChannelId(channelId);
        // 更新支付通道配置
        this.saveBatch(payChannelConfigs);
    }

}
