import request from '@/framework/tools/request'

// 支付记录列表
export function getPayRecordPageList(data) {
  return request({
    url: '/pay/payRecord/getPayRecordPageList',
    method: 'post',
    data
  })
}
