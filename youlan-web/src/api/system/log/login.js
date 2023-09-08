import request from '@/framework/tools/request'

// 查询登录日志列表
export function getLoginLogPageList(data) {
  return request({
    url: '/system/loginLog/getLoginLogPageList',
    method: 'post',
    data
  })
}

// 删除登录日志
export function removeLoginLog(data) {
  return request({
    url: '/system/loginLog/removeLoginLog',
    method: 'post',
    data
  })
}

// 解锁用户登录状态
export function unlockLoginUser(params) {
  return request({
    url: '/system/loginLog/unlockLoginUser',
    method: 'post',
    params
  })
}

// 清空登录日志
export function cleanLoginLogList() {
  return request({
    url: '/system/loginLog/cleanLoginLogList',
    method: 'post'
  })
}
