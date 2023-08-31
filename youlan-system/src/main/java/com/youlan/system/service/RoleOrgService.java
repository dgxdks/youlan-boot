package com.youlan.system.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.RoleOrg;
import com.youlan.system.mapper.RoleOrgMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class RoleOrgService extends BaseServiceImpl<RoleOrgMapper, RoleOrg> {

    /**
     * 更新角色关联机构
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleOrgBatch(Long roleId, List<Long> ordIdList) {
        List<RoleOrg> roleOrgList = toRoleOrgList(roleId, ordIdList);
        this.removeByRoleId(roleId);
        this.saveBatch(roleOrgList);
    }

    /**
     * 根据角色ID和机构ID转为用户关联机构对象列表
     */
    public List<RoleOrg> toRoleOrgList(Long roleId, List<Long> orgIdList) {
        if (ObjectUtil.isNull(roleId) || CollectionUtil.isEmpty(orgIdList)) {
            return new ArrayList<>();
        }
        return orgIdList.stream()
                .map(ordId -> new RoleOrg().setRoleId(roleId).setOrgId(ordId))
                .collect(Collectors.toList());
    }

    /**
     * 根据角色ID删除角色关联机构信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeByRoleId(Long roleId) {
        this.remove(Wrappers.<RoleOrg>lambdaQuery().eq(RoleOrg::getRoleId, roleId));
    }

    /**
     * 根据角色ID获取角色关联机构ID列表
     */
    public List<Long> getOrgIdListByRoleId(Long roleId) {
        return this.lambdaQuery()
                .select(RoleOrg::getOrgId)
                .eq(RoleOrg::getRoleId, roleId)
                .list()
                .stream()
                .map(RoleOrg::getOrgId)
                .collect(Collectors.toList());
    }
}
