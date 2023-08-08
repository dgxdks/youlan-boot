package com.youlan.system.service;

import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.DictType;
import com.youlan.system.mapper.DictTypeMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DictTypeService extends BaseServiceImpl<DictTypeMapper, DictType> {

    public DictType loadByTypeKey(String typeKey) {
        return this.loadOne(DictType::getTypeKey, typeKey);
    }
}
