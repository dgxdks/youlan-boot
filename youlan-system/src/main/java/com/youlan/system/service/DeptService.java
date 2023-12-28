package com.youlan.system.service;

import cn.hutool.core.collection.CollectionUtil;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.Dept;
import com.youlan.system.mapper.DeptMapper;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeptService extends BaseServiceImpl<DeptMapper, Dept> {
    /**
     * 获取部门信息且不为空
     */
    public Dept loadDeptNotNull(Serializable id) {
        return this.loadOneOpt(id)
                .orElseThrow(ApiResultCode.D0009::getException);
    }

    /**
     * 根据部门ID获取机构ID列表
     */
    public List<Long> getOrgIdListByIds(List<Long> deptIds) {
        if (CollectionUtil.isEmpty(deptIds)) {
            return new ArrayList<>();
        }
        return this.lambdaQuery()
                .select(Dept::getOrgId)
                .in(Dept::getId, deptIds)
                .list()
                .stream()
                .map(Dept::getOrgId)
                .collect(Collectors.toList());
    }
}
