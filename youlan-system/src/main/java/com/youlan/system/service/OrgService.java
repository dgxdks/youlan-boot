package com.youlan.system.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.youlan.common.core.exception.BizRuntimeException;
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

import static com.youlan.common.db.constant.DBConstant.VAL_STATUS_ENABLED;

@Slf4j
@Service
@AllArgsConstructor
public class OrgService extends BaseServiceImpl<OrgMapper, Org> {

    /**
     * 根据机构ID获取机构名称
     */
    public String getOrgNameByOrgId(Long ordId) {
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
        //机构名称不能重复
        this.checkOrgNameRepeat(org.getOrgName());
        String orgAncestors = StrUtil.join(StringPool.COMMA, parentOrg.getOrgAncestors());
        Integer orgLevel = parentOrg.getOrgLevel() + 1;
        org.setOrgAncestors(orgAncestors);
        org.setOrgLevel(orgLevel);
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

    /**
     * 校验机构名称是否重复
     */
    public void checkOrgNameRepeat(String orgName) {
        Org repeatNameOrg = this.loadOne(Org::getOrgName, orgName);
        if (ObjectUtil.isNotNull(repeatNameOrg)) {
            throw new BizRuntimeException("机构名称已存在");
        }
    }

    /**
     * 校验机构名称是否重复
     */
    public void checkOrgNameRepeat(List<String> orgNameList) {
        List<Org> orgList = this.lambdaQuery()
                .select(Org::getOrgName)
                .in(Org::getOrgName, orgNameList)
                .list();
        if (CollectionUtil.isNotEmpty(orgList)) {
            throw new BizRuntimeException("机构名称已存在");
        }
    }
}
