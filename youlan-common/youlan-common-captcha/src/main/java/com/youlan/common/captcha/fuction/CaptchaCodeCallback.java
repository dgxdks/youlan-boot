package com.youlan.common.captcha.fuction;

@FunctionalInterface
public interface CaptchaCodeCallback {
    void send(String captchaCode);
}
