import { ObjectUtil } from '@/framework/tools/index'

export default {
  objectToQuery(params) {
    const pairs = []
    for (const propName of Object.keys(params)) {
      const value = params[propName]
      const part = encodeURIComponent(propName) + '='
      if (ObjectUtil.isEmpty(value)) {
        pairs.push(part)
      } else if (ObjectUtil.isObject(value)) {
        pairs.push(part + encodeURIComponent(JSONUtil.toJsonStr(value)))
      } else {
        pairs.push(part + encodeURIComponent(value))
      }
    }
    return pairs.join('&')
  },
  toQueryUrl(url, params) {
    if (ObjectUtil.isObject(params)) {
      return url + '?' + this.objectToQuery(params)
    }
    return url + '?' + params
  }
}
