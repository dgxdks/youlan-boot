import { accountLogin, getLoginInfo, logout } from '@/api/system/login'
import { ArrayUtil, CookieUtil, StrUtil } from '../../framework/tools'
import { tokenValuePrefix } from '@/settings'

function getTokenHeaders() {
  return {
    [CookieUtil.getTokenName()]: tokenValuePrefix + CookieUtil.getTokenValue()
  }
}

const user = {
  state: {
    tokenName: CookieUtil.getTokenName(),
    tokenValue: CookieUtil.getTokenValue(),
    tokenHeaders: getTokenHeaders(),
    userName: '',
    avatar: '',
    roleList: [],
    permissionList: []
  },

  mutations: {
    SET_TOKEN: (state, token) => {
      state.tokenName = token.tokenName
      state.tokenValue = token.tokenValue
      state.tokenHeaders = getTokenHeaders()
      CookieUtil.setTokenName(token.tokenName)
      CookieUtil.setTokenValue(token.tokenValue)
    },
    SET_USER_NAME: (state, userName) => {
      state.userName = userName
    },
    SET_AVATAR: (state, avatar) => {
      state.avatar = avatar
    },
    SET_ROLE_LIST: (state, roleList) => {
      state.roleList = roleList
    },
    SET_PERMISSION_LIST: (state, permissionList) => {
      state.permissionList = permissionList
    }
  },

  actions: {
    // 登录
    AccountLogin({ commit }, loginForm) {
      const data = {
        userName: loginForm.userName.trim(),
        userPassword: loginForm.userPassword,
        captchaCode: loginForm.captchaCode,
        captchaId: loginForm.captchaId
      }
      return new Promise((resolve, reject) => {
        accountLogin(data).then(res => {
          commit('SET_TOKEN', res)
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 获取用户登录信息
    GetLoginInfo({ commit, state }) {
      return new Promise((resolve, reject) => {
        getLoginInfo().then(res => {
          const user = res.user
          // 设置用户名
          commit('SET_USER_NAME', user.userName)
          // 设置用户头像
          const avatar = StrUtil.isBlank(user.avatar) ? require('@/assets/images/profile.jpg') : user.avatar
          commit('SET_AVATAR', avatar)
          // 设置用户角色信息
          if (ArrayUtil.isNotEmpty(res.roleList)) {
            commit('SET_ROLE_LIST', res.roleList)
            commit('SET_PERMISSION_LIST', res.permissionList)
          }
          resolve(res)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 退出系统
    Logout({ commit, state }) {
      return new Promise((resolve, reject) => {
        logout(state.token).then(() => {
          commit('SET_TOKEN', '')
          commit('SET_ROLES', [])
          commit('SET_PERMISSIONS', [])
          CookieUtil.removeTokenValue()
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    }
  }
}

export default user
