package com.youlan.plugin.pay.service.biz;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.youlan.common.validator.helper.ValidatorHelper;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.entity.PayOrder;
import com.youlan.plugin.pay.entity.dto.CreatePayOrderDTO;
import com.youlan.plugin.pay.entity.vo.CreatePayOrderVO;
import com.youlan.plugin.pay.service.PayConfigService;
import com.youlan.plugin.pay.service.PayOrderService;
import com.youlan.plugin.pay.utils.PayOrderUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PayBizService {
    private final PayConfigService payConfigService;
    private final PayOrderService payOrderService;

    /**
     * 创建支付订单
     *
     * @param dto 创建支付订单参数
     * @return 支付订单
     */
    public CreatePayOrderVO createPayOrder(CreatePayOrderDTO dto) {
        // 创建支付订单参数校验
        ValidatorHelper.validateWithThrow(dto);
        // 支付配置ID校验
        PayConfig payConfig = this.payConfigService.loadPayConfigIfExists(dto.getConfigId());
        // 订单存在则直接返回原订单信息
        PayOrder payOrder = this.payOrderService.loadPayOrderByMchOrderId(dto.getMchOrderId());
        if (ObjectUtil.isNotNull(payOrder)) {
            log.warn("支付订单已存在: {dto: {}, payOrder:{}}", JSONUtil.toJsonStr(dto), JSONUtil.toJsonStr(payOrder));
            return PayOrderUtil.convertToCreatePayOrderVO(payOrder);
        }
        // 创建支付订单
        payOrder = new PayOrder()
                .setConfigId(dto.getConfigId())
                .setTradeType(dto.getTradeType())
                .setPayAmount(dto.getPayAmount())
                .setExpireTime(dto.getExpireTime())
                .setClientIp(dto.getClientIp())
                .setMchOrderId(dto.getMchOrderId())
                .setSubject(dto.getSubject())
                .setDetail(dto.getDetail())
                .setNotifyUrl(payConfig.getPayNotifyUrl());
        this.payOrderService.save(payOrder);
        return PayOrderUtil.convertToCreatePayOrderVO(payOrder);
    }

}
