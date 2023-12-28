import request from '@/framework/tools/request'

// 支付配置分页
export function getPayConfigPageList(data) {
  return request({
    url: '/pay/payConfig/getPayConfigPageList',
    method: 'post',
    data
  })
}

// 支付配置列表
export function getPayConfigList(data) {
  return request({
    url: '/pay/payConfig/getPayConfigList',
    method: 'post',
    data
  })
}

// 支付配置详情
export function loadPayConfig(params) {
  return request({
    url: '/pay/payConfig/loadPayConfig',
    method: 'post',
    params
  })
}

// 支付配置删除
export function removePayConfig(data) {
  return request({
    url: '/pay/payConfig/removePayConfig',
    method: 'post',
    data
  })
}

// 支付配置导出
export function exportPayConfigList(data) {
  return request({
    url: '/pay/payConfig/exportPayConfigList',
    method: 'post',
    data
  })
}

// 支付配置修改
export function updatePayConfig(data) {
  return request({
    url: '/pay/payConfig/updatePayConfig',
    method: 'post',
    data
  })
}

// 支付配置新增
export function addPayConfig(data) {
  return request({
    url: '/pay/payConfig/addPayConfig',
    method: 'post',
    data
  })
}

// 支付配置缓存刷新
export function refreshPayConfigCache(data) {
  return request({
    url: '/pay/payConfig/refreshPayConfigCache',
    method: 'post',
    data
  })
}

// 支付配置状态更新
export function updatePayConfigStatus(params) {
  return request({
    url: '/pay/payConfig/updatePayConfigStatus',
    method: 'post',
    params
  })
}
