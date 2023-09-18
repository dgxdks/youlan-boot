import request from '@/framework/tools/request'

// 账户登录
export function accountLogin(data) {
  return request({
    url: '/system/login/accountLogin',
    method: 'post',
    data
  })
}

// 获取登录信息
export function getLoginInfo() {
  return request({
    url: '/system/login/getLoginInfo',
    method: 'post'
  })
}

// 获取路由
export function getMenuTreeList() {
  return request({
    url: '/system/login/getMenuTreeList',
    method: 'post'
  })
}

// 退出方法
export function logout() {
  return request({
    url: '/system/login/logout',
    method: 'post'
  })
}
