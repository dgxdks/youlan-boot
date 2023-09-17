<template>
  <div class="login">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
      <h3 class="title">{{ $store.state.settings.loginTitle }}</h3>
      <el-form-item prop="userName">
        <el-input
          v-model="loginForm.userName"
          auto-complete="off"
          placeholder="账号"
          type="text"
        >
          <svg-icon slot="prefix" class="el-input__icon input-icon" icon-class="user" />
        </el-input>
      </el-form-item>
      <el-form-item prop="userPassword">
        <el-input
          v-model="loginForm.userPassword"
          auto-complete="off"
          placeholder="密码"
          type="password"
          @keyup.enter.native="handleLogin"
        >
          <svg-icon slot="prefix" class="el-input__icon input-icon" icon-class="password" />
        </el-input>
      </el-form-item>
      <el-form-item v-if="captchaEnabled" prop="captchaCode">
        <el-input
          v-model="loginForm.captchaCode"
          auto-complete="off"
          placeholder="验证码"
          style="width: 63%"
          @keyup.enter.native="handleLogin"
        >
          <svg-icon slot="prefix" class="el-input__icon input-icon" icon-class="validCode" />
        </el-input>
        <div class="login-code">
          <img :src="captchaImg" alt="" class="login-code-img" @click="getCode">
        </div>
      </el-form-item>
      <el-checkbox v-model="loginForm.rememberMe" style="margin:0 0 25px 0;">记住密码</el-checkbox>
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="medium"
          style="width:100%;"
          type="primary"
          @click.native.prevent="handleLogin"
        >
          <span v-if="!loading">登 录</span>
          <span v-else>登 录 中...</span>
        </el-button>
        <div v-if="register" style="float: right;">
          <router-link :to="'/register'" class="link-type">立即注册</router-link>
        </div>
      </el-form-item>
    </el-form>
    <!--  底部  -->
    <div class="el-login-footer">
      <span v-html="$store.state.settings.loginFooter" />
    </div>
  </div>
</template>

<script>

import { getImageCaptcha } from '@/api/system/captcha'

export default {
  name: 'Login',
  data() {
    return {
      captchaImg: null,
      loginForm: {
        userName: null,
        userPassword: null,
        rememberMe: false,
        captchaCode: null,
        captchaId: null
      },
      loginRules: {
        userName: [
          { required: true, trigger: 'blur', message: '请输入您的账号' }
        ],
        userPassword: [
          { required: true, trigger: 'blur', message: '请输入您的密码' }
        ],
        captchaCode: [{ required: true, trigger: 'change', message: '请输入验证码' }]
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: true,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      getImageCaptcha().then(res => {
        this.captchaEnabled = res.data.captchaEnabled
        if (this.captchaEnabled) {
          this.captchaImg = 'data:image/gif;base64,' + res.data.captchaImg
          this.loginForm.captchaId = res.data.captchaId
        }
      })
    },
    getCookie() {
      const userPassword = this.$cookie.getUserPassword()
      this.loginForm = {
        userName: this.$cookie.getUserName(),
        userPassword: this.$str.isNotBlank(userPassword) ? this.$crypto.aesDecrypt(userPassword) : null,
        rememberMe: this.$cookie.getRememberMe()
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            this.$cookie.setUserName(this.loginForm.userName)
            this.$cookie.setUserPassword(this.loginForm.userPassword)
            this.$cookie.setRememberMe(this.loginForm.rememberMe)
          } else {
            this.$cookie.removeUserName()
            this.$cookie.removeUserPassword()
            this.$cookie.removeRememberMe()
          }
          this.$store.dispatch('AccountLogin', this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || '/' }).catch(error => {
              console.log(error)
            })
          }).catch(error => {
            console.log(error)
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    }
  }
}
</script>

<style lang="scss" rel="stylesheet/scss">
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url("../assets/images/login-background3.jpeg");
  background-size: cover;
}

.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

.login-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;

  .el-input {
    height: 38px;

    input {
      height: 38px;
    }
  }

  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}

.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}

.login-code {
  width: 33%;
  height: 38px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle;
  }
}

.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}

.login-code-img {
  height: 38px;
}
</style>
