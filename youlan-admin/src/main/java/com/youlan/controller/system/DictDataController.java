package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.framework.anno.OperationLog;
import com.youlan.framework.constant.OperationLogType;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.entity.DictData;
import com.youlan.system.service.DictDataService;
import com.youlan.system.service.biz.DictBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

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
    @OperationLog(name = "字典值", type = OperationLogType.OPERATION_LOG_TYPE_ADD)
    public ApiResult addDictData(@Validated @RequestBody DictData dictData) {
        return toSuccess(dictBizService.addDictData(dictData));
    }

    @SaCheckPermission("system:dict:update")
    @Operation(summary = "字典值修改")
    @PostMapping("/updateDictData")
    @OperationLog(name = "字典值", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateDictData(@Validated @RequestBody DictData dictData) {
        if (ObjectUtil.isEmpty(dictData.getId())) {
            return toError(ApiResultCode.C0001);
        }
        return toSuccess(dictBizService.updateDictData(dictData));
    }

    @SaCheckPermission("system:dict:load")
    @Operation(summary = "字典值详情")
    @PostMapping("/loadDictData")
    public ApiResult loadDictData(@RequestParam Long id) {
        return toSuccess(dictDataService.loadOne(id));
    }

    @SaCheckPermission("system:dict:remove")
    @Operation(summary = "字典值删除")
    @PostMapping("/removeDictData")
    @OperationLog(name = "字典值", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removeDictData(@RequestBody ListDTO<Long> ids) {
        dictBizService.removeDictData(ids.getList());
        return toSuccess();
    }

    @SaCheckPermission("system:dict:list")
    @Operation(summary = "字典值分页")
    @PostMapping("/getDictDataPageList")
    @OperationLog(name = "字典值", type = OperationLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getDictDataPageList(@RequestBody DictData dictData) {
        IPage<DictData> pageRes = dictDataService.loadPage(dictData, DBHelper.getQueryWrapper(dictData));
        return toSuccess(pageRes);
    }

    @Operation(summary = "字典值列表(typeKey)")
    @PostMapping("/getDictDataListByTypeKey")
    @OperationLog(name = "字典值", type = OperationLogType.OPERATION_LOG_TYPE_LIST)
    public ApiResult getDictDataListByTypeKey(@RequestParam String typeKey) {
        return toSuccess(dictBizService.getDictCache(typeKey));
    }

    @SaCheckPermission("system:dict:export")
    @Operation(summary = "字典值导出")
    @PostMapping("/exportDictDataList")
    @OperationLog(name = "字典值", type = OperationLogType.OPERATION_LOG_TYPE_EXPORT)
    public void exportDictDataList(@RequestBody DictData dictData, HttpServletResponse response) throws IOException {
        List<DictData> dictDataList = dictDataService.loadMore(DBHelper.getQueryWrapper(dictData));
        toExcel("字典值.xlsx", "字典值", DictData.class, dictDataList, response);
    }
}
