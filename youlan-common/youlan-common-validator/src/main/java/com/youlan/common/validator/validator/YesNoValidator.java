package com.youlan.common.validator.validator;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.youlan.common.validator.anno.YesNo;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.youlan.common.core.db.constant.DBConstant.VAL_NO;
import static com.youlan.common.core.db.constant.DBConstant.VAL_YES;


public class YesNoValidator implements ConstraintValidator<YesNo, String> {
    private YesNo yesNo;

    @Override
    public void initialize(YesNo constraintAnnotation) {
        yesNo = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StrUtil.isBlank(value)) {
            return true;
        }
        String[] extend = yesNo.extend();
        String[] statusAll = ArrayUtil.append(extend, VAL_YES, VAL_NO);
        return ArrayUtil.contains(statusAll, value);
    }
}
