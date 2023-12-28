package com.youlan.plugin.pay.entity.response;

import com.youlan.plugin.pay.enums.ResponseSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class BaseResponse {

    @Schema(description = "响应类型")
    private ResponseSource responseSource;

    @Schema(description = "原始数据")
    private Object rawData;

    @Schema(description = "错误码")
    private String errorCode;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "成功时间")
    private Date successTime;

    @Schema(description = "客户端ID")
    private String clientId;
}
