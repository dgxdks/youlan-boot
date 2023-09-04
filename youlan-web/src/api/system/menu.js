import request from '@/framework/tools/request'

// 查询菜单列表
export function getMenuPageList(data) {
  return request({
    url: '/system/menu/getMenuPageList',
    method: 'post',
    data
  })
}

// 查询菜单详细
export function loadMenu(params) {
  return request({
    url: '/system/menu/loadMenu',
    method: 'post',
    params
  })
}

// 查询菜单下拉树结构
export function getMenuTreeList(data) {
  return request({
    url: '/system/menu/getMenuTreeList',
    method: 'post',
    data
  })
}

// 新增菜单
export function addMenu(data) {
  return request({
    url: '/system/menu/addMenu',
    method: 'post',
    data
  })
}

// 修改菜单
export function updateMenu(data) {
  return request({
    url: '/system/menu/updateMenu',
    method: 'post',
    data
  })
}

// 删除菜单
export function removeMenu(params) {
  return request({
    url: '/system/menu/removeMenu',
    method: 'post',
    params
  })
}
