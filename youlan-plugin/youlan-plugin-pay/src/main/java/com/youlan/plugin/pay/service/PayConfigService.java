package com.youlan.plugin.pay.service;

import cn.hutool.core.lang.Assert;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.mapper.PayConfigMapper;
import com.youlan.plugin.pay.params.PayParams;
import com.youlan.plugin.pay.utils.PayUtil;
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

    /**
     * 新增支付配置
     */
    public void addPayConfig(PayConfig payConfig) {
        beforeAddOrUpdatePayConfig(payConfig);
        this.save(payConfig);
    }

    /**
     * 修改支付配置
     */
    public void updatePayConfig(PayConfig payConfig) {
        beforeAddOrUpdatePayConfig(payConfig);
        this.updateById(payConfig);
    }

    /**
     * 修改微信支付配置状态
     */
    public void updatePayConfigStatus(Long id, String status) {
        updateStatus(id, status);
        this.updateStatus(id, status);
    }

    /**
     * 新增或修改微信配置前置操作
     */
    public void beforeAddOrUpdatePayConfig(PayConfig payConfig) {
        PayParams payParams = PayUtil.parsePayParams(payConfig);
        // 校验操作
        payParams.validate();
    }

}
