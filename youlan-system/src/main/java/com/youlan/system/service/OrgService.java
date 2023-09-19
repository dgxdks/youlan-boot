package com.youlan.system.service;

import cn.hutool.core.collection.CollectionUtil;
import com.youlan.common.core.helper.ListHelper;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.Org;
import com.youlan.system.entity.dto.OrgPageDTO;
import com.youlan.system.entity.vo.OrgVO;
import com.youlan.system.mapper.OrgMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * 根据机构ID列表获取机构ID与机构名称的映射
     */
    public Map<Long, String> loadOrgNameMapByOrgIdList(List<Long> orgIdList) {
        if (CollectionUtil.isEmpty(orgIdList)) {
            return new HashMap<>();
        }
        ArrayList<Long> distinctOrgIdList = CollectionUtil.distinct(orgIdList);
        return this.lambdaQuery()
                .select(Org::getOrgId, Org::getOrgName)
                .in(Org::getOrgId, distinctOrgIdList)
                .list()
                .stream()
                .collect(Collectors.toMap(Org::getOrgId, Org::getOrgName));
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
        List<OrgVO> orgList = this.getBaseMapper().getOrgList(dto);
        return ListHelper.getTreeList(orgList, OrgVO::getChildren, OrgVO::getOrgId, OrgVO::getParentOrgId, OrgVO::getOrgSort);
    }
}
