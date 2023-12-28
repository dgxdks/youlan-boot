package com.youlan.plugin.pay.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.plugin.pay.entity.PayOrder;
import com.youlan.plugin.pay.entity.dto.PayOrderPageDTO;
import com.youlan.plugin.pay.entity.vo.PayOrderVO;
import com.youlan.plugin.pay.enums.PayStatus;
import com.youlan.plugin.pay.mapper.PayOrderMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PayOrderService extends BaseServiceImpl<PayOrderMapper, PayOrder> {

    /**
     * 获取可以过期的支付订单
     *
     * @return 支付订单列表
     */
    public List<PayOrder> getPayOrderListCanExpire() {
        return this.lambdaQuery()
                .eq(PayOrder::getPayStatus, PayStatus.WAITING)
                .le(PayOrder::getExpireTime, new Date())
                .list();
    }

    /**
     * 更新支付订单退款金额
     *
     * @param id           支付订单ID
     * @param refundAmount 退款金额
     */
    public void updatePayOrderRefundAmount(Long id, BigDecimal refundAmount) {
        PayOrder payOrder = loadPayOrderCanRefund(id, refundAmount);
        // 获取已退款金额
        BigDecimal oldRefundAmount = payOrder.getRefundAmount();
        boolean update = this.lambdaUpdate()
                .eq(PayOrder::getId, id)
                .eq(PayOrder::getRefundAmount, oldRefundAmount)
                .set(PayOrder::getRefundAmount, oldRefundAmount.add(refundAmount))
                .update();
        if (!update) {
            throw new BizRuntimeException("退款金额更新失败");
        }
    }

    /**
     * 根据商户订单号获取可以退款的支付订单
     *
     * @param mchOrderId   商户订单号
     * @param refundAmount 退款金额
     * @return 支付订单
     */
    public PayOrder loadPayOrderCanRefundByMchOrderId(String mchOrderId, BigDecimal refundAmount) {
        PayOrder payOrder = loadPayOrderByMchOrderId(mchOrderId);
        validatePayOrderCanRefund(payOrder, refundAmount);
        return payOrder;
    }

    /**
     * 获取可以退款的支付订单
     *
     * @param id           支付订单ID
     * @param refundAmount 退款金额
     * @return 支付订单
     */
    public PayOrder loadPayOrderCanRefund(Long id, BigDecimal refundAmount) {
        PayOrder payOrder = this.loadPayOrderNotNull(id);
        validatePayOrderCanRefund(payOrder, refundAmount);
        return payOrder;
    }

    /**
     * 校验支付订单是否可退款
     *
     * @param payOrder     支付订单
     * @param refundAmount 退款金额
     */
    public void validatePayOrderCanRefund(PayOrder payOrder, BigDecimal refundAmount) {
        Assert.notNull(payOrder, ApiResultCode.E0007::getException);
        Assert.notNull(refundAmount, "退款金额不能为空");
        Assert.isTrue(refundAmount.compareTo(new BigDecimal(0)) > 0, "退款金额必须大于0");
        PayStatus payStatus = payOrder.getPayStatus();
        // 非已支付或已退款状态不允许退款
        if (!payStatus.isSuccess() && !payStatus.isRefund()) {
            throw new BizRuntimeException(ApiResultCode.E0025);
        }
        // 获取支付金额
        BigDecimal payAmount = payOrder.getPayAmount();
        // 获取已退款金额
        BigDecimal oldRefundAmount = payOrder.getRefundAmount();
        // 要退款的金额+已退款的金额不能大于支付金额
        if (refundAmount.add(oldRefundAmount).compareTo(payAmount) > 0) {
            throw new BizRuntimeException(ApiResultCode.E0026);
        }
    }

    /**
     * 根据支付订单ID和支付状态更新支付订单
     *
     * @param id        支付订单ID
     * @param payStatus 支付状态
     * @param payOrder  支付订单更新值
     * @return 是否更新成功
     */
    public boolean updateByIdAndPayStatus(Long id, PayStatus payStatus, PayOrder payOrder) {
        LambdaQueryWrapper<PayOrder> queryWrapper = Wrappers.<PayOrder>lambdaQuery()
                .eq(PayOrder::getId, id)
                .eq(PayOrder::getPayStatus, payStatus);
        return this.update(payOrder, queryWrapper);
    }

    /**
     * 根据商户订单号获取支付订单
     *
     * @param mchOrderId 商户订单号
     * @return 支付订单
     */
    public PayOrder loadPayOrderByMchOrderId(String mchOrderId) {
        List<PayOrder> payOrderList = this.lambdaQuery()
                .eq(PayOrder::getMchOrderId, mchOrderId)
                .list();
        return CollectionUtil.getFirst(payOrderList);
    }

    /**
     * 根据商户订单号获取支付订单且不为空
     *
     * @param mchOrderId 商户订单号
     * @return 支付订单
     */
    public PayOrder loadPayOrderByMchOrderNotNull(String mchOrderId) {
        PayOrder payOrder = loadPayOrderByMchOrderId(mchOrderId);
        Assert.notNull(payOrder, ApiResultCode.E0007::getException);
        return payOrder;
    }

    /**
     * 获取支付订单且不为空
     *
     * @param id 订单ID
     * @return 支付订单
     */
    public PayOrder loadPayOrderNotNull(Long id) {
        PayOrder payOrder = this.getById(id);
        if (ObjectUtil.isNull(payOrder)) {
            throw new BizRuntimeException(ApiResultCode.E0007);
        }
        return payOrder;
    }

    /**
     * 获取支付订单分页
     *
     * @param dto 支付订单查询参数
     * @return 支付订单分页
     */
    public IPage<PayOrderVO> getPayOrderPageList(PayOrderPageDTO dto) {
        List<String> sortColumns = List.of("create_time");
        Page<PayOrderVO> page = DBHelper.getPage(dto, sortColumns);
        return this.getBaseMapper().getPayOrderPageList(page, dto);
    }
}
