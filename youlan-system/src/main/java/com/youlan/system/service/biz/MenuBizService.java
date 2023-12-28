package com.youlan.system.service.biz;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.system.entity.Menu;
import com.youlan.system.entity.RoleMenu;
import com.youlan.system.service.MenuService;
import com.youlan.system.service.RoleMenuService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class MenuBizService {
    private final MenuService menuService;
    private final RoleMenuService roleMenuService;

    /**
     * 菜单新增
     */
    public boolean addMenu(Menu menu) {
        beforeAddOrUpdateMenu(menu);
        return menuService.save(menu);
    }

    /**
     * 菜单修改
     */
    public boolean updateMenu(Menu menu) {
        beforeAddOrUpdateMenu(menu);
        return menuService.updateById(menu);
    }

    /**
     * 菜单删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeMenu(List<Long> ids) {
        for (Long id : ids) {
            if (ObjectUtil.isNull(id)) {
                continue;
            }
            boolean hasChild = menuService.exists(Wrappers.<Menu>lambdaQuery().eq(Menu::getParentId, id));
            if (hasChild) {
                throw new BizRuntimeException("存在子菜单时不能删除");
            }
            boolean hasRole = roleMenuService.exists(Wrappers.<RoleMenu>lambdaQuery().eq(RoleMenu::getMenuId, id));
            if (hasRole) {
                throw new BizRuntimeException("菜单已绑定角色时不能删除");
            }
        }
        menuService.removeBatchByIds(ids);
    }

    /**
     * 菜单新增或修改的前置操作
     */
    public void beforeAddOrUpdateMenu(Menu menu) {
        checkMenuNameRepeat(menu);
        // 如果权限字符为空则设置为null
        if (StrUtil.isBlank(menu.getMenuPerms())) {
            menu.setMenuPerms(null);
        }
        // 自己不能创建在自己名下
        if (ObjectUtil.isNotNull(menu.getId()) && ObjectUtil.equal(menu.getId(), menu.getParentId())) {
            throw new BizRuntimeException("上级菜单不能选择自己");
        }
        // 如果是frame则路由地址必须是外部连接
        String isFrame = menu.getIsFrame();
        if (DBConstant.yesNo2Boolean(isFrame) && !menu.getRoutePath().startsWith("http")) {
            String routePath = menu.getRoutePath();
            if (!routePath.startsWith("http://") && !routePath.startsWith("https://")) {
                throw new BizRuntimeException("路由地址必须已http(s)://开头");
            }
            if (!Validator.isUrl(routePath)) {
                throw new BizRuntimeException("路由地址必须是合法的外部连接");
            }
        }
    }

    /**
     * 校验菜单名称重复
     */
    public void checkMenuNameRepeat(Menu menu) {
        LambdaQueryWrapper<Menu> queryWrapper = Wrappers.<Menu>lambdaQuery()
                .eq(Menu::getMenuName, menu.getMenuName())
                .eq(Menu::getParentId, menu.getParentId())
                .ne(ObjectUtil.isNotNull(menu.getId()), Menu::getId, menu.getId());
        if (menuService.exists(queryWrapper)) {
            throw new BizRuntimeException("菜单名称已存在");
        }
    }
}
