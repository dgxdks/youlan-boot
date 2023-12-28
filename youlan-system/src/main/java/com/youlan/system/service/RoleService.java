package com.youlan.system.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.Role;
import com.youlan.system.mapper.RoleMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class RoleService extends BaseServiceImpl<RoleMapper, Role> {


    /**
     * 获取角色信息且不为空
     */
    public Role loadRoleNotNull(Long id) {
        return this.loadOneOpt(id)
                .orElseThrow(ApiResultCode.A0017::getException);
    }

    /**
     * 根据角色字符获取角色信息
     */
    public Role loadRoleByRoleStr(String roleStr) {
        return this.loadOne(Role::getRoleStr, roleStr);
    }

    /**
     * 根据角色ID获取权限字符列表
     */
    public List<String> getRoleStrList(List<Long> roleIdList) {
        return this.lambdaQuery()
                .select(Role::getRoleStr)
                .in(Role::getId, roleIdList)
                .list()
                .stream()
                .map(Role::getRoleStr)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 根据角色ID获取权限字符
     */
    public String getRoleStr(Long roleId) {
        List<String> roleStrList = getRoleStrList(Collections.singletonList(roleId));
        if (CollectionUtil.isEmpty(roleStrList)) {
            throw new BizRuntimeException(ApiResultCode.A0017);
        }
        return CollectionUtil.getFirst(roleStrList);
    }

    /**
     * 更新用户角色数据范围
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleScope(Long roleId, String roleScope) {
        if (ObjectUtil.isNull(roleId) || StrUtil.isBlank(roleScope)) {
            return;
        }
        this.lambdaUpdate()
                .eq(Role::getId, roleId)
                .set(Role::getRoleScope, roleScope)
                .update();
    }

    /**
     * 检查角色名是否重复
     */
    public void checkRoleNameRepeat(Role role) {
        List<Role> roleList = this.lambdaQuery()
                .select(Role::getId)
                .eq(Role::getRoleName, role.getRoleName())
                .list();
        if (CollectionUtil.isEmpty(roleList)) {
            return;
        }
        for (Role retRole : roleList) {
            if (!retRole.getId().equals(role.getId())) {
                throw new BizRuntimeException(StrUtil.format("新增角色名称[{}]已存在", role.getRoleName()));
            }
        }
    }

    /**
     * 检查角色字符是否重复
     */
    public void checkRoleStrRepeat(Role role) {
        List<Role> roleList = this.lambdaQuery()
                .select(Role::getId)
                .eq(Role::getRoleStr, role.getRoleStr())
                .list();
        if (CollectionUtil.isEmpty(roleList)) {
            return;
        }
        for (Role retRole : roleList) {
            if (!retRole.getId().equals(role.getId())) {
                throw new BizRuntimeException(StrUtil.format("新增角色字符[{}]已存在", role.getRoleStr()));
            }
        }
    }
}
