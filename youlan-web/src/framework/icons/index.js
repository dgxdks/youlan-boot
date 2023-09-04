import Vue from 'vue'
import BaseSvgIcon from '@/framework/components/BaseSvgIcon.vue'
// svg component

// register globally
Vue.component('svg-icon', BaseSvgIcon)
Vue.component('base-svg-icon', BaseSvgIcon)

const req = require.context('./svg', false, /\.svg$/)
const requireAll = requireContext => requireContext.keys().map(item => {
  requireContext(item)
  return item.match(/\.\/(.*)\.svg/)[1]
})
const icons = requireAll(req)
export default icons
