package com.youlan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlan.system.entity.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DeptMapper extends BaseMapper<Dept> {
    boolean saveTest(@Param("aa")String aa, @Param("bb")String bb);
}
