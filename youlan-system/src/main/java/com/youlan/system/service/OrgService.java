package com.youlan.system.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.helper.ListHelper;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.common.validator.helper.ValidatorHelper;
import com.youlan.system.entity.Org;
import com.youlan.system.entity.dto.OrgDTO;
import com.youlan.system.entity.dto.OrgPageDTO;
import com.youlan.system.entity.vo.OrgVO;
import com.youlan.system.mapper.OrgMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.youlan.common.db.constant.DBConstant.VAL_STATUS_ENABLED;

@Slf4j
@Service
@AllArgsConstructor
public class OrgService extends BaseServiceImpl<OrgMapper, Org> {

    /**
     * 新增机构
     */
    @Transactional(rollbackFor = Exception.class)
    public Org addOrg(OrgDTO dto) {
        beforeAddOrgUpdateOrg(dto);
        Org parentOrg = this.getById(dto.getParentOrgId());
        // 父级机构必须指定且不能被禁用
        Assert.notNull(parentOrg, ApiResultCode.D0004::getException);
        Assert.equals(VAL_STATUS_ENABLED, parentOrg.getOrgStatus(), ApiResultCode.D0005::getException);
        Org org = BeanUtil.copyProperties(dto, Org.class);
        //设置机构层级 父层级+1
        org.setOrgLevel(parentOrg.getOrgLevel() + 1);
        // 生成机构祖级 父祖级+父机构ID
        String orgAncestors = StrUtil.join(StringPool.COMMA, parentOrg.getOrgAncestors(), parentOrg.getOrgId());
        org.setOrgAncestors(orgAncestors);
        this.save(org);
        return org;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateOrg(OrgDTO dto) {
        beforeAddOrgUpdateOrg(dto);
        Org newParentOrg = this.getById(dto.getParentOrgId());
        // 父级机构必须指定
        Assert.notNull(newParentOrg, ApiResultCode.D0004::getException);
        Org oldOrg = this.getById(dto.getOrgId());
        // 当前机构必须存在
        Assert.notNull(oldOrg, ApiResultCode.D0001::getException);
        Org org = BeanUtil.copyProperties(dto, Org.class);
        // 父级机构不能同时是要更新机构的子机构
        if (StrUtil.split(newParentOrg.getOrgAncestors(), StringPool.COMMA).contains(org.getOrgId().toString())) {
            throw new BizRuntimeException("父机构不能同时是当前机构的子机构");
        }
        //如果变更了父级机构子机构也要同步更新
        if (ObjectUtil.notEqual(oldOrg.getParentOrgId(), newParentOrg.getOrgId())) {
            //设置新的祖级
            String newAncestors = newParentOrg.getOrgAncestors() + StringPool.COMMA + newParentOrg.getOrgId();
            org.setOrgAncestors(newAncestors);
            //更新子机构祖级
            this.getBaseMapper().replaceChildOrgAncestors(oldOrg.getOrgId(), oldOrg.getOrgAncestors(), newAncestors);
        }
        this.updateById(org);
    }

    /**
     * 校验机构参数
     */
    public void beforeAddOrgUpdateOrg(OrgDTO org) {
        // 关联性校验(再校验一次避免机构衍生表插进来的数据)
        ValidatorHelper.validateWithThrow(org);
        // 必须指定父机构
        Assert.notNull(org, ApiResultCode.D0004::getException);
        // 机构类型必须指定
        Assert.notBlank(org.getOrgType(), ApiResultCode.D0006::getException);
        // 机构名称必须指定
        Assert.notBlank(org.getOrgName(), ApiResultCode.D0007::getException);
        // 机构名称长度校验
        Assert.checkBetween(org.getOrgName().length(), 1, 30, () -> ApiResultCode.D0007.getException(30));
        // 未指定机构状态则设置
        if (StrUtil.isBlank(org.getOrgStatus())) {
            org.setOrgStatus(VAL_STATUS_ENABLED);
        }
    }

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
     * 获取机构详情且不为空
     */
    public Org loadOrgNotNull(Long orgId) {
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
