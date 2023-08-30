import request from '@/framework/tools/request'

export default {
  upload(uploadUrl, formParams, paramsData, headers, config) {
    config = config || {}
    const formData = new FormData()
    for (const paramName in formParams) {
      formData.append(paramName, formParams[paramName])
    }
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
