import request from '@/framework/tools/request'
import { ModalUtil, ObjectUtil, StrUtil } from '@/framework/tools/index'
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
  saveAs(blob, fileName, opts) {
    saveAs(blob, fileName, opts)
  }
}

