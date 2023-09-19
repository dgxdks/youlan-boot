package com.youlan.system.permission.handler;

import cn.dev33.satoken.exception.NotWebContextException;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.core.servlet.helper.ServletHelper;
import com.youlan.system.enums.RoleScope;
import com.youlan.system.helper.SystemAuthHelper;
import com.youlan.system.permission.anno.DataPermission;
import com.youlan.system.permission.anno.DataPermissions;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SystemDataPermissionHandler implements MultiDataPermissionHandler {
    private static final String ROLE_SCOPE_MAP_NAME = "ROLE_SCOPE_MAP";
    private static final ConcurrentHashMap<String, Optional<DataPermission[]>> ANNO_CACHE = new ConcurrentHashMap<>();

    @Override
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
        try {
            Optional<DataPermission[]> dataPermissionAnnoOpt = findDataPermissionAnno(mappedStatementId);
            // msId对应的
            if (dataPermissionAnnoOpt.isEmpty()) {
                return null;
            }
            //如果是管理员则直接返回
            if (SystemAuthHelper.isAdmin()) {
                return null;
            }
            DataPermission[] dataPermissionArr = dataPermissionAnnoOpt.get();
            Map<String, RoleScope> roleScopeMap = getOrCreateRoleScopeMap();
            // roleScopeMap是查出来的角色范围，如果查出来都没有权限就得抛出异常中断执行
            if (CollectionUtil.isEmpty(roleScopeMap)) {
                throw new BizRuntimeException(ApiResultCode.A0025);
            }
            // 包含所有数据权限则直接返回
            if (roleScopeMap.containsValue(RoleScope.ALL)) {
                return null;
            }
            //如果表有别名优先使用别名进行匹配
            String tableAlias = table.getName();
            if (ObjectUtil.isNotNull(table.getAlias()) && StrUtil.isNotBlank(table.getAlias().getName())) {
                tableAlias = table.getAlias().getName();
            }
            List<String> roleSqlItems = new ArrayList<>();
            // 不同角色的权限范围之间是OR关系，相同角色的多个权限注解配置是AND关系
            for (Map.Entry<String, RoleScope> roleScopeEntry : roleScopeMap.entrySet()) {
                List<String> permissionSqlItems = new ArrayList<>();
                for (DataPermission dataPermissionAnno : dataPermissionArr) {
                    String roleStr = roleScopeEntry.getKey();
                    RoleScope roleScope = roleScopeEntry.getValue();
                    String permissionTableAlias = dataPermissionAnno.tableAlias();
                    String orgIdColumn = dataPermissionAnno.orgIdColumn();
                    String userIdColumn = dataPermissionAnno.userIdColumn();
                    if (!StrUtil.equals(tableAlias, permissionTableAlias)) {
                        continue;
                    }
                    if (roleScope == RoleScope.CUSTOM && StrUtil.isNotBlank(orgIdColumn)) {
                        permissionSqlItems.add(StrUtil.format(RoleScope.CUSTOM.getSql(), permissionTableAlias + StrPool.DOT + orgIdColumn, roleStr));
                    }
                    if (roleScope == RoleScope.CURRENT_ORG && StrUtil.isNotBlank(orgIdColumn)) {
                        String orgId = SystemAuthHelper.getOrgId().toString();
                        permissionSqlItems.add(StrUtil.format(RoleScope.CURRENT_ORG.getSql(), permissionTableAlias + StrPool.DOT + orgIdColumn, orgId));
                    }
                    if (roleScope == RoleScope.ORG_BELOW && StrUtil.isNotBlank(orgIdColumn)) {
                        String orgId = SystemAuthHelper.getOrgId().toString();
                        permissionSqlItems.add(StrUtil.format(RoleScope.ORG_BELOW.getSql(), permissionTableAlias + StrPool.DOT + orgIdColumn, orgId, orgId));
                    }
                    if (roleScope == RoleScope.CURRENT_USER) {
                        // 优先判断是否指定了机构ID列，如果指定了机构ID列则用机构ID列过滤
                        if (StrUtil.isNotBlank(orgIdColumn)) {
                            String orgId = SystemAuthHelper.getOrgId().toString();
                            permissionSqlItems.add(StrUtil.format(RoleScope.CURRENT_USER.getSql(), permissionTableAlias + StrPool.DOT + orgIdColumn, orgId));
                        } else if (StrUtil.isNotBlank(userIdColumn)) {
                            String userId = SystemAuthHelper.getUserId().toString();
                            permissionSqlItems.add(StrUtil.format(RoleScope.CURRENT_USER.getSql(), permissionTableAlias + StrPool.DOT + userIdColumn, userId));
                        }
                    }
                }
                if (CollectionUtil.isEmpty(permissionSqlItems)) {
                    continue;
                }
                String permissionSql = "(" + CollectionUtil.join(permissionSqlItems, "AND") + ")";
                roleSqlItems.add(permissionSql);
            }
            // 未匹配到直接返回
            if (CollectionUtil.isEmpty(roleSqlItems)) {
                return null;
            }
            String roleScopeSql = "(" + CollectionUtil.join(roleSqlItems, "OR") + ")";
            return CCJSqlParserUtil.parseExpression(roleScopeSql);
        } catch (NotWebContextException e) {
            return null;
        } catch (BizRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new BizRuntimeException(e);
        }
    }

    /**
     * 获取角色字符与角色数据范围映射管理
     */
    public Map<String, RoleScope> getOrCreateRoleScopeMap() {
        //为当前请求缓存角色权限范围信息
        Map<String, RoleScope> roleScopeMap = ServletHelper.getAttribute(ROLE_SCOPE_MAP_NAME);
        if (ObjectUtil.isNull(roleScopeMap)) {
            roleScopeMap = new ConcurrentHashMap<>();
            List<String> roleStrList = SystemAuthHelper.getRoleStrList(SystemAuthHelper.getUserId());
            for (String roleStr : roleStrList) {
                String roleScope = SystemAuthHelper.getRoleScope(roleStr);
                // 不为空且是支持的值
                if (StrUtil.isBlank(roleScope) || !RoleScope.contains(roleScope)) {
                    continue;
                }
                RoleScope roleScopeEnum = RoleScope.convert(roleScope);
                if (ObjectUtil.isNull(roleScopeEnum)) {
                    continue;
                }
                roleScopeMap.put(roleStr, roleScopeEnum);
            }
        }
        return roleScopeMap;
    }

    /**
     * 获取数据权限注解
     */
    public Optional<DataPermission[]> findDataPermissionAnno(String msId) {
        // msId的格式类似于com.youlan.system.mapper.UserMapper.getList
        return ANNO_CACHE.computeIfAbsent(msId, key -> {
            final String mapperClzName = StrUtil.subBefore(msId, StringPool.DOT, true);
            final String mapperMethodName = StrUtil.subAfter(msId, StringPool.DOT, true);
            Class<Object> mapperClz = ClassUtil.loadClass(mapperClzName);
            if (ObjectUtil.isNull(mapperClz)) {
                return Optional.empty();
            }
            //返回方法名称匹配的第一个方法
            Method[] mapperMethods = ClassUtil.getDeclaredMethods(mapperClz);
            Method mapperMethod = ArrayUtil.firstMatch(method -> method.getName().equals(mapperMethodName), mapperMethods);
            if (ObjectUtil.isNull(mapperMethod)) {
                return Optional.empty();
            }
            DataPermissions dataPermissionsAnno = AnnotationUtil.getAnnotation(mapperMethod, DataPermissions.class);
            if (ObjectUtil.isNull(dataPermissionsAnno) || ArrayUtil.isEmpty(dataPermissionsAnno.value())) {
                return Optional.empty();
            }
            return Optional.of(dataPermissionsAnno.value());
        });
    }

    /**
     * 断言机构ID列不为空
     */
    public void assertOrgIdColumnNotBlank(String orgIdColumn) {
        Assert.notBlank(orgIdColumn, ApiResultCode.A0026::getException);
    }

    /**
     * 断言用户ID不为空
     */
    public void assertUserIdColumnNotBlanc(String userIdColumn) {
        Assert.notBlank(userIdColumn, ApiResultCode.A0027::getException);
    }
}
