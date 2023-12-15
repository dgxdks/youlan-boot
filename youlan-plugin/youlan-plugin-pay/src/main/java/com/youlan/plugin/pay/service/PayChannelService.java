package com.youlan.plugin.pay.service;

import cn.hutool.core.lang.Assert;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.plugin.pay.entity.PayChannel;
import com.youlan.plugin.pay.mapper.PayChannelMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PayChannelService extends BaseServiceImpl<PayChannelMapper, PayChannel> {

    /**
     * 获取可用的支付渠道
     */
    public PayChannel loadPayChannelEnabled(Long id) {
        PayChannel payChannel = this.loadPayChannelNotNull(id);
        Assert.equals(DBConstant.VAL_STATUS_ENABLED, payChannel.getStatus(), ApiResultCode.E0021::getException);
        return payChannel;
    }

    /**
     * 获取支付渠道且不为空
     */
    public PayChannel loadPayChannelNotNull(Long id) {
        return this.loadOneOpt(id)
                .orElseThrow(ApiResultCode.E0020::getException);
    }
}
