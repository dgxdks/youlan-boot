package com.youlan.plugin.pay.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.plugin.pay.entity.PayRefundOrder;
import com.youlan.plugin.pay.entity.dto.PayRefundOrderPageDTO;
import com.youlan.plugin.pay.entity.vo.PayRefundOrderVO;
import com.youlan.plugin.pay.enums.RefundStatus;
import com.youlan.plugin.pay.mapper.PayRefundOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PayRefundOrderService extends BaseServiceImpl<PayRefundOrderMapper, PayRefundOrder> {

    /**
     * 获取待退款订单列表
     *
     * @return 待退款订单列表
     */
    public List<PayRefundOrder> getRefundWaitingOrderList() {
        return this.lambdaQuery()
                .eq(PayRefundOrder::getRefundStatus, RefundStatus.WAITING)
                .list();
    }

    /**
     * 是否存在待退款订单
     *
     * @param orderId 支付订单ID
     * @return 是否存在
     */
    public boolean existsRefundWaitingByOrderId(Long orderId) {
        return this.lambdaQuery()
                .eq(PayRefundOrder::getOrderId, orderId)
                .eq(PayRefundOrder::getRefundStatus, RefundStatus.WAITING.getCode())
                .exists();
    }

    /**
     * 根据退款订单ID和退款状态更新退款订单
     *
     * @param id             退款订单ID
     * @param refundStatus   退款状态
     * @param payRefundOrder 退款订单
     * @return 是否更新成功
     */
    public boolean updateByIdAndRefundStatus(Long id, RefundStatus refundStatus, PayRefundOrder payRefundOrder) {
        LambdaQueryWrapper<PayRefundOrder> queryWrapper = Wrappers.<PayRefundOrder>lambdaQuery()
                .eq(PayRefundOrder::getId, id)
                .eq(PayRefundOrder::getRefundStatus, refundStatus);
        return this.update(payRefundOrder, queryWrapper);
    }

    /**
     * 获取退款订单分页
     *
     * @param dto 退款订单查询参数
     * @return 退款订单分页
     */
    public IPage<PayRefundOrderVO> getPayRefundOrderPageList(PayRefundOrderPageDTO dto) {
        List<String> sortColumns = List.of("create_time");
        Page<PayRefundOrderVO> page = DBHelper.getPage(dto, sortColumns);
        return this.getBaseMapper().getPayRefundOrderPageList(page, dto);
    }

    /**
     * 获取退款订单且不为空
     *
     * @param id 退款订单ID
     * @return 退款订单
     */
    public PayRefundOrder loadRefundOrderNotNull(Long id) {
        return this.loadOneOpt(id)
                .orElseThrow(ApiResultCode.E0029::getException);
    }

    /**
     * 根据商户退款号获取退款订单
     *
     * @param mchRefundId 商户退款号
     * @return 退款订单
     */
    public PayRefundOrder loadRefundOrderByMchRefundIdNotNull(String mchRefundId) {
        return this.loadOneOpt(PayRefundOrder::getMchRefundId, mchRefundId)
                .orElseThrow(ApiResultCode.E0029::getException);
    }

    /**
     * 根据外部退款订单号获取退款订单
     *
     * @param outRefundNo 外部退款订单号
     * @return 退款订单
     */
    public PayRefundOrder loadRefundOrderByOuRefundNoNotNull(String outRefundNo) {
        return this.loadOneOpt(PayRefundOrder::getOutRefundNo, outRefundNo)
                .orElseThrow(ApiResultCode.E0029::getException);
    }

    /**
     * 根据商户退款号判断退款订单是否存在
     *
     * @param mchRefundId 商户退款号
     * @return 是否存在
     */
    public boolean existsByMchRefundId(String mchRefundId) {
        return this.lambdaQuery()
                .eq(PayRefundOrder::getMchRefundId, mchRefundId)
                .exists();
    }

}
