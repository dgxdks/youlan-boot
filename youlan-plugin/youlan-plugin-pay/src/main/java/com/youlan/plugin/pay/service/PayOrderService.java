package com.youlan.plugin.pay.service;

import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.plugin.pay.entity.PayOrder;
import com.youlan.plugin.pay.mapper.PayOrderMapper;
import org.springframework.stereotype.Service;

@Service
public class PayOrderService extends BaseServiceImpl<PayOrderMapper, PayOrder> {

    /**
     * 根据商户订单号获取支付订单
     *
     * @param mchOrderId 商户订单号
     * @return 支付订单
     */
    public PayOrder loadPayOrderByMchOrderId(String mchOrderId) {
        return this.lambdaQuery()
                .eq(PayOrder::getMchOrderId, mchOrderId)
                .one();
    }
}
