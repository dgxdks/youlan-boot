package com.youlan.system.service;

import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.DictType;
import com.youlan.system.mapper.DictTypeMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
@AllArgsConstructor
public class DictTypeService extends BaseServiceImpl<DictTypeMapper, DictType> {

    /**
     * 根据类型键名获取字典类型且不为空
     */
    public DictType loadDictTypeByTypeKeyNotNull(String typeKey) {
        return this.loadOneOpt(DictType::getTypeKey, typeKey)
                .orElseThrow(ApiResultCode.B0014::getException);
    }

    /**
     * 获取字典类型且不为空
     */
    public DictType loadDictTypeNotNull(Serializable id) {
        return this.loadOneOpt(id)
                .orElseThrow(ApiResultCode.B0014::getException);
    }
}
