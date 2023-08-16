import { Loading, Message, MessageBox, Notification } from 'element-ui'

export default {
  // 消息提示
  infoM(content) {
    Message.info(content)
  },
  // 错误消息
  error(content) {
    Message.error(content)
  },
  // 成功消息
  success(content) {
    Message.success(content)
  },
  // 警告消息
  warning(content) {
    Message.warning(content)
  },
  // 弹出提示
  alert(content) {
    return MessageBox.alert(content, '系统提示')
  },
  // 错误提示
  alertError(content) {
    return MessageBox.alert(content, '系统提示', { type: 'error' })
  },
  // 成功提示
  alertSuccess(content) {
    return MessageBox.alert(content, '系统提示', { type: 'success' })
  },
  // 警告提示
  alertWarning(content) {
    return MessageBox.alert(content, '系统提示', { type: 'warning' })
  },
  // 通知提示
  notify(content) {
    Notification.info(content)
  },
  // 错误通知
  notifyError(content) {
    Notification.error(content)
  },
  // 成功通知
  notifySuccess(content) {
    Notification.success(content)
  },
  // 警告通知
  notifyWarning(content) {
    Notification.warning(content)
  },
  // 确认窗体
  confirm(content) {
    return MessageBox.confirm(content, '系统提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  },
  // 提交内容
  prompt(content) {
    return MessageBox.prompt(content, '系统提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  },
  // 打开遮罩层
  loading(content) {
    return new Promise((resolve) => {
      let loading = Loading.service({
        lock: true,
        text: content,
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      resolve(loading)
    })
  }
}
