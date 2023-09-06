import request from '@/framework/tools/request'

// 查询岗位列表
export function getPostPageList(data) {
  return request({
    url: '/system/post/getPostPageList',
    method: 'post',
    data
  })
}

// 查询岗位列表
export function getPostList(data) {
  return request({
    url: '/system/post/getPostList',
    method: 'post',
    data
  })
}

// 查询岗位详细
export function loadPost(params) {
  return request({
    url: '/system/post/loadPost',
    method: 'post',
    params
  })
}

// 新增岗位
export function addPost(data) {
  return request({
    url: '/system/post/addPost',
    method: 'post',
    data
  })
}

// 修改岗位
export function updatePost(data) {
  return request({
    url: '/system/post/updatePost',
    method: 'post',
    data
  })
}

// 删除岗位
export function removePost(data) {
  return request({
    url: '/system/post/removePost',
    method: 'post',
    data
  })
}
