package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.entity.DictData;
import com.youlan.system.service.DictDataService;
import com.youlan.system.service.biz.DictBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "数据字典")
@RestController
@RequestMapping("/system/dictData")
@AllArgsConstructor
public class DictDataController extends BaseController {
    private final DictDataService dictDataService;
    private final DictBizService dictBizService;

    @SaCheckPermission("system:dict:add")
    @Operation(summary = "字典值新增")
    @PostMapping("/addDictData")
    public ApiResult addDictData(@Validated @RequestBody DictData dictData) {
        return toSuccess(dictBizService.addDictData(dictData));
    }

    @SaCheckPermission("system:dict:update")
    @Operation(summary = "字典值修改")
    @PostMapping("/updateDictData")
    public ApiResult updateDictData(@Validated @RequestBody DictData dictData) {
        if (ObjectUtil.isEmpty(dictData.getId())) {
            return toError(ApiResultCode.C0001);
        }
        return toSuccess(dictBizService.updateDictData(dictData));
    }

    @SaCheckPermission("system:dict:remove")
    @Operation(summary = "字典值删除")
    @PostMapping("/removeDictData")
    public ApiResult removeDictData(@RequestBody ListDTO<Long> ids) {
        return toSuccess(dictBizService.removeDictData(ids.getList()));
    }

    @SaCheckPermission("system:dict:list")
    @Operation(summary = "字典值分页")
    @PostMapping("/getDictDataPageList")
    public ApiResult getDictDataPageList(@RequestBody DictData dictData) {
        IPage<DictData> pageRes = dictDataService.loadPage(dictData, DBHelper.getQueryWrapper(dictData));
        return toSuccess(pageRes);
    }

    @SaCheckPermission("system:dict:list")
    @Operation(summary = "字典值列表(typeKey)")
    @PostMapping("/getDictDataListByTypeKey")
    public ApiResult getDictDataListByTypeKey(@RequestParam String typeKey) {
        return toSuccess(dictBizService.getDictDataList(typeKey));
    }
}
