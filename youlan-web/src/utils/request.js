import axios from 'axios'
import { Message, MessageBox } from 'element-ui'
import store from '@/store'
import { canRepeatSubmit, canRepeatSubmitMinInterval, tokenValuePrefix } from '@/settings'
import { JSONUtil, ObjectUtil } from '@/utils/tools'
import { StorageUtil } from '@/utils/tools/storage'

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 创建axios实例
const service = axios.create({
  // 公共请求地址
  baseURL: process.env.VUE_APP_BASE_API,
  // 请求超时时间
  timeout: 15000
})

// request拦截器
service.interceptors.request.use(config => {
  // 是否需要防止数据重复提交
  const requestCanRepeatSubmit = config.canRepeatSubmit ? config.canRepeatSubmit : canRepeatSubmit
  if (store.state.token) {
    // 让每个请求携带自定义token 请根据实际情况自行修改
    config.headers[store.state.tokenName] = tokenValuePrefix + store.state.tokenValue
  }
  // 如果不允许重复提交则需要判断请求
  if (!requestCanRepeatSubmit && (config.method === 'post' || config.method === 'put')) {
    const submitInfo = {
      url: config.url,
      params: JSONUtil.toJsonStr(config.params),
      data: JSONUtil.toJsonStr(config.data),
      timestamp: new Date().getTime()
    }
    const sessionSubmitInfo = StorageUtil.getSessionStorageJson('submitInfo')
    if (sessionSubmitInfo) {
      const url = sessionSubmitInfo.url
      const params = sessionSubmitInfo.params
      const data = sessionSubmitInfo.data
      const timestamp = sessionSubmitInfo.timestamp
      const interval = submitInfo.timestamp - timestamp
      if (submitInfo.url === url && submitInfo.params === params && submitInfo.data === data && interval < canRepeatSubmitMinInterval) {
        const message = '数据正在处理，请勿重复提交'
        console.warn(`[${url}]: ` + message)
        return Promise.reject(new Error(message))
      }
    } else {
      StorageUtil.setSessionStorageJson('submitInfo', submitInfo)
    }
  }
  return config
}, error => {
  console.log(error)
  return Promise.reject(error)
})

// 响应拦截器
service.interceptors.response.use(response => {
  // 响应体
  const data = response.data
  // 二进制数据则直接返回
  if (ObjectUtil.isBlob(data) || ObjectUtil.isArrayBuffer(data)) {
    return data
  }
  // 响应状态码
  const status = data.status || '00000'
  // 响应错误信息
  const errorMsg = data.errorMsg || '系统未知错误，请反馈给管理员'
  // 判断是否已登录
  if (status === '00000') {
    return data.data
  }
  if (status === 'A0003') {
    MessageBox.confirm('登录状态已过期，您可以继续留在该页面，或者重新登录', '系统提示', {
      confirmButtonText: '重新登录', cancelButtonText: '取消', type: 'warning'
    }).then(() => {
      store.dispatch('Logout').then(() => {
        location.href = '/index'
      })
    }).catch(error => {
      console.log(error)
    })
    return Promise.reject('无效的会话，或者会话已过期，请重新登录。')
  }
  if (status === '00001') {
    Message.warning(errorMsg)
    return Promise.reject(new Error(errorMsg))
  }
  Message.error(errorMsg)
  return Promise.reject(new Error(errorMsg))
}, error => {
  console.log(error)
  let { message } = error
  if (message === 'Network Error') {
    message = '后端接口连接异常'
  } else if (message.includes('timeout')) {
    message = '系统接口请求超时'
  } else if (message.includes('Request failed with status code')) {
    message = '系统接口' + message.substr(message.length - 3) + '异常'
  }
  Message.error({ message: message, duration: 5 * 1000 })
  return Promise.reject(error)
})

export default service
