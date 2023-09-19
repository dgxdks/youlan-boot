package com.youlan.system.permission.anno;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.system.entity.dto.OrgPageDTO;
import com.youlan.system.enums.RoleScope;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {
    /**
     * 指定SQL中表别名(SQL解析时如果未获取到SQL中表的别名时会用表明与此值匹配)
     */
    String tableAlias();

    /**
     * 指定表对应的机构ID列
     */
    String orgIdColumn() default StrUtil.EMPTY;

    /**
     * 指定表对应的用户ID列
     * 有时候当角色权限范围是{@link RoleScope#CURRENT_USER}时，但是上面指定的表有机构ID列没有用户ID列
     * 这时候可以指定orgIdColumn，拦截器会优先获取机构列的值，并通过获取用户ID对应的机构ID做为条件进行过滤
     *
     * @see com.youlan.system.mapper.OrgMapper#getOrgPageList(IPage, OrgPageDTO)
     * @see com.youlan.system.mapper.OrgMapper#getOrgList(OrgPageDTO)
     */
    String userIdColumn() default StrUtil.EMPTY;
}
