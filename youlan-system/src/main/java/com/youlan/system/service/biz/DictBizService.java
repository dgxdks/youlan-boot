package com.youlan.system.service.biz;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.redis.helper.RedisHelper;
import com.youlan.system.constant.SystemConstant;
import com.youlan.system.entity.DictData;
import com.youlan.system.entity.DictType;
import com.youlan.system.service.DictDataService;
import com.youlan.system.service.DictTypeService;
import com.youlan.system.utils.SystemUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DictBizService {
    private final DictDataService dictDataService;
    private final DictTypeService dictTypeService;
    private final RedisHelper redisHelper;

    /**
     * 初始化缓存诗句
     */
    @PostConstruct
    public void initDictCache() {
        this.setAllDictCache();
    }

    /**
     * 获取字典数据(支持缓存)
     */
    public List<DictData> getDictDataList(String typeKey) {
        List<DictData> dictDataList = redisHelper.get(SystemUtil.getDictRedisKey(typeKey));
        if (CollectionUtil.isNotEmpty(dictDataList)) {
            return dictDataList;
        }
        dictDataList = dictDataService.loadMore(DictData::getTypeKey, typeKey);
        if (CollectionUtil.isNotEmpty(dictDataList)) {
            this.setDictCache(typeKey, dictDataList);
        }
        return dictDataList;
    }

    /**
     * 获取数据字典Map(支持缓存)
     */
    public Map<String, DictData> getDictDataValueMap(String typeKey) {
        List<DictData> dictDataList = getDictDataList(typeKey);
        if (CollectionUtil.isEmpty(dictDataList)) {
            return new HashMap<>();
        }
        return dictDataList.stream()
                .collect(Collectors.toMap(DictData::getDataValue, dictData -> dictData));
    }

    /**
     * 获取数据字典Map(支持缓存)
     */
    public Map<String, DictData> getDictDataNameMap(String typeKey) {
        List<DictData> dictDataList = getDictDataList(typeKey);
        if (CollectionUtil.isEmpty(dictDataList)) {
            return new HashMap<>();
        }
        return dictDataList.stream()
                .collect(Collectors.toMap(DictData::getDataName, dictData -> dictData));
    }


    /**
     * 新增字典值
     */
    public boolean addDictData(DictData dictData) {
        beforeAddOrgUpdateDictData(dictData);
        boolean save = dictDataService.save(dictData);
        if (save) {
            this.removeDictCache(dictData.getTypeKey());
        }
        return save;
    }

    /**
     * 更新字典值
     */
    public boolean updateDictData(DictData dictData) {
        beforeAddOrgUpdateDictData(dictData);
        // 字典类型键名不能修改
        dictData.setTypeKey(null);
        boolean update = dictDataService.updateById(dictData);
        if (update) {
            this.removeDictCache(dictData.getTypeKey());
        }
        return update;
    }

    /**
     * 批量删除字典值
     * 需要删除字典值、对应字典缓存
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeDictData(List<Long> idList) {
        List<String> typeKeyList = dictDataService.lambdaQuery().select(DictData::getTypeKey).in(DictData::getId, idList)
                .list()
                .stream()
                .map(DictData::getTypeKey)
                .collect(Collectors.toList());
        boolean removed = dictDataService.removeBatchByIds(idList);
        if (removed) {
            typeKeyList.forEach(this::removeDictCache);
        }
    }

    /**
     * 新增获取修改字典值前置操作
     */
    public void beforeAddOrgUpdateDictData(DictData dictData) {
        // 必须指定字典类型键名
        DictType dictType = dictTypeService.loadDictTypeByTypeKeyIfExists(dictData.getTypeKey());
        // 新增时字典类型不能被禁用
        if (ObjectUtil.isNull(dictData.getId()) && DBConstant.VAL_STATUS_DISABLED.equals(dictType.getStatus())) {
            throw new BizRuntimeException(ApiResultCode.B0015);
        }
        // 字典值+字典类型键名必须唯一
        String typeKey = dictData.getTypeKey();
        String dataName = dictData.getDataName();
        boolean dictDataExists = dictDataService.lambdaQuery()
                .select(DictData::getId)
                .eq(DictData::getTypeKey, typeKey)
                .eq(DictData::getDataName, dataName)
                .ne(ObjectUtil.isNotNull(dictData.getId()), DictData::getId, dictData.getId())
                .exists();
        if (dictDataExists) {
            throw new BizRuntimeException(StrUtil.format("字典类型下[{}]的字典名称[{}]重复", typeKey, dataName));
        }
    }


    /**
     * 新增字典类型
     */
    public void addDictType(DictType dictType) {
        beforeAddOrgUpdateDictType(dictType);
        dictTypeService.save(dictType);
    }

    /**
     * 修改字典类型
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDictType(DictType dictType) {
        beforeAddOrgUpdateDictType(dictType);
        DictType oldDictType = dictTypeService.loadDictTypeIfExists(dictType.getId());
        // 如果前后typeKey不一致则字典值表也需要同步
        if (ObjectUtil.notEqual(oldDictType.getTypeKey(), dictType.getTypeKey())) {
            // 更新字典值中的typeKey
            dictDataService.lambdaUpdate()
                    .eq(DictData::getTypeKey, oldDictType.getTypeKey())
                    .set(DictData::getTypeKey, dictType.getTypeKey())
                    .update();
        }
        // 更新字典类型
        boolean update = dictTypeService.updateById(dictType);
        // 删除旧缓存
        if (update) {
            this.removeDictCache(oldDictType.getTypeKey());
        }
    }

    /**
     * 删除字典类型
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeDictType(List<Long> ids) {
        List<String> typeKeyList = new ArrayList<>();
        for (Long id : ids) {
            DictType dictType = dictTypeService.loadDictTypeIfExists(id);
            boolean dictTypeExists = dictDataService.lambdaQuery()
                    .eq(DictData::getTypeKey, dictType.getTypeKey())
                    .exists();
            if (dictTypeExists) {
                throw new BizRuntimeException("字典类型绑定字典值时不能删除");
            }
            typeKeyList.add(dictType.getTypeKey());
        }
        boolean removed = dictTypeService.removeBatchByIds(ids);
        if (removed) {
            for (String typeKey : typeKeyList) {
                removeDictCache(typeKey);
            }
        }
    }

    /**
     * 新增或修改字典类型前置操作
     */
    public void beforeAddOrgUpdateDictType(DictType dictType) {
        LambdaQueryWrapper<DictType> typeKeyWrapper = Wrappers.<DictType>lambdaQuery()
                .eq(DictType::getTypeKey, dictType.getTypeKey())
                .ne(ObjectUtil.isNotNull(dictType.getId()), DictType::getId, dictType.getId());
        boolean typeKeyExists = dictTypeService.exists(typeKeyWrapper);
        if (typeKeyExists) {
            throw new BizRuntimeException("字典类型已存在");
        }
    }

    /**
     * 设置所有字典缓存
     */
    public void setAllDictCache() {
        List<DictData> cacheList = dictDataService.getBaseMapper().getCacheList();
        Map<String, List<DictData>> dictDataGroup = cacheList.stream()
                .collect(Collectors.groupingBy(DictData::getTypeKey));
        dictDataGroup.forEach(this::setDictCache);
    }

    /**
     * 设置字典缓存
     */
    public void setDictCache(String typeKey, List<DictData> dictDataList) {
        redisHelper.set(SystemUtil.getDictRedisKey(typeKey), dictDataList);
    }

    /**
     * 删除字典缓存
     */
    public void removeDictCache(String typeKey) {
        redisHelper.delete(SystemUtil.getDictRedisKey(typeKey));
    }

    /**
     * 删除所有字典缓存
     */
    public void removeAllDictCache() {
        redisHelper.deleteByPattern(SystemConstant.REDIS_PREFIX_DICT + StringPool.ASTERISK);
    }

    /**
     * 刷新所有字典缓存
     */
    public void refreshAllDictCache() {
        this.removeAllDictCache();
        this.setAllDictCache();
    }
}
