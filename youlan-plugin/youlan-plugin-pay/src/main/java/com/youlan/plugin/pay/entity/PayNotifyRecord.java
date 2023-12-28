package com.youlan.plugin.pay.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.db.anno.Query;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.db.enums.QueryType;
import com.youlan.plugin.pay.enums.NotifyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@TableName("t_pay_notify_record")
@EqualsAndHashCode(callSuper = true)
public class PayNotifyRecord extends PageDTO {

    @Schema(description = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @Query
    @Schema(description = "回调通知ID")
    private Long notifyId;

    @Schema(description = "回调次数")
    private Integer notifyTimes;

    @Schema(description = "请求体")
    private String requestBody;

    @Schema(description = "响应体")
    private String responseBody;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Query
    @Schema(description = "回调状态(1-等待回调 2-回调成功 3-回调失败 4-请求成功 5-请求失败)")
    private NotifyStatus notifyStatus;

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
