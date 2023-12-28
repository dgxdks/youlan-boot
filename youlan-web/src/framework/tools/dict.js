import store from '@/store'
import { ArrayUtil, StrUtil } from '@/framework/tools/index'
import { getDictDataListByTypeKey } from '@/api/system/dict/data'

const fieldMapping = {
  // 字典类型映射字段
  typeField: 'typeKey',
  // 字典值映射字段
  valueField: 'dataValue',
  // 字段名称映射字段
  nameField: 'dataName',
  // 组件UI字段名称映射
  uiField: 'uiClass',
  // 组件class字段名称映射
  cssField: 'cssClass'
}
/**
 * 静态数据库字典配置样例
 * web_common_status: [
 *   { type: 'web_common_status', value: '1', name: '正常', ui: 'primary' },
 *   { type: 'web_common_status', value: '2', name: '停用', ui: 'danger' }
 * ]
 */
const staticDict = {
  sms_type: [
    { type: 'sms_type', value: '1', ui: 'primary', name: '标准短信' },
    { type: 'sms_type', value: '2', ui: 'info', name: '异步短信' },
    { type: 'sms_type', value: '3', ui: 'warning', name: '延迟短信' }
  ],
  sms_send_type: [
    { type: 'sms_send_type', value: '1', ui: 'primary', name: '单个发送' },
    { type: 'sms_send_type', value: '2', ui: 'danger', name: '批量发送' }
  ],
  sms_send_status: [
    { type: 'sms_send_status', value: '1', name: '成功', ui: 'success' },
    { type: 'sms_send_status', value: '2', name: '失败', ui: 'danger' }
  ]
}
// 管理数据字典加载状态
const loadingStatus = {}
export default {
  // 根据字典类型获取字典
  loadDict(typeKey) {
    if (StrUtil.isBlank(typeKey)) {
      return []
    }
    // 如果store中有则直接返回
    const dict = store.getters.dict
    if (ArrayUtil.isNotEmpty(dict[typeKey])) {
      return dict[typeKey]
    }
    // store中没有则在静态字典中查找，如果有则返回并向store中设置
    if (ArrayUtil.isNotEmpty(staticDict[typeKey])) {
      store.commit('dict/SET_DICT', {
        type: typeKey,
        values: staticDict[typeKey]
      })
      return staticDict[typeKey]
    }
    // 没有则需要同步获取
    const _this = this
    // 如果正在加载则不重复加载
    if (loadingStatus[typeKey]) {
      return
    }
    loadingStatus[typeKey] = true
    getDictDataListByTypeKey({ typeKey }).then(res => {
      if (ArrayUtil.isNotEmpty(res.data)) {
        store.commit('dict/SET_DICT', {
          type: typeKey,
          values: _this.formatDict(res.data)
        })
        loadingStatus[typeKey] = false
      }
    }).catch(error => {
      loadingStatus[typeKey] = false
      console.log(error)
    })
  },
  // 根据字典类型列表获取字典
  loadDictList(typeKeys) {
    if (ArrayUtil.isEmpty(typeKeys)) {
      return []
    }
    for (let i = 0; i < typeKeys.length; i++) {
      this.loadDict(typeKeys[i])
    }
  },
  // 刷新字典缓存
  refreshDict(typeKey) {
    // 未指定typeKey则刷新所有
    const dict = typeKey ? { typeKey: [] } : store.getters.dict
    for (const typeKey in dict) {
      getDictDataListByTypeKey({ typeKey }).then(res => {
        if (ArrayUtil.isNotEmpty(res.data)) {
          store.commit('dict/SET_DICT', {
            type: typeKey,
            values: this.formatDict(res.data)
          })
        }
      }).catch(error => {
        console.log(error)
      })
    }
  },
  // 删除字典
  removeDict(typeKey) {
    if (StrUtil.isBlank(typeKey)) {
      return
    }
    store.commit('dict/REMOVE_DICT', typeKey)
  },
  // 根据fieldMapping格式化字典值
  formatDict(dictDataList) {
    return dictDataList.map(item => {
      return {
        type: item[fieldMapping.typeField],
        value: item[fieldMapping.valueField],
        name: item[fieldMapping.nameField],
        ui: item[fieldMapping.uiField],
        class: item[fieldMapping.cssField]
      }
    })
  }
}
