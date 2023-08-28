import { JSONUtil } from '@/framework/tools/index'

export default {
  setLocalStorage(key, value) {
    localStorage.setItem(key, value)
  },
  getLocalStorage(key) {
    return localStorage.getItem(key)
  },

  setLocalStorageJson(key, value) {
    this.setLocalStorage(key, JSONUtil.toJsonStr(value))
  },

  getLocalStorageJson(key) {
    return JSONUtil.toJsonObject(this.getLocalStorage(key))
  },

  setSessionStorage(key, value) {
    sessionStorage.setItem(key, value)
  },

  getSessionStorage(key) {
    return sessionStorage.getItem(key)
  },

  setSessionStorageJson(key, value) {
    this.setSessionStorage(key, JSONUtil.toJsonStr(value))
  },

  getSessionStorageJson(key) {
    return JSONUtil.toJsonObject(this.getSessionStorage(key))
  }

}
