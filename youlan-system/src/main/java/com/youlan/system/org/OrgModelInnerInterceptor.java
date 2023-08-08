package com.youlan.system.org;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.youlan.common.core.exception.BizRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.update.UpdateSet;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.SINGLE_QUOTE;
import static com.youlan.common.core.db.constant.DBConstant.VAL_STS_NO;
import static com.youlan.common.core.db.constant.DBConstant.VAL_STS_YES;
import static com.youlan.system.constant.SystemConstant.*;

@Slf4j
public class OrgModelInnerInterceptor implements InnerInterceptor {
    private final ConcurrentHashMap<String, Optional<AbstractOrgModel>> entityClassCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> orgColumnNameMapping = new ConcurrentHashMap<>();
    private final Column orgStsEqualToLeftColumn = new Column(ALIAS_ORG_TABLE + StringPool.DOT + COL_ORG_STS);
    private final Column orgStsEqualToRightColumn = new Column(SINGLE_QUOTE + VAL_STS_NO + SINGLE_QUOTE);
    private final Column leftJoinOnLeftColumn = new Column(ALIAS_ENTITY_TABLE + StringPool.DOT + COL_ORG_ID);
    private final Column leftJoinOnRightColumn = new Column(ALIAS_ORG_TABLE + StringPool.DOT + COL_ORG_ID);
    private final EqualsTo orgStsEqualTo = new EqualsTo(orgStsEqualToLeftColumn, orgStsEqualToRightColumn);

    public OrgModelInnerInterceptor() {
        orgColumnNameMapping.put(COL_ORG_NAME, ALIAS_ORG_TABLE + StringPool.DOT + COL_ORG_NAME);
        orgColumnNameMapping.put(COL_ORG_STS, ALIAS_ORG_TABLE + StringPool.DOT + COL_ORG_STS);
        orgColumnNameMapping.put(COL_ORG_TYPE, ALIAS_ORG_TABLE + StringPool.DOT + COL_ORG_TYPE);
        orgColumnNameMapping.put(COL_ORG_LEVEL, ALIAS_ORG_TABLE + StringPool.DOT + COL_ORG_LEVEL);
        orgColumnNameMapping.put(COL_ORG_ANCESTORS, ALIAS_ORG_TABLE + StringPool.DOT + COL_ORG_ANCESTORS);
        orgColumnNameMapping.put(COL_PARENT_ORG_ID, ALIAS_ORG_TABLE + StringPool.DOT + COL_PARENT_ORG_ID);
        orgColumnNameMapping.put(COL_ORG_SORT, ALIAS_ORG_TABLE + StringPool.DOT + COL_ORG_SORT);
        orgColumnNameMapping.put(COL_ORG_REMARK, ALIAS_ORG_TABLE + StringPool.DOT + COL_ORG_REMARK);
        orgColumnNameMapping.put(COL_ORG_STATUS, ALIAS_ORG_TABLE + StringPool.DOT + COL_ORG_STATUS);
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();
        PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
        Object parameter = mpSh.parameterHandler().getParameterObject();
        //只处理新增 查询 修改
        if (sct != SqlCommandType.SELECT && sct != SqlCommandType.UPDATE && sct != SqlCommandType.DELETE) {
            return;
        }
        //不是机构模型不处理
        Optional<AbstractOrgModel> orgModelOpt = orgModelClass(ms.getId());
        if (orgModelOpt.isEmpty()) {
            return;
        }
        Statement statement;
        try {
            statement = CCJSqlParserUtil.parse(mpBs.sql());
        } catch (JSQLParserException e) {
            throw new BizRuntimeException(e);
        }
        //无法解析statement不处理
        if (ObjectUtil.isNull(statement)) {
            return;
        }
        //如果是map类型参数但是不包含下面参数则直接返回，因为sql来源于mapper.xml
        if (parameter instanceof Map) {
            Map parameterMap = (Map) parameter;
            if (!parameterMap.containsKey(Constants.ENTITY) && !parameterMap.containsKey(Constants.WRAPPER)) {
                return;
            }
        }
        AbstractOrgModel orgModel = orgModelOpt.get();
        if (statement instanceof Select) {
            processSelect((Select) statement, orgModel);
            mpBs.sql(statement.toString());
        } else if (statement instanceof Update) {
            processLogicDelete((Update) statement, orgModel.getClass());
            processUpdate((Update) statement, orgModel);
            mpBs.sql(statement.toString());
        } else if (statement instanceof Delete) {
            processDelete((Delete) statement, orgModel);
            mpBs.sql(statement.toString());
        }
    }

    /**
     * 处理查询语句
     */
    public void processSelect(Select select, AbstractOrgModel orgModel) {
        SelectBody selectBody = select.getSelectBody();
        if (!(selectBody instanceof PlainSelect)) {
            return;
        }
        PlainSelect plainSelect = (PlainSelect) selectBody;
        //处理查询列
        processSelectItems(plainSelect.getSelectItems());
        //处理from语句
        plainSelect.getFromItem().setAlias(new Alias(ALIAS_ENTITY_TABLE, true));
        //处理左关联语句
        plainSelect.addJoins(getLeftJoin());
        //处理where语句
        processWhere(plainSelect.getWhere());
        //增加机构表逻辑删除条件
        plainSelect.setWhere(new AndExpression(plainSelect.getWhere(), orgStsEqualTo));
    }

    /**
     * 处理更新语句
     */
    public void processUpdate(Update update, AbstractOrgModel orgModel) {
        update.getTable().setAlias(new Alias(ALIAS_ENTITY_TABLE, true));
        update.setStartJoins(Collections.singletonList(getLeftJoin()));
        processUpdateSets(update.getUpdateSets());
        processWhere(update.getWhere());
    }

