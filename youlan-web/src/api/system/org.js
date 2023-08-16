import request from '@/utils/request'

export function getOrgTreeList(data) {
  return request({
    url: '/system/org/getOrgTreeList',
    method: 'post',
    data
  })
}
