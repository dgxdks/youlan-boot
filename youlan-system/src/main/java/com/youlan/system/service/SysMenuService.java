package com.youlan.system.service;


import com.youlan.common.service.BaseServiceImpl;
import com.youlan.system.entity.SysMenu;
import com.youlan.system.mapper.SysMenuMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class SysMenuService extends BaseServiceImpl<SysMenuMapper, SysMenu> {
}
