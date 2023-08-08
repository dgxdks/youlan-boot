package com.youlan.system.service;

import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.DictData;
import com.youlan.system.mapper.DictDataMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DictDataService extends BaseServiceImpl<DictDataMapper, DictData> {

}
