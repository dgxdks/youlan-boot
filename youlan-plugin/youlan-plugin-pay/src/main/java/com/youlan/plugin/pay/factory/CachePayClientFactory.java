package com.youlan.plugin.pay.factory;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.youlan.plugin.pay.client.PayClient;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.enums.TradeType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class CachePayClientFactory extends DefaultPayClientFactory {
    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final ConcurrentHashMap<String, PayClient> PAY_CLIENT_CACHE = new ConcurrentHashMap<>();

    @Override
    public PayClient createPayClient(PayConfig payConfig, TradeType tradeType) {
        String clientCacheKey = payConfig.getId() + StrPool.COLON + tradeType.getCode();
        PayClient oldPayClient = PAY_CLIENT_CACHE.computeIfAbsent(clientCacheKey, key -> super.createPayClient(payConfig, tradeType));
        // 新旧支付参数如果不相等则需要刷新支付客户端的支付参数
        String oldPayParams = oldPayClient.payConfig().getParams();
        String newPayParams = payConfig.getParams();
        // 如果一致则直接返回支付客户端
        if (StrUtil.equals(newPayParams, oldPayParams)) {
            return oldPayClient;
        }
        try {
            // 未获取到锁直接返回原有客户端
            if (!LOCK.tryLock()) {
                return oldPayClient;
            }
            // 创建新客户端
            PayClient newPayClient = super.createPayClient(payConfig, tradeType);
            PAY_CLIENT_CACHE.put(clientCacheKey, newPayClient);
            // 停止旧客户端
            oldPayClient.stopClient();
            log.info("更新支付客户端：{configId: {}, tradeType: {}}", payConfig.getId(), tradeType);
        } finally {
            LOCK.unlock();
        }
        return super.createPayClient(payConfig, tradeType);
    }
}
