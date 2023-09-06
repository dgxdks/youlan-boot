package com.youlan.system.service;

import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.Dept;
import com.youlan.system.mapper.DeptMapper;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class DeptService extends BaseServiceImpl<DeptMapper, Dept> {
    /**
     * 如果存在返回部门信息
     */
    public Dept loadDeptIfExists(Serializable id) {
        return this.loadOneOpt(id)
                .orElseThrow(ApiResultCode.D0009::getException);
    }
}
