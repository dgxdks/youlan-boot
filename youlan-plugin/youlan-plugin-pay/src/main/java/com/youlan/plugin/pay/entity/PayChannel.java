package com.youlan.plugin.pay.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.db.anno.Query;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.db.enums.QueryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Data
@TableName("t_pay_channel")
@EqualsAndHashCode(callSuper = true)
public class PayChannel extends PageDTO {

    @Schema(description = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "通道名称不能为空")
    @Query(type = QueryType.LIKE)
    @NotBlank(message = "通道名称不能为空")
    @Schema(description = "通道名称")
    private String name;

    @URL(message = "支付回调地址必须是合法URL")
    @NotBlank(message = "支付回调地址不能为空")
    @Schema(description = "支付回调地址")
    private String payNotifyUrl;

    @URL(message = "退款回调地址必须是合法URL")
    @NotBlank(message = "退款回调地址不能为空")
    @Schema(description = "退款回调地址")
    private String refundNotifyUrl;

    @Query
    @Schema(description = DBConstant.DESC_STATUS)
    @TableField(fill = FieldFill.INSERT)
    private String status;

    @Schema(description = DBConstant.DESC_REMARK)
    private String remark;

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

    @Schema(description = "支付通道配置列表")
    @TableField(exist = false)
    private List<PayChannelConfig> payChannelConfigs;

}
