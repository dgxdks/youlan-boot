package com.youlan.common.validator.validator;

import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import com.youlan.common.validator.anno.Phone;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<Phone, String> {
    private Phone.Type type;

    @Override
    public void initialize(Phone constraintAnnotation) {
        this.type = constraintAnnotation.type();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StrUtil.isBlank(value)) {
            return true;
        }
        switch (type) {
            case MOBILE:
                return PhoneUtil.isMobile(value);
            case MOBILE_HK:
                return PhoneUtil.isMobileHk(value);
            case MOBILE_TW:
                return PhoneUtil.isMobileTw(value);
            case MOBILE_MO:
                return PhoneUtil.isMobileMo(value);
            case TEL:
                return PhoneUtil.isTel(value);
            case TEL_400800:
                return PhoneUtil.isTel400800(value);
            case PHONE:
                return PhoneUtil.isPhone(value);
            default:
                return true;
        }
    }
}
