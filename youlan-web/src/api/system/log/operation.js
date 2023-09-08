import request from '@/framework/tools/request'

// 查询操作日志列表
export function getOperationLogPageList(data) {
  return request({
    url: '/system/operationLog/getOperationLogPageList',
    method: 'post',
    data
  })
}

// 删除操作日志
export function removeOperationLog(data) {
  return request({
    url: '/system/operationLog/removeOperationLog/',
    method: 'post',
    data
  })
}

// 清空操作日志
export function cleanOperationLogList() {
  return request({
    url: '/system/operationLog/cleanOperationLogList',
    method: 'post'
  })
}
