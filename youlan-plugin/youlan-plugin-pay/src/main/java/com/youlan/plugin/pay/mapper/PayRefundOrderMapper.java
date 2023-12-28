package com.youlan.plugin.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlan.plugin.pay.entity.PayRefundOrder;
import com.youlan.plugin.pay.entity.dto.PayRefundOrderPageDTO;
import com.youlan.plugin.pay.entity.vo.PayRefundOrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PayRefundOrderMapper extends BaseMapper<PayRefundOrder> {

    /**
     * 退款订单分页
     */
    IPage<PayRefundOrderVO> getPayRefundOrderPageList(@Param("page") Page<PayRefundOrderVO> page, @Param("dto") PayRefundOrderPageDTO dto);

}
