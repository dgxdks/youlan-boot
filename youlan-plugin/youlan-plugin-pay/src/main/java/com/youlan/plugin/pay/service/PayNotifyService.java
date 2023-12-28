package com.youlan.plugin.pay.service;

import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.plugin.pay.entity.PayNotify;
import com.youlan.plugin.pay.enums.NotifyStatus;
import com.youlan.plugin.pay.mapper.PayNotifyMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PayNotifyService extends BaseServiceImpl<PayNotifyMapper, PayNotify> {

    /**
     * 获取支付回调且不为空
     *
     * @param id 支付回调ID
     * @return 支付回调
     */
    public PayNotify loadPayNotifyNotNull(Long id) {
        return this.loadOneOpt(id)
                .orElseThrow(() -> new BizRuntimeException("支付回调不存在"));
    }

    /**
     * 获取可以回调的支付回调列表
     *
     * @return 支付回调列表
     */
    public List<PayNotify> getPayNotifyListCanNotify() {
        return this.lambdaQuery()
                .in(PayNotify::getNotifyStatus, NotifyStatus.NOTIFY_WAITING, NotifyStatus.REQUEST_SUCCESS, NotifyStatus.REQUEST_FAILED)
                .le(PayNotify::getNextNotifyTime, new Date())
                .list();
    }

}
