const req = require.context('./svg', false, /\.svg$/)
const requireAll = requireContext => requireContext.keys().map(item => {
  requireContext(item)
  return item.match(/\.\/(.*)\.svg/)[1]
})
const icons = requireAll(req)
export default icons
