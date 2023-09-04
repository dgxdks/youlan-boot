import { ObjectUtil } from '@/framework/tools/index'

export default {
  isBlank(str) {
    if (!ObjectUtil.isString(str)) {
      return true
    }
    return !str || (str && str.trim() === '')
  },
  isNotBlank(str) {
    return !this.isBlank(str)
  },
  toBoolean(str) {
    return Boolean(str)
  },
  // 首字母大写
  upperFirst(str) {
    if (this.isBlank(str)) {
      return str
    }
    return str.slice(0, 1).toUpperCase() + str.slice(1)
  },
  // 路径左侧添加/
  paddingSlashLeft(url) {
    if (!ObjectUtil.isString(url)) {
      return url
    }
    if (url.startsWith('/')) {
      return url
    }
    return '/' + url
  },
  // 路径左侧删除/
  removeSlashLeft(url) {
    if (!ObjectUtil.isString(url)) {
      return url
    }
    if (url.startsWith('/')) {
      return url.replace('/', '')
    }
    return url
  },
  // 路径右侧删除/
  removeSlashRight(url) {
    if (!ObjectUtil.isString(url)) {
      return url
    }
    if (url.endsWith('/')) {
      return ''.substring(0, url.length)
    }
    return url
  },
  // 是否包含此字符
  isContains(str, search) {
    if (this.isBlank(str) || this.isBlank(search)) {
      return false
    }
    return str.indexOf(search) !== -1
  }
}
