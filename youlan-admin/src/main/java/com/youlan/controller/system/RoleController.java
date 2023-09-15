package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.common.core.helper.ListHelper;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.system.anno.OperationLog;
import com.youlan.system.constant.OperationLogType;
import com.youlan.controller.base.BaseController;
import com.youlan.system.entity.Role;
import com.youlan.system.entity.dto.UserRolePageDTO;
import com.youlan.system.helper.SystemAuthHelper;
import com.youlan.system.service.RoleService;
import com.youlan.system.service.UserRoleService;
import com.youlan.system.service.biz.RoleBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/role")
@AllArgsConstructor
public class RoleController extends BaseController {
    private final RoleService roleService;
    private final RoleBizService roleBizService;
    private final UserRoleService userRoleService;

    @SaCheckPermission("system:role:add")
    @Operation(summary = "角色新增")
    @PostMapping("/addRole")
    @OperationLog(name = "角色", type = OperationLogType.OPERATION_LOG_TYPE_ADD)
    public ApiResult addRole(@Validated @RequestBody Role role) {
        return toSuccess(roleBizService.addRole(role));
    }

    @SaCheckPermission("system:role:update")
    @Operation(summary = "角色修改")
    @PostMapping("/updateRole")
    @OperationLog(name = "角色", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateRole(@Validated @RequestBody Role role) {
        if (ObjectUtil.isNull(role.getId())) {
            return toError(ApiResultCode.C0001);
        }
        SystemAuthHelper.checkRoleNotAdmin(role.getId());
        SystemAuthHelper.checkHasRoleId(role.getId());
        return toSuccess(roleBizService.updateRole(role));
    }

    @SaCheckPermission("system:role:remove")
    @Operation(summary = "角色删除")
    @PostMapping("/removeRole")
    @OperationLog(name = "角色", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removeRole(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        dto.getList().forEach(SystemAuthHelper::checkRoleNotAdmin);
        dto.getList().forEach(SystemAuthHelper::checkHasRoleId);
        return toSuccess(roleBizService.removeRole(dto.getList()));
    }

    @SaCheckPermission("system:role:load")
    @Operation(summary = "角色详情")
    @PostMapping("/loadRole")
    public ApiResult loadRole(@RequestParam Long id) {
        return toSuccess(roleBizService.loadRole(id));
    }

    @SaCheckPermission("system:role:list")
    @Operation(summary = "角色列表")
    @PostMapping("/getRoleList")
    @OperationLog(name = "角色", type = OperationLogType.OPERATION_LOG_TYPE_LIST)
    public ApiResult getRoleList(@RequestBody Role role) {
        // TODO: 2023/8/23 需要考虑数据权限 只能看当前用户可以看的角色
        List<Role> roleList = roleService.getBaseMapper().getRoleList(role);
        //不显示管理员角色信息
        roleList = ListHelper.filterList(roleList, r -> !SystemAuthHelper.isAdminRole(r.getRoleStr()));
        return toSuccess(roleList);
    }

    @SaCheckPermission("system:role:list")
    @Operation(summary = "角色分页")
    @PostMapping("/getRolePageList")
    @OperationLog(name = "角色", type = OperationLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getRolePageList(@RequestBody Role role) {
        // TODO: 2023/8/30 缺少数据权限
        IPage<Role> roleList = roleService.getBaseMapper().getRoleList(DBHelper.getPage(role), role);
        return toSuccess(roleList);
    }

    @SaCheckPermission("system:role:export")
    @Operation(summary = "角色导出")
    @PostMapping("/exportRoleList")
    @OperationLog(name = "角色", type = OperationLogType.OPERATION_LOG_TYPE_EXPORT)
    public void exportRoleList(@RequestBody Role role, HttpServletResponse response) throws IOException {
        // TODO: 2023/8/30 缺少数据权限
        List<Role> roleList = roleService.getBaseMapper().getRoleList(role);
        toExcel("角色数据.xlsx", Role.class, roleList, response);
    }

    @SaCheckPermission("system:role:update")
    @Operation(summary = "角色数据权限修改")
    @PostMapping("/updateRoleScope")
    @OperationLog(name = "角色", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateRoleScope(@RequestBody Role role) {
        if (ObjectUtil.isNull(role.getId())) {
            return toError(ApiResultCode.C0001);
        }
        SystemAuthHelper.checkRoleNotAdmin(role.getId());
        SystemAuthHelper.checkHasRoleId(role.getId());
        return toSuccess(roleBizService.updateRoleScope(role));
    }

    @SaCheckPermission("system:role:update")
    @Operation(summary = "角色状态修改")
    @PostMapping("/updateRoleStatus")
    @OperationLog(name = "角色", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateRoleStatus(@RequestParam Long id, @RequestParam String status) {
        SystemAuthHelper.checkRoleNotAdmin(id);
        SystemAuthHelper.checkHasRoleId(id);
        roleBizService.updateRoleStatus(id, status);
        return toSuccess();
    }

    @SaCheckPermission("system:role:remove")
    @Operation(summary = "角色缓存刷新")
    @PostMapping("/refreshRoleCache")
    @OperationLog(name = "角色", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult refreshRoleCache(@RequestParam Long id) {
        SystemAuthHelper.checkRoleNotAdmin(id);
        SystemAuthHelper.checkHasRoleId(id);
        roleBizService.refreshRoleCache(id);
        return toSuccess();
    }

    @SaCheckPermission("system:role:list")
    @Operation(summary = "授权用户分页")
    @PostMapping("/getAuthUserPageList")
    public ApiResult getAuthUserPageList(@RequestBody UserRolePageDTO dto) {
        if (ObjectUtil.isNull(dto.getRoleId())) {
            return toError(ApiResultCode.A0017);
        }
        SystemAuthHelper.checkHasRoleId(dto.getRoleId());
        return toSuccess(roleBizService.getAuthUserPageList(dto));
    }

    @SaCheckPermission("system:role:list")
    @Operation(summary = "未授权用户分页")
    @PostMapping("/getUnAuthUserPageList")
    public ApiResult getUnAuthUserPageList(@RequestBody UserRolePageDTO dto) {
        if (ObjectUtil.isNull(dto.getRoleId())) {
            return toError(ApiResultCode.A0017);
        }
        SystemAuthHelper.checkHasRoleId(dto.getRoleId());
        return toSuccess(roleBizService.getUnAuthUserPageList(dto));
    }

    @SaCheckPermission("system:role:update")
    @Operation(summary = "授权用户取消")
    @PostMapping("/cancelAuthUser")
    public ApiResult cancelAuthUser(@RequestParam Long roleId, @RequestParam List<Long> userIds) {
        SystemAuthHelper.checkHasRoleId(roleId);
        SystemAuthHelper.checkHasUserIds(userIds);
        roleBizService.cancelAuthUser(roleId, userIds);
        return toSuccess();
    }

    @SaCheckPermission("system:role:update")
    @Operation(summary = "授权用户新增")
    @PostMapping("/addAuthUser")
    public ApiResult addAuthUser(@RequestParam Long roleId, @RequestParam List<Long> userIds) {
        SystemAuthHelper.checkHasRoleId(roleId);
        SystemAuthHelper.checkHasUserIds(userIds);
        roleBizService.addAuthUser(roleId, userIds);
        return toSuccess();
    }
}
