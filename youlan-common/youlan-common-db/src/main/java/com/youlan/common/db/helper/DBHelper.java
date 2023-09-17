package com.youlan.common.db.helper;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlan.common.db.anno.Query;
import com.youlan.common.db.constant.MybatisConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.db.entity.dto.PageSortDTO;
import com.youlan.common.db.enums.QueryType;
import com.youlan.common.db.exception.QueryException;
import com.youlan.common.validator.helper.ValidatorHelper;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DBHelper {
    private static final IdentifierGenerator IDENTIFIER_GENERATOR = new DefaultIdentifierGenerator(NetUtil.getLocalhost());
    private static final ConcurrentHashMap<Class<?>, Map<Field, Query>> CACHE_FIELD = new ConcurrentHashMap<>();

    /**
     * 获取分页对象
     *
     * @param pageDTO 分页参数
     * @param <T>     数据库实体
     * @return 分页对象
     */
    public static <T> Page<T> getPage(PageDTO pageDTO) {
        // 不是true就是不需要统计总数
        boolean needTotal = ObjectUtil.equal(pageDTO.getIsNeedTotal(), true);
        return getPage(pageDTO.getPageNum(), pageDTO.getPageSize(), needTotal);
    }

    /**
     * 获取分页对象(为了安全只有指定了排序列的情况下才会在分页对象中注入排序参数)
     *
     * @param pageDTO     分页参数
     * @param sortColumns 排序列
     * @param <T>         数据库实体
     * @return 分页对象
     */
    public static <T> Page<T> getPage(PageDTO pageDTO, final List<String> sortColumns) {
        Page<T> page = getPage(pageDTO);
        List<PageSortDTO> sortList = pageDTO.getSortList();
        if (CollectionUtil.isNotEmpty(sortList) && CollectionUtil.isNotEmpty(sortColumns)) {
            //强制转下划线
            List<String> underlineSortColumns = sortColumns.stream()
                    .map(StrUtil::toUnderlineCase)
                    .collect(Collectors.toList());
            //转排序元素
            List<OrderItem> orderItems = sortList.stream()
                    .map(sort -> {
                        // 强制校验
                        ValidatorHelper.validateWithThrow(sort);
                        OrderItem orderItem = new OrderItem();
                        orderItem.setColumn(StrUtil.toUnderlineCase(sort.getColumn()));
                        orderItem.setAsc(sort.getAsc());
                        return orderItem;
                    })
                    .filter(item -> underlineSortColumns.contains(item.getColumn()))
                    .collect(Collectors.toList());
            page.addOrder(orderItems);
        }
        return page;
    }

    /**
     * 获取分页对象
     *
     * @param pageNum  页码
     * @param pageSize 分页条数
     * @param <T>      数据库实体
     * @return 分页对象
     */
    public static <T> Page<T> getPage(Long pageNum, Long pageSize) {
        return getPage(pageNum, pageSize, true);
    }

    /**
     * 获取分页对象
     *
     * @param pageNum   页码
     * @param pageSize  分页条数
     * @param needTotal 是否需要统计总数
     * @param <T>       数据库实体
     * @return 分页对象
     */
    public static <T> Page<T> getPage(Long pageNum, Long pageSize, boolean needTotal) {
        //如果页码小于1则指定为1
        if (pageNum == null || pageNum < 1) {
            pageNum = 1L;
        }
        //如果页数小于1则指定为默认分页数
        if (pageSize == null || pageSize < 1) {
            pageSize = MybatisConstant.DEFAULT_PAGE_SIZE;
        }
        return new Page<>(pageNum, pageSize, needTotal);
    }

    /**
     * 根据对象中的注解获取QueryWrapper
     *
     * @param query 查询参数
     * @param <T>   数据库实体
     * @return 查询对象
     */
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

    private static void addQueryWrapper(Object query, Field field, Query anno, QueryWrapper<?> queryWrapper) {
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

    /**
     * 随机ID生成器
     */
    public static IdentifierGenerator identifierGenerator() {
        return IDENTIFIER_GENERATOR;
    }
}
