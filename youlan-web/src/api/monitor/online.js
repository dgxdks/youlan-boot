import request from '@/framework/tools/request'

// 在线用户分页
export function getOnlineUserPageList(data) {
  return request({
    url: '/monitor/online/getOnlineUserPageList',
    method: 'post',
    data
  })
}

// 在线强踢
export function kickoutOnlineUser(params) {
  return request({
    url: '/monitor/online/kickoutOnlineUser',
    method: 'post',
    params
  })
}
