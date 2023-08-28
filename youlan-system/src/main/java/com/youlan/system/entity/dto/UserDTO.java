package com.youlan.system.entity.dto;

import com.youlan.common.crypto.anno.DecryptField;
import com.youlan.common.crypto.anno.EncryptField;
import com.youlan.common.crypto.enums.AlgorithmType;
import com.youlan.common.validator.Insert;
import com.youlan.common.validator.Update;
import com.youlan.common.validator.anno.*;
import com.youlan.common.db.constant.DBConstant;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.*;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {

    @Schema(title = DBConstant.DESC_ID)
    private Long id;

    @NotNull(message = "机构ID不能为空", groups = {Insert.class, Update.class})
    @Schema(title = "机构ID")
    private Long orgId;

    @Xss(message = "用户账号不能包含脚本字符")
    @NotBlank(message = "用户账号不能为空", groups = {Insert.class})
    @Schema(title = "用户账号")
    private String userName;

    @DecryptField(algorithm = AlgorithmType.AES)
    @NotBlank(message = "用户密码不能为空", groups = {Insert.class})
    @Schema(title = "用户密码")
    private String userPassword;

    @Phone(message = "用户手机号码格式不正确", groups = {Insert.class, Update.class})
    @Schema(title = "用户手机")
    private String userMobile;

    @Xss(message = "用户昵称不能包含脚本字符", groups = {Insert.class, Update.class})
    @NotBlank(message = "用户昵称不能为空")
    @Schema(title = "用户昵称")
    private String nickName;

    @Email(message = "用户邮箱格式不正确", groups = {Insert.class, Update.class})
    @Schema(title = "用户邮箱")
    private String email;

    @Schema(title = "用户性别(字典类型[sys_user_sex])")
    private String sex;

    @Schema(title = "用户岗位列表")
    private List<Long> postIdList;

    @Schema(title = "用户角色列表")
    private List<Long> roleIdList;

    @Schema(title = DBConstant.DESC_STATUS)
    private String status;

    @Schema(title = DBConstant.DESC_REMARK)
    private String remark;

}
