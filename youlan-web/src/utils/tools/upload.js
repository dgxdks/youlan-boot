import request from '@/utils/request'

export default {
  upload(uploadUrl, formParams, paramsData, headers) {
    const formData = new FormData()
    for (const paramName in formParams) {
      if (formParams[paramName]) {
        formData.append(paramName, formParams[paramName])
      }
    }
    return request({
      url: uploadUrl,
      method: 'post',
      headers: headers || {},
      contentType: 'multipart/form-data',
      data: formData,
      params: paramsData
    })
  }
}
