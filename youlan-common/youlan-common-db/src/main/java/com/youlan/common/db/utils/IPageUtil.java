package com.youlan.common.db.utils;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlan.common.db.constant.MybatisConstant;
import com.youlan.common.db.entity.dto.PageDTO;

public class IPageUtil {

    public static <T> IPage<T> getIPage(PageDTO pageDTO) {
        boolean needTotal = ObjectUtil.isNotNull(pageDTO.getNeedTotal()) ? pageDTO.getNeedTotal() : true;
        return getIPage(pageDTO.getPageNum(), pageDTO.getPageSize(), needTotal);
    }

    public static <T> IPage<T> getIPage(Long pageNum, Long pageSize) {
        return getIPage(pageNum, pageSize, true);
    }

    public static <T> IPage<T> getIPage(Long pageNum, Long pageSize, boolean needTotal) {
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
}
