import request from '@/framework/tools/request'

export function getOrgTreeList(data) {
  return request({
    url: '/system/org/getOrgTreeList',
    method: 'post',
    data
  })
}
