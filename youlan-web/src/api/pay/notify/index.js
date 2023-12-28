import request from '@/framework/tools/request'

// 支付回调分页
export function getPayNotifyPageList(data) {
  return request({
    url: '/pay/payNotify/getPayNotifyPageList',
    method: 'post',
    data
  })
}

// 支付回调详情
export function loadPayNotify(params) {
  return request({
    url: '/pay/payNotify/loadPayNotify',
    method: 'post',
    params
  })
}

// 支付回调记录分页
export function getPayNotifyRecordPageList(data) {
  return request({
    url: '/pay/payNotify/getPayNotifyRecordPageList',
    method: 'post',
    data
  })
}

// 支付回调详情
export function loadPayNotifyRecord(params) {
  return request({
    url: '/pay/payNotify/loadPayNotifyRecord',
    method: 'post',
    params
  })
}
