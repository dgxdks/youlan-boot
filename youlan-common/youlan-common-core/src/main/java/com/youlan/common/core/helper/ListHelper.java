package com.youlan.common.core.helper;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.exception.BizRuntimeException;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ListHelper {

    /**
     * 获取元素树列表
     *
     * @param itemList     元素列表
     * @param childrenFunc 获取元素子集的方法
     * @param idFunc       获取元素ID的方法
     * @param pIdFunc      获取元素父ID的方法
     * @return 树列表
     */
    public static <T, S extends Comparable<S>> List<T> getTreeList(Collection<T> itemList, Function<T, List<T>> childrenFunc, Function<T, ?> idFunc, Function<T, ?> pIdFunc) {
        return getTreeList(itemList, childrenFunc, idFunc, pIdFunc, null, false);
    }

    /**
     * 获取元素树列表
     *
     * @param itemList     元素列表
     * @param childrenFunc 获取元素子集的方法
     * @param idFunc       获取元素ID的方法
     * @param pIdFunc      获取元素父ID的方法
     * @param sortFuc      获取元素排序值的方法
     * @return 树列表
     */
    public static <T, S extends Comparable<S>> List<T> getTreeList(Collection<T> itemList, Function<T, List<T>> childrenFunc, Function<T, ?> idFunc, Function<T, ?> pIdFunc, Function<T, S> sortFuc) {
        return getTreeList(itemList, childrenFunc, idFunc, pIdFunc, sortFuc, false);
    }

    /**
     * 获取元素树列表
     *
     * @param itemList     元素列表
     * @param childrenFunc 获取元素子集的方法
     * @param idFunc       获取元素ID的方法
     * @param pIdFunc      获取元素父ID的方法
     * @param sortFuc      获取元素排序值的方法
     * @param isDesc       是否降序
     * @return 树列表
     */
    public static <T, S extends Comparable<S>> List<T> getTreeList(Collection<T> itemList, Function<T, List<T>> childrenFunc, Function<T, ?> idFunc, Function<T, ?> pIdFunc, Function<T, S> sortFuc, boolean isDesc) {
        if (ObjectUtil.isNull(sortFuc)) {
            return getTreeList(itemList, childrenFunc, idFunc, pIdFunc, (Comparator<T>) null);
        }
        Comparator<T> comparator = (leftItem, rightItem) -> {
            S leftComparable = Optional.ofNullable(sortFuc.apply(leftItem)).orElseThrow(() -> new BizRuntimeException("排序元素不能是null"));
            S rightComparable = Optional.ofNullable(sortFuc.apply(rightItem)).orElseThrow(() -> new BizRuntimeException("排序元素不能是null"));
            return leftComparable.compareTo(rightComparable);
        };
        return getTreeList(itemList, childrenFunc, idFunc, pIdFunc, isDesc ? comparator.reversed() : comparator);
    }


    /**
     * 获取元素树列表
     *
     * @param itemList     元素列表
     * @param childrenFunc 获取元素子集的方法
     * @param idFunc       获取元素ID的方法
     * @param pIdFunc      获取元素父ID的方法
     * @param comparator   元素比较器
     * @return 树列表
     */
    public static <T> List<T> getTreeList(Collection<T> itemList, Function<T, List<T>> childrenFunc, Function<T, ?> idFunc, Function<T, ?> pIdFunc, Comparator<T> comparator) {
        List<T> treeList = new ArrayList<>();
        Map<?, T> idGroupMap = itemList.stream()
                .collect(Collectors.toMap(idFunc, item -> item));
        itemList.forEach(item -> {
            Object pId = pIdFunc.apply(item);
            T pItem = idGroupMap.get(pId);
            //通过父ID未找到元素时说明是顶级元素,找到则添加至父级元素的子集中
            if (ObjectUtil.isNull(pItem)) {
                treeList.add(item);
            } else {
                List<T> childrenItems = childrenFunc.apply(pItem);
                if (ObjectUtil.isNull(childrenItems)) {
                    throw new BizRuntimeException("子元素集合不能是null");
                }
                childrenItems.add(item);
                //比较器不为空则需要排序
                if (ObjectUtil.isNotNull(comparator)) {
                    childrenItems.sort(comparator);
                }
            }
        });
        treeList.sort(comparator);
        return treeList;
    }

    /**
     * 过滤集合返回符合条件的新集合
     *
     * @param itemList  元素列表
     * @param predicate 符合条件
     * @return 符合条件的新集合
     */
    public static <T> List<T> filterList(Collection<T> itemList, Predicate<T> predicate) {
        if (CollectionUtil.isEmpty(itemList)) {
            return new ArrayList<>();
        }
        return itemList.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * 映射集合返回新的集合
     *
     * @param itemList 元素列表
     * @param function 映射喊出
     * @return 新的映射集合
     */
    public static <T, V> List<V> mapList(Collection<T> itemList, Function<T, V> function) {
        if (CollectionUtil.isEmpty(itemList)) {
            return new ArrayList<>();
        }
        return itemList.stream()
                .map(function)
                .collect(Collectors.toList());
    }
}
