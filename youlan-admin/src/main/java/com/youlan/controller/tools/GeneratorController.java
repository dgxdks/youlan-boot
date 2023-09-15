package com.youlan.controller.tools;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.controller.base.BaseController;
import com.youlan.tools.constant.GeneratorConstant;
import com.youlan.tools.entity.DBTable;
import com.youlan.tools.entity.dto.GeneratorDTO;
import com.youlan.tools.entity.dto.GeneratorPageDTO;
import com.youlan.tools.service.GeneratorTableService;
import com.youlan.tools.service.biz.GeneratorBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Tag(name = "代码生成")
@RestController
@RequestMapping("/tools/generator")
@AllArgsConstructor
public class GeneratorController extends BaseController {
    private final GeneratorBizService generatorBizService;
    private final GeneratorTableService generatorTableService;

    @SaCheckPermission("tools:generator:list")
    @Operation(summary = "数据库表分页")
    @PostMapping("/getDbTablePageList")
    public ApiResult getDbTablePageList(@RequestBody DBTable dbTable) {
        return toSuccess(generatorBizService.getDbTablePageList(dbTable));
    }

    @SaCheckPermission("tools:generator:update")
    @Operation(summary = "数据库表导入")
    @PostMapping("/importDbTableList")
    public ApiResult importDbTableList(@RequestBody ListDTO<String> tableNames) {
        generatorBizService.importDbTableList(tableNames.getList());
        return toSuccess();
    }

    @SaCheckPermission("tools:generator:update")
    @Operation(summary = "数据库表同步")
    @PostMapping("/syncDbTable")
    public ApiResult syncDbTable(@RequestParam Long id) {
        return toSuccess(generatorBizService.syncDbTable(id));
    }

    @SaCheckPermission("tools:generator:code")
    @Operation(summary = "代码下载")
    @PostMapping("/downloadCode")
    public void downloadCode(@RequestBody ListDTO<Long> dto, HttpServletResponse response) throws IOException {
        byte[] codeBytes = generatorBizService.downloadCode(dto.getList());
        toDownload(GeneratorConstant.FILE_NAME_CODE_ZIP, codeBytes, response);
    }

    @SaCheckPermission("tools:generator:code")
    @Operation(summary = "代码写入")
    @PostMapping("/writeCode")
    public ApiResult writeCode(@Validated @RequestBody ListDTO<Long> dto) throws IOException {
        generatorBizService.writeCode(dto.getList());
        return toSuccess();
    }

    @SaCheckPermission("tools:generator:preview")
    @Operation(summary = "代码预览")
    @PostMapping(value = "previewCode")
    public ApiResult viewCode(@RequestParam Long id) {
        return toSuccess(generatorBizService.viewCode(id));
    }

    @SaCheckPermission("tools:generator:load")
    @Operation(summary = "生成表详情")
    @PostMapping("/loadTable")
    public ApiResult loadTable(@RequestParam Long id) {
        return toSuccess(generatorBizService.load(id));
    }

    @SaCheckPermission("tools:generator:remove")
    @Operation(summary = "生成表删除")
    @PostMapping("/removeTable")
    public ApiResult removeTable(@Validated @RequestBody ListDTO<Long> dto) {
        generatorBizService.remove(dto.getList());
        return toSuccess();
    }

    @SaCheckPermission("tools:generator:update")
    @Operation(summary = "生成表修改")
    @PostMapping("/updateTable")
    public ApiResult updateTable(@Validated @RequestBody GeneratorDTO dto) {
        generatorBizService.update(dto);
        return toSuccess();
    }

    @SaCheckPermission("tools:generator:list")
    @Operation(summary = "生成表分页")
    @PostMapping("/getTablePageList")
    public ApiResult getTablePageList(@RequestBody GeneratorPageDTO dto) {
        return toSuccess(generatorTableService.loadPage(dto, DBHelper.getQueryWrapper(dto)));
    }
}
