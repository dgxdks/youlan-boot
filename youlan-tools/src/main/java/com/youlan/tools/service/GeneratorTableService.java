package com.youlan.tools.service;

import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.tools.entity.GeneratorTable;
import com.youlan.tools.mapper.GeneratorTableMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GeneratorTableService extends BaseServiceImpl<GeneratorTableMapper, GeneratorTable> {

}
