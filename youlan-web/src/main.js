import Vue from 'vue'

import Cookies from 'js-cookie'

import ElementUI from 'element-ui'
import './assets/styles/element-variables.scss'

import '@/assets/styles/index.scss' // global css
import '@/assets/styles/ruoyi.scss' // ruoyi css
import App from './App'
import store from './store'
import router from './router'
import directive from './directive' // directive
import plugins from './plugins' // plugins
import './assets/icons' // icon
import './permission' // permission control
import { getDicts } from '@/api/system/dict/data'
import { getConfigKey } from '@/api/system/config'
import { addDateRange, handleTree, parseTime, resetForm, selectDictLabel, selectDictLabels } from '@/utils/ruoyi'
// 分页组件
import Pagination from '@/components/Pagination'
// 自定义表格工具组件
import RightToolbar from '@/components/RightToolbar'
// 富文本组件
import Editor from '@/components/Editor'
// 文件上传组件
import FileUpload from '@/components/FileUpload'
// 图片上传组件
import ImageUpload from '@/components/ImageUpload'
// 图片预览组件
import ImagePreview from '@/components/ImagePreview'
// 字典标签组件
import DictTag from '@/components/DictTag'
// 头部标签组件
import VueMeta from 'vue-meta'
// 表格查询参数组件
import TableQuery from '@/views/components/TableQuery/index.vue'
// 表格操作选项组件
import TableOptions from '@/views/components/TableOptions/index.vue'
import RowSplit2 from '@/views/components/RowSplit2/index.vue'
import RowSplit3 from '@/views/components/RowSplit3/index.vue'
import BaseDialog from '@/views/components/BaseDialog/index.vue'
import mixin from '@/mixin'
import DictSelect from '@/views/components/DictSelect/index.vue'
import DictRadio from '@/views/components/DictRadio/index.vue'
import OrgTree from '@/views/components/OrgTree/index.vue'
import BaseSwitch from '@/views/components/BaseSwitch/index.vue'
import OrgSelect from '@/views/components/OrgSelect/index.vue'

// 全局方法挂载
Vue.prototype.getDicts = getDicts
Vue.prototype.getConfigKey = getConfigKey
Vue.prototype.parseTime = parseTime
Vue.prototype.resetForm = resetForm
Vue.prototype.addDateRange = addDateRange
Vue.prototype.selectDictLabel = selectDictLabel
Vue.prototype.selectDictLabels = selectDictLabels
// Vue.prototype.download = download
Vue.prototype.handleTree = handleTree

// 全局组件挂载
Vue.component('OrgSelect', OrgSelect)
Vue.component('BaseSwitch', BaseSwitch)
Vue.component('DictSelect', DictSelect)
Vue.component('OrgTree', OrgTree)
Vue.component('DictRadio', DictRadio)
Vue.component('BaseDialog', BaseDialog)
Vue.component('RowSplit2', RowSplit2)
Vue.component('RowSplit3', RowSplit3)
Vue.component('TableOptions', TableOptions)
Vue.component('TableQuery', TableQuery)
Vue.component('DictTag', DictTag)
Vue.component('Pagination', Pagination)
Vue.component('RightToolbar', RightToolbar)
Vue.component('Editor', Editor)
Vue.component('FileUpload', FileUpload)
Vue.component('ImageUpload', ImageUpload)
Vue.component('ImagePreview', ImagePreview)

Vue.use(directive)
Vue.use(plugins)
Vue.use(mixin)
Vue.use(VueMeta)

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online! ! !
 */

Vue.use(ElementUI, {
  size: Cookies.get('size') || 'medium' // set element-ui default size
})

Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
