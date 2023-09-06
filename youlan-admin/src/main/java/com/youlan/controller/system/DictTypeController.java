package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.entity.DictType;
import com.youlan.system.entity.LoginLog;
import com.youlan.system.service.DictTypeService;
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
@RequestMapping("/system/dictType")
@AllArgsConstructor
public class DictTypeController extends BaseController {
    private final DictBizService dictBizService;
    private final DictTypeService dictTypeService;

    @SaCheckPermission("system:dict:add")
    @Operation(summary = "字典类型新增")
    @PostMapping("/addDictType")
    public ApiResult addDictType(@Validated @RequestBody DictType dictType) {
        dictBizService.addDictType(dictType);
        return toSuccess();
    }

    @SaCheckPermission("system:dict:update")
    @Operation(summary = "字典类型修改")
    @PostMapping("/updateDictType")
    public ApiResult updateDictType(@Validated @RequestBody DictType dictType) {
        if (ObjectUtil.isEmpty(dictType.getId())) {
            return toError(ApiResultCode.C0001);
        }
        dictBizService.updateDictType(dictType);
        return toSuccess();
    }

    @SaCheckPermission("system:dict:load")
    @Operation(summary = "字典类型详情")
    @PostMapping("/loadDictType")
    public ApiResult loadDictType(@RequestParam Long id) {
        return toSuccess(dictTypeService.loadOne(id));
    }

    @SaCheckPermission("system:dict:remove")
    @Operation(summary = "字典类型删除")
    @PostMapping("/removeDictType")
    public ApiResult removeDictType(@RequestBody ListDTO<Long> ids) {
        dictBizService.removeDictType(ids.getList());
        return toSuccess();
    }

    @SaCheckPermission("system:dict:list")
    @Operation(summary = "字典类型分页")
    @PostMapping("/getDictTypePageList")
    public ApiResult getDictTypePageList(@RequestBody DictType dictType) {
        return toSuccess(dictTypeService.loadPage(dictType, DBHelper.getQueryWrapper(dictType)));
    }

    @SaCheckPermission("system:dict:export")
    @Operation(summary = "系统配置导出")
    @PostMapping("/exportDictTypeList")
    public void exportConfigList(@RequestBody DictType dictType, HttpServletResponse response) throws IOException {
        List<DictType> dictTypeList = dictTypeService.loadMore(DBHelper.getQueryWrapper(dictType));
        toExcel("字典类型.xlsx", "字典类型", DictType.class, dictTypeList, response);
    }
}
