package com.youlan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.system.entity.Role;
import com.youlan.system.permission.anno.DataPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    @DataPermission
    IPage<Role> getRoleList(@Param("page") IPage<Role> page, @Param("role") Role role);

    @DataPermission
    List<Role> getRoleList(@Param("role") Role role);
}
