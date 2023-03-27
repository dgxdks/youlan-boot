package com.youlan.common.entity;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.common.enums.ResultEnum;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResultData<T> {
    public String code = ResultEnum.OK.getCode();
    public String msg = ResultEnum.OK.getMsg();
    public Object data;

    public ResultData() {
    }

    public ResultData(T data) {
        this.data = data;
    }

    public ResultData(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public static <V> ResultData<V> ok(V data) {
        return new ResultData<>(data);
    }

    public static ResultData<?> ok() {
        return new ResultData<>();
    }

    public static <V> ResultData<PageData<V>> okPage(IPage<V> iPage) {
        return new ResultData<>(new PageData<>(iPage));
    }

    public static ResultData<?> error() {
        return new ResultData<>(ResultEnum.ERROR.getCode(), ResultEnum.ERROR.getMsg());
    }

    public static ResultData<?> error(String msg) {
        return new ResultData<>(ResultEnum.ERROR.getCode(), msg);
    }

    public static ResultData<?> error(String code, String msg) {
        return new ResultData<>(code, msg);
    }
}
