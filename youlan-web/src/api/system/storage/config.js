import request from '@/framework/tools/request'

// 存储配置分页
export function getStorageConfigPageList(data) {
  return request({
    url: '/system/storageConfig/getStorageConfigPageList',
    method: 'post',
    data
  })
}

// 存储配置详情
export function loadStorageConfig(params) {
  return request({
    url: '/system/storageConfig/loadStorageConfig',
    method: 'post',
    params
  })
}

// 存储配置删除
export function removeStorageConfig(data) {
  return request({
    url: '/system/storageConfig/removeStorageConfig',
    method: 'post',
    data
  })
}

// 存储配置导出
export function exportStorageConfig(data) {
  return request({
    url: '/system/storageConfig/exportStorageConfig',
    method: 'post',
    data
  })
}

// 存储配置修改
export function updateStorageConfig(data) {
  return request({
    url: '/system/storageConfig/updateStorageConfig',
    method: 'post',
    data
  })
}

// 存储配置新增
export function addStorageConfig(data) {
  return request({
    url: '/system/storageConfig/addStorageConfig',
    method: 'post',
    data
  })
}

// 存储配置缓存刷新
export function refreshStorageConfigCache(data) {
  return request({
    url: '/system/storageConfig/refreshStorageConfigCache',
    method: 'post',
    data
  })
}

// 存储配置状态更新
export function updateStorageConfigStatus(params) {
  return request({
    url: '/system/storageConfig/updateStorageConfigStatus',
    method: 'post',
    params
  })
}

// 存储配置是否默认
export function updateStorageConfigIsDefault(params) {
  return request({
    url: '/system/storageConfig/updateStorageConfigIsDefault',
    method: 'post',
    params
  })
}
