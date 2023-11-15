package com.youlan.system.anno;

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
     * 绑定SQL中的表名，数据权限的条件SQL会生成在这个表下
     * 例如 select * from t_a as a left join t_b as b on a.id = b.id where a.sts = 1 and b.sts = 1
     * 如果绑定的是别名为a的表，则数据权限的条件SQL会拼接在 where a.sts = 1 and b.sts = 1 and ${permissionSql}
     * 如果绑定的是别名为b的表，则数据权限的条件SQL会拼接在 on a.id = b.id and ${permissionSql}
     * 这两个查询出来的结果是可能会有区别的
     */
    String tableBind();

    /**
     * 指定机构ID列，列可以是{@link #tableBind()}表的列也可以自己指定的任意列
     */
    String orgIdColumn() default StrUtil.EMPTY;

    /**
     * 指定用户ID列，指定机构ID列，列可以是tableBind表的列也可以自己指定的任意列
     *
     * @see com.youlan.system.mapper.OrgMapper#getOrgPageList(IPage, OrgPageDTO)
     * @see com.youlan.system.mapper.OrgMapper#getOrgList(OrgPageDTO)
     */
    String userIdColumn() default StrUtil.EMPTY;

    /**
     * 是否用{@link #orgIdColumn()}替代{@link #userIdColumn()}, 仅角色权限范围是{@link RoleScope#CURRENT_USER}时有效
     * 有时候当角色权限范围是{@link RoleScope#CURRENT_USER}时，想通过用户的orgId进行过滤而不是用userId过滤可以修改为true
     */
    boolean orgIdReplaceUserId() default false;
}
