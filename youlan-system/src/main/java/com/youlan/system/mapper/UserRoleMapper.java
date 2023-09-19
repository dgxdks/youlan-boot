package com.youlan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.system.entity.Role;
import com.youlan.system.entity.UserRole;
import com.youlan.system.entity.dto.UserRolePageDTO;
import com.youlan.system.entity.vo.UserVO;
import com.youlan.system.permission.anno.DataPermissions;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
    @DataPermissions(value = {})
    IPage<UserVO> getAuthUserList(@Param("page") IPage<Role> page, @Param("dto") UserRolePageDTO dto);

    @DataPermissions(value = {})
    IPage<UserVO> getUnAuthUserList(@Param("page") IPage<Role> page, @Param("dto") UserRolePageDTO dto);

    List<String> getRoleNameListByUserId(@Param("userId") Long userId);

    List<String> getRoleStrListByUserId(@Param("userId") Long userId);
}
