package com.youlan.controller.system;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.youlan.common.core.entity.dto.ListDTO;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.utils.QueryWrapperUtil;
import com.youlan.framework.controller.BaseController;
import com.youlan.framework.constant.SystemLogType;
import com.youlan.framework.anno.SystemLog;
import com.youlan.system.entity.Menu;
import com.youlan.system.service.MenuService;
import com.youlan.system.entity.Menu;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Tag(name = "菜单")
@RestController
@RequestMapping("/system/menu")
@AllArgsConstructor
public class MenuController extends BaseController {
    private final MenuService menuService;

    @Operation(summary = "菜单新增")
    @PostMapping("/addMenu")
    @SystemLog(name = "菜单", type = SystemLogType.OPERATION_LOG_TYPE_ADD)
    public ApiResult addMenu(@Validated @RequestBody Menu menu) {
        return toSuccess(menuService.save(menu));
    }

    @Operation(summary = "菜单修改")
    @PostMapping("/updateMenu")
    @SystemLog(name = "菜单", type = SystemLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateMenu(@Validated @RequestBody Menu menu) {
        if (ObjectUtil.isNull(menu.getId())) {
            return toError(ApiResultCode.C0001);
        }
        return toSuccess(menuService.updateById(menu));
    }

    @Operation(summary = "菜单删除")
    @PostMapping("/removeMenu")
    @SystemLog(name = "菜单", type = SystemLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removeMenu(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        return toSuccess(menuService.removeBatchByIds(dto.getList()));
    }

    @Operation(summary = "菜单详情")
    @PostMapping("/loadMenu")
    public ApiResult loadMenu(@RequestParam Long id) {
        return toSuccess(menuService.loadOne(id));
    }

    @Operation(summary = "菜单分页")
    @PostMapping("/getMenuPageList")
    @SystemLog(name = "菜单", type = SystemLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getMenuPageList(@RequestBody Menu menu) {
        return toSuccess(menuService.loadPage(menu, QueryWrapperUtil.getQueryWrapper(menu)));
    }

    @Operation(summary = "菜单列表")
    @PostMapping("/getMenuList")
    @SystemLog(name = "菜单", type = SystemLogType.OPERATION_LOG_TYPE_LIST)
    public ApiResult getMenuList(@RequestBody Menu menu) {
            return toSuccess(menuService.loadMore(QueryWrapperUtil.getQueryWrapper(menu)));
    }

    @Operation(summary = "菜单导出")
    @PostMapping("/exportMenuList")
    @SystemLog(name = "菜单", type = SystemLogType.OPERATION_LOG_TYPE_EXPORT)
    public void exportMenuList(@RequestBody Menu menu, HttpServletResponse response) throws IOException {
            List<Menu> menuList = menuService.loadMore(QueryWrapperUtil.getQueryWrapper(menu));
            toExcel("菜单.xlsx", "菜单", Menu.class, menuList, response);
    }

}
