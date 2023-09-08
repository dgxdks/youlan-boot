import Vue from 'vue'

import ElementUI from 'element-ui'
import './assets/styles/element-variables.scss'
import '@/assets/styles/index.scss'
import '@/assets/styles/ruoyi.scss'
import App from './App'
import store from './store'
import router from './router'
import './permission'
// 头部标签组件
import VueMeta from 'vue-meta'
import framework from '@/framework'
import { CookieUtil } from '@/framework/tools'

Vue.use(framework)
Vue.use(VueMeta)

Vue.use(ElementUI, {
  size: CookieUtil.get('size') || 'medium' // set element-ui default size
})

Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
