<template>
  <el-form ref="form" :model="user" :rules="rules" label-width="80px">
    <el-form-item label="用户昵称" prop="nickName">
      <el-input v-model="user.nickName" maxlength="30" />
    </el-form-item>
    <el-form-item label="手机号码" prop="userMobile">
      <el-input v-model="user.userMobile" maxlength="11" />
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input v-model="user.email" maxlength="50" />
    </el-form-item>
    <el-form-item label="性别">
      <!-- 排除掉未知-->
      <dict-radio v-model="user.sex" dict-type="sys_user_sex" :exclude="['3']" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" size="mini" @click="submit">保存</el-button>
      <el-button type="danger" size="mini" @click="close">关闭</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { updateUserProfile } from '@/api/system/user'

export default {
  props: {
    user: {
      type: Object,
      default: {}
    }
  },
  data() {
    return {
      // 表单校验
      rules: {
        nickName: [
          this.$validator.requiredRule('用户昵称不能为空')
        ],
        email: [
          this.$validator.requiredRule('邮箱地址不能为空'),
          this.$validator.emailRule('请输入正确的邮箱地址')
        ],
        userMobile: [
          this.$validator.requiredRule('手机号码不能为空'),
          this.$validator.mobileRule('请输入正确的手机号码')
        ]
      }
    }
  },

  methods: {
    submit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          const data = {
            id: this.user.id,
            nickName: this.user.nickName,
            userMobile: this.user.userMobile,
            email: this.user.email,
            sex: this.user.sex,
            orgId: this.user.orgId
          }
          updateUserProfile(data).then(res => {
            this.$modal.success('修改成功')
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
