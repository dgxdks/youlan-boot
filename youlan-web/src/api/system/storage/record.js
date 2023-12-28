// 存储记录分页
import request from '@/framework/tools/request'

export function getStorageRecordPageList(data) {
  return request({
    url: '/system/storageRecord/getStorageRecordPageList',
    method: 'post',
    data
  })
}

// 删除存储记录
export function removeStorageRecord(data) {
  return request({
    url: '/system/storageRecord/removeStorageRecord',
    method: 'post',
    data
  })
}

// 清空存储记录
export function cleanStorageRecordList(data) {
  return request({
    url: '/system/storageRecord/cleanStorageRecordList',
    method: 'post',
    data
  })
}
