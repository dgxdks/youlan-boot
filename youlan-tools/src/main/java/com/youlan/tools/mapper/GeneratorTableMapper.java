package com.youlan.tools.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlan.tools.entity.DBTable;
import com.youlan.tools.entity.GeneratorTable;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GeneratorTableMapper extends BaseMapper<GeneratorTable> {
    List<DBTable> getDbTableList(DBTable dbTable);
}
