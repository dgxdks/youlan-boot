package com.youlan.system.service;

import com.youlan.system.entity.Dept;
import com.youlan.system.mapper.DeptMapper;
import com.youlan.system.org.service.AbstractOrgModelService;
import org.springframework.stereotype.Service;

@Service
public class DeptService extends AbstractOrgModelService<DeptMapper, Dept> {

    public DeptService(OrgService orgService) {
        super(orgService);
    }
}
