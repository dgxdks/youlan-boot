package com.youlan.common.db.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youlan.common.db.anno.Query;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.db.enums.QueryType;
import com.youlan.common.db.exception.QueryException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class QueryWrapperUtil {
    private static final ConcurrentHashMap<Class<?>, Map<Field, Query>> CACHE_FIELD = new ConcurrentHashMap<>();

    public static <T> QueryWrapper<T> getQueryWrapper(PageDTO query) {
        QueryWrapper<T> queryWrapper = getQueryWrapper((Object) query);
        List<String> sortList = query.getSortList();
        boolean isAsc = ObjectUtil.isNotNull(query.getIsAsc()) ? query.getIsAsc() : true;
        queryWrapper.orderBy(CollectionUtil.isNotEmpty(sortList), isAsc, sortList);
        return queryWrapper;
    }

    public static <T> QueryWrapper<T> getQueryWrapper(Object query) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (query == null) {
            return queryWrapper;
        }
        Class<?> queryClass = query.getClass();
        Map<Field, Query> fieldMapping = CACHE_FIELD.computeIfAbsent(queryClass, key -> Arrays.stream(ReflectUtil.getFields(queryClass))
                .filter(field -> ObjectUtil.isNotNull(field.getAnnotation(Query.class)))
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toMap(field -> field, field -> field.getAnnotation(Query.class))));
        fieldMapping.forEach((key, value) -> addQueryWrapper(query, key, value, queryWrapper));
        return queryWrapper;
    }

    public static void addQueryWrapper(Object query, Field field, Query anno, QueryWrapper<?> queryWrapper) {
        try {
            Object queryValue = field.get(query);
            String fieldName = field.getName();
            String[] columnName = anno.column();
            QueryType queryType = anno.type();
            boolean nullEnabled = anno.nullEnabled();
            boolean isNullVal = ObjectUtil.isEmpty(queryValue);
            //如果查询条件不允许是null则当值为空的时候直接返回
            if (!nullEnabled && isNullVal) {
                return;
            }
            //如果未指定查询列则默认使用属性名当做查询列
            List<String> conditionNames = new ArrayList<>();
            if (ArrayUtil.isEmpty(columnName)) {
                conditionNames.add(StrUtil.toUnderlineCase(fieldName));
            } else {
                Arrays.asList(columnName).forEach(name -> conditionNames.add(StrUtil.toUnderlineCase(name)));
            }
            switch (queryType) {
                case EQUAL:
                    queryWrapper.and(wrapper -> conditionNames.forEach(conditionName -> {
                        wrapper.or();
                        if (isNullVal) {
                            wrapper.isNull(conditionName);
                        } else {
                            wrapper.eq(conditionName, queryValue);
                        }
                    }));
                    break;
                case NOT_EQUAL:
                    queryWrapper.and(wrapper -> conditionNames.forEach(conditionName -> {
                        wrapper.or();
                        if (isNullVal) {
                            wrapper.isNotNull(conditionName);
                        } else {
                            wrapper.ne(conditionName, queryValue);
                        }
                    }));
                    break;
                case GREATER_THAN:
                    queryWrapper.and(wrapper -> conditionNames.forEach(conditionName -> {
                        wrapper.or();
                        wrapper.gt(conditionName, queryValue);
                    }));
                    break;
                case GREATER_EQUAL:
                    queryWrapper.and(wrapper -> conditionNames.forEach(conditionName -> {
                        wrapper.or();
                        wrapper.ge(conditionName, queryValue);
                    }));
                    break;
                case LESS_THAN:
                    queryWrapper.and(wrapper -> conditionNames.forEach(conditionName -> {
                        wrapper.or();
                        wrapper.lt(conditionName, queryValue);
                    }));
                    break;
                case LESS_EQUAL:
                    queryWrapper.and(wrapper -> conditionNames.forEach(conditionName -> {
                        wrapper.or();
                        wrapper.le(conditionName, queryValue);
                    }));
                    break;
                case LIKE:
                    queryWrapper.and(wrapper -> conditionNames.forEach(conditionName -> {
                        wrapper.or();
                        wrapper.like(conditionName, queryValue);
                    }));
                    break;
                case LIKE_LEFT:
                    queryWrapper.and(wrapper -> conditionNames.forEach(conditionName -> {
                        wrapper.or();
                        wrapper.likeLeft(conditionName, queryValue);
                    }));
                    break;
                case LIKE_RIGHT:
                    queryWrapper.and(wrapper -> conditionNames.forEach(conditionName -> {
                        wrapper.or();
                        wrapper.likeRight(conditionName, queryValue);
                    }));
                    break;
                case IN:
                    queryWrapper.and(wrapper -> conditionNames.forEach(conditionName -> {
                        wrapper.or();
                        wrapper.in(conditionName, queryValue);
                    }));
                    break;
                case NOT_IN:
                    queryWrapper.and(wrapper -> conditionNames.forEach(conditionName -> {
                        wrapper.or();
                        wrapper.notIn(conditionName, queryValue);
                    }));
                    break;
                case BETWEEN:
                    if (!(queryValue instanceof Collection)) {
                        break;
                    }
                    Collection<?> collection = (Collection<?>) queryValue;
                    if (collection.size() < 2) {
                        break;
                    }
                    Object begin = CollectionUtil.get(collection, 0);
                    Object end = CollectionUtil.get(collection, 1);
                    queryWrapper.and(wrapper -> conditionNames.forEach(conditionName -> {
                        wrapper.or();
                        wrapper.between(conditionName, begin, end);
                    }));

                default:
                    break;
            }
        } catch (IllegalAccessException e) {
            throw new QueryException(e);
        }
    }
}
