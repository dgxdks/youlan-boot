package com.youlan.controller.tools;

import com.youlan.common.core.entity.dto.ListDTO;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.db.utils.QueryWrapperUtil;
import com.youlan.framework.controller.BaseController;
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

    @Operation(summary = "数据库表列表")
    @PostMapping("/getDbTableList")
    public ApiResult getDbTableList(@RequestBody DBTable dbTable) {
        return toSuccess(generatorBizService.getDbTableList(dbTable));
    }

    @Operation(summary = "数据库表导入")
    @PostMapping("/importDbTableList")
    public ApiResult importDbTableList(@RequestBody ListDTO<String> tableNames) {
        generatorBizService.importDbTableList(tableNames.getList());
        return toSuccess();
    }

    @Operation(summary = "数据库表同步")
    @PostMapping("/syncDbTable")
    public ApiResult syncDbTable(@RequestParam Long id) {
        return toSuccess(generatorBizService.syncDbTable(id));
    }

    @Operation(summary = "代码下载")
    @PostMapping("/downloadCode")
    public void downloadCode(@RequestBody ListDTO<Long> dto, HttpServletResponse response) throws IOException {
        byte[] codeBytes = generatorBizService.downloadCode(dto.getList());
        toDownload(GeneratorConstant.FILE_NAME_CODE_ZIP, codeBytes, response);
    }

    @Operation(summary = "代码写入")
    @PostMapping("/writeCode")
    public ApiResult writeCode(@Validated @RequestBody ListDTO<Long> dto) throws IOException {
        generatorBizService.writeCode(dto.getList());
        return toSuccess();
    }

    @Operation(summary = "代码预览")
    @PostMapping(value = "viewCode")
    public ApiResult viewCode(@RequestParam Long id) {
        return toSuccess(generatorBizService.viewCode(id));
    }

    @Operation(summary = "生成表详情")
    @PostMapping("/loadTable")
    public ApiResult loadTable(@RequestParam Long id) {
        return toSuccess(generatorBizService.load(id));
    }

    @Operation(summary = "生成表删除")
    @PostMapping("/removeTable")
    public ApiResult removeTable(@Validated @RequestBody ListDTO<Long> dto) {
        generatorBizService.remove(dto.getList());
        return toSuccess();
    }

    @Operation(summary = "生成表修改")
    @PostMapping("/updateTable")
    public ApiResult updateTable(@Validated @RequestBody GeneratorDTO dto) {
        generatorBizService.update(dto);
        return toSuccess();
    }

    @Operation(summary = "生成表分页")
    @PostMapping("/getTablePageList")
    public ApiResult getTablePageList(@RequestBody GeneratorPageDTO dto) {
        return toSuccess(generatorTableService.loadPage(dto, QueryWrapperUtil.getQueryWrapper(dto)));
    }
}
