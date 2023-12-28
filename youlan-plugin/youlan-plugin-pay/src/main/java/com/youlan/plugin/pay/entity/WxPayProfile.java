package com.youlan.plugin.pay.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.plugin.pay.config.PayProfile;
import com.youlan.plugin.pay.enums.WxPayApiVersion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@TableName("t_pay_wx_profile")
@EqualsAndHashCode(callSuper = true)
public class WxPayProfile extends PageDTO implements PayProfile {

    @Schema(description = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "配置名称不能为空", groups = {V2.class, V3.class})
    @Schema(description = "配置名称")
    private String name;

    @NotBlank(message = "应用ID不能为空", groups = {V2.class, V3.class})
    @Schema(description = "应用ID")
    private String appId;

    @NotBlank(message = "商户ID不能为空", groups = {V2.class, V3.class})
    @Schema(description = "商户ID")
    private String mchId;

    @NotBlank(message = "API版本不能为空", groups = {V2.class, V3.class})
    @Schema(description = "API版本")
    private WxPayApiVersion apiVersion;

    @NotBlank(message = "商户密钥不能为空", groups = {V2.class})
    @Schema(description = "商户密钥")
    private String mchKey;

    @NotBlank(message = "证书内容不能为空", groups = {V2.class})
    @Schema(description = "apiclient_cert.p12证书内容")
    private String keyContent;

    @Schema(name = "apiclient_key.pem证书内容")
    private String privateKeyContent;

    @Schema(name = "apiclient_cert.pem证书内容")
    private String privateCertContent;

    @NotBlank()
    @Schema(description = "子应用ID")
    private String subAppId;

    @Schema(description = "子商户号")
    private String subMchId;

    @Schema(description = "支付回调地址")
    private String payNotifyUrl;

    @Schema(description = "退款回调地址")
    private String refundNotifyUrl;

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

    interface V2 {
    }

    interface V3 {
    }
}
