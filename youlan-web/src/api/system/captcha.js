import request from '@/framework/tools/request'

// 获取验证码
export function getImageCaptcha() {
  return request({
    url: '/system/captcha/getImageCaptcha',
    method: 'post',
    timeout: 20000
  })
}
