package com.youlan.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.system.permission.handler.SystemDataPermissionHandler;
import com.youlan.system.helper.SystemAuthHelper;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class MybatisConfig {
    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        //数据权限插件
        // ********* 必须放在分页插件前面，不然分页插件如果先执行count且返回是0会中断后面真实的查询逻辑 *********
        interceptor.addInnerInterceptor(new DataPermissionInterceptor(new SystemDataPermissionHandler()));

        // 分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();

        // 设置数据库类型为mysql
        paginationInnerInterceptor.setDbType(DbType.MYSQL);

        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInnerInterceptor.setMaxLimit(-1L);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        //防全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean(MetaObjectHandler.class)
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.setFieldValByName(DBConstant.COL_CREATE_ID_CAMEL, SystemAuthHelper.getUserId(), metaObject);
                this.setFieldValByName(DBConstant.COL_CREATE_BY_CAMEL, SystemAuthHelper.getUserName(), metaObject);
                this.setFieldValByName(DBConstant.COL_CREATE_TIME_CAMEL, new Date(), metaObject);
                this.setFieldValByName(DBConstant.COL_STATUS, DBConstant.VAL_STATUS_ENABLED, metaObject);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.setFieldValByName(DBConstant.COL_UPDATE_ID_CAMEL, SystemAuthHelper.getUserId(), metaObject);
                this.setFieldValByName(DBConstant.COL_UPDATE_BY_CAMEL, SystemAuthHelper.getUserName(), metaObject);
                this.setFieldValByName(DBConstant.COL_UPDATE_TIME_CAMEL, new Date(), metaObject);
            }
        };
    }
}
