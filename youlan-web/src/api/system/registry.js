import request from '@/framework/tools/request'

// 账户注册
export function accountRegistry(data) {
  return request({
    url: '/system/registry/accountRegistry',
    method: 'post',
    data
  })
}

// 账户开关
export function accountRegistryEnabled() {
  return request({
    url: '/system/registry/accountRegistryEnabled',
    method: 'post'
  })
}
