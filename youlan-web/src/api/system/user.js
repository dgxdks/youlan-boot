import request from '@/framework/tools/request'

// 查询用户列表
export function getUserPageList(data) {
  return request({
    url: '/system/user/getUserPageList',
    method: 'post',
    data
  })
}

// 查询用户详细
export function loadUser(params) {
  return request({
    url: '/system/user/loadUser',
    method: 'post',
    params
  })
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/system/user/addUser',
    method: 'post',
    data: data
  })
}

// 修改用户
export function updateUser(data) {
  return request({
    url: '/system/user/updateUser',
    method: 'post',
    data: data
  })
}

// 删除用户
export function removeUser(data) {
  return request({
    url: '/system/user/removeUser',
    method: 'post',
    data
  })
}

// 用户密码重置
export function resetUserPasswd(data) {
  return request({
    url: '/system/user/resetUserPasswd',
    method: 'post',
    data
  })
}

// 用户状态修改
export function updateUserStatus(params) {
  return request({
    url: '/system/user/updateUserStatus',
    method: 'post',
    params
  })
}

// 查询用户个人信息
export function loadUserProfile() {
  return request({
    url: '/system/user/profile/loadUserProfile',
    method: 'get'
  })
}

// 修改用户个人信息
export function updateUserProfile(data) {
  return request({
    url: '/system/user/profile/updateUserProfile',
    method: 'post',
    data
  })
}

// 修改用户密码
export function updateUserPasswd(data) {
  return request({
    url: '/system/user/profile/updateUserPasswd',
    method: 'post',
    data
  })
}

// 用户头像上传
export function uploadAvatar(data) {
  return request({
    url: '/system/user/profile/avatar',
    method: 'post',
    data: data
  })
}

// 查询授权角色
export function getAuthRole(userId) {
  return request({
    url: '/system/user/authRole/' + userId,
    method: 'get'
  })
}

// 保存授权角色
export function updateAuthRole(data) {
  return request({
    url: '/system/user/authRole',
    method: 'put',
    params: data
  })
}
