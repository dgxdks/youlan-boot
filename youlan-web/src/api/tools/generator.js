import request from '@/framework/tools/request'

// 数据库列表
export function getDbTablePageList(data) {
  return request({
    url: '/tools/generator/getDbTablePageList',
    method: 'post',
    data
  })
}

// 数据库表导入
export function importDbTableList(data) {
  return request({
    url: '/tools/generator/importDbTableList',
    method: 'post',
    data
  })
}

// 数据库表同步
export function syncDbTable(params) {
  return request({
    url: '/tools/generator/syncDbTable',
    method: 'post',
    params
  })
}

// 代码下载
export function downloadCode(data) {
  return request({
    url: '/tools/generator/downloadCode',
    method: 'post',
    data
  })
}

// 代码写入
export function writeCode(data) {
  return request({
    url: '/tools/generator/writeCode',
    method: 'post',
    data
  })
}

// 代码预览
export function previewCode(params) {
  return request({
    url: '/tools/generator/previewCode',
    method: 'post',
    params
  })
}

// 生成表详情
export function loadTable(params) {
  return request({
    url: '/tools/generator/loadTable',
    method: 'post',
    params
  })
}

// 生成表删除
export function removeTable(data) {
  return request({
    url: '/tools/generator/removeTable',
    method: 'post',
    data
  })
}

// 生成表修改
export function updateTale(data) {
  return request({
    url: '/tools/generator/updateTable',
    method: 'post',
    data
  })
}

// 生成表分页
export function getTablePageList(data) {
  return request({
    url: '/tools/generator/getTablePageList',
    method: 'post',
    data
  })
}
