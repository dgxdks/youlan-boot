package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.entity.dto.ListDTO;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.utils.QueryWrapperUtil;
import com.youlan.framework.anno.SystemLog;
import com.youlan.framework.constant.SystemLogType;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.entity.Role;
import com.youlan.system.entity.dto.RoleOrgDTO;
import com.youlan.system.service.RoleService;
import com.youlan.system.service.biz.RoleBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/role")
@AllArgsConstructor
public class RoleController extends BaseController {
    private final RoleService roleService;
    private final RoleBizService roleBizService;

    @SaCheckPermission("system:role:addRole")
    @Operation(summary = "角色新增")
    @PostMapping("/addRole")
    @SystemLog(name = "角色", type = SystemLogType.OPERATION_LOG_TYPE_ADD)
    public ApiResult addRole(@Validated @RequestBody Role role) {
        return toSuccess(roleService.addRole(role));
    }

    @SaCheckPermission("system:role:updateRole")
    @Operation(summary = "角色修改")
    @PostMapping("/updateRole")
    @SystemLog(name = "角色", type = SystemLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateRole(@Validated @RequestBody Role role) {
        if (ObjectUtil.isNull(role.getId())) {
            return toError(ApiResultCode.C0001);
        }
        return toSuccess(roleService.updateRole(role));
    }

    @SaCheckPermission("system:role:removeRole")
    @Operation(summary = "角色删除")
    @PostMapping("/removeRole")
    @SystemLog(name = "角色", type = SystemLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removeRole(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        return toSuccess(roleService.removeBatchByIds(dto.getList()));
    }

    @SaCheckPermission("system:role:loadRole")
    @Operation(summary = "角色详情")
    @PostMapping("/loadRole")
    public ApiResult loadRole(@RequestParam Long id) {
        return toSuccess(roleService.loadOne(id));
    }

    @SaCheckPermission("system:role:getRolePageList")
    @Operation(summary = "角色分页")
    @PostMapping("/getRolePageList")
    @SystemLog(name = "角色", type = SystemLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getRolePageList(@RequestBody Role role) {
        return toSuccess(roleService.loadPage(role, QueryWrapperUtil.getQueryWrapper(role)));
    }

    @SaCheckPermission("system:role:updateRoleScope")
    @Operation(summary = "角色数据权限修改")
    @PostMapping("/updateRoleScope")
    @SystemLog(name = "角色", type = SystemLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateRoleScope(@RequestBody RoleOrgDTO dto) {
        return toSuccess(roleBizService.updateRoleScope(dto));
    }

}
