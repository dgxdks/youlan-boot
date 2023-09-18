package com.youlan.system.permission.handler;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaStorage;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.youlan.system.permission.anno.DataPermission;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;

import java.util.Map;

public class SystemDataPermissionHandler implements MultiDataPermissionHandler {

    @Override
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
        System.out.println(table);
        //忽略无效的msId
        try {
            SaStorage saStorage = SaHolder.getStorage();
            System.out.println(saStorage);
        } catch (Exception e) {

        }
        Map<String, Object> beansWithAnnotation = SpringUtil.getApplicationContext().getBeansWithAnnotation(DataPermission.class);
        System.out.println(beansWithAnnotation);
        return null;
    }
}
