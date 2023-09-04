package com.youlan.system.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.UserRole;
import com.youlan.system.mapper.UserRoleMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserRoleService extends BaseServiceImpl<UserRoleMapper, UserRole> {

    /**
     * 新增用户关联角色信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void addUserRoleBatch(List<Long> userIds, Long roleId) {
        if (CollectionUtil.isEmpty(userIds)) {
            return;
        }
        List<UserRole> userRoleList = userIds.stream()
                .map(userId -> toUserRoleList(userId, Collections.singletonList(roleId)))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        this.saveBatch(userRoleList);
    }

    /**
     * 新增用户关联角色信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void addUserRoleBatch(Long userId, List<Long> roleIdList) {
        List<UserRole> userRoleList = toUserRoleList(userId, roleIdList);
        this.saveBatch(userRoleList);
    }

    /**
     * 修改用户关联角色信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUserRoleBatch(Long userId, List<Long> roleIdList) {
        List<UserRole> userRoleList = toUserRoleList(userId, roleIdList);
        this.removeByUserId(userId);
        this.saveBatch(userRoleList);
    }

    /**
     * 根据用户ID删除用户关联角色信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeByUserId(Long userId) {
        this.remove(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserId, userId));
    }

    /**
     * 根据用户ID查询用户关联角色列表
     */
    public List<UserRole> getUserRoleListByUserId(Long userId) {
        return this.lambdaQuery().eq(UserRole::getUserId, userId).list();
    }

    /**
     * 根据用户ID查询用户关联角色ID列表
     */
    public List<Long> getRoleIdListByUserId(Long userId) {
        return getUserRoleListByUserId(userId)
                .stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
    }

    /**
     * 根据用户ID和角色ID列表转为用户关联角色对象列表
     */
    public List<UserRole> toUserRoleList(Long userId, List<Long> roleIdList) {
        if (ObjectUtil.isEmpty(userId) || CollectionUtil.isEmpty(roleIdList)) {
            return new ArrayList<>();
        }
        return roleIdList.stream()
                .map(roleId -> new UserRole().setUserId(userId).setRoleId(roleId))
                .collect(Collectors.toList());
    }
}
