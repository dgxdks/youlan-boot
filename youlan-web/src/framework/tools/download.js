import request from '@/framework/tools/request'
import { EnvUtil, ModalUtil, ObjectUtil, StrUtil, UrlUtil } from '@/framework/tools/index'
import saveAs from 'file-saver'

/**
 * 通用下载方法
 */
function download(config, fileName) {
  return new Promise((resolve, reject) => {
    ModalUtil.loading('正在下载数据, 请稍等')
    request({
      ...config,
      responseType: 'blob'
    }).then(res => {
      const { data, headers } = res
      if (!ObjectUtil.isBlob(data)) {
        reject(data)
      } else if (StrUtil.isContains(data.type, 'application/json')) {
        data.text().then(text => {
          reject(text)
        })
      } else {
        if ((StrUtil.isBlank(fileName))) {
          const contentDisposition = headers['content-disposition']
          if (StrUtil.isNotBlank(contentDisposition)) {
            const splits = contentDisposition.split('=')
            if (splits.length >= 2) {
              fileName = decodeURIComponent(fileName = splits[1])
              fileName = fileName.replace(/"/g, '')
            }
          }
        }
        saveAs(data, fileName)
      }
      ModalUtil.loadingClose()
    }).catch(error => {
      ModalUtil.loadingClose()
      console.log(error)
    })
  })
}

export default {
  /**
     * GET请求下载
     * @param url 下载地址
     * @param params 路径参数
     * @param config axios配置
     * @returns {Promise<unknown>}
     */
  get(url, params, config) {
    return this.getAsName(url, params, config)
  },
  /**
     * GET请求下载
     * @param url 下载地址
     * @param params 路径参数
     * @param config axios配置
     * @param fileName 文件名称
     * @returns {Promise<unknown>}
     */
  getAsName(url, params, fileName, config = { timeout: 30 * 1000 }) {
    return download({
      url,
      method: 'get',
      params,
      ...config
    }, fileName)
  },
  /**
     * POST请求下载
     * @param url 下载地址
     * @param params 路径参数
     * @param data body参数
     * @param config axios配置
     * @returns {Promise<unknown>}
     */
  post(url, params, data, config) {
    return this.postAsName(url, params, data, config)
  },
  /**
     * POST请求下载
     * @param url 下载地址
     * @param params 路径参数
     * @param data body参数
     * @param config axios配置
     * @param fileName 文件名称
     * @returns {Promise<unknown>}
     */
  postAsName(url, params, data, fileName, config = { tiemout: 30 * 1000 }) {
    return download({
      url,
      method: 'post',
      params,
      data,
      ...config
    }, fileName)
  },
  // 保存文件
  saveUrlAsFile(url, fileName, opts) {
    if (StrUtil.isBlank(url)) {
      return Promise.reject()
    }
    // 兼容blob:前缀url
    if (UrlUtil.isExternalUrl(url)) {
      return new Promise((resolve, reject) => {
        saveAs(url, fileName, resolve)
      })
    } else {
      return this.getAsName(this.parseDownloadUrl(url), {}, fileName, { timeout: 30 * 1000 })
    }
  },
  // 保存文件
  saveAsFile(blob, fileName, opts) {
    saveAs(blob, fileName, opts)
  },
  // 保存文件url
  saveFileAsUrl(file) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader()
      reader.readAsDataURL(file)
      reader.onload = () => {
        resolve(reader.result)
      }
      reader.onerror = (error) => {
        reject(error)
      }
    })
  },
  // 解析文件src路径
  parseSrcUrl(url) {
    if (StrUtil.isBlank(url)) {
      return url
    }
    if (UrlUtil.isExternalUrl(url)) {
      return url
    }
    return StrUtil.removeSlashRight(EnvUtil.getBaseApi()) + '/system/storage/download/url/' + StrUtil.removeSlashLeft(url)
  },
  // 解析文件下载路径
  parseDownloadUrl(url) {
    if (StrUtil.isBlank(url)) {
      return url
    }
    if (UrlUtil.isExternalUrl(url)) {
      return url
    }
    return '/system/storage/download/url/' + StrUtil.removeSlashLeft(url)
  }
}

