package com.youlan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlan.system.entity.Org;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

@Mapper
public interface OrgMapper extends BaseMapper<Org> {
    boolean removeByOrgModelTableId(@Param("tableName") String tableName, @Param("idList") Collection<?> idList);
}
