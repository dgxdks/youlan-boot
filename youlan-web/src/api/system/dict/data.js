import request from '@/framework/tools/request'

// 查询字典数据列表
export function getDictDataListByTypeKey(params) {
  return request({
    url: '/system/dictData/getDictDataListByTypeKey',
    method: 'post',
    params,
    canRepeatSubmit: true
  })
}
// 查询字典数据列表
export function getDictDataPageList(data) {
  return request({
    url: '/system/dictData/getDictDataPageList',
    method: 'post',
    data
  })
}

// 新增字典数据
export function addDictData(data) {
  return request({
    url: '/system/dictData/addDictData',
    method: 'post',
    data: data
  })
}

// 修改字典数据
export function updateDictData(data) {
  return request({
    url: '/system/dictData/updateDictData',
    method: 'post',
    data: data
  })
}

// 删除字典数据
export function removeDictData(data) {
  return request({
    url: '/system/dictData/removeDictData',
    method: 'post',
    data
  })
}
