package com.youlan.plugin.pay.entity.request;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Data
@Accessors(chain = true)
public class PayRequest {

    @NotBlank(message = "外部订单号不能为空")
    @Schema(description = "外部交易订单号")
    private String outTradeNo;

    @Length(max = 32, message = "标题信息不能超过32个长度")
    @Schema(description = "标题信息")
    private String subject;

    @Length(max = 128, message = "详情信息不能超过128个长度")
    @Schema(description = "详情信息")
    private String detail;

    @NotBlank(message = "回调地址不能为空")
    @URL(message = "回调地址必须是URL格式")
    @Schema(description = "回调地址")
    private String notifyUrl;

    @URL(message = "返回地址必须是URL格式")
    @Schema(description = "返回地址")
    private String returnUrl;

    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0", inclusive = false, message = "支付金额必须大于0")
    @Schema(description = "支付金额")
    private BigDecimal amount;

    @NotNull(message = "过期时间不能为空")
    @Schema(description = "过期时间")
    private Date expireTime;

    @NotBlank(message = "客户端IP不能为空")
    @Schema(description = "客户端IP")
    private String clientIp;

    @Schema(description = "额外参数")
    private Map<String, String> extraParams;

    /**
     * 获取额外参数且不为空
     *
     * @param paramKey 参数键名
     * @return 参数
     */
    public String getExtraParamNotNull(String paramKey) {
        String extraParam = getExtraParam(paramKey);
        Assert.notBlank(extraParam, () -> new BizRuntimeException(StrUtil.format("extraParams中没有{}参数", paramKey)));
        return extraParam;
    }

    /**
     * 获取额外参数
     *
     * @param paramKey 参数键名
     * @return 参数
     */
    public String getExtraParam(String paramKey) {
        if (ObjectUtil.isNull(extraParams)) {
            return null;
        }
        return extraParams.get(paramKey);
    }

}
