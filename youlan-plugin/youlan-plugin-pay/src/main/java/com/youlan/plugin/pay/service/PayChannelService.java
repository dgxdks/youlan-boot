package com.youlan.plugin.pay.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.common.redis.helper.RedisHelper;
import com.youlan.plugin.pay.entity.PayChannel;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.enums.TradeType;
import com.youlan.plugin.pay.mapper.PayChannelMapper;
import com.youlan.plugin.pay.utils.PayUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PayChannelService extends BaseServiceImpl<PayChannelMapper, PayChannel> {

    private final PayChannelConfigService payChannelConfigService;

    /**
     * 获取可用的支付配置
     */
    public PayConfig loadPayConfigEnabled(Long channelId, TradeType tradeType) {
        PayConfig payConfig = this.loadPayConfigIfExists(channelId, tradeType);
        Assert.equals(DBConstant.VAL_STATUS_ENABLED, payConfig.getStatus(), ApiResultCode.E0012::getException);
        return payConfig;
    }

    /**
     * 如果存在获取支付配置
     */
    public PayConfig loadPayConfigIfExists(Long channelId, TradeType tradeType) {
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
     * 获取可用的支付渠道
     */
    public PayChannel loadPayChannelEnabled(Long id) {
        PayChannel payChannel = this.loadPayChannelIfExists(id);
        Assert.equals(DBConstant.VAL_STATUS_ENABLED, payChannel.getStatus(), ApiResultCode.E0021::getException);
        return payChannel;
    }

    /**
     * 如果存在获取支付渠道
     */
    public PayChannel loadPayChannelIfExists(Long id) {
        return this.loadOneOpt(id)
                .orElseThrow(ApiResultCode.E0020::getException);
    }

}
