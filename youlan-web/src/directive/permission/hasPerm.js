/**
 * v-hasPerm 操作权限处理
 * Copyright (c) 2019 ruoyi
 */

import { ArrayUtil, AuthUtil } from '@/utils/tools'

export default {
  inserted(el, binding, vnode) {
    const { value } = binding
    const all_permission = '*'
    if (ArrayUtil.isNotEmpty(value)) {
      const hasPermissions = value.some(AuthUtil.hasPermission)
      if (!hasPermissions) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error(`请设置操作权限标签值`)
    }
  }
}
