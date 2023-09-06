package com.youlan.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youlan.common.core.helper.ListHelper;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.Org;
import com.youlan.system.entity.dto.OrgPageDTO;
import com.youlan.system.entity.vo.OrgVO;
import com.youlan.system.mapper.OrgMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class OrgService extends BaseServiceImpl<OrgMapper, Org> {

    /**
     * 根据机构ID获取机构名称
     */
    public String loadOrgNameByOrgId(Long ordId) {
        Org org = this.lambdaQuery()
                .select(Org::getOrgId, Org::getOrgName)
                .eq(Org::getOrgId, ordId)
                .oneOpt()
                .orElseThrow(ApiResultCode.D0001::getException);
        return org.getOrgName();
    }

    /**
     * 查询机构详情
     */
    public Org loadOrgIfExist(Long orgId) {
        return this.loadOneOpt(orgId)
                .orElseThrow(ApiResultCode.D0001::getException);
    }

    /**
     * 获取机构树列表
     */
    @SuppressWarnings("all")
    public List<OrgVO> getOrgTreeList(OrgPageDTO dto) {
        QueryWrapper<Org> queryWrapper = DBHelper.getQueryWrapper(dto);
        List<OrgVO> orgList = this.loadMore(queryWrapper, OrgVO.class);
        return ListHelper.getTreeList(orgList, OrgVO::getChildren, OrgVO::getOrgId, OrgVO::getParentOrgId, OrgVO::getOrgSort);
    }
}
