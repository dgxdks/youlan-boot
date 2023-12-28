<!--eslint-disable-->
<template>
  <el-form ref="editForm" :model="editForm" :rules="editRules" label-width="120px">
    <el-form-item label="API版本" prop="apiVersion">
      <el-radio-group v-model="editForm.apiVersion">
        <el-radio v-for="item in apiVersionList" :key="item" disable-transitions :label="item">
          {{ item }}
        </el-radio>
      </el-radio-group>
    </el-form-item>
    <base-row-split2>
      <el-form-item label="应用ID" prop="appId">
        <base-form-label slot="label" content="微信公众号或者小程序等的appid" label="应用ID"/>
        <el-input v-model="editForm.appId" placeholder="请输入应用ID"/>
      </el-form-item>
      <el-form-item label="商户ID" prop="mchId">
        <base-form-label slot="label" content="微信支付商户号" label="商户ID"/>
        <el-input v-model="editForm.mchId" placeholder="请输入商户ID"/>
      </el-form-item>
      <el-form-item label="子应用ID" prop="subAppId">
        <base-form-label slot="label" content="服务商模式下的子商户公众账号或者小程序等的appid" label="子应用ID"/>
        <el-input v-model="editForm.subAppId" placeholder="请输入子应用ID"/>
      </el-form-item>
      <el-form-item label="子商户ID" prop="subMchId">
        <base-form-label slot="label" content="服务商模式下的子商户号" label="子商户ID"/>
        <el-input v-model="editForm.subMchId" placeholder="请输入子商户ID"/>
      </el-form-item>
    </base-row-split2>
    <el-form-item label="商户秘钥" prop="mchKey">
      <base-form-label slot="label" content="微信支付商户密钥" label="商户秘钥"/>
      <el-input type="textarea" v-model="editForm.mchKey" placeholder="请输入商户秘钥" rows="5"/>
    </el-form-item>
    <el-form-item v-if="isV2" label="appclient_cert.p12证书" prop="keyContent">
      <base-form-label slot="label" content="apiclient_cert.p12证书文件(编辑时不重新上传证书会使用之前上传的证书)" label="appclient_cert.p12证书"/>
      <file-upload-drag ref="keyContent" :limit="1" accept=".p12" @onChange="handleKeyContent"></file-upload-drag>
    </el-form-item>
    <el-form-item v-if="isV3" label="appclient_key.pem证书" prop="privateKeyContent">
      <base-form-label slot="label" content="appclient_key.pem证书文件(编辑时不重新上传证书会使用之前上传的证书)" label="appclient_key.pem证书"/>
      <file-upload-drag ref="privateKeyContent" :limit="1" accept=".pem"
                        @onChange="handlePrivateKeyContent"></file-upload-drag>
    </el-form-item>
    <el-form-item v-if="isV3" label="appclient_cert.pem证书" prop="privateCertContent">
      <base-form-label slot="label" content="appclient_cert.pem证书文件(编辑时不重新上传证书会使用之前上传的证书)" label="appclient_cert.pem证书"/>
      <file-upload-drag ref="privateCertContent" :limit="1" accept=".pem"
                        @onChange="handlePrivateCertContent"></file-upload-drag>
    </el-form-item>
  </el-form>
</template>

<script>
export default {
  name: 'WechatConfigFrom',
  props: {
    params: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      apiVersionList: ['V2', 'V3'],
      editForm: {},
      editRules: {
        mchId: [
          this.$validator.requiredRule('商户ID不能为空')
        ],
        mchKey: [
          this.$validator.requiredRule('商户秘钥不能为空')
        ],
        keyContent: [
          this.$validator.requiredRule('appclient_cert.p12证书必须上传')
        ],
        privateKeyContent: [
          this.$validator.requiredRule('appclient_key.pem证书必须上传')
        ],
        privateCertContent: [
          this.$validator.requiredRule('appclient_cert.pem证书必须上传')
        ]
      }
    }
  },
  computed: {
    isV2() {
      return this.editForm.apiVersion === 'V2'
    },
    isV3() {
      return this.editForm.apiVersion === 'V3'
    }
  },
  watch: {
    params: {
      handler(newVal, oldVal) {
        if (newVal) {
          this.editForm = {
            ...this.editForm,
            ...this.$json.toJsonObject(this.$json.toJsonObject(newVal))
          }
        } else {
          this.resetEditForm()
        }
      },
      immediate: true
    },
    editForm: {
      handler(newVal, oldVal) {
        this.$emit('update:params', this.$json.toJsonStr(newVal))
      },
      deep: true
    },
    'editForm.apiVersion': {
      handler(newVal) {
        this.$refs.editForm && this.$refs.editForm.clearValidate()
      }
    }
  },
  mounted() {
  },
  methods: {
    validate(callback) {
      return this.$refs.editForm.validate(callback)
    },
    resetEditForm() {
      this.editForm = {
        apiVersion: 'V2',
        appId: null,
        mchId: null,
        mchKey: null,
        keyContent: null,
        privateKeyContent: null,
        privateCertContent: null,
        subAppId: null,
        subMchId: null
      }
      this.$refs.editForm && this.$refs.editForm.resetFields()
      this.$refs.keyContent && this.$refs.keyContent.clear()
      this.$refs.privateKeyContent && this.$refs.privateKeyContent.clear()
      this.$refs.privateCertContent && this.$refs.privateCertContent.clear()
    },
    handleKeyContent(file, fileList) {
      this.$download.saveFileAsBase64(file.raw).then(res => {
        this.editForm.keyContent = res
      }).catch(error => {
        console.log(error)
        this.$modal.error('apiclient_cert.p12证书上传失败')
      })
    },
    handlePrivateKeyContent(file, fileList) {
      this.$download.saveFileAsText(file.raw).then(res => {
        this.editForm.privateKeyContent = res
      }).catch(error => {
        console.log(error)
        this.$modal.error('apiclient_key.pem证书上传失败')
      })
    },
    handlePrivateCertContent(file, fileList) {
      this.$download.saveFileAsText(file.raw).then(res => {
        this.editForm.privateCertContent = res
      }).catch(error => {
        console.log(error)
        this.$modal.error('apiclient_cert.pem证书上传失败')
      })
    }
  }
}
</script>

<style lang="scss">
.el-upload-dragger {
  width: 100%;
}

.el-upload {
  width: 100%;
}
</style>
