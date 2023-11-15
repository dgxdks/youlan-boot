import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { ArrayUtil, CookieUtil } from '@/framework/tools'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register']

router.beforeEach((to, from, next) => {
  NProgress.start()
  const tokenValue = CookieUtil.getTokenValue()
  if (tokenValue) {
    to.meta.title && store.dispatch('settings/setTitle', to.meta.title)
    /* has token*/
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else {
      if (ArrayUtil.isEmpty(store.getters.roleList)) {
        // 获取用户登录信息
        store.dispatch('GetLoginInfo').then(() => {
          // 获取完成用户登录信息后再判断一次用户角色列表
          if (ArrayUtil.isEmpty(store.getters.roleList)) {
            store.dispatch('Logout').then(() => {
              Message.error('用户角色信息异常')
            })
          } else {
            store.dispatch('GenerateRoutes').then(accessRoutes => {
              // 根据roles权限生成可访问的路由表
              router.addRoutes(accessRoutes) // 动态添加可访问路由表
              next({ ...to, replace: true }) // hack方法 确保addRoutes已完成
            })
          }
        }).catch(err => {
          console.log(err)
          store.dispatch('Logout').then(() => {
            Message.error(err)
            next({ path: '/' })
          })
        })
      } else {
        next()
      }
    }
  } else {
    // 没有token
    if (whiteList.indexOf(to.path) !== -1) {
      // 在免登录白名单，直接进入
      next()
    } else {
      next(`/login?redirect=${to.fullPath}`) // 否则全部重定向到登录页
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
