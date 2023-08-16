import { ArrayUtil, StrUtil } from '@/utils/tools'

const state = {
  dict: {

  }
}
const mutations = {
  SET_DICT: (state, { type, values }) => {
    if (StrUtil.isNotBlank(type) && ArrayUtil.isNotEmpty(values)) {
      state.dict = {
        ...state.dict,
        [type]: values
      }
    }
  },
  REMOVE_DICT: (state, type) => {
    state.dict[type] = []
  },
  CLEAN_DICT: (state) => {
    state.dict = {}
  }
}

const actions = {
  // 设置字典
  SetDict({ commit }, data) {
    commit('SET_DICT', data)
  },
  // 删除字典
  RemoveDict({ commit }, key) {
    commit('REMOVE_DICT', key)
  },
  // 清空字典
  CleanDict({ commit }) {
    commit('CLEAN_DICT')
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

