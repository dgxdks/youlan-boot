package com.youlan.plugin.pay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.plugin.pay.enums.NotifyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("t_pay_notify_record")
public class PayNotifyRecord {

    @Schema(description = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "回调通知ID")
    private Long notifyId;

    @Schema(description = "重试次数")
    private Integer notifyTimes;

    @Schema(description = "请求体")
    private String requestBody;

    @Schema(description = "响应体")
    private String responseBody;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "回调状态(1-等待回调 2-回调成功 3-回调失败 4-请求成功 5-请求失败)")
    private NotifyStatus notifyStatus;

}
