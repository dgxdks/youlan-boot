package com.youlan.system.satoken;

import cn.dev33.satoken.stp.StpInterface;
import com.youlan.system.helper.SystemAuthHelper;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class StpInterfaceSystemPermissionImpl implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> roleStrList = getRoleList(loginId, loginType);
        return roleStrList.stream()
                .map(SystemAuthHelper::getUserMenuPerms)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());

    }

    @Override
    public List<String> getRoleList(final Object loginId, String loginType) {
        return SystemAuthHelper.getRoleStrList(Long.valueOf(loginId.toString()));
    }
}
