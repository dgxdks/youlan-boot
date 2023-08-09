package com.youlan.system.org;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.helper.ListHelper;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.Org;
import com.youlan.system.entity.dto.OrgPageDTO;
import com.youlan.system.entity.vo.OrgVO;
import com.youlan.system.service.OrgService;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.youlan.system.constant.SystemConstant.COL_ORG_TYPE;
import static com.youlan.system.constant.SystemConstant.ORG_TYPE_PLATFORM;

/**
 * 拦截器中有些SQL不好重写，故重写部分service方法
 */
public abstract class AbstractOrgModelService<M extends BaseMapper<T>, T extends AbstractOrgModel> extends BaseServiceImpl<M, T> {
    private final OrgService orgService;
    private final TableInfo tableInfo;

    public AbstractOrgModelService(OrgService orgService) {
        this.orgService = orgService;
        this.tableInfo = TableInfoHelper.getTableInfo(getEntityClass());
    }

    /**
     * 查询机构树列表
     */
    public <DTO extends OrgPageDTO, VO extends OrgVO<VO>> List<VO> getOrgTreeList(DTO dto, Class<VO> clz) {
        QueryWrapper<T> queryWrapper = DBHelper.getQueryWrapper(dto);
        queryWrapper.in(COL_ORG_TYPE, ORG_TYPE_PLATFORM, getOrgType());
        List<VO> voList = loadMore(queryWrapper, clz);
        return ListHelper.getTreeList(voList, VO::getChildren, VO::getOrgId, VO::getParentOrgId, VO::getOrgSort);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(T entity) {
        //处理机构表逻辑
        saveOrg(entity);
        T insertEntity = getInsertEntity(entity);
        return SqlHelper.retBool(getBaseMapper().insert(insertEntity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(Collection<T> entityList) {
        //交给重载处理
        return saveBatch(entityList, DEFAULT_BATCH_SIZE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        //处理机构表逻辑
        saveOrgBatch(entityList);
        List<T> insertEntityList = entityList.stream().map(this::getInsertEntity).collect(Collectors.toList());
        String sqlStatement = getSqlStatement(SqlMethod.INSERT_ONE);
        return executeBatch(insertEntityList, batchSize, (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(T entity) {
        //交给重载处理
        if (ObjectUtil.isNull(entity)) {
            Object tableKeyValue = getTableKeyValue(entity);
            return StringUtils.checkValNull(tableKeyValue) || Objects.isNull(getById((Serializable) tableKeyValue)) ? save(entity) : updateById(entity);
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(T entity, Wrapper<T> updateWrapper) {
        //交给重载处理
        return update(entity, updateWrapper) || saveOrUpdate(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateBatch(Collection<T> entityList) {
        //交给重载处理
        return saveOrUpdateBatch(entityList, DEFAULT_BATCH_SIZE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        this.saveOrUpdateOrgBatch(entityList);
        List<T> insertEntityList = entityList.stream().map(this::getInsertEntity).collect(Collectors.toList());
        return SqlHelper.saveOrUpdateBatch(this.entityClass, this.mapperClass, this.log, insertEntityList, batchSize, (sqlSession, entity) -> {
            Object idVal = getTableKeyValue(entity);
            //返回true则会走新增逻辑
            return StringUtils.checkValNull(idVal)
                    || CollectionUtils.isEmpty(sqlSession.selectList(getSqlStatement(SqlMethod.SELECT_BY_ID), entity));
        }, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(getSqlStatement(SqlMethod.UPDATE_BY_ID), param);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        //处理机构表逻辑
        removeOrgById(id);
        this.orgService.getBaseMapper().removeByOrgModelTableId(getTableName(), Collections.singletonList(id));
        return super.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        TableInfo tableInfo = TableInfoHelper.getTableInfo(getEntityClass());
        if (tableInfo.isWithLogicDelete() && tableInfo.isWithUpdateFill()) {
            //交给重载处理
            return removeBatchByIds(list, true);
        }
        //处理机构表逻辑
        removeOrgByIds(list);
        return SqlHelper.retBool(getBaseMapper().deleteBatchIds(list));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id, boolean useFill) {
        //处理机构表逻辑
        removeOrgById(id);
        this.orgService.getBaseMapper().removeByOrgModelTableId(getTableName(), Collections.singletonList(id));
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        if (useFill && tableInfo.isWithLogicDelete()) {
            if (!entityClass.isAssignableFrom(id.getClass())) {
                T instance = tableInfo.newInstance();
                tableInfo.setPropertyValue(instance, tableInfo.getKeyProperty(), id);
                return removeById(instance);
            }
        }
        return SqlHelper.retBool(getBaseMapper().deleteById(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeBatchByIds(Collection<?> list, int batchSize) {
        //交给重载处理
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        return removeBatchByIds(list, batchSize, tableInfo.isWithLogicDelete() && tableInfo.isWithUpdateFill());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeBatchByIds(Collection<?> list, int batchSize, boolean useFill) {
        //处理机构表逻辑
        removeOrgByIds(list);
        String sqlStatement = getSqlStatement(SqlMethod.DELETE_BY_ID);
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        return executeBatch(list, batchSize, (sqlSession, e) -> {
            if (useFill && tableInfo.isWithLogicDelete()) {
                if (entityClass.isAssignableFrom(e.getClass())) {
                    sqlSession.update(sqlStatement, e);
                } else {
                    T instance = tableInfo.newInstance();
                    tableInfo.setPropertyValue(instance, tableInfo.getKeyProperty(), e);
                    sqlSession.update(sqlStatement, instance);
                }
            } else {
                sqlSession.update(sqlStatement, e);
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(T entity) {
        //处理机构表逻辑
        Object tableKeyValue = getTableKeyValue(entity);
        if (ObjectUtil.isNotNull(tableKeyValue)) {
            removeOrgById(tableKeyValue);
        }
        return SqlHelper.retBool(getBaseMapper().deleteById(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByMap(Map<String, Object> columnMap) {
        throw new UnsupportedOperationException("不允许使用此方法");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean remove(Wrapper<T> queryWrapper) {
        //处理机构表逻辑 只查询主键列
        String keyColumn = getKeyColumn();
        if (queryWrapper instanceof QueryWrapper) {
            ((QueryWrapper<T>) queryWrapper).select(keyColumn);
        } else if (queryWrapper instanceof LambdaQueryWrapper) {
            ((LambdaQueryWrapper<T>) queryWrapper).setEntity(getEmptyEntity());
            ((LambdaQueryWrapper<T>) queryWrapper).select(tableFieldInfo -> keyColumn.equals(tableFieldInfo.getColumn()));
        } else {
            throw new UnsupportedOperationException("只允许使用QueryWrapper或LambdaQueryWrapper");
        }
        //查询主键列表并删除
        List<T> orgModelList = this.list(queryWrapper);
        if (CollectionUtil.isNotEmpty(orgModelList)) {
            List<Object> idList = orgModelList.stream().map(this::getTableKeyValue).collect(Collectors.toList());
            removeOrgByIds(idList);
        }
        return SqlHelper.retBool(getBaseMapper().delete(queryWrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection<?> list, boolean useFill) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        if (useFill) {
            //交给重载处理
            return removeBatchByIds(list, true);
        }
        //处理机构表逻辑
        removeOrgByIds(list);
        return SqlHelper.retBool(getBaseMapper().deleteBatchIds(list));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeBatchByIds(Collection<?> list) {
        //交给重载处理
        return removeBatchByIds(list, DEFAULT_BATCH_SIZE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeBatchByIds(Collection<?> list, boolean useFill) {
        //交给重载处理
        return removeBatchByIds(list, DEFAULT_BATCH_SIZE, useFill);
    }

    /**
     * 根据当前表主键ID删除机构表数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeOrgByIds(Collection<?> ids) {
        this.orgService.getBaseMapper().removeByOrgModelTableId(getTableName(), ids);
    }

    /**
     * 根据当前表主键ID删除机构表数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeOrgById(Object id) {
        removeOrgByIds(Collections.singletonList(id));
    }

    /**
     * 保存机构数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrg(T entity) {
        //处理机构表逻辑
        Org org = BeanUtil.copyProperties(entity, Org.class);
        org.setOrgType(entity.orgType());
        boolean save = this.orgService.addOrg(org);
        if (!save) {
            throw new BizRuntimeException("新增机构失败,中断后续操作");
        }
        entity.setOrgId(org.getOrgId());
    }

    /**
     * 批量保存机构数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrgBatch(Collection<T> entityList) {
        processOrgBatch(entityList, this.orgService::saveBatch);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateOrgBatch(Collection<T> entityList) {
        processOrgBatch(entityList, this.orgService::saveOrUpdateBatch);
    }

    @Transactional(rollbackFor = Exception.class)
    public void processOrgBatch(Collection<T> entityList, Predicate<List<Org>> consumer) {
        Map<Org, T> orgEntityMapping = new HashMap<>();
        List<Org> orgList = new ArrayList<>();
        entityList.forEach(entity -> {
            Org org = BeanUtil.copyProperties(entity, Org.class);
            orgList.add(org);
            orgEntityMapping.put(org, entity);
        });
        orgList.forEach(this.orgService::addOrgBefore);
        boolean batchResult = consumer.test(orgList);
        if (!batchResult) {
            throw new BizRuntimeException("新增机构失败,中断后续操作");
        }
        orgList.forEach(org -> {
            T entity = orgEntityMapping.get(org);
            entity.setOrgId(org.getOrgId());
        });
    }

    /**
     * 获取数据库实体空对象
     */
    public T getEmptyEntity() {
        return tableInfo.newInstance();
    }

    /**
     * 当前机构类型
     */
    public String getOrgType() {
        return getEmptyEntity().orgType();
    }

    /**
     * 获取表名称
     */
    public String getTableName() {
        return tableInfo.getTableName();
    }

    /**
     * 获取实体类中主键对应的值
     */
    public Object getTableKeyValue(T entity) {
        return tableInfo.getPropertyValue(entity, tableInfo.getKeyProperty());
    }

    /**
     * 获取当前实体主键列名称
     */
    public String getKeyColumn() {
        return tableInfo.getKeyColumn();
    }

    /**
     * 生成不包含{@link AbstractOrgModel}属性值的对象，即真实要插入当前表的数据
     */
    public T getInsertEntity(T orgModel) {
        T insertEntity = BeanUtil.copyProperties(orgModel, entityClass);
        insertEntity.setOrgName(null);
        insertEntity.setOrgType(null);
        insertEntity.setOrgLevel(null);
        insertEntity.setOrgAncestors(null);
        insertEntity.setParentOrgId(null);
        insertEntity.setOrgSort(null);
        insertEntity.setOrgRemark(null);
        insertEntity.setOrgStatus(null);
        return insertEntity;
    }

    public OrgService getOrgService() {
        return orgService;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }
}
