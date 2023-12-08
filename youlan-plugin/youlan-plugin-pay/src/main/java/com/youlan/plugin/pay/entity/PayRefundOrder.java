package com.youlan.plugin.pay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlan.common.db.constant.DBConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("t_pay_refund_order")
public class PayRefundOrder {

    @Schema(description = DBConstant.DESC_ID)
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

}
