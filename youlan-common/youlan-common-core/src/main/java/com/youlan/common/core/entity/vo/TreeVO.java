package com.youlan.common.core.entity.vo;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class TreeVO<T> {
    @Schema(title = "数据子集列表")
    private List<T> children = new ArrayList<>();

    public boolean hasChildren() {
        return CollectionUtil.isNotEmpty(children);
    }

    public boolean noChildren() {
        return CollectionUtil.isEmpty(children);
    }

    public void sortChildren(Function<T, Integer> sortFuc) {
        sortChildren(sortFuc, false);
    }

    public void sortChildren(Function<T, Integer> sortFuc, boolean desc) {
        children.sort((item1, item2) -> {
            Integer sort1 = Optional.ofNullable(sortFuc.apply(item1)).orElse(0);
            Integer sort2 = Optional.ofNullable(sortFuc.apply(item2)).orElse(0);
            return desc ? sort2 - sort1 : sort1 - sort2;
        });
    }

    public static <VO extends TreeVO<VO>> List<VO> getTreeList(List<VO> itemList, Function<VO, ?> idFuc, Function<VO, ?> pIdFuc, Function<VO, Integer> sortFuc) {
        List<VO> treeList = new ArrayList<>();
        Map<?, VO> idGroupMap = itemList.stream().collect(Collectors.toMap(idFuc, item -> item));
        itemList.forEach(item -> {
            Object pId = pIdFuc.apply(item);
            VO pItem = idGroupMap.get(pId);
            //通过父ID未找到元素时说明是顶级元素，找到则添加至父级元素的子集中
            if (ObjectUtil.isNull(pItem)) {
                treeList.add(item);
            } else {
                pItem.getChildren().add(item);
            }
            if (ObjectUtil.isNotNull(sortFuc)) {
                pItem.sortChildren(sortFuc);
            }
        });
        return treeList;
    }

    public static <VO extends TreeVO<VO>> List<VO> getTreeList(List<VO> itemList, Function<VO, ?> idFuc, Function<VO, ?> pIdFuc) {
        return getTreeList(itemList, idFuc, pIdFuc, null);
    }
}
