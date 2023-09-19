package com.youlan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.system.entity.Role;
import com.youlan.system.permission.anno.DataPermissions;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    @DataPermissions(value = {})
    IPage<Role> getRolePageList(@Param("page") IPage<Role> page, @Param("role") Role role);

    @DataPermissions(value = {})
    List<Role> getRoleList(@Param("role") Role role);
}
