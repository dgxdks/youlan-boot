package com.youlan.tools.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlan.tools.entity.DBTableColumn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DBTableColumnMapper extends BaseMapper<DBTableColumn> {
    @InterceptorIgnore
    List<DBTableColumn> getDbTableColumnList(@Param("tableName") String tableName);
}
