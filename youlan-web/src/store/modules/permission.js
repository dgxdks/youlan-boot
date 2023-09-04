import router, { constantRoutes, dynamicRoutes } from '@/router'
import { getMenuTreeList } from '@/api/system/login'
import { ArrayUtil, AuthUtil, EnvUtil, ObjectUtil, StrUtil } from '@/framework/tools'
import Layout from '@/layout'

const permission = {
  state: {
    routes: [],
    addRoutes: [],
    defaultRoutes: [],
    topbarRoutes: [],
    sidebarRoutes: []
  },
  mutations: {
    SET_ROUTES: (state, routes) => {
      state.addRoutes = routes
      state.routes = constantRoutes.concat(routes)
    },
    SET_DEFAULT_ROUTES: (state, routes) => {
      state.defaultRoutes = constantRoutes.concat(routes)
    },
    SET_TOPBAR_ROUTES: (state, routes) => {
      state.topbarRoutes = routes
    },
    SET_SIDEBAR_ROUTERS: (state, routes) => {
      state.sidebarRoutes = routes
    }
  },
  actions: {
    // 生成路由
    GenerateRoutes({ commit }) {
      return new Promise(resolve => {
        // 向后端请求路由数据
        getMenuTreeList().then(res => {
          // 获取菜单路由
          const menuRoutes = menuListConvertToRoutes(res)
          // 获取动态权限路由
          const dynamicPermissionRoutes = getPermissionDynamicRoutes()
          // 设置符合权限的动态权限路由
          router.addRoutes(dynamicPermissionRoutes)
          commit('SET_SIDEBAR_ROUTERS', constantRoutes.concat(menuRoutes))
          commit('SET_DEFAULT_ROUTES', menuRoutes)
          commit('SET_TOPBAR_ROUTES', menuRoutes)
          // 设置最终生成的路由并返回
          const generateRoutes = menuRoutes.concat({ path: '*', redirect: '/404', hidden: true })
          commit('SET_ROUTES', generateRoutes)
          resolve(generateRoutes)
        })
      })
    }
  }
}

// 过滤动态路由配置中符合当前用户角色权限的路由
function getPermissionDynamicRoutes() {
  const dynamicPermissionRoutes = []
  dynamicRoutes.forEach(route => {
    if (route.permissions) {
      if (AuthUtil.hasPermissionOr(route.permissions)) {
        dynamicPermissionRoutes.push(route)
      }
    } else if (route.roles) {
      if (AuthUtil.hasRoleOr(route.roles)) {
        dynamicPermissionRoutes.push(route)
      }
    }
  })
  return dynamicPermissionRoutes
}

// 将菜单信息转为路由信息
function menuListConvertToRoutes(menuList, parentMenu) {
  return menuList.map(menu => {
    const routePath = menu.routePath
    const menuType = menu.menuType
    const visible = menu.visible
    const route = {
      name: StrUtil.upperFirst(menu.routePath),
      path: isDirectoryMenu(menuType) ? StrUtil.paddingSlashLeft(routePath) : StrUtil.removeSlashLeft(routePath),
      query: menu.routeQuery,
      component: Layout,
      alwaysShow: true,
      hidden: needHiddenMenu(visible),
      children: [],
      meta: {
        title: menu.menuName,
        icon: menu.menuIcon,
        noCache: isDirectoryMenu(menuType) || menu.routeCache !== '1'
      }
    }
    // 目录类型不允许面包屑导航处进行点击
    if (isDirectoryMenu(menuType)) {
      route.redirect = 'noRedirect'
    }
    // 如果是菜单类型则需要关闭alwaysShow
    if (isRouteMenu(menuType)) {
      route.alwaysShow = false
    }
    // 如果有父级菜单则生成路由名称时需要与父级组合生成
    if (ObjectUtil.isNotEmpty(parentMenu)) {
      route.name = StrUtil.upperFirst(parentMenu.routePath) + route.name
    }
    // 如果指定了组件路径则加载组件路径
    if (StrUtil.isNotBlank(menu.componentPath)) {
      route.component = loadComponent(menu.componentPath)
    }
    // 如果存在子菜单则递归装换子菜单路由
    if (ArrayUtil.isNotEmpty(menu.children)) {
      route.children = menuListConvertToRoutes(menu.children)
      route.alwaysShow = true
    }
    return route
  })
}

// 是否是目录菜单
function isDirectoryMenu(menuType) {
  return menuType === '1'
}

// 是否是产生路由的菜单
function isRouteMenu(menuType) {
  return menuType === '2'
}

// 菜单是否隐藏
function needHiddenMenu(visible) {
  return visible !== '1'
}

function loadComponent(component) {
  if (EnvUtil.isDevEnv()) {
    return (resolve) => require([`@/views/${component}`], resolve)
  } else {
    // 使用 import 实现生产环境的路由懒加载
    return () => import(`@/views/${component}`)
  }
}

export default permission
