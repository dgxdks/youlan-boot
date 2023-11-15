package com.youlan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.system.entity.User;
import com.youlan.system.entity.dto.UserPageDTO;
import com.youlan.system.entity.vo.UserVO;
import com.youlan.system.anno.DataPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 用户分页
     */
    @DataPermission(tableBind = "user", orgIdColumn = "org.org_id", userIdColumn = "user.id")
    IPage<UserVO> getUserPageList(@Param("page") IPage<User> page, @Param("dto") UserPageDTO dto);

    /**
     * 用户列表
     */
    @DataPermission(tableBind = "user", orgIdColumn = "org.org_id", userIdColumn = "user.id")
    List<UserVO> getUserList(@Param("dto") UserPageDTO dto);


    /**
     * 判断当前用户是否拥有此用户ID
     */
    @DataPermission(tableBind = "user", orgIdColumn = "org.org_id", userIdColumn = "user.id")
    List<Long> hasUserId(@Param("userId") Long userId);

    /**
     * 判断当前用户是否拥有此用户ID
     */
    @DataPermission(tableBind = "user", orgIdColumn = "org.org_id", userIdColumn = "user.id")
    List<Long> hasUserIds(@Param("userIds") List<Long> userIds);
}
