import { ObjectUtil } from '@/framework/tools/index'

export default {
  // 数组为空
  isEmpty(array) {
    if (ObjectUtil.isEmpty(array)) {
      return true
    }
    if (!ObjectUtil.isArray(array)) {
      return true
    }
    return array.length === 0
  },
  // 数组非空
  isNotEmpty(array) {
    return !this.isEmpty(array)
  },
  // 数组去重
  distinct(array) {
    if (!ObjectUtil.isArray(array)) {
      return array
    }
    return Array.from(new Set(array))
  }
}
