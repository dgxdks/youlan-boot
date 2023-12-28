package com.youlan.common.core.restful;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.youlan.common.core.restful.enums.ApiResultCode;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ApiResult {
    private String status = ApiResultCode.OK.getStatus();
    private String errorMsg = ApiResultCode.OK.getErrorMsg();
    private Object data;

    public ApiResult() {

    }

    public ApiResult(Object data) {
        this.data = data;
    }

    public static ApiResult ok(Object data) {
        return new ApiResult(data);
    }

    public static ApiResult ok(Object data, String status, String errorMsg) {
        return new ApiResult(data)
                .setStatus(status)
                .setErrorMsg(errorMsg);
    }

    public static ApiResult ok(Object data, ApiResultCode apiResultCode) {
        return new ApiResult(data)
                .setStatus(apiResultCode.getStatus())
                .setErrorMsg(apiResultCode.getErrorMsg());
    }

    public static ApiResult ok(Object data, String status) {
        return new ApiResult(data)
                .setStatus(status);
    }

    public static ApiResult error(ApiResultCode codeEnum) {
        return new ApiResult()
                .setStatus(codeEnum.getStatus())
                .setErrorMsg(codeEnum.getErrorMsg());
    }

    public static ApiResult error(String status, String errorMsg) {
        return new ApiResult()
                .setStatus(status)
                .setErrorMsg(errorMsg);
    }

    public static ApiResult error(String status) {
        return new ApiResult()
                .setStatus(status)
                .setErrorMsg(ApiResultCode.ERROR.getErrorMsg());
    }

    public static ApiResult error() {
        return new ApiResult()
                .setStatus(ApiResultCode.ERROR.getStatus())
                .setErrorMsg(ApiResultCode.ERROR.getErrorMsg());
    }

    @Hidden
    public boolean isOk() {
        return ObjectUtil.equal(ApiResultCode.OK.getStatus(), getStatus());
    }

    public String toString() {
        return JSONUtil.toJsonStr(this);
    }

}
