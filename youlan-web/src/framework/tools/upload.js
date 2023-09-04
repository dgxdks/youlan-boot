import request from '@/framework/tools/request'

export default {
  upload(uploadUrl, formData, paramsData, headers, config) {
    config = config || {}
    return request({
      url: uploadUrl,
      method: 'post',
      headers: headers || {},
      contentType: 'multipart/form-data',
      data: formData,
      params: paramsData,
      ...config
    })
  }
}
