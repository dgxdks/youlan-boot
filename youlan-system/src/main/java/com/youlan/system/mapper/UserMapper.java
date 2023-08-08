package com.youlan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlan.system.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
