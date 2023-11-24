package com.youlan.plugin.pay.utils;

import com.youlan.plugin.pay.entity.PayOrder;
import com.youlan.plugin.pay.entity.vo.CreatePayOrderVO;

public class PayOrderUtil {

    /**
     * 转换为创建支付订单VO
     *
     * @param payOrder 支付订单
     * @return 创建支付订单VO
     */
    public static CreatePayOrderVO convertToCreatePayOrderVO(PayOrder payOrder) {
        return new CreatePayOrderVO()
                .setOrderId(payOrder.getId());
    }
}
