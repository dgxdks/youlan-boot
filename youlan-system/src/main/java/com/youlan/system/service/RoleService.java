package com.youlan.system.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.Role;
import com.youlan.system.mapper.RoleMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class RoleService extends BaseServiceImpl<RoleMapper, Role> {

    /**
     * 新增角色
     */
    public boolean addRole(Role role) {
        checkRoleNameRepeat(role);
        checkRoleStrRepeat(role);
        return this.save(role);
    }

    /**
     * 修改角色
     */
    public boolean updateRole(Role role) {
        checkRoleNameRepeat(role);
        checkRoleStrRepeat(role);
        return this.updateById(role);
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
