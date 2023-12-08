package com.youlan.plugin.pay.service;

import cn.hutool.core.lang.Assert;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.mapper.PayConfigMapper;
import org.springframework.stereotype.Service;

@Service
public class PayConfigService extends BaseServiceImpl<PayConfigMapper, PayConfig> {

    /**
     * 获取可用的支付配置
     */
    public PayConfig loadPayConfigEnabled(Long id) {
        PayConfig payConfig = this.loadPayConfigIfExists(id);
        Assert.equals(DBConstant.VAL_STATUS_ENABLED, payConfig.getStatus(), ApiResultCode.E0012::getException);
        return payConfig;
    }

    /**
     * 如果存在获取支付配置
     */
    public PayConfig loadPayConfigIfExists(Long id) {
        return this.loadOneOpt(id)
                .orElseThrow(() -> new BizRuntimeException(ApiResultCode.E0013));
    }

}
