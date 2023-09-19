package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.common.excel.helper.ExcelHelper;
import com.youlan.common.validator.Insert;
import com.youlan.common.validator.Update;
import com.youlan.controller.base.BaseController;
import com.youlan.system.anno.OperationLog;
import com.youlan.system.constant.OperationLogType;
import com.youlan.system.constant.SystemConstant;
import com.youlan.system.entity.Role;
import com.youlan.system.entity.dto.UserDTO;
import com.youlan.system.entity.dto.UserPageDTO;
import com.youlan.system.entity.dto.UserResetPasswdDTO;
import com.youlan.system.entity.vo.UserTemplateVO;
import com.youlan.system.entity.vo.UserVO;
import com.youlan.system.excel.listener.UserExcelListener;
import com.youlan.system.helper.SystemAuthHelper;
import com.youlan.system.service.RoleService;
import com.youlan.system.service.UserService;
import com.youlan.system.service.biz.UserBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
@AllArgsConstructor
public class UserController extends BaseController {
    private final UserBizService userBizService;
    private final UserService userService;
    private final RoleService roleService;

    @SaCheckPermission("system:user:add")
    @Operation(summary = "用户新增")
    @PostMapping("/addUser")
    @OperationLog(name = "用户", type = OperationLogType.OPERATION_LOG_TYPE_ADD)
    public ApiResult addUser(@Validated(Insert.class) @RequestBody UserDTO dto) {
        return toSuccess(userBizService.addUser(dto));
    }

    @SaCheckPermission("system:user:update")
    @Operation(summary = "用户修改")
    @PostMapping("/updateUser")
    @OperationLog(name = "用户", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateUser(@Validated(Update.class) @RequestBody UserDTO dto) {
        // TODO: 2023/8/28 数据权限
        if (ObjectUtil.isNull(dto.getId())) {
            return toError(ApiResultCode.C0001);
        }
        SystemAuthHelper.checkUserNotAdmin(dto.getId());
        SystemAuthHelper.checkHasUserId(dto.getId());
        return toSuccess(userBizService.updateUser(dto));
    }

    @SaCheckPermission("system:user:remove")
    @Operation(summary = "用户删除")
    @PostMapping("/removeUser")
    @OperationLog(name = "用户", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removeUser(@Validated @RequestBody ListDTO<Long> dto) {
        // TODO: 2023/8/28 数据权限
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        //不能删除自己
        if (CollectionUtil.contains(dto.getList(), SystemAuthHelper.getUserId())) {
            throw new BizRuntimeException(ApiResultCode.A0012);
        }
        dto.getList().forEach(SystemAuthHelper::checkUserNotAdmin);
        return toSuccess(userBizService.removeUser(dto.getList()));
    }

    @SaCheckPermission("system:user:load")
    @Operation(summary = "用户详情")
    @PostMapping("/loadUser")
    public ApiResult loadUser(@RequestParam Long id) {
        // TODO: 2023/8/30 缺少数据权限
        SystemAuthHelper.checkHasUserId(id);
        return toSuccess(userBizService.loadUser(id));
    }

    @SaCheckPermission("system:user:list")
    @Operation(summary = "用户分页")
    @PostMapping("/getUserPageList")
    @OperationLog(name = "用户", type = OperationLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getUserPageList(@RequestBody UserPageDTO dto) {
        // TODO: 2023/8/30 缺少数据权限
        return toSuccess(userBizService.getUserPageList(dto));
    }

    @SaCheckPermission("system:user:export")
    @Operation(summary = "用户导出")
    @PostMapping("/exportUserList")
    @OperationLog(name = "用户", type = OperationLogType.OPERATION_LOG_TYPE_EXPORT)
    public void exportUserList(@RequestBody UserPageDTO dto, HttpServletResponse response) throws IOException {
        // TODO: 2023/8/30 缺少数据权限
        List<UserVO> userList = userBizService.exportUserList(dto);
        toExcel("用户.xlsx", "用户", UserVO.class, userList, response);
    }

    @SaCheckPermission("system:user:import")
    @Operation(summary = "用户导入")
    @PostMapping("/importUserList")
    @OperationLog(name = "用户", type = OperationLogType.OPERATION_LOG_TYPE_IMPORT)
    public ApiResult importUserList(@RequestPart("file") MultipartFile file, boolean cover) throws IOException {
        UserExcelListener userExcelListener = new UserExcelListener(cover);
        ExcelHelper.read(file.getInputStream(), UserTemplateVO.class, userExcelListener)
                .sheet()
                .doRead();
        return toSuccess(userExcelListener.getResultMsg());
    }

    @SaCheckPermission("system:user:resetPasswd")
    @Operation(summary = "用户密码重置")
    @PostMapping("/resetUserPasswd")
    @OperationLog(name = "用户", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult resetUserPasswd(@Validated @RequestBody UserResetPasswdDTO dto) {
        // TODO: 2023/8/23 数据权限
        if (ObjectUtil.isNull(dto.getId())) {
            return toError(ApiResultCode.C0001);
        }
        SystemAuthHelper.checkUserNotAdmin(dto.getId());
        SystemAuthHelper.checkHasUserId(dto.getId());
        return toSuccess(userBizService.resetUserPasswd(dto));
    }

    @SaCheckPermission("system:user:update")
    @Operation(summary = "用户状态修改")
    @PostMapping("/updateUserStatus")
    @OperationLog(name = "用户", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateUserStatus(@RequestParam Long id, @RequestParam String status) {
        SystemAuthHelper.checkUserNotAdmin(id);
        SystemAuthHelper.checkHasUserId(id);
        return toSuccess(userService.updateStatus(id, status));
    }

    @SaCheckPermission("system:user:list")
    @Operation(summary = "授权角色分页")
    @PostMapping("/getAuthRolePageList")
    public ApiResult getAuthRolePageList(@RequestBody Role role) {
        // 此排除管理员角色ID
        role.setIdExcludes(Collections.singletonList(SystemConstant.ADMIN_ROLE_ID));
        IPage<Role> roleList = roleService.getBaseMapper().getRolePageList(DBHelper.getPage(role), role);
        return toSuccess(roleList);
    }

    @SaCheckPermission("system:user:update")
    @Operation(summary = "授权角色修改")
    @PostMapping("/updateAuthRole")
    public ApiResult updateAuthRole(@RequestParam Long userId, @RequestParam List<Long> roleIds) {
        SystemAuthHelper.checkHasUserId(userId);
        SystemAuthHelper.checkHasRoleIds(roleIds);
        userBizService.updateAuthRole(userId, roleIds);
        return toSuccess();
    }

    @Operation(summary = "下载用户导入模版")
    @PostMapping("/downloadUserTemplate")

    public void downloadUserTemplate() throws IOException {
        toExcel("用户数据", UserTemplateVO.class, new ArrayList<>());
    }
}
