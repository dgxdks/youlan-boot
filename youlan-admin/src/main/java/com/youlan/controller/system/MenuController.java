package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.helper.ListHelper;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.framework.anno.OperationLog;
import com.youlan.framework.constant.OperationLogType;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.entity.Menu;
import com.youlan.system.service.MenuService;
import com.youlan.system.service.biz.MenuBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "菜单管理")
@RestController
@RequestMapping("/system/menu")
@AllArgsConstructor
public class MenuController extends BaseController {
    private final MenuService menuService;
    private final MenuBizService menuBizService;

    @SaCheckPermission("system:menu:add")
    @Operation(summary = "菜单新增")
    @PostMapping("/addMenu")
    @OperationLog(name = "菜单", type = OperationLogType.OPERATION_LOG_TYPE_ADD)
    public ApiResult addMenu(@Validated @RequestBody Menu menu) {
        return toSuccess(menuBizService.addMenu(menu));
    }

    @SaCheckPermission("system:menu:update")
    @Operation(summary = "菜单修改")
    @PostMapping("/updateMenu")
    @OperationLog(name = "菜单", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateMenu(@Validated @RequestBody Menu menu) {
        if (ObjectUtil.isNull(menu.getId())) {
            return toError(ApiResultCode.C0001);
        }
        return toSuccess(menuBizService.updateMenu(menu));
    }

    @SaCheckPermission("system:menu:remove")
    @Operation(summary = "菜单删除")
    @PostMapping("/removeMenu")
    @OperationLog(name = "菜单", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removeMenu(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        menuBizService.removeMenu(dto.getList());
        return toSuccess();
    }

    @SaCheckPermission("system:menu:load")
    @Operation(summary = "菜单详情")
    @PostMapping("/loadMenu")
    public ApiResult loadMenu(@RequestParam Long id) {
        return toSuccess(menuService.loadOne(id));
    }

    @SaCheckPermission("system:menu:list")
    @Operation(summary = "菜单分页")
    @PostMapping("/getMenuPageList")
    @OperationLog(name = "菜单", type = OperationLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getMenuPageList(@RequestBody Menu menu) {
        return toSuccess(menuService.loadPage(menu, DBHelper.getQueryWrapper(menu)));
    }

    @Operation(summary = "菜单树列表")
    @PostMapping("/getMenuTreeList")
    public ApiResult getMenuTreeList(@RequestBody Menu menu) {
        // 拥有角色编辑、菜单新增、菜单修改权限才能访问此接口
        String[] permissions = {"system:role:update", "system:menu:add", "system:menu:update"};
        StpUtil.checkPermissionOr(permissions);
        List<Menu> menus = menuService.loadMore(DBHelper.getQueryWrapper(menu));
        List<Menu> treeList = ListHelper.getTreeList(menus, Menu::getChildren, Menu::getId, Menu::getParentId, Menu::getSort);
        return toSuccess(treeList);
    }
}
