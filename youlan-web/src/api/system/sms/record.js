import request from '@/framework/tools/request'

// 短信记录分页
export function getSmsRecordPageList(data) {
  return request({
    url: '/system/smsRecord/getSmsRecordPageList',
    method: 'post',
    data
  })
}

// 删除短信记录
export function removeSmsRecord(data) {
  return request({
    url: '/system/smsRecord/removeSmsRecord',
    method: 'post',
    data
  })
}

// 清空短信记录
export function cleanSmsRecordList(data) {
  return request({
    url: '/system/smsRecord/cleanSmsRecordList',
    method: 'post',
    data
  })
}
