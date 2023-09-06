package com.youlan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlan.system.entity.Org;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrgMapper extends BaseMapper<Org> {
    /**
     * 替换指定子机构的祖级
     */
    boolean replaceChildOrgAncestors(@Param("orgId") Long orgId, @Param("oldAncestors") String oldAncestors,
                                     @Param("newAncestors") String newAncestors);
}
