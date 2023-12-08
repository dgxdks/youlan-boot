import request from '@/framework/tools/request'

// 支付订单列表
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
