import { ObjectUtil } from '@/utils/tools/index'

export default {
  isEmpty(array) {
    if (ObjectUtil.isEmpty(array)) {
      return true
    }
    if (!ObjectUtil.isArray(array)) {
      return true
    }
    return array.length === 0
  },
  isNotEmpty(array) {
    return !this.isEmpty(array)
  }
}
