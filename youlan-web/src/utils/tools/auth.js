import store from '@/store'

//当前用户权限列表
function currentPermissionList() {
  return store.getters && store.getters.permissionList
}

//当前用户角色列表
function currentRoleList() {
  return store.getters && store.getters.roleList
}

//校验权限
function checkPermission(permission) {
  const all_permission = '*'
  const permissionList = currentPermissionList()
  if (permissionList && permissionList.length > 0) {
    return permissionList.some(v => {
      return all_permission === v || v === permission
    })
  } else {
    return false
  }
}

//校验角色
function checkRole(role) {
  const super_admin = 'admin'
  const roleList = currentRoleList()
  if (roleList && roleList.length > 0) {
    return roleList.some(v => {
      return super_admin === v || v === role
    })
  } else {
    return false
  }
}

export default {
  // 验证用户是否具备某权限
  hasPermission(permission) {
    return checkPermission(permission)
  },
  // 验证用户是否含有指定权限，只需包含其中一个
  hasPermissionOr(permissions) {
    return permissions.some(item => {
      return checkPermission(item)
    })
  },
  // 验证用户是否含有指定权限，必须全部拥有
  hasPermissionAnd(permissions) {
    return permissions.every(item => {
      return checkPermission(item)
    })
  },
  // 验证用户是否具备某角色
  hasRole(role) {
    return checkRole(role)
  },
  // 验证用户是否含有指定角色，只需包含其中一个
  hasRoleOr(roles) {
    return roles.some(item => {
      return checkRole(item)
    })
  },
  // 验证用户是否含有指定角色，必须全部拥有
  hasRoleAnd(roles) {
    return roles.every(item => {
      return checkRole(item)
    })
  }
}
