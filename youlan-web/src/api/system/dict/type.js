import request from '@/framework/tools/request'

// 查询字典类型列表
export function getDictTypePageList(data) {
  return request({
    url: '/system/dictType/getDictTypePageList',
    method: 'post',
    data
  })
}

// 查询字典类型列表
export function getDictTypeList(data) {
  return request({
    url: '/system/dictType/getDictTypeList',
    method: 'post',
    data
  })
}

// 查询字典类型详细
export function loadDictType(params) {
  return request({
    url: '/system/dictType/loadDictType',
    method: 'post',
    params
  })
}

// 新增字典类型
export function addDictType(data) {
  return request({
    url: '/system/dictType/addDictType',
    method: 'post',
    data
  })
}

// 修改字典类型
export function updateDictType(data) {
  return request({
    url: '/system/dictType/updateDictType',
    method: 'post',
    data
  })
}

// 删除字典类型
export function removeDictType(data) {
  return request({
    url: '/system/dictType/removeDictType',
    method: 'post',
    data
  })
}

// 刷新字典缓存
export function refreshDictCache() {
  return request({
    url: '/system/dictType/refreshDictCache',
    method: 'post'
  })
}

