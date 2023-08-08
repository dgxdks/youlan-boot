package com.youlan.system.service.biz;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youlan.common.core.db.constant.DBConstant;
import com.youlan.common.core.exception.BizRuntimeException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        dictTypeCheck(dictData.getTypeKey());
        dataNameRepeatCheck(dictData);
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
        dictTypeCheck(dictData.getTypeKey());
        dataNameRepeatCheck(dictData);
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
    public boolean removeDictData(List<Long> idList) {
        List<String> typeKeyList = dictDataService.lambdaQuery().select(DictData::getTypeKey).in(DictData::getId, idList)
                .list()
                .stream()
                .map(DictData::getTypeKey)
                .collect(Collectors.toList());
        dictDataService.removeBatchByIds(idList);
        typeKeyList.forEach(this::removeDictCache);
        return true;
    }

    /**
     * 批量删除字典类型
     * 需要删除对应字典类型、关联字典值、字典缓存
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean removeDictType(List<Long> idList) {
        List<String> typeKeyList = dictTypeService.lambdaQuery().select(DictType::getTypeKey).in(DictType::getId, idList)
                .list()
                .stream()
                .map(DictType::getTypeKey)
                .collect(Collectors.toList());
        dictTypeService.removeBatchByIds(idList);
        for (String typeKey : typeKeyList) {
            dictDataService.remove(Wrappers.<DictData>lambdaQuery().eq(DictData::getTypeKey, typeKey));
            removeDictCache(typeKey);
        }
        return true;
    }

    /**
     * 新增字典类型
     */
    public boolean addDictType(DictType dictType) {
        String typeKey = dictType.getTypeKey();
        if (dictTypeService.loadByTypeKey(typeKey) != null) {
            throw new BizRuntimeException("字典类型键名已存在");
        }
        return dictTypeService.save(dictType);
    }

    /**
     * 修改字典类型
     */
    public boolean updateDictType(DictType dto) {
        String typeKey = dto.getTypeKey();
        DictType dictType = dictTypeService.loadByTypeKey(typeKey);
        if (ObjectUtil.isNotNull(dictType) && Objects.equals(dictType.getId(), dto.getId())) {
            throw new BizRuntimeException("字典类型键名已存在");
        }
        boolean update = dictTypeService.updateById(dictType);
        if (update) {
            this.removeDictCache(typeKey);
        }
        return update;
    }

    /**
     * 字典值名称校验
     */
    public void dataNameRepeatCheck(DictData dictData) {
        String typeKey = dictData.getTypeKey();
        String dataName = dictData.getDataName();
        List<DictData> dictDataList = dictDataService.lambdaQuery()
                .select(DictData::getId)
                .eq(DictData::getTypeKey, typeKey)
                .eq(DictData::getDataName, dataName)
                .list();
        if (CollectionUtil.isEmpty(dictDataList)) {
            return;
        }
        for (DictData retDictData : dictDataList) {
            if (!retDictData.getId().equals(dictData.getId())) {
                throw new BizRuntimeException(StrUtil.format("字典类型下[{}]字典名称[{}]重复", typeKey, dataName));
            }
        }
    }

    /**
     * 字典类型校验
     */
    public void dictTypeCheck(String typeKey) {
        DictType dictType = dictTypeService.loadOneOpt(DictType::getTypeKey, typeKey)
                .orElseThrow(() -> new BizRuntimeException("字典类型不存在"));
        if (DBConstant.VAL_STATUS_DISABLED.equals(dictType.getStatus())) {
            throw new BizRuntimeException("字典类型被禁用");
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
