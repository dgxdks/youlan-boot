package com.youlan.plugin.pay.service.biz;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.helper.ListHelper;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.plugin.pay.entity.PayChannel;
import com.youlan.plugin.pay.entity.PayChannelConfig;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.enums.TradeType;
import com.youlan.plugin.pay.service.PayChannelConfigService;
import com.youlan.plugin.pay.service.PayChannelService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PayChannelBizService {
    private final PayChannelService payChannelService;
    private final PayChannelConfigService payChannelConfigService;

    /**
     * 获取可用的支付配置
     */
    public PayConfig loadPayConfigEnabled(Long channelId, TradeType tradeType) {
        PayConfig payConfig = this.loadPayConfigNotNull(channelId, tradeType);
        Assert.equals(DBConstant.VAL_STATUS_ENABLED, payConfig.getStatus(), ApiResultCode.E0012::getException);
        return payConfig;
    }

    /**
     * 获取支付配置且不为空
     */
    public PayConfig loadPayConfigNotNull(Long channelId, TradeType tradeType) {
        PayConfig payConfig = this.loadPayConfig(channelId, tradeType);
        if (ObjectUtil.isNull(payConfig)) {
            throw new BizRuntimeException(ApiResultCode.E0022, new Object[]{channelId, tradeType});
        }
        return payConfig;
    }

    /**
     * 获取支付配置
     */
    public PayConfig loadPayConfig(Long channelId, TradeType tradeType) {
        List<PayConfig> payConfigList = this.payChannelConfigService.getBaseMapper()
                .getPayConfigList(channelId, tradeType);
        return CollectionUtil.getFirst(payConfigList);
    }

    /**
     * 删除支付通道
     */
    @Transactional(rollbackFor = Exception.class)
    public void removePayChannel(List<Long> ids) {
        // 删除支付通道
        payChannelService.removeBatchByIds(ids);
        // 删除支付通道配置
        payChannelConfigService.removeByChannelIds(ids);
    }

    /**
     * 支付通道分页
     */
    public IPage<PayChannel> getPayChannelPageList(PayChannel payChannel) {
        IPage<PayChannel> payChannelPageResult = payChannelService.loadPage(payChannel, DBHelper.getQueryWrapper(payChannel));
        this.setPayChannelConfigList(payChannelPageResult.getRecords());
        return payChannelPageResult;
    }

    /**
     * 支付通道详情
     */
    public PayChannel loadPayChannel(Long id) {
        PayChannel payChannel = payChannelService.loadPayChannelNotNull(id);
        this.setPayChannelConfigList(Collections.singletonList(payChannel));
        return payChannel;
    }

    /**
     * 设置支付通道配置列表
     */
    private void setPayChannelConfigList(List<PayChannel> payChannelList) {
        if (CollectionUtil.isEmpty(payChannelList)) {
            return;
        }
        // 获取支付通道ID列表
        List<Long> channelIdList = ListHelper.mapList(payChannelList, PayChannel::getId);
        // 查询支付通道配置列表
        List<PayChannelConfig> pageChannelConfigList = payChannelConfigService.getBaseMapper().getListByChannelIds(channelIdList);
        // 根据支付通道ID对通道配置列表进行分组
        Map<Long, List<PayChannelConfig>> payChannelConfigMap = pageChannelConfigList.stream()
                .collect(Collectors.groupingBy(PayChannelConfig::getChannelId));
        payChannelList.forEach(payChannel -> {
            payChannel.setPayChannelConfigs(payChannelConfigMap.get(payChannel.getId()));
        });
    }

    /**
     * 新增支付通道
     */
    @Transactional(rollbackFor = Exception.class)
    public void addPayChannel(PayChannel payChannel) {
        boolean save = payChannelService.save(payChannel);
        if (!save) {
            throw new BizRuntimeException("新增通道配置失败");
        }
        payChannelConfigService.updatePayChannelConfig(payChannel.getId(), payChannel.getPayChannelConfigs());
    }

    /**
     * 修改支付通道
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePayChannel(PayChannel payChannel) {
        boolean update = payChannelService.updateById(payChannel);
        if (!update) {
            throw new BizRuntimeException("修改通道配置失败");
        }
        payChannelConfigService.updatePayChannelConfig(payChannel.getId(), payChannel.getPayChannelConfigs());
    }

}
