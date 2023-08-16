const getters = {
  sidebar: state => state.app.sidebar,
  size: state => state.app.size,
  device: state => state.app.device,
  dict: state => state.dict.dict,
  visitedViews: state => state.tagsView.visitedViews,
  cachedViews: state => state.tagsView.cachedViews,
  tokenName: state => state.user.tokenName,
  tokenValue: state => state.user.tokenValue,
  avatar: state => state.user.avatar,
  userName: state => state.user.userName,
  roleList: state => state.user.roleList,
  permissionList: state => state.user.permissionList,
  permission_routes: state => state.permission.routes,
  topbarRouters: state => state.permission.topbarRoutes,
  defaultRoutes: state => state.permission.defaultRoutes,
  sidebarRouters: state => state.permission.sidebarRoutes
}
export default getters
