module.exports = {
  /**
   * 登录页标题内容
   */
  loginTitle: '幽兰后台管理系统',
  /**
   * 登录页底部内容
   */
  loginFooter: 'Copyright © 2023 <a href="https://youlan.online" target="_blank">youlan.online</a> All Rights Reserved.',
  /**
   * token值前缀
   */
  tokenValuePrefix: '',
  /**
   * 允许重复提交的最小间隔时间(ms)
   */
  canRepeatSubmitMinInterval: 1000,
  /**
   * 是否允许重复提交
   */
  canRepeatSubmit: false,
  /**
   * 侧边栏主题 深色主题theme-dark，浅色主题theme-light
   */
  sideTheme: 'theme-dark',
  /**
   * 是否系统布局配置
   */
  showSettings: false,
  /**
   * 是否显示顶部导航
   */
  topNav: false,
  /**
   * 是否显示 tagsView
   */
  tagsView: true,
  /**
   * 是否固定头部
   */
  fixedHeader: false,
  /**
   * 是否显示logo
   */
  sidebarLogo: true,
  /**
   * 是否显示动态标题
   */
  dynamicTitle: false,
  /**
   * @type {string | array} 'production' | ['production', 'development']
   * @description Need show err logs component.
   * The default is only used in the production env
   * If you want to also use it in dev, you can pass ['production', 'development']
   */
  errorLog: 'production'
}
