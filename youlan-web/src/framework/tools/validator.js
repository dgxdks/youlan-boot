export default {
  requiredRule(message, trigger) {
    return {
      required: true,
      message: message || '此项必须填写',
      trigger: trigger || ['blur']
    }
  },
  sizeRule(min, max, message, trigger) {
    return {
      min,
      max,
      message: message || `大小必须介于${min} 和 ${max}之间`,
      trigger: trigger || ['blur']
    }
  },
  emailRule(message, trigger) {
    return {
      type: 'email',
      message: message || '邮箱格式不正确',
      trigger: trigger || ['blur', 'change']
    }
  },
  mobileRule(message, trigger) {
    return {
      pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
      message: message || '手机号码格式不正确',
      trigger: trigger || ['blur']
    }
  }
}
