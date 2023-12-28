import request from '@/framework/tools/request'

// 支付订单分页
export function getPayOrderPageList(data) {
  return request({
    url: '/pay/payOrder/getPayOrderPageList',
    method: 'post',
    data
  })
}

// 支付订单删除
export function removePayOrder(data) {
  return request({
    url: '/pay/payOrder/removePayOrder',
    method: 'post',
    data
  })
}

// 支付订单同步
export function syncPayOrder(params) {
  return request({
    url: '/pay/payOrder/syncPayOrder',
    method: 'post',
    params
  })
}
