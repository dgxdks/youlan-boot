package com.youlan.common.validator.validator;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.youlan.common.validator.anno.Status;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.youlan.common.core.db.constant.DBConstant.VAL_STATUS_DISABLED;
import static com.youlan.common.core.db.constant.DBConstant.VAL_STATUS_ENABLED;


public class StatusValidator implements ConstraintValidator<Status, String> {
    private Status status;

    @Override
    public void initialize(Status constraintAnnotation) {
        status = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StrUtil.isBlank(value)) {
            return true;
        }
        String[] extend = status.extend();
        String[] statusAll = ArrayUtil.append(extend, VAL_STATUS_DISABLED, VAL_STATUS_ENABLED);
        return ArrayUtil.contains(statusAll, value);
    }
}
