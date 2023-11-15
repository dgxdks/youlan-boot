package com.youlan.system.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.youlan.common.db.enums.DBStatus;
import com.youlan.common.excel.anno.ExcelEnumProperty;
import com.youlan.common.excel.converter.EnumConverter;
import com.youlan.common.validator.anno.Phone;
import com.youlan.system.anno.ExcelDictProperty;
import com.youlan.system.converter.DictConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ColumnWidth(20)
@HeadFontStyle(fontHeightInPoints = 12)
@ExcelIgnoreUnannotated
public class UserTemplateVO {

    @NotNull(message = "机构编码必须填写")
    @ExcelProperty(value = "机构编码")
    private Long orgId;

    @NotBlank(message = "用户账号必须填写")
    @Size(min = 1, max = 30, message = "用户账号长度不能超过{max}个字符")
    @ExcelProperty(value = "用户账号")
    private String userName;

    @Phone(message = "用户手机格式不正确")
    @ExcelProperty(value = "用户手机")
    @Schema(description = "用户手机")
    private String userMobile;

    @NotBlank(message = "用户昵称必须填写")
    @Size(min = 1, max = 30, message = "用户昵称长度不能超过{max}个字符")
    @ExcelProperty(value = "用户昵称")
    private String nickName;

    @Email(message = "用户邮箱格式不正确")
    @ExcelProperty(value = "用户邮箱")
    private String email;

    @ExcelProperty(value = "用户性别", converter = DictConverter.class)
    @ExcelDictProperty("sys_user_sex")
    private String sex;

    @ExcelProperty(value = "账号状态", converter = EnumConverter.class)
    @ExcelEnumProperty(value = DBStatus.class)
    private String status;

    @ExcelProperty(value = "备注")
    private String remark;
}
