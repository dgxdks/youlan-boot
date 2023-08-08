package com.youlan.tools.service;

import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.tools.entity.DBTableColumn;
import com.youlan.tools.mapper.DBTableColumnMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DBTableColumnService extends BaseServiceImpl<DBTableColumnMapper, DBTableColumn> {
    public List<DBTableColumn> getDbTableColumnList(String tableName) {
        return this.getBaseMapper().getDbTableColumnList(tableName);
    }
}
