import '@/framework/icons'
import hasRole from '@/framework/directive/permission/hasRole'
import hasPerm from '@/framework/directive/permission/hasPerm'
import clipboard from '@/framework/directive/module/clipboard'
import dialogDrag from '@/framework/directive/dialog/drag'
import dialogDragWidth from '@/framework/directive/dialog/dragWidth'
import dialogDragHeight from '@/framework/directive/dialog/dragHeight'
import dict from '@/framework/mixin/dict'
import OrgSelect from '@/views/components/OrgSelect.vue'
import PostSelect from '@/views/components/PostSelect.vue'
import RoleSelect from '@/views/components/RoleSelect.vue'
import BaseSwitch from '@/framework/components/BaseSwitch.vue'
import DictSelect from '@/framework/components/DictSelect.vue'
import OrgTree from '@/views/components/OrgTree.vue'
import DictRadio from '@/framework/components/DictRadio.vue'
import BaseDialog from '@/framework/components/BaseDialog.vue'
import BaseRowSplit from '@/framework/components/BaseRowSplit.vue'
import BaseRowSplit2 from '@/framework/components/BaseRowSplit2.vue'
import BaseRowSplit3 from '@/framework/components/BaseRowSplit3.vue'
import TableOptions from '@/components/TableOptions/index.vue'
import DictTag from '@/framework/components/DictTag.vue'
import Pagination from '@/components/Pagination/index.vue'
import BaseTableToolbar from '@/framework/components/BaseTableToolbar.vue'
import Editor from '@/components/Editor/index.vue'
import FileUpload from '@/framework/components/FileUpload.vue'
import FileUploadDrag from '@/framework/components/FileUploadDrag.vue'
import ImageUpload from '@/components/ImageUpload/index.vue'
import ImagePreview from '@/framework/components/ImagePreview.vue'
import BaseDateRangePicker from '@/framework/components/BaseDateRangePicker.vue'
import Vue from 'vue'
import tab from '@/framework/tools/tab'
import BaseDownloadButton from '@/framework/components/BaseDownloadButton.vue'
import Tools, {
  ArrayUtil,
  AuthUtil, ConfigUtil,
  CookieUtil,
  CryptoUtil, DateUtil,
  DictUtil, DownloadUtil, ModalUtil,
  ObjectUtil, StorageUtil,
  StrUtil, TabUtil,
  UploadUtil,
  UrlUtil, ValidatorUtil
} from '@/framework/tools'
import BaseUploadButton from '@/framework/components/BaseUploadButton.vue'
import BaseRemoveButton from '@/framework/components/BaseRemoveButton.vue'
import BaseUpdateButton from '@/framework/components/BaseUpdateButton.vue'
import BaseAddButton from '@/framework/components/BaseAddButton.vue'
import BaseSearchButton from '@/framework/components/BaseSearchButton.vue'
import BaseResetButton from '@/framework/components/BaseResetButton.vue'
import BaseColumnMenu from '@/framework/components/BaseColumnMenu.vue'
import BaseTextButton from '@/framework/components/BaseTextButton.vue'
import BaseFormLabel from '@/framework/components/BaseFormLabel.vue'
import BaseCloseButton from '@/framework/components/BaseCloseButton.vue'
import BaseIconSelect from '@/framework/components/BaseIconSelect.vue'
import BaseDateTimeRangePicker from '@/framework/components/BaseDateTimeRangePicker.vue'
import BaseDetailButton from '@/framework/components/BaseDetailButton.vue'
import BaseDrawer from '@/framework/components/BaseDrawer.vue'

// 全局组件
function mountComponent() {
  Vue.component('OrgSelect', OrgSelect)
  Vue.component('PostSelect', PostSelect)
  Vue.component('RoleSelect', RoleSelect)
  Vue.component('BaseSwitch', BaseSwitch)
  Vue.component('DictSelect', DictSelect)
  Vue.component('DictTag', DictTag)
  Vue.component('OrgTree', OrgTree)
  Vue.component('DictRadio', DictRadio)
  Vue.component('BaseDialog', BaseDialog)
  Vue.component('BaseDrawer', BaseDrawer)
  Vue.component('BaseRowSplit', BaseRowSplit)
  Vue.component('BaseRowSplit2', BaseRowSplit2)
  Vue.component('BaseRowSplit3', BaseRowSplit3)
  Vue.component('TableOptions', TableOptions)
  Vue.component('Pagination', Pagination)
  Vue.component('RightToolbar', BaseTableToolbar)
  Vue.component('Editor', Editor)
  Vue.component('FileUpload', FileUpload)
  Vue.component('FileUploadDrag', FileUploadDrag)
  Vue.component('ImageUpload', ImageUpload)
  Vue.component('ImagePreview', ImagePreview)
  Vue.component('BaseDateRangePicker', BaseDateRangePicker)
  Vue.component('BaseDateTimeRangePicker', BaseDateTimeRangePicker)
  Vue.component('BaseDownloadButton', BaseDownloadButton)
  Vue.component('BaseUploadButton', BaseUploadButton)
  Vue.component('BaseDetailButton', BaseDetailButton)
  Vue.component('BaseRemoveButton', BaseRemoveButton)
  Vue.component('BaseUpdateButton', BaseUpdateButton)
  Vue.component('BaseAddButton', BaseAddButton)
  Vue.component('BaseSearchButton', BaseSearchButton)
  Vue.component('BaseResetButton', BaseResetButton)
  Vue.component('BaseCloseButton', BaseCloseButton)
  Vue.component('BaseColumnMenu', BaseColumnMenu)
  Vue.component('BaseTextButton', BaseTextButton)
  Vue.component('BaseFormLabel', BaseFormLabel)
  Vue.component('BaseIconSelect', BaseIconSelect)
}

// 全局指令
function mountDirective() {
  Vue.directive('hasRole', hasRole)
  Vue.directive('hasPerm', hasPerm)
  Vue.directive('clipboard', clipboard)
  Vue.directive('dialogDrag', dialogDrag)
  Vue.directive('dialogDragWidth', dialogDragWidth)
  Vue.directive('dialogDragHeight', dialogDragHeight)
}

// 全局混入
function mountMixin() {
  Vue.mixin(dict)
}

function mountPrototype() {
  // 全局挂载
  Vue.prototype.$tab = tab
  // 字符串工具
  Vue.prototype.$str = StrUtil
  // 浏览器Storage工具
  Vue.prototype.$storage = StorageUtil
  // 加密工具
  Vue.prototype.$crypto = CryptoUtil
  // url工具
  Vue.prototype.$url = UrlUtil
  // cookie工具
  Vue.prototype.$cookie = CookieUtil
  // 权限工具
  Vue.prototype.$auth = AuthUtil
  // 数据工具
  Vue.prototype.$array = ArrayUtil
  // 字典工具
  Vue.prototype.$dict = DictUtil
  // 上传工具
  Vue.prototype.$upload = UploadUtil
  // 对象工具
  Vue.prototype.$obj = ObjectUtil
  // 校验工具
  Vue.prototype.$validator = ValidatorUtil
  // 下载工具
  Vue.prototype.$download = DownloadUtil
  // 模态框对象
  Vue.prototype.$modal = ModalUtil
  // 日期工具
  Vue.prototype.$date = DateUtil
  // 页签工具
  Vue.prototype.$tab = TabUtil
  // 系统参数
  Vue.prototype.$config = ConfigUtil
  // 全量工具包
  Vue.prototype.$tools = Tools
}

const install = function(Vue) {
  mountPrototype()
  mountDirective()
  mountMixin()
  mountComponent()
}

export default install
