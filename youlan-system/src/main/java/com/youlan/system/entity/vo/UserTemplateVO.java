package com.youlan.system.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.enums.DBStatus;
import com.youlan.common.excel.anno.ExcelEnumProperty;
import com.youlan.common.excel.converter.EnumConverter;
import com.youlan.system.excel.converter.DictConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@ColumnWidth(20)
@HeadFontStyle(fontHeightInPoints = 12)
@ExcelIgnoreUnannotated
public class UserTemplateVO {

    @ExcelProperty(value = "机构名称")
    @Schema(title = "机构名称")
    private String orgName;

    @ExcelProperty(value = "用户账号")
    @Schema(title = "用户账号")
    private String userName;

    @ExcelProperty(value = "用户手机")
    @Schema(title = "用户手机")
    private String userMobile;

    @ExcelProperty(value = "用户昵称")
    @Schema(title = "用户昵称")
    private String nickName;

    @ExcelProperty(value = "用户邮箱")
    @Schema(title = "用户邮箱")
    private String email;

    @ExcelProperty(value = "用户性别", converter = DictConverter.class)
    @Schema(title = "用户性别(字典类型[sys_user_sex])")
    private String sex;

    @ExcelProperty(value = "账号状态", converter = EnumConverter.class)
    @ExcelEnumProperty(value = DBStatus.class)
    @Schema(title = DBConstant.DESC_STATUS)
    private String status;

    @ExcelProperty(value = "备注")
    @Schema(title = DBConstant.DESC_REMARK)
    private String remark;
}