    /**
     * 处理逻辑删除逻辑
     * 1.逻辑删除时会走update语句所以需要处理</br>
     * 2.如果上层通过update方法主动设置逻辑删除字段的值则此处不会主动处理机构表中的逻辑删除字段</br>
     */
    public void processLogicDelete(Update update, Class<?> entityClass) {
        //处理逻辑删除，因为逻辑删除时也走update，删除机构衍生表数据的话也要删除机构表对应数据
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        //如果没有逻辑删除字段则不处理
        if (!tableInfo.isWithLogicDelete()) {
            return;
        }
        TableFieldInfo logicDeleteFieldInfo = tableInfo.getLogicDeleteFieldInfo();
        //获取当前表逻辑删除对应的值
        String logicDeleteValue = logicDeleteFieldInfo.getLogicDeleteValue();
        ArrayList<UpdateSet> updateSets = update.getUpdateSets();
        //如果set语句中包含逻辑删除字段且值等于标记删除值则机构表中对应数据也要删除
        AtomicBoolean isLogicDelete = new AtomicBoolean(false);
        AtomicBoolean isStrLogicColumn = new AtomicBoolean(false);
        updateSets.forEach(updateSet -> {
            ArrayList<Column> columns = updateSet.getColumns();
            ArrayList<Expression> expressions = updateSet.getExpressions();
            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                String columnName = column.getColumnName();
                if (ObjectUtil.notEqual(columnName, logicDeleteFieldInfo.getColumn())) {
                    continue;
                }
                Expression expression = expressions.get(i);
                //需要处理不同列类型下的值 set name = 'value', 如果是数值类型则是 set name = value
                String columnValue = expression.toString();
                //字符类型逻辑删除列
                if (columnValue.contains("'") && StrUtil.equals(columnValue, SINGLE_QUOTE + logicDeleteValue + SINGLE_QUOTE)) {
                    isLogicDelete.set(true);
                    isStrLogicColumn.set(true);
                }
                //非字符类型逻辑删除列
                if (StrUtil.equals(columnValue, logicDeleteValue)) {
                    isLogicDelete.set(true);
                }
            }
        });
        UpdateSet logicUpdateSet = new UpdateSet();
        //存在逻辑删除则处理
        if (isLogicDelete.get()) {
            if (isStrLogicColumn.get()) {
                logicUpdateSet.add(new Column(COL_ORG_STS), new StringValue(VAL_STS_YES));
            } else {
                logicUpdateSet.add(new Column(COL_ORG_STS), new LongValue(VAL_STS_YES));
            }
            update.addUpdateSet(logicUpdateSet);
        }
    }

    /**
     * 处理删除语句
     */
    public void processDelete(Delete delete, AbstractOrgModel orgModel) {
        delete.getTable().setAlias(new Alias(ALIAS_ENTITY_TABLE, true));
        delete.addTables(new Table(ALIAS_ENTITY_TABLE));
        delete.addJoins(Collections.singletonList(getLeftJoin()));
        processWhere(delete.getWhere());
    }

    /**
     * 处理set语句
     */
    public void processUpdateSets(List<UpdateSet> updateSets) {
        updateSets.forEach(updateSet -> {
            updateSet.getColumns().forEach(this::processWhere);
        });
    }

    /**
     * 处理where语句
     */
    public void processWhere(Expression expression) {
        if (ObjectUtil.isNull(expression)) {
            return;
        }
        expression.accept(new ExpressionVisitorAdapter() {
            @Override
            public void visit(Column column) {
                processExpression(column);
            }
        });
    }

    /**
     * 获取左关联语句
     */
    public Join getLeftJoin() {
        Join join = new Join();
        join.setLeft(true);
        //指定右表信息
        FromItem rightItem = new Table(ORG_TABLE);
        rightItem.setAlias(new Alias(ALIAS_ORG_TABLE, true));
        join.setRightItem(rightItem);
        //设置on条件
        EqualsTo equalsTo = new EqualsTo(leftJoinOnLeftColumn, leftJoinOnRightColumn);
        join.setOnExpressions(List.of(equalsTo));
        return join;
    }

    /**
     * 处理查询列
     */
    public void processSelectItems(List<SelectItem> selectItems) {
        for (SelectItem selectItem : selectItems) {
            selectItem.accept(new SelectItemVisitorAdapter() {
                @Override
                public void visit(AllColumns allColumns) {
                }

                @Override
                public void visit(SelectExpressionItem selectExpressionItem) {
                    Expression expression = selectExpressionItem.getExpression();
                    processExpression(expression);
                }
            });
        }
    }

    /**
     * 处理列表达式
     */
    public void processExpression(Expression expression) {
        if (expression instanceof Column) {
            Column column = (Column) expression;
            String columnName = column.getColumnName();
            column.setColumnName(orgColumnNameMapping.getOrDefault(columnName, ALIAS_ENTITY_TABLE + StringPool.DOT + columnName));
        }
    }

    /**
     * 获取实体类
     */
    public Optional<AbstractOrgModel> orgModelClass(String msId) {
        final String className = msId.substring(0, msId.lastIndexOf('.'));
        return entityClassCache.computeIfAbsent(msId, key -> {
            try {
                Class<?> targetClass = ReflectionKit.getSuperClassGenericType(Class.forName(className), Mapper.class, 0);
                Object targetObject = ClassUtils.newInstance(targetClass);
                //如果数据模型不继承此类型则不进行SQL注入
                if (targetObject instanceof AbstractOrgModel) {
                    return Optional.of((AbstractOrgModel) targetObject);
                } else {
                    return Optional.empty();
                }
            } catch (ClassNotFoundException e) {
                throw new BizRuntimeException(e);
            }
        });
    }
}
