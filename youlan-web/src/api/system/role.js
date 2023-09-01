import request from '@/framework/tools/request'

// 查询角色列表
export function getRolePageList(data) {
  return request({
    url: '/system/role/getRolePageList',
    method: 'post',
    data
  })
}

export function getRoleList(data) {
  return request({
    url: '/system/role/getRoleList',
    method: 'post',
    data
  })
}

// 查询角色详细
export function loadRole(params) {
  return request({
    url: '/system/role/loadRole',
    method: 'post',
    params
  })
}

// 新增角色
export function addRole(data) {
  return request({
    url: '/system/role/addRole',
    method: 'post',
    data
  })
}

// 修改角色
export function updateRole(data) {
  return request({
    url: '/system/role/updateRole',
    method: 'post',
    data
  })
}

// 修改角色数据权限
export function updateRoleScope(data) {
  return request({
    url: '/system/role/updateRoleScope',
    method: 'post',
    data
  })
}

// 角色状态修改
export function updateRoleStatus(params) {
  return request({
    url: '/system/role/updateRoleStatus',
    method: 'post',
    params
  })
}

// 删除角色
export function removeRole(params) {
  return request({
    url: '/system/role/removeRole',
    method: 'post',
    params
  })
}

// 查询角色已授权用户列表
export function getAllocatedUserPageList(data) {
  return request({
    url: '/system/role/authUser/getAllocatedUserPageList',
    method: 'post',
    data
  })
}

// 查询角色未授权用户列表
export function getUnallocatedUserPageList(data) {
  return request({
    url: '/system/role/authUser/getUnallocatedUserPageList',
    method: 'post',
    data
  })
}

// 取消用户授权角色
export function authUserCancel(data) {
  return request({
    url: '/system/role/authUser/cancel',
    method: 'put',
    data: data
  })
}

// 批量取消用户授权角色
export function authUserCancelAll(data) {
  return request({
    url: '/system/role/authUser/cancelAll',
    method: 'put',
    params: data
  })
}

// 授权用户选择
export function authUserSelectAll(data) {
  return request({
    url: '/system/role/authUser/selectAll',
    method: 'put',
    params: data
  })
}
