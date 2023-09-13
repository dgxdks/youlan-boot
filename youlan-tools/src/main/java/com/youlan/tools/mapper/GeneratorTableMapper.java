package com.youlan.tools.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.tools.entity.DBTable;
import com.youlan.tools.entity.GeneratorTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GeneratorTableMapper extends BaseMapper<GeneratorTable> {
    IPage<DBTable> getDbTableList(@Param("page") IPage<DBTable> page, @Param("dt") DBTable dbTable);
}
