import request from '@/framework/tools/request'
import { ModalUtil, ObjectUtil, StrUtil } from '@/framework/tools/index'
import saveAs from 'file-saver'

function download(config, fileName) {
  ModalUtil.loading('正在下载数据, 请稍等')
  request({
    ...config,
    responseType: 'blob'
  }).then(res => {
    const { data, headers } = res
    if (ObjectUtil.isBlob(data)) {
      if (StrUtil.isBlank(fileName)) {
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
    } else {
      ModalUtil.error('数据格式异常')
    }
    ModalUtil.loadingClose()
  }).catch(error => {
    ModalUtil.loadingClose()
    console.log(error)
  })
}

export default {
  get(url, params) {
    this.getAsName(url, params)
  },
  getAsName(url, params, fileName) {
    download({
      url,
      method: 'get',
      params
    }, fileName)
  },
  post(url, params, data) {
    this.postAsName(url, params, data)
  },
  postAsName(url, params, data, fileName) {
    download({
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

