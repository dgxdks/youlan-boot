package com.youlan.system.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.enums.DBStatus;
import com.youlan.common.excel.anno.ExcelEnumProperty;
import com.youlan.common.excel.converter.EnumConverter;
import com.youlan.system.excel.converter.DictConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ExcelIgnoreUnannotated
public class UserVO {

    @ExcelProperty(value = "主键ID")
    @Schema(title = DBConstant.DESC_ID)
    private Long id;

    @Schema(title = "机构ID")
    private Long orgId;

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

    @Schema(title = "用户头像")
    private String avatar;

    @ExcelProperty(value = "用户性别", converter = DictConverter.class)
    @Schema(title = "用户性别(字典类型[sys_user_sex])")
    private String sex;

    @ExcelProperty(value = "状态(1-正常 2-停用)", converter = EnumConverter.class)
    @ExcelEnumProperty(value = DBStatus.class)
    @Schema(title = DBConstant.DESC_STATUS)
    private String status;

    @ExcelProperty(value = "最后登录IP")
    @Schema(title = "最后登录IP")
    private String loginIp;

    @ExcelProperty(value = "最后登录时间")
    @Schema(title = "最后登录时间")
    private String loginTime;

    @ExcelProperty(value = "备注")
    @Schema(title = DBConstant.DESC_REMARK)
    private String remark;

    @ExcelProperty(value = "创建用户ID")
    @Schema(title = DBConstant.DESC_CREATE_ID)
    private Long createId;

    @ExcelProperty(value = "创建用户")
    @Schema(title = DBConstant.DESC_CREATE_BY)
    private String createBy;

    @ExcelProperty(value = "修改用户ID")
    @Schema(title = DBConstant.DESC_UPDATE_ID)
    private Long updateId;

    @ExcelProperty(value = "修改用户")
    @Schema(title = DBConstant.DESC_UPDATE_BY)
    private String updateBy;

    @ExcelProperty(value = "创建时间")
    @Schema(title = DBConstant.DESC_CREATE_TIME)
    private Date createTime;

    @ExcelProperty(value = "修改时间")
    @Schema(title = DBConstant.DESC_UPDATE_TIME)
    private Date updateTime;

    @Schema(title = "用户角色ID列表")
    private List<Long> roleIdList;

    @Schema(title = "用户关联岗位列表")
    private List<Long> postIdList;

}
