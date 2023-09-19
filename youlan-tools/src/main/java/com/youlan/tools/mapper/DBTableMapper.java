package com.youlan.tools.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlan.tools.entity.DBTable;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DBTableMapper extends BaseMapper<DBTable> {

    @InterceptorIgnore
    List<DBTable> getList(DBTable dbTable);
}
