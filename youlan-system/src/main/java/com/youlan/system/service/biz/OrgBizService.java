package com.youlan.system.service.biz;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.validator.helper.ValidatorHelper;
import com.youlan.system.entity.Org;
import com.youlan.system.entity.dto.OrgDTO;
import com.youlan.system.service.OrgService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.youlan.common.db.constant.DBConstant.VAL_STATUS_ENABLED;

@Slf4j
@Service
@AllArgsConstructor
public class OrgBizService {
    private final OrgService orgService;

    /**
     * 新增机构
     */
    @Transactional(rollbackFor = Exception.class)
    public Org addOrg(OrgDTO dto) {
        beforeAddOrgUpdateOrg(dto);
        Org parentOrg = orgService.getById(dto.getParentOrgId());
        // 父级机构必须指定且不能被禁用
        Assert.notNull(parentOrg, ApiResultCode.D0004::getException);
        Assert.equals(VAL_STATUS_ENABLED, parentOrg.getOrgStatus(), ApiResultCode.D0005::getException);
        Org org = BeanUtil.copyProperties(dto, Org.class);
        //设置机构层级 父层级+1
        org.setOrgLevel(parentOrg.getOrgLevel() + 1);
        // 生成机构祖级 父祖级+父机构ID
        String orgAncestors = StrUtil.join(StringPool.COMMA, parentOrg.getOrgAncestors(), parentOrg.getOrgId());
        org.setOrgAncestors(orgAncestors);
        orgService.save(org);
        return org;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateOrg(OrgDTO dto) {
        beforeAddOrgUpdateOrg(dto);
        Org newParentOrg = orgService.getById(dto.getParentOrgId());
        // 父级机构必须指定
        Assert.notNull(newParentOrg, ApiResultCode.D0004::getException);
        Org oldOrg = orgService.getById(dto.getOrgId());
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
            orgService.getBaseMapper().replaceChildOrgAncestors(oldOrg.getOrgId(), oldOrg.getOrgAncestors(), newAncestors);
        }
        orgService.updateById(org);
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
}
