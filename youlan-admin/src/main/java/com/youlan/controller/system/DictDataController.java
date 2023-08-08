package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.common.core.entity.dto.ListDTO;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.utils.QueryWrapperUtil;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.entity.DictData;
import com.youlan.system.service.DictDataService;
import com.youlan.system.service.biz.DictBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "数据字典")
@RestController
@RequestMapping("/system/dictData")
@AllArgsConstructor
public class DictDataController extends BaseController {
    private final DictDataService dictDataService;
    private final DictBizService dictBizService;

    @SaCheckPermission("system:dictData:addDictData")
    @Operation(summary = "字典值新增")
    @PostMapping("/addDictData")
    public ApiResult addDictData(@Validated @RequestBody DictData dictData) {
        return toSuccess(dictBizService.addDictData(dictData));
    }

    @SaCheckPermission("system:dictData:updateDictData")
    @Operation(summary = "字典值修改")
    @PostMapping("/updateDictData")
    public ApiResult updateDictData(@Validated @RequestBody DictData dictData) {
        if (ObjectUtil.isEmpty(dictData.getId())) {
            return toError(ApiResultCode.C0001);
        }
        return toSuccess(dictBizService.updateDictData(dictData));
    }

    @SaCheckPermission("system:dictData:removeDictData")
    @Operation(summary = "字典值删除")
    @PostMapping("/removeDictData")
    public ApiResult removeDictData(@RequestBody ListDTO<Long> ids) {
        return toSuccess(dictBizService.removeDictData(ids.getList()));
    }

    @SaCheckPermission("system:dictData:getDictDataPageList")
    @Operation(summary = "字典值分页")
    @PostMapping("/getDictDataPageList")
    public ApiResult getDictDataPageList(@RequestBody DictData dictData) {
        IPage<DictData> pageRes = dictDataService.loadPage(dictData, QueryWrapperUtil.getQueryWrapper(dictData));
        return toSuccess(pageRes);
    }
}