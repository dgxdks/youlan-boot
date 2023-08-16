import { JSONUtil, StrUtil } from '@/utils/tools/index'
import Cookies from 'js-cookie'

export default {
  set(key, value, { options }) {
    Cookies.set(key, value, options)
  },
  get(key) {
    return Cookies.get(key)
  },
  getJson(key) {
    return JSONUtil.toJsonObject(this.get(key))
  },
  getBoolean(key) {
    return StrUtil.toBoolean(this.get(key))
  },
  remove(key) {
    return Cookies.remove(key)
  },
  getUserName() {
    return this.get('userName')
  },
  setUserName(userName) {
    this.set('userName', userName, { expires: 30 })
  },
  removeUserName() {
    return this.remove('userName')
  },
  getUserPassword() {
    return this.get('userPassword')
  },
  setUserPassword(userPassword) {
    this.set('userPassword', userPassword, { expires: 30 })
  },
  removeUserPassword() {
    return this.remove('userPassword')
  },
  getRememberMe() {
    return this.getBoolean('rememberMe')
  },
  setRememberMe(rememberMe) {
    this.set('rememberMe', StrUtil.toBoolean(rememberMe), { expires: 30 })
  },
  removeRememberMe() {
    return this.remove('rememberMe')
  },
  getTokenName() {
    return this.get('tn')
  },
  setTokenName(tokenName) {
    return this.set('tn', tokenName, {})
  },
  removeTokenName() {
    return this.remove('tn')
  },
  getTokenValue() {
    return this.get('tv')
  },
  setTokenValue(tokenValue) {
    this.set('tv', tokenValue, {})
  },
  removeTokenValue() {
    this.remove('tv')
  }
}
