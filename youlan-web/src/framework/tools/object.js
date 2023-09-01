export default {
  isEmpty(obj) {
    if (this.isBoolean(obj)) {
      return false
    }
    if (this.isNumber(obj)) {
      return false
    }
    if (this.isString(obj)) {
      return false
    }
    return !obj || obj === '' || typeof (obj) === 'undefined'
  },
  isNotEmpty(obj) {
    return !this.isEmpty(obj)
  },
  isObject(obj) {
    return obj && typeof obj === 'object'
  },
  isArray(obj) {
    return Array.isArray(obj) || toString.call(obj) === '[object Array]'
  },
  isBoolean(obj) {
    return typeof obj === 'boolean'
  },
  isNumber(obj) {
    return typeof obj === 'number'
  },
  isString(obj) {
    return typeof obj === 'string'
  },
  isFunction(obj) {
    return toString.call(obj) === '[object Function]'
  },
  isDate(obj) {
    return toString.call(obj) === '[object Date]'
  },
  isFile(obj) {
    return toString.call(obj) === '[object File]'
  },
  isBlob(obj) {
    return toString.call(obj) === '[object Blob]'
  },
  isStream(obj) {
    return this.isObject(obj) && this.isFunction(obj.pipe)
  },
  isArrayBuffer(obj) {
    return toString.call(obj) === '[object ArrayBuffer]'
  },
  isFormData(obj) {
    return obj && obj instanceof FormData
  }
}
