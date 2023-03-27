package com.youlan.common.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    public void load(Serializable id, Object target) {
        T data = getById(id);
        if (data != null) {
            BeanUtil.copyProperties(data, target);
        }
    }

    public <VO> VO load(Serializable id, Class<VO> clz) {
        T data = getById(id);
        if (data == null) {
            return null;
        }
        return BeanUtil.copyProperties(data, clz);
    }

    public <VO> VO load(QueryWrapper<T> queryWrapper, Class<VO> clz) {
        return CollectionUtil.getFirst(loads(queryWrapper, clz));
    }

    public <VO> VO load(SFunction<T, ?> sFunction, Object value, Class<VO> clz) {
        return CollectionUtil.getFirst(loads(sFunction, value, clz));
    }

    public <VO> VO load(SFunction<T, ?> sFunction1, Object value1, SFunction<T, ?> sFunction2, Object value2, Class<VO> clz) {
        return CollectionUtil.getFirst(loads(sFunction1, value1, sFunction2, value2, clz));
    }

    public <VO> VO load(SFunction<T, ?> sFunction1, Object value1, SFunction<T, ?> sFunction2, Object value2, SFunction<T, ?> sFunction3, Object value3, Class<VO> clz) {
        return CollectionUtil.getFirst(loads(sFunction1, value1, sFunction2, value2, sFunction3, value3, clz));
    }

    public <VO> List<VO> loads(QueryWrapper<T> queryWrapper, Class<VO> clz) {
        List<T> dataList = this.list(queryWrapper);
        if (CollectionUtil.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        return BeanUtil.copyToList(dataList, clz);
    }

    public <VO> List<VO> loads(SFunction<T, ?> sFunction, Object value, Class<VO> clz) {
        List<T> dataList = this.lambdaQuery()
                .eq(sFunction, value)
                .list();
        if (CollectionUtil.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        return BeanUtil.copyToList(dataList, clz);
    }

    public <VO> List<VO> loads(SFunction<T, ?> sFunction1, Object value1, SFunction<T, ?> sFunction2, Object value2, Class<VO> clz) {
        List<T> dataList = this.lambdaQuery()
                .eq(sFunction1, value1)
                .eq(sFunction2, value2)
                .list();
        if (CollectionUtil.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        return BeanUtil.copyToList(dataList, clz);
    }

    public <VO> List<VO> loads(SFunction<T, ?> sFunction1, Object value1, SFunction<T, ?> sFunction2, Object value2, SFunction<T, ?> sFunction3, Object value3, Class<VO> clz) {
        List<T> dataList = this.lambdaQuery()
                .eq(sFunction1, value1)
                .eq(sFunction2, value2)
                .eq(sFunction3, value3)
                .list();
        if (CollectionUtil.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        return BeanUtil.copyToList(dataList, clz);
    }
}
