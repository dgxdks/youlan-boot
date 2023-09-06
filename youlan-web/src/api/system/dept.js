import request from '@/framework/tools/request'

export function getDeptTreeList(data) {
  return request({
    url: '/system/dept/getDeptTreeList',
    method: 'post',
    data
  })
}

// 查询部门列表（排除节点）
export function listDeptExcludeChild(deptId) {
  return request({
    url: '/system/dept/list/exclude/' + deptId,
    method: 'get'
  })
}

// 查询部门详细
export function loadDept(params) {
  return request({
    url: '/system/dept/loadDept',
    method: 'post',
    params
  })
}

// 新增部门
export function addDept(data) {
  return request({
    url: '/system/dept/addDept',
    method: 'post',
    data
  })
}

// 修改部门
export function updateDept(data) {
  return request({
    url: '/system/dept/updateDept',
    method: 'post',
    data
  })
}

// 删除部门
export function removeDept(data) {
  return request({
    url: '/system/dept/removeDept',
    method: 'post',
    data
  })
}
