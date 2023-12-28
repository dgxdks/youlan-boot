package com.youlan.plugin.pay.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.plugin.pay.config.PayProperties;
import com.youlan.plugin.pay.entity.PayRecord;
import com.youlan.plugin.pay.enums.PayStatus;
import com.youlan.plugin.pay.mapper.PayRecordMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PayRecordService extends BaseServiceImpl<PayRecordMapper, PayRecord> {
    private final PayProperties payProperties;

    /**
     * 获取可以同步的支付记录列表
     *
     * @return 支付记录列表
     */
    public List<PayRecord> getPayRecordCanSync() {
        return this.lambdaQuery()
                .eq(PayRecord::getPayStatus, PayStatus.WAITING)
                .ge(PayRecord::getCreateTime, DateUtil.offsetMinute(new Date(), -payProperties.getRecordSyncInMinutes()))
                .list();
    }

    /**
     * 根据订单ID列表删除支付记录
     *
     * @param orderIds 订单ID列表
     */
    public void removePayRecordByOrderIds(List<Long> orderIds) {
        if (CollectionUtil.isEmpty(orderIds)) {
            return;
        }
        LambdaQueryWrapper<PayRecord> queryWrapper = Wrappers.<PayRecord>lambdaQuery()
                .in(PayRecord::getOrderId, orderIds);
        this.remove(queryWrapper);
    }

    /**
     * 根据支付记录ID和支付状态更新支付记录
     *
     * @param id        支付记录ID
     * @param payStatus 支付状态
     * @param payRecord 支付记录更新值
     * @return 是否更新成功
     */
    public boolean updateByIdAndPayStatus(Long id, PayStatus payStatus, PayRecord payRecord) {
        LambdaQueryWrapper<PayRecord> queryWrapper = Wrappers.<PayRecord>lambdaQuery()
                .eq(PayRecord::getId, id)
                .eq(PayRecord::getPayStatus, payStatus);
        return this.update(payRecord, queryWrapper);
    }

    /**
     * 根据外部交易订单号获取支付记录且不为空
     *
     * @param outTradeNo 外部交易订单号
     * @return 支付记录
     */
    public PayRecord loadPayRecordByOutTradeNoNotNull(String outTradeNo) {
        return this.loadOneOpt(PayRecord::getOutTradeNo, outTradeNo)
                .orElseThrow(ApiResultCode.E0015::getException);
    }

    /**
     * 获取支付记录且不为空
     *
     * @param id 支付记录ID
     * @return 支付记录
     */
    public PayRecord loadPayRecordNotNull(Serializable id) {
        return this.loadOneOpt(id)
                .orElseThrow(ApiResultCode.E0015::getException);
    }

    /**
     * 根据订单ID获取支付记录列表
     *
     * @param orderId 支付订单ID
     * @return 支付记录列表
     */
    public List<PayRecord> getPayRecordListByOrderId(Long orderId) {
        return this.lambdaQuery()
                .eq(PayRecord::getOrderId, orderId)
                .list();
    }

}
