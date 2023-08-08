package com.youlan.system.service;

import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.OperationLog;
import com.youlan.system.mapper.OperationLogMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class OperationLogService extends BaseServiceImpl<OperationLogMapper, OperationLog> {

}
