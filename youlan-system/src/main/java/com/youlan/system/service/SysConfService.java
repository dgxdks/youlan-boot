package com.youlan.system.service;

import com.youlan.common.service.BaseServiceImpl;
import com.youlan.system.entity.SysConf;
import com.youlan.system.mapper.SysConfMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class SysConfService extends BaseServiceImpl<SysConfMapper, SysConf> {
}
