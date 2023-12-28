import request from '@/framework/tools/request'

// 支付通道分页
export function getPayChannelPageList(data) {
  return request({
    url: '/pay/payChannel/getPayChannelPageList',
    method: 'post',
    data
  })
}

// 支付通道列表
export function getPayChannelList(data) {
  return request({
    url: '/pay/payChannel/getPayChannelList',
    method: 'post',
    data
  })
}

// 支付通道详情
export function loadPayChannel(params) {
  return request({
    url: '/pay/payChannel/loadPayChannel',
    method: 'post',
    params
  })
}

// 支付通道删除
export function removePayChannel(data) {
  return request({
    url: '/pay/payChannel/removePayChannel',
    method: 'post',
    data
  })
}

// 支付通道导出
export function exportPayChannelList(data) {
  return request({
    url: '/pay/payChannel/exportPayChannelList',
    method: 'post',
    data
  })
}

// 支付通道修改
export function updatePayChannel(data) {
  return request({
    url: '/pay/payChannel/updatePayChannel',
    method: 'post',
    data
  })
}

// 支付通道新增
export function addPayChannel(data) {
  return request({
    url: '/pay/payChannel/addPayChannel',
    method: 'post',
    data
  })
}

// 支付通道缓存刷新
export function refreshPayChannelCache(data) {
  return request({
    url: '/pay/payChannel/refreshPayChannelCache',
    method: 'post',
    data
  })
}

// 支付通道状态更新
export function updatePayChannelStatus(params) {
  return request({
    url: '/pay/payChannel/updatePayChannelStatus',
    method: 'post',
    params
  })
}
