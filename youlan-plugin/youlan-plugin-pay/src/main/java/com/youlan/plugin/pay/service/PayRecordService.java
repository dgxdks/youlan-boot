package com.youlan.plugin.pay.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.entity.PayOrder;
import com.youlan.plugin.pay.entity.PayRecord;
import com.youlan.plugin.pay.entity.dto.SubmitPayOrderDTO;
import com.youlan.plugin.pay.enums.PayStatus;
import com.youlan.plugin.pay.mapper.PayRecordMapper;
import com.youlan.plugin.pay.utils.PayUtil;
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
        PayRecord payRecord = this.loadPayRecordIfExists(id);
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
    }

    /**
     * 更新支付记录为已关闭
     *
     * @param id              支付记录ID
     * @param updatePayRecord 支付记录更新值
     */
    public void updatePayStatusClosed(Long id, PayRecord updatePayRecord) {
        PayRecord payRecord = this.loadPayRecordIfExists(id);
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
    }

    /**
     * 创建支付记录
     *
     * @param submitPayOrderDTO 提交支付订单参数
     * @param payOrder          支付订单
     * @param payConfig         支付配置
     * @return 支付记录
     */
    public PayRecord createPayRecord(SubmitPayOrderDTO submitPayOrderDTO, PayOrder payOrder, PayConfig payConfig) {
        String outTradeNo = PayUtil.generatePayOrderNo();
        PayRecord payRecord = new PayRecord()
                .setOutTradeNo(outTradeNo)
                .setOrderId(payOrder.getId())
                .setConfigId(payConfig.getId())
                .setTradeType(submitPayOrderDTO.getTradeType())
                .setClientIp(submitPayOrderDTO.getClientIp())
                .setPayStatus(PayStatus.WAITING)
                .setExtraParams(submitPayOrderDTO.getExtraParams());
        this.save(payRecord);
        return payRecord;
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
     * 如果存在获取支付记录
     *
     * @param id 支付记录ID
     * @return 支付记录
     */
    public PayRecord loadPayRecordIfExists(Serializable id) {
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
