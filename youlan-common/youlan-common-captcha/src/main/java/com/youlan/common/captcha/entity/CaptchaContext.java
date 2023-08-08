package com.youlan.common.captcha.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 复杂配置时使用验证码配置上下文进行全局配置
 */
@Data
@Accessors(chain = true)
public class CaptchaContext {

    /**
     * 验证码图片宽度(仅图片验证码有效)
     */
    private int width = 200;

    /**
     * 验证码图片高度(仅图片验证码有效)
     */
    private int height = 100;

    /**
     * 验证码内容长度
     */
    private int codeLength = 5;

    /**
     * 验证码干扰线条数(仅生成干扰线验证码时生效)
     */
    private int lineNumber = 150;

    /**
     * 验证码干扰扭曲线条数
     */
    private int shearNumber = 5;

    /**
     * 验证码干扰圆圈数(仅生成干扰圆圈验证码时生效)
     */
    private int circleNumber = 5;

    /**
     * 四则运算验证码参与计算的数字位数(仅四则运算验证码时生效)
     */
    private int mathNumberLength = 1;

    /**
     * 验证码超时时间(s)
     */
    private long codeTimeout = 60;

    /**
     * 验证码超时时间内最大生成个数(仅对验证码限流时生效,不设置时不做限流限制)
     */
    private Integer codeLimit;

    /**
     * 验证码来源ID(仅当生成验证码时对来源方进行限流时生效,例如通过用户手机号限制单个手机号验证码最大生成次数)
     */
    private String sourceId;

    /**
     * 生成的验证码
     */
    private String captchaCode;
}
