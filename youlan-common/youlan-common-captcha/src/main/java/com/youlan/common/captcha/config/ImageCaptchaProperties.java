package com.youlan.common.captcha.config;

import com.youlan.common.captcha.enums.CodeType;
import com.youlan.common.captcha.enums.ImageType;
import lombok.Data;

import java.awt.*;

@Data
public class ImageCaptchaProperties {
    /**
     * 验证码超时时间(s)
     */
    private int codeTimeout = 60;

    /**
     * 验证码图片类型
     */
    private ImageType imageType = ImageType.LINE;

    /**
     * 验证码码类型
     */
    private CodeType codeType = CodeType.MATH;

    /**
     * 验证码图片宽度(仅图片验证码有效)
     */
    private int width = 160;

    /**
     * 验证码图片高度(仅图片验证码有效)
     */
    private int height = 60;

    /**
     * 验证码图片背景颜色
     */
    private Color color = Color.PINK;

    /**
     * 验证码字体
     */
    private Font font = new Font("Arial", Font.BOLD, 58);

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
    private int circleNumber = 10;
}
