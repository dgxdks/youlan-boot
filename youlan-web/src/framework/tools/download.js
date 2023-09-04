import request from '@/framework/tools/request'
import { EnvUtil, ModalUtil, ObjectUtil, StrUtil, UrlUtil } from '@/framework/tools/index'
import saveAs from 'file-saver'

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
  get(url, params) {
    return this.getAsName(url, params)
  },
  getAsName(url, params, fileName) {
    return download({
      url,
      method: 'get',
      params
    }, fileName)
  },
  post(url, params, data) {
    return this.postAsName(url, params, data)
  },
  postAsName(url, params, data, fileName) {
    return download({
      url,
      method: 'post',
      params,
      data
    }, fileName)
  },
  saveAsFile(blob, fileName, opts) {
    saveAs(blob, fileName, opts)
  },
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
  parseDownloadUrl(url) {
    if (StrUtil.isBlank(url)) {
      return url
    }
    if (UrlUtil.isHttpUrl(url)) {
      return url
    }
    return StrUtil.removeSlashRight(EnvUtil.getBaseApi()) + '/system/storage/download/url/' + StrUtil.removeSlashLeft(url)
  }
}

