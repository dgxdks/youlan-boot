package com.youlan.plugin.pay.service.biz;

import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.params.PayParams;
import com.youlan.plugin.pay.service.PayChannelConfigService;
import com.youlan.plugin.pay.service.PayConfigService;
import com.youlan.plugin.pay.utils.PayUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PayConfigBizService {
    private final PayConfigService payConfigService;
    private final PayChannelConfigService payChannelConfigService;

    /**
     * 删除支付配置
     */
    @Transactional(rollbackFor = Exception.class)
    public void removePayConfig(List<Long> ids) {
        // 删除支付配置
        this.payConfigService.removeBatchByIds(ids);
        // 删除支付通道配置
        this.payChannelConfigService.removeByConfigIds(ids);
    }

    /**
     * 新增支付配置
     */
    public void addPayConfig(PayConfig payConfig) {
        beforeAddOrUpdatePayConfig(payConfig);
        this.payConfigService.save(payConfig);
    }

    /**
     * 修改支付配置
     */
    public void updatePayConfig(PayConfig payConfig) {
        beforeAddOrUpdatePayConfig(payConfig);
        this.payConfigService.updateById(payConfig);
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
