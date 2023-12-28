package com.youlan.plugin.pay.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.redis.helper.RedisHelper;
import com.youlan.plugin.pay.config.PayProperties;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.enums.PayType;
import com.youlan.plugin.pay.enums.TradeType;
import com.youlan.plugin.pay.params.PayParams;

import java.time.Duration;
import java.util.Date;

import static com.youlan.plugin.pay.constant.PayConstant.*;

public class PayUtil {
    private static final PayProperties payProperties = SpringUtil.getBean(PayProperties.class);

    /**
     * 解析支付参数
     *
     * @param payConfig 支付配置
     * @return 支付参数
     */
    public static PayParams parsePayParams(PayConfig payConfig) {
        Assert.notNull(payConfig, "支付配置不能为空");
        return parsePayParams(payConfig.getParams(), payConfig.getType());
    }

    /**
     * 解析支付参数
     *
     * @param params  支付参数
     * @param payType 支付类型
     * @return 支付参数
     */
    public static PayParams parsePayParams(String params, PayType payType) {
        // 支付类型必须指定
        Assert.notNull(payType, "支付类型不能为空");
        // 配置参数必须是JSON格式
        Assert.isTrue(JSONUtil.isTypeJSON(params), () -> new BizRuntimeException("配置参数必须是JSON格式"));
        // 配置参数必须通过校验
        Class<? extends PayParams> paramsClass = payType.getParamsClass();
        return JSONUtil.toBean(params, paramsClass);
    }

    /**
     * 生成统一支付回调地址
     *
     * @param configId  支付配置ID
     * @param tradeType 交易类型
     * @return 支付回调地址
     */
    public static String generateUnifiedPayNotifyUrl(Long configId, TradeType tradeType) {
        return StrUtil.removeSuffix(payProperties.getPayNotifyUrl(), StrPool.SLASH) +
                StrPool.SLASH + configId + StrPool.SLASH + tradeType.getCode();
    }

    /**
     * 生成统一退款回调地址
     *
     * @param configId  支付配置ID
     * @param tradeType 交易类型
     * @return 退款回调地址
     */
    public static String generateUnifiedRefundNotifyUrl(Long configId, TradeType tradeType) {
        return StrUtil.removeSuffix(payProperties.getRefundNotifyUrl(), StrPool.SLASH) +
                StrPool.SLASH + configId + StrPool.SLASH + tradeType.getCode();
    }


    /**
     * 生成支付订单号
     *
     * @return 支付订单号
     */
    public static String generatePayOrderNo() {
        return generateOrderNo(payProperties.getPayOrderPrefix());
    }

    /**
     * 生成退款订单号
     *
     * @return 退款订单号
     */
    public static String generateRefundOrderNo() {
        return generateOrderNo(payProperties.getRefundOrderPrefix());
    }

    /**
     * 生成订单号
     *
     * @param prefix 前缀
     * @return 订单号
     */
    public static String generateOrderNo(String prefix) {
        if (ObjectUtil.isNull(prefix)) {
            prefix = StrUtil.EMPTY;
        }
        String orderNo = prefix + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN);
        String orderNoRedisKey = getOrderNoRedisKey(orderNo);
        long count = RedisHelper.incrementAtomicLong(orderNoRedisKey);
        RedisHelper.expireCacheObject(orderNoRedisKey, Duration.ofSeconds(1));
        return orderNo + count;
    }

    /**
     * 获取订单号redis key
     *
     * @param orderNo 订单号
     * @return 订单号redis key
     */
    public static String getOrderNoRedisKey(String orderNo) {
        return REDIS_PREFIX_ORDER_NO + orderNo;
    }

    /**
     * 获取支付回调redis key
     *
     * @param notifyId 通知ID
     * @return 支付回调redis key
     */
    public static String getPayNotifyRedisKey(Long notifyId) {
        return REDIS_PREFIX_PAY_NOTIFY + notifyId;
    }

    /**
     * 获取退款订单同步redis key
     *
     * @param orderId 订单ID
     * @return 退款订单同步redis key
     */
    public static String getRefundOrderSyncRedisKey(Long orderId) {
        return REDIS_PREFIX_REFUND_ORDER_SYNC + orderId;
    }
}
