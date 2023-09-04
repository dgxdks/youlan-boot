<template>
  <el-form ref="form" :model="user" :rules="rules" label-width="80px">
    <el-form-item label="旧密码" prop="oldPasswd">
      <el-input v-model="user.oldPasswd" placeholder="请输入旧密码" type="password" show-password />
    </el-form-item>
    <el-form-item label="新密码" prop="newPasswd">
      <el-input v-model="user.newPasswd" placeholder="请输入新密码" type="password" show-password />
    </el-form-item>
    <el-form-item label="确认密码" prop="confirmPasswd">
      <el-input v-model="user.confirmPasswd" placeholder="请确认新密码" type="password" show-password />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" size="mini" @click="submit">保存</el-button>
      <el-button type="danger" size="mini" @click="close">关闭</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { updateUserPasswd } from '@/api/system/user'

export default {
  data() {
    const equalToPassword = (rule, value, callback) => {
      if (this.user.newPasswd !== value) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      user: {
        oldPasswd: null,
        newPasswd: null,
        confirmPasswd: null
      },
      // 表单校验
      rules: {
        oldPasswd: [
          { required: true, message: '旧密码不能为空', trigger: 'blur' }
        ],
        newPasswd: [
          { required: true, message: '新密码不能为空', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        confirmPasswd: [
          { required: true, message: '确认密码不能为空', trigger: 'blur' },
          { required: true, validator: equalToPassword, trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    submit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          const data = {
            oldPasswd: this.$crypto.aesEncrypt(this.user.oldPasswd),
            newPasswd: this.$crypto.aesEncrypt(this.user.newPasswd),
            confirmPasswd: this.$crypto.aesEncrypt(this.user.confirmPasswd)
          }
          updateUserPasswd(data).then(res => {
            this.$modal.success('修改成功')
            this.$refs.form.resetFields()
          })
        }
      })
    },
    close() {
      this.$tab.closePage()
    }
  }
}
</script>
