package com.youlan.plugin.pay.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.plugin.pay.entity.PayRecord;
import com.youlan.plugin.pay.enums.PayStatus;
import com.youlan.plugin.pay.mapper.PayRecordMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PayRecordService extends BaseServiceImpl<PayRecordMapper, PayRecord> {

    /**
     * 更新支付记录为已支付
     *
     * @param id              支付记录ID
     * @param updatePayRecord 支付记录更新值
     */
    public void updatePayStatusSuccess(Long id, PayRecord updatePayRecord) {
        PayRecord payRecord = this.loadPayRecordNotNull(id);
        PayStatus payStatus = payRecord.getPayStatus();
        switch (payStatus) {
            case SUCCESS:
                log.info("支付记录是已支付状态，无需更新：{id: {}}", id);
                return;
            case WAITING:
                break;
            default:
                throw new BizRuntimeException(ApiResultCode.E0016);
        }
        if (ObjectUtil.isNull(updatePayRecord)) {
            updatePayRecord = new PayRecord();
        }
        updatePayRecord.setPayStatus(PayStatus.SUCCESS);
        boolean update = this.updateByIdAndPayStatus(id, payRecord.getPayStatus(), updatePayRecord);
        if (!update) {
            throw new BizRuntimeException(ApiResultCode.E0016);
        }
        log.info("支付记录更新为已支付状态：{id: {}}", id);
    }

    /**
     * 更新支付记录为已关闭
     *
     * @param id              支付记录ID
     * @param updatePayRecord 支付记录更新值
     */
    public void updatePayStatusClosed(Long id, PayRecord updatePayRecord) {
        PayRecord payRecord = this.loadPayRecordNotNull(id);
        PayStatus payStatus = payRecord.getPayStatus();
        switch (payStatus) {
            case CLOSED:
                log.info("支付记录是已关闭状态，无需更新：{id: {}}", id);
                return;
            case SUCCESS:
                log.info("支付记录是已成功状态，无需更新：{id: {}}", id);
                return;
            case WAITING:
                break;
            default:
                throw new BizRuntimeException(ApiResultCode.E0016);
        }
        if (ObjectUtil.isNull(updatePayRecord)) {
            updatePayRecord = new PayRecord();
        }
        updatePayRecord.setPayStatus(PayStatus.CLOSED);
        boolean update = this.updateByIdAndPayStatus(id, payRecord.getPayStatus(), updatePayRecord);
        if (!update) {
            throw new BizRuntimeException(ApiResultCode.E0016);
        }
        log.info("支付记录更新为已关闭状态：{id: {}}", id);
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
                .orElseThrow(() -> new BizRuntimeException(ApiResultCode.E0015));
    }

    /**
     * 获取支付记录且不为空
     *
     * @param id 支付记录ID
     * @return 支付记录
     */
    public PayRecord loadPayRecordNotNull(Serializable id) {
        return this.loadOneOpt(id)
                .orElseThrow(() -> new BizRuntimeException(ApiResultCode.E0015));
    }

    /**
     * 根据订单ID获取支付记录
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
