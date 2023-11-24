package com.youlan.plugin.pay.service;

import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.mapper.PayConfigMapper;
import com.youlan.plugin.pay.params.PayParams;
import com.youlan.plugin.pay.utils.PayConfigUtil;
import org.springframework.stereotype.Service;

@Service
public class PayConfigService extends BaseServiceImpl<PayConfigMapper, PayConfig> {

    /**
     * 如果存在返回支付配置
     */
    public PayConfig loadPayConfigIfExists(Long id) {
        return this.loadOneOpt(id)
                .orElseThrow(() -> new BizRuntimeException("支付配置不存在"));
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
     * 刷新微信支付配置缓存
     */
    public void refreshPayConfigCache() {

    }

    /**
     * 新增或修改微信配置前置操作
     */
    public void beforeAddOrUpdatePayConfig(PayConfig payConfig) {
        PayParams payParams = PayConfigUtil.getPayParams(payConfig.getParams(), payConfig.getType());
        // 校验操作
        payParams.validate();
    }
}
