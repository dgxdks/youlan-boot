package com.youlan.plugin.sms.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.plugin.sms.constant.SmsConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.sms4j.api.entity.SmsResponse;

import java.util.Date;

import static com.youlan.common.db.constant.DBConstant.DESC_ID;

@Data
@ColumnWidth(20)
@HeadFontStyle(fontHeightInPoints = 12)
@ExcelIgnoreUnannotated
@TableName("t_sms_record")
@EqualsAndHashCode(callSuper = true)
public class SmsRecord extends PageDTO {

    @Schema(description = DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "配置标识")
    private String configId;

    @Schema(description = "模版ID")
    private String templateId;

    @Schema(description = "短信类型(1-标准短信 2-异步短信 3-延迟短信)")
    private String smsType;

    @Schema(description = "发送类型(1-单个发送 2-批量发送)")
    private String sendType;

    @Schema(description = "发送状态(1-成功 2-失败)")
    private String sendStatus;

    @Schema(description = "发送批次")
    private String sendBatch;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "消息内容")
    private String message;

    @Schema(description = "响应数据")
    private String responseData;

    @Schema(description = "延迟时间(ms)")
    private Long delayedTime;

    @Schema(description = "发送时间")
    private Date sendTime;

    @Schema(description = "响应时间")
    private Date responseTime;
}
