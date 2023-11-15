import request from '@/framework/tools/request'

// 短信厂商分页
export function getSmsSupplierPageList(data) {
  return request({
    url: '/system/smsSupplier/getSmsSupplierPageList',
    method: 'post',
    data
  })
}

// 短信厂商详情
export function loadSmsSupplier(params) {
  return request({
    url: '/system/smsSupplier/loadSmsSupplier',
    method: 'post',
    params
  })
}

// 短信厂商删除
export function removeSmsSupplier(data) {
  return request({
    url: '/system/smsSupplier/removeSmsSupplier',
    method: 'post',
    data
  })
}

// 短信厂商导出
export function exportSmsSupplier(data) {
  return request({
    url: '/system/smsSupplier/exportSmsSupplier',
    method: 'post',
    data
  })
}

// 短信厂商修改
export function updateSmsSupplier(data) {
  return request({
    url: '/system/smsSupplier/updateSmsSupplier',
    method: 'post',
    data
  })
}

// 短信厂商新增
export function addSmsSupplier(data) {
  return request({
    url: '/system/smsSupplier/addSmsSupplier',
    method: 'post',
    data
  })
}

// 短信厂商缓存刷新
export function refreshSmsSupplierCache(data) {
  return request({
    url: '/system/smsSupplier/refreshSmsSupplierCache',
    method: 'post',
    data
  })
}

// 短信厂商状态更新
export function updateSmsSupplierStatus(params) {
  return request({
    url: '/system/smsSupplier/updateSmsSupplierStatus',
    method: 'post',
    params
  })
}

// 短信厂商额外参数
export function getSmsSupplierExtraParams(params) {
  return request({
    url: '/system/smsSupplier/getSmsSupplierExtraParams',
    method: 'post',
    params
  })
}
