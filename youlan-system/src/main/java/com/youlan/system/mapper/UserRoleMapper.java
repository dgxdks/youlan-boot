package com.youlan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.system.entity.Role;
import com.youlan.system.entity.User;
import com.youlan.system.entity.UserRole;
import com.youlan.system.entity.dto.UserRolePageDTO;
import com.youlan.system.permission.anno.DataPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
    @DataPermission
    IPage<User> getUserListByRoleId(@Param("page") IPage<Role> page, @Param("dto") UserRolePageDTO dto);
}
