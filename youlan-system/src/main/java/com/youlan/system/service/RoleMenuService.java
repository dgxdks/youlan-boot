package com.youlan.system.service;

import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.RoleMenu;
import com.youlan.system.mapper.RoleMenuMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleMenuService extends BaseServiceImpl<RoleMenuMapper, RoleMenu> {

}