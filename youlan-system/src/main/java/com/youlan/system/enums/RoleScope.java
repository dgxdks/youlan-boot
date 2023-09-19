package com.youlan.system.enums;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.youlan.system.constant.SystemConstant.*;

@Getter
@AllArgsConstructor
public enum RoleScope {
    /**
     * 全部数据权限
     */
    ALL(ROLE_SCOPE_ALL, "全部数据权限", StrUtil.EMPTY),
    /**
     * 自定义数据权限
     */
    CUSTOM(ROLE_SCOPE_CUSTOM, "自定义数据权限", " {} IN (SELECT org_id FROM t_sys_role_org AS ro LEFT JOIN t_sys_role AS r ON ro.role_id = r.id WHERE role_str = '{}') "),
    /**
     * 仅当前机构数据权限
     */
    CURRENT_ORG(ROLE_SCOPE_CURRENT_ORG, "仅当前机构数据权限", " {} = {} "),
    /**
     * 当前机构及以下数据权限
     */
    ORG_BELOW(ROLE_SCOPE_ORG_BELOW, "当前机构及以下数据权限", " {} IN ( SELECT org_id FROM t_sys_org WHERE org_id = {} or find_in_set({}, org_ancestors)) "),
    /**
     * 仅本人数据权限
     */
    CURRENT_USER(ROLE_SCOPE_CURRENT_USER, "仅本人数据权限", " {} = {} ");

    private final String code;
    private final String text;
    private final String sql;

    public static boolean contains(String roleScope) {
        return Arrays.stream(RoleScope.values())
                .anyMatch(item -> item.getCode().equals(roleScope));
    }

    public static RoleScope convert(String roleScope) {
        List<RoleScope> roleScopes = Arrays.stream(RoleScope.values())
                .filter(item -> item.getCode().equals(roleScope))
                .collect(Collectors.toList());
        return CollectionUtil.getFirst(roleScopes);
    }
}
