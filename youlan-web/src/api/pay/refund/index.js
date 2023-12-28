import request from '@/framework/tools/request'

// 退款订单创建
export function createPayRefundOrder(data) {
  return request({
    url: '/pay/payRefundOrder/createPayRefundOrder',
    method: 'post',
    data
  })
}

// 退款订单分页
export function getPayRefundOrderPageList(data) {
  return request({
    url: '/pay/payRefundOrder/getPayRefundOrderPageList',
    method: 'post',
    data
  })
}

// 退款订单删除
export function removePayRefundOrder(data) {
  return request({
    url: '/pay/payRefundOrder/removePayRefundOrder',
    method: 'post',
    data
  })
}

// 退款订单同步
export function syncPayRefundOrder(params) {
  return request({
    url: '/pay/payRefundOrder/syncPayRefundOrder',
    method: 'post',
    params
  })
}
