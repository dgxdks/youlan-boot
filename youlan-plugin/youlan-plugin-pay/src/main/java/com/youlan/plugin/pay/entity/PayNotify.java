package com.youlan.plugin.pay.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.db.anno.Query;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.db.enums.QueryType;
import com.youlan.plugin.pay.enums.NotifyStatus;
import com.youlan.plugin.pay.enums.NotifyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@TableName("t_pay_notify")
@EqualsAndHashCode(callSuper = true)
public class PayNotify extends PageDTO {

    @Schema(description = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @Query
    @Schema(description = "回调类型(1-支付回调 2-退款回调)")
    private NotifyType notifyType;

    @Query
    @Schema(description = "回调状态(1-等待回调 2-回调成功 3-回调失败 4-请求成功 5-请求失败)")
    private NotifyStatus notifyStatus;

    @Query(type = QueryType.LIKE_RIGHT)
    @Schema(description = "订单ID(支付回调时-支付订单ID 退款回调时-退款订单ID)")
    private Long orderId;

    @Schema(description = "回调地址")
    private String notifyUrl;

    @Schema(description = "通知次数")
    private Integer notifyTimes;

    @Schema(description = "下次回调时间")
    private Date nextNotifyTime;

    @Schema(description = "最后回调时间")
    private Date lastNotifyTime;

    @Schema(description = DBConstant.DESC_CREATE_ID)
    @TableField(fill = FieldFill.INSERT)
    private Long createId;

    @Schema(description = DBConstant.DESC_CREATE_BY)
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @Schema(description = DBConstant.DESC_UPDATE_ID)
    @TableField(fill = FieldFill.UPDATE)
    private Long updateId;

    @Schema(description = DBConstant.DESC_UPDATE_BY)
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

    @Schema(description = DBConstant.DESC_CREATE_TIME)
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = DBConstant.DESC_UPDATE_TIME)
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @Query(type = QueryType.BETWEEN, column = "create_time")
    @Schema(description = DBConstant.DESC_CREATE_TIME)
    @TableField(exist = false)
    private List<Date> createTimeRange;

}
