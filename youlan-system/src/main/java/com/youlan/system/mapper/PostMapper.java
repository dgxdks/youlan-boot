package com.youlan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlan.system.entity.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
}
