import request from '@/framework/tools/request'

// 系统配置新增
export function addConfig(data) {
  return request({
    url: '/system/config/addConfig',
    method: 'post',
    data
  })
}

// 系统配置修改
export function updateConfig(data) {
  return request({
    url: '/system/config/updateConfig',
    method: 'post',
    data
  })
}

// 系统配置删除
export function removeConfig(data) {
  return request({
    url: '/system/config/removeConfig',
    method: 'post',
    data
  })
}

// 系统配置详情
export function loadConfig(params) {
  return request({
    url: '/system/config/loadConfig',
    method: 'post',
    params
  })
}

// 系统配置详情
export function loadConfigByConfigKey(params) {
  return request({
    url: '/system/config/loadConfigByConfigKey',
    method: 'post',
    params
  })
}

// 系统配置分页
export function getConfigPageList(data) {
  return request({
    url: '/system/config/getConfigPageList',
    method: 'post',
    data
  })
}

// 系统配置导出
export function exportConfigList(data) {
  return request({
    url: '/system/config/getConfigPageList',
    method: 'post',
    data
  })
}

// 系统配置缓存刷新
export function refreshConfigCache() {
  return request({
    url: '/system/config/refreshConfigCache',
    method: 'post'
  })
}
