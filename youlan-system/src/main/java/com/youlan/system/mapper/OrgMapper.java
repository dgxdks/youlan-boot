package com.youlan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.system.entity.Org;
import com.youlan.system.entity.dto.OrgPageDTO;
import com.youlan.system.entity.vo.OrgVO;
import com.youlan.system.permission.anno.DataPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrgMapper extends BaseMapper<Org> {

    /**
     * 机构分页
     */
    @DataPermission(tableBind = "t_sys_org", orgIdColumn = "org_id", orgIdReplaceUserId = true)
    List<OrgVO> getOrgPageList(@Param("page") IPage<Org> page, @Param("dto") OrgPageDTO dto);


    /**
     * 机构列表
     */
    @DataPermission(tableBind = "t_sys_org", orgIdColumn = "org_id", orgIdReplaceUserId = true)
    List<OrgVO> getOrgList(@Param("dto") OrgPageDTO dto);

    @DataPermission(tableBind = "t_sys_org", orgIdColumn = "org_id", orgIdReplaceUserId = true)
    List<Long> hasOrgId(@Param("orgId") Long orgId);

    @DataPermission(tableBind = "t_sys_org", orgIdColumn = "org_id", orgIdReplaceUserId = true)
    List<Long> hasOrgIds(@Param("orgIds") List<Long> orgIds);

    /**
     * 替换指定子机构的祖级
     */
    boolean replaceChildOrgAncestors(@Param("orgId") Long orgId, @Param("oldAncestors") String oldAncestors,
                                     @Param("newAncestors") String newAncestors);
}
