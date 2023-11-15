import request from '@/framework/tools/request'

// 通知公告新增
export function addNotice(data) {
  return request({
    url: '/system/notice/addNotice',
    method: 'post',
    data
  })
}

// 通知公告修改
export function updateNotice(data) {
  return request({
    url: '/system/notice/updateNotice',
    method: 'post',
    data
  })
}

// 通知公告删除
export function removeNotice(data) {
  return request({
    url: '/system/notice/removeNotice',
    method: 'post',
    data
  })
}

// 通知公告详情
export function loadNotice(params) {
  return request({
    url: '/system/notice/loadNotice',
    method: 'post',
    params
  })
}

// 通知公告分页
export function getNoticePageList(data) {
  return request({
    url: '/system/notice/getNoticePageList',
    method: 'post',
    data
  })
}
