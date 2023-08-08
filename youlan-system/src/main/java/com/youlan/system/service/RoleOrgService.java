package com.youlan.system.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.RoleOrg;
import com.youlan.system.mapper.RoleOrgMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class RoleOrgService extends BaseServiceImpl<RoleOrgMapper, RoleOrg> {
    /**
     * 根据角色ID删除角色关联机构信息
     */
    public boolean removeByRoleId(Long roleId) {
        return this.remove(Wrappers.<RoleOrg>lambdaQuery().eq(RoleOrg::getRoleId, roleId));
    }
}
