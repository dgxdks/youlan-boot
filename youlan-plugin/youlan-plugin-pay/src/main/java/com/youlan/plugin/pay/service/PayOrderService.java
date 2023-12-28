package com.youlan.plugin.pay.service;

import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.plugin.pay.entity.PayOrder;
import com.youlan.plugin.pay.mapper.PayOrderMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public class PayOrderService extends BaseServiceImpl<PayOrderMapper, PayOrder> {
}
