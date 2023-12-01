package com.youlan.plugin.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlan.plugin.pay.entity.PayChannelConfig;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.enums.TradeType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PayChannelConfigMapper extends BaseMapper<PayChannelConfig> {

    /**
     * 获取支付配置列表
     *
     * @param channelId 支付渠道ID
     * @param tradeType 交易类型
     * @return 支付配置列表集合
     */
    List<PayConfig> getPayConfigList(@Param("channelId") Long channelId, @Param("tradeType") TradeType tradeType);


    /**
     * 获取支付配置列表
     *
     * @param channelIds 支付通道ID列表
     * @return 支付配置列表
     */
    List<PayChannelConfig> getListByChannelIds(@Param("channelIds") List<Long> channelIds);
}
