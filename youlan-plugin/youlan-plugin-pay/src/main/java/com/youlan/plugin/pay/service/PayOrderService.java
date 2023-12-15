package com.youlan.plugin.pay.service;

import cn.hutool.core.collection.CollectionUtil;
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
import com.youlan.plugin.pay.entity.dto.PayOrderDTO;
import com.youlan.plugin.pay.entity.vo.PayOrderVO;
import com.youlan.plugin.pay.enums.PayStatus;
import com.youlan.plugin.pay.mapper.PayOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PayOrderService extends BaseServiceImpl<PayOrderMapper, PayOrder> {

    /**
     * 更新支付订单为已支付状态
     *
     * @param id             支付订单ID
     * @param updatePayOrder 支付订单更新值
     */
    public void updatePayOrderSuccess(Long id, PayOrder updatePayOrder) {
        PayOrder payOrder = this.loadPayOrderNotNull(id);
        PayStatus payStatus = payOrder.getPayStatus();
        switch (payStatus) {
            case SUCCESS:
                log.info("支付订单是已支付状态，无需更新：{id: {}}", id);
                return;
            case WAITING:
                break;
            default:
                throw new BizRuntimeException(ApiResultCode.E0009);
        }
        if (ObjectUtil.isNull(updatePayOrder)) {
            updatePayOrder = new PayOrder();
        }
        updatePayOrder.setPayStatus(PayStatus.SUCCESS);
        boolean update = this.updateByIdAndPayStatus(id, payOrder.getPayStatus(), updatePayOrder);
        if (!update) {
            throw new BizRuntimeException(ApiResultCode.E0009);
        }
        log.info("支付订单更新为已支付状态：{id: {}}", id);
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
    public IPage<PayOrderVO> getPayOrderPageList(PayOrderDTO dto) {
        List<String> sortColumns = List.of("create_time");
        Page<PayOrderVO> page = DBHelper.getPage(dto, sortColumns);
        return this.getBaseMapper().getPayOrderPageList(page, dto);
    }
}
