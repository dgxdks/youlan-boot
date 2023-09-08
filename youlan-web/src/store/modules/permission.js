import router, { constantRoutes, dynamicRoutes } from '@/router'
import { getMenuTreeList } from '@/api/system/login'
import { ArrayUtil, AuthUtil, EnvUtil, StrUtil } from '@/framework/tools'
import Layout from '@/layout'
import InnerLink from '@/layout/components/InnerLink/index.vue'
import ParentView from '@/components/ParentView/index.vue'

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
    const menuType = menu.menuType
    const routePath = menu.routePath
    const routeQuery = menu.routeQuery
    const visible = menu.visible
    const route = {
      name: StrUtil.upperFirst(routePath),
      path: isDirectoryMenu(menuType) && !parentMenu ? StrUtil.paddingSlashLeft(routePath) : StrUtil.removeSlashLeft(routePath),
      query: routeQuery,
      // 一级菜单使用Layout
      component: !parentMenu ? Layout : null,
      alwaysShow: true,
      hidden: needHiddenMenu(visible),
      children: [],
      meta: {
        title: menu.menuName,
        icon: menu.menuIcon,
        noCache: isDirectoryMenu(menuType) || menu.routeCache !== '1'
      }
    }
    // 不是菜单类型不允许面包屑导航处进行点击
    if (!isRouteMenu(menuType)) {
      route.redirect = 'noRedirect'
    }
    // 如果是菜单类型将不再有下级菜单则需要关闭alwaysShow
    if (isRouteMenu(menuType)) {
      route.alwaysShow = false
    }
    // 如果指定了组件路径则加载组件路径
    if (StrUtil.isNotBlank(menu.componentPath)) {
      route.component = loadComponent(menu.componentPath)
    }
    // iframe处理逻辑
    if (isFrame(menu.isFrame)) {
      // 如果菜单是菜单类型被指定为iframe则需要走内部链接打开
      if (isRouteMenu(menuType)) {
        let path = routePath.replace(/http:\/\//g, '')
        path = path.replace(/https:\/\//g, '')
        path = path.replace(/www/g, '')
        path = path.replace(/\//g, '')
        route.path = StrUtil.paddingSlashLeft(path)
        route.component = InnerLink
        if (routePath.startsWith('http')) {
          route.meta.link = routePath
        } else {
          route.meta.link = StrUtil.removeSlashRight(EnvUtil.getBaseApi()) + '/' + StrUtil.removeSlashLeft(routePath)
        }
      }
      // 如果是目录类型被指定iframe且没有子路
      if (isDirectoryMenu(menuType) && ArrayUtil.isEmpty(menu.children)) {
        route.alwaysShow = false
      }
    }
    // 如果存在子菜单则递归装换子菜单路由
    if (ArrayUtil.isNotEmpty(menu.children)) {
      // 有父级菜单也有子菜单则当前菜单再加一级 route-view
      if (parentMenu) {
        route.component = ParentView
      }
      route.children = menuListConvertToRoutes(menu.children, menu)
      route.alwaysShow = true
    }
    return route
  })
}

// 是否iframe
function isFrame(isFrame) {
  return isFrame === '1'
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
    return () => Promise.resolve(require(`@/views/${component}`))
  }
}

export default permission
