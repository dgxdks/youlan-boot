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
  // 组件class字段名称已映射
  classField: 'classField'
}
/**
 * 静态数据库字典配置样例
 * web_common_status: [
 *   { type: 'web_common_status', value: '1', name: '正常' },
 *   { type: 'web_common_status', value: '2', name: '停用' }
 * ]
 */
const staticDict = {}
// 管理数据字典加载状态
const loadingStatus = {}
export default {

  // 根据字典类型获取字典
  loadDict(typeKey) {
    if (StrUtil.isBlank(typeKey)) {
      return []
    }
    // 尝试初始化字典
    store.commit('dict/INIT_DICT', typeKey)
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
      if (ArrayUtil.isNotEmpty(res)) {
        store.commit('dict/SET_DICT', {
          type: typeKey,
          values: _this.formatDict(res)
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
  // 根据fieldMapping格式化字典值
  formatDict(dictDataList) {
    return dictDataList.map(item => {
      return {
        type: item[fieldMapping.typeField],
        value: item[fieldMapping.valueField],
        name: item[fieldMapping.nameField],
        ui: item[fieldMapping.uiField],
        class: item[fieldMapping.classField]
      }
    })
  }
}
