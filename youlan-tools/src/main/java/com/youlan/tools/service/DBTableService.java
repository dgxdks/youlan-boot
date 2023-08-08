package com.youlan.tools.service;

import cn.hutool.core.collection.CollectionUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.tools.entity.DBTable;
import com.youlan.tools.mapper.DBTableMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DBTableService extends BaseServiceImpl<DBTableMapper, DBTable> {
    public DBTable getDbTable(String tableName) {
        DBTable dbTable = CollectionUtil.getFirst(this.getBaseMapper().getList(new DBTable().setTableName(tableName)));
        if (dbTable == null) {
            throw new BizRuntimeException("数据库表不存在");
        }
        return dbTable;
    }
}
