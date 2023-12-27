package com.youlan.plugin.pay.convert;

import com.youlan.plugin.pay.entity.PayOrder;
import com.youlan.plugin.pay.entity.vo.CreatePayOrderVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PayOrderConvert {
    PayOrderConvert INSTANCE = Mappers.getMapper(PayOrderConvert.class);

    @Mapping(target = "orderId", source = "id")
    CreatePayOrderVO convertToCreatePayOrderVO(PayOrder payOrder);

}
