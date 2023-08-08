package com.youlan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlan.system.entity.DictData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DictDataMapper extends BaseMapper<DictData> {
    List<DictData> getCacheList();
}
