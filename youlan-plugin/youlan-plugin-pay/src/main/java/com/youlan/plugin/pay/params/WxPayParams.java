package com.youlan.plugin.pay.params;

import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.validator.helper.ValidatorHelper;
import com.youlan.plugin.pay.enums.WxApiVersion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode
public class WxPayParams implements PayParams {

    @Schema(description = "应用ID")
    private String appId;

    @NotBlank(message = "商户ID不能为空", groups = {V2.class, V3.class})
    @Schema(description = "商户ID")
    private String mchId;

    @NotNull(message = "API版本不能为空", groups = {V2.class, V3.class})
    @Schema(description = "API版本")
    private WxApiVersion apiVersion;

    @NotBlank(message = "商户密钥不能为空", groups = {V2.class})
    @Schema(description = "商户密钥")
    private String mchKey;

    @NotBlank(message = "apiclient_cert.p12证书不能为空", groups = {V2.class})
    @Schema(description = "apiclient_cert.p12证书内容")
    private String keyContent;

    @NotBlank(message = "apiclient_key.pem证书不能为空", groups = {V3.class})
    @Schema(name = "apiclient_key.pem证书")
    private String privateKeyContent;

    @NotBlank(message = "apiclient_cert.pem证书不能为空", groups = {V3.class})
    @Schema(name = "apiclient_cert.pem证书")
    private String privateCertContent;

    @Schema(description = "子应用ID")
    private String subAppId;

    @Schema(description = "子商户号")
    private String subMchId;

    @Override
    public void validate() {
        switch (apiVersion) {
            case V2:
                ValidatorHelper.validateWithThrow(this, V2.class);
                break;
            case V3:
                ValidatorHelper.validateWithThrow(this, V3.class);
                break;
            default:
                throw new BizRuntimeException("不支持的API版本");
        }
    }

    public interface V2 {
    }

    public interface V3 {
    }
}
