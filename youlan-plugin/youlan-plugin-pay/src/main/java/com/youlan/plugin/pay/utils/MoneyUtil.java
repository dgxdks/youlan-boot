package com.youlan.plugin.pay.utils;

import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;

public class MoneyUtil {

    /**
     * 元转分
     *
     * @param yuan 元
     * @return 分
     */
    public static BigDecimal yuanToFen(Number yuan) {
        return NumberUtil.mul(yuan, 100);
    }

    /**
     * 分转元
     *
     * @param fen 分
     * @return 元
     */
    public static BigDecimal fenToYuan(Number fen) {
        return NumberUtil.div(fen, 100, 2);
    }

}
