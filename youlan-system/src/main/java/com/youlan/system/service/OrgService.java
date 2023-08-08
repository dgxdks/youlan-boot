package com.youlan.system.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.common.db.utils.QueryWrapperUtil;
import com.youlan.system.entity.Org;
import com.youlan.system.entity.dto.OrgPageDTO;
import com.youlan.system.entity.vo.OrgVO;
import com.youlan.system.mapper.OrgMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.youlan.common.core.db.constant.DBConstant.VAL_STATUS_DISABLED;
import static com.youlan.common.core.db.constant.DBConstant.VAL_STATUS_ENABLED;

@Slf4j
@Service
@AllArgsConstructor
public class OrgService extends BaseServiceImpl<OrgMapper, Org> {

    /**
     * 根据机构ID列表获取机构ID和机构名称映射关系
     */
    public Map<Long, String> getOrgIdOrgNameMap(Collection<Long> orgIdList) {
        Set<Long> orgIdSet = new HashSet<>(orgIdList);
        List<Org> orgList = this.lambdaQuery()
                .select(Org::getOrgId, Org::getOrgName)
                .in(Org::getOrgId, orgIdSet)
                .list();
        return orgList.stream().collect(Collectors.toMap(Org::getOrgId, Org::getOrgName));
    }

    /**
     * 查询机构详情
     */
    public Org loadOrgIfExist(Long orgId) {
        Optional<Org> orgOpt = this.loadOneOpt(orgId);
        return orgOpt.orElseThrow(() -> new BizRuntimeException("机构信息不存在"));
    }

    /**
     * 查询没有被禁用的机构
     */
    public Org loadOrgIfEnabled(Long orgId) {
        Org org = loadOrgIfExist(orgId);
        if (VAL_STATUS_DISABLED.equals(org.getOrgStatus())) {
            throw new BizRuntimeException("机构已被禁用");
        }
        return org;
    }

    /**
     * 新增机构
     */
    public boolean addOrg(Org org) {
        this.addOrgBefore(org);
        return this.save(org);
    }

    /**
     * 新增机构前置处理
     */
    public void addOrgBefore(Org org) {
        Org parentOrg = this.getById(org.getParentOrgId());
        if (ObjectUtil.isNull(parentOrg)) {
            throw new BizRuntimeException("父级机构不存在");
        }
        if (!StrUtil.equals(VAL_STATUS_ENABLED, parentOrg.getOrgStatus())) {
            throw new BizRuntimeException("父级机构已被禁用");
        }
        if (StrUtil.isBlank(org.getOrgType())) {
            throw new BizRuntimeException("机构类型不能为空");
        }
        if (StrUtil.isBlank(org.getOrgName())) {
            throw new BizRuntimeException("机构名称不能为空");
        }
        String orgAncestors = StrUtil.join(StringPool.COMMA, parentOrg.getOrgAncestors());
        Integer orgLevel = parentOrg.getOrgLevel() + 1;
        org.setOrgAncestors(orgAncestors);
        org.setOrgLevel(orgLevel);
    }

    @SuppressWarnings("all")
    public List<OrgVO> getOrgTreeList(OrgPageDTO dto) {
        QueryWrapper<Org> queryWrapper = QueryWrapperUtil.getQueryWrapper(dto);
        List<OrgVO> orgList = this.loadMore(queryWrapper, OrgVO.class);
        return toTreeList(orgList, OrgVO::getOrgId, OrgVO::getParentOrgId, OrgVO::getOrgSort);
    }
}
