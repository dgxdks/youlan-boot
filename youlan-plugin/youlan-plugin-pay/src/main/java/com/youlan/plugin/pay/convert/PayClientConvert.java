package com.youlan.plugin.pay.convert;

import com.youlan.plugin.pay.entity.PayChannel;
import com.youlan.plugin.pay.entity.PayOrder;
import com.youlan.plugin.pay.entity.PayRecord;
import com.youlan.plugin.pay.entity.dto.SubmitPayOrderDTO;
import com.youlan.plugin.pay.entity.request.PayRequest;
import com.youlan.plugin.pay.utils.PayUtil;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PayClientConvert {
    PayClientConvert INSTANCE = Mappers.getMapper(PayClientConvert.class);

    /**
     *
     * 转换为支付请求
     */
    default PayRequest convertPayRequest(SubmitPayOrderDTO dto, PayOrder payOrder, PayRecord payRecord, PayChannel payChannel) {
        return new PayRequest()
                .setOutTradeNo(payRecord.getOutTradeNo())
                .setSubject(payOrder.getSubject())
                .setDetail(payOrder.getDetail())
                .setNotifyUrl(PayUtil.generateUnifiedPayNotifyUrl(payChannel.getId(), payRecord.getTradeType()))
                .setReturnUrl(dto.getReturnUrl())
                .setAmount(payOrder.getPayAmount())
                .setExpireTime(payOrder.getExpireTime())
                .setClientIp(dto.getClientIp())
                .setExtraParams(dto.getExtraParams());
    }

}
