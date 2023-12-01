package com.youlan.plugin.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlan.plugin.pay.entity.PayOrder;
import com.youlan.plugin.pay.entity.dto.PayOrderDTO;
import com.youlan.plugin.pay.entity.vo.PayOrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PayOrderMapper extends BaseMapper<PayOrder> {
    /**
     * 支付订单分页
     */
    IPage<PayOrderVO> getPayOrderPageList(@Param("page") Page<PayOrderVO> page, @Param("dto") PayOrderDTO dto);

}
