package com.youlan.common.core.helper;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.youlan.common.core.restful.enums.ApiResultCode;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public class MessageHelper {
    private static final MessageSource MESSAGE_SOURCE = SpringUtil.getBean(MessageSource.class);

    /**
     * 获取国际化消息
     */
    public static String message(String code, Object[] args, Locale local, String defaultValue) {
        String message = MESSAGE_SOURCE.getMessage(code, args, defaultValue, local);
        if (StrUtil.equals(message, code)) {
            return StrUtil.format(message, args);
        }
        return message;
    }

    /**
     * 获取国际化消息
     */
    public static String message(String code) {
        return message(code, new Object[]{});
    }

    /**
     * 获取国际化消息
     */
    public static String message(String code, Object[] args) {
        return message(code, args, code);
    }

    /**
     * 获取国际化消息
     */
    public static String message(String code, Object[] args, String defaultValue) {
        return message(code, args, LocaleContextHolder.getLocale(), defaultValue);
    }

    /**
     * 获取国际化消息
     */
    public static String message(ApiResultCode apiResultCode, Object[] args) {
        return message(apiResultCode.getErrorMsg(), args);
    }
}
