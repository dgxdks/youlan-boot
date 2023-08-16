import { ObjectUtil } from '@/utils/tools/index'

export default {
  toJsonStr(obj) {
    if (typeof obj === 'object') {
      return JSON.stringify(obj)
    }
    return obj
  },
  toJsonObject(str) {
    if (ObjectUtil.isEmpty(str)) {
      return null
    }
    if (ObjectUtil.isObject(str)) {
      return str
    }
    return JSON.parse(str)
  }
}
