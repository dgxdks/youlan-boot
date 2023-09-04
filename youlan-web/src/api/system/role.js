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

// 授权用户分页
export function getAuthUserPageList(data) {
  return request({
    url: '/system/role/getAuthUserPageList',
    method: 'post',
    data
  })
}

// 未授权用户分页
export function getUnAuthUserPageList(data) {
  return request({
    url: '/system/role/getUnAuthUserPageList',
    method: 'post',
    data
  })
}

// 授权用户取消
export function cancelAuthUser(params) {
  return request({
    url: '/system/role/cancelAuthUser',
    method: 'post',
    params
  })
}

// 授权用户新增
export function addAuthUser(params) {
  return request({
    url: '/system/role/addAuthUser',
    method: 'post',
    params
  })
}
