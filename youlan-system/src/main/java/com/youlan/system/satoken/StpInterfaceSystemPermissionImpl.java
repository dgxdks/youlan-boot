package com.youlan.system.satoken;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.extra.spring.SpringUtil;
import com.youlan.system.service.biz.RoleBizService;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class StpInterfaceSystemPermissionImpl implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> roleIdList = getRoleList(loginId, loginType);
        //如果当前角色ID无缓存权限信息则从通过角色ID与权限的缓存方法进行查询
        return roleIdList.stream()
                .map(roleId -> roleBizService().getMenuPermsListCache(Long.valueOf(roleId)))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

    }

    @Override
    public List<String> getRoleList(final Object loginId, String loginType) {
        //如果当前角色无缓存角色信息的话从数据库查询,当用户关联角色信息发生变动时会清除这个缓存，也会触发数据库查询操作
        return roleBizService().getRoleStrListCache(Long.valueOf(loginId.toString()));
    }

    public RoleBizService roleBizService() {
        return SpringUtil.getBean(RoleBizService.class);
    }
}
