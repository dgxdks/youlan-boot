// 支付订单列表
import request from '@/framework/tools/request'

export function getPayOrderPageList(data) {
  return request({
    url: '/pay/payOrder/getPayOrderPageList',
    method: 'post',
    data
  })
}
