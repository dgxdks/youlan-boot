import { ArrayUtil, AuthUtil } from '../../tools'

export default {
  inserted(el, binding, vnode) {
    const { value } = binding
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
