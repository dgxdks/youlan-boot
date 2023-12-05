<template>
  <el-form ref="editForm" :model="editForm" :rules="editRules" label-width="120px">
    <el-form-item label="JSAPI支付" prop="status">
      <base-form-label slot="label" content="微信JSAPI支付配置" label="JSAPI支付" />
      <pay-config-select v-model="editForm.WX_JSAPI" :pay-configs="payConfigs" placeholder="请关联微信JSAPI支付配置" />
    </el-form-item>
    <el-form-item label="APP支付" prop="status">
      <base-form-label slot="label" content="微信APP支付配置" label="APP支付" />
      <pay-config-select v-model="editForm.WX_APP" :pay-configs="payConfigs" placeholder="请关联微信APP支付配置" />
    </el-form-item>
    <el-form-item label="H5支付" prop="status">
      <base-form-label slot="label" content="微信H5支付配置" label="H5支付" />
      <pay-config-select v-model="editForm.WX_H5" :pay-configs="payConfigs" placeholder="请关联微信H5支付配置" />
    </el-form-item>
    <el-form-item label="Native支付" prop="status">
      <base-form-label slot="label" content="微信Native支付配置" label="Native支付" />
      <pay-config-select v-model="editForm.WX_NATIVE" :pay-configs="payConfigs" placeholder="请关联微信Native支付配置" />
    </el-form-item>
    <el-form-item label="小程序支付" prop="status">
      <base-form-label slot="label" content="微信小程序支付配置" label="小程序支付" />
      <pay-config-select v-model="editForm.WX_MINI" :pay-configs="payConfigs" placeholder="请关联微信小程序支付配置" />
    </el-form-item>
    <el-form-item label="付款码支付" prop="status">
      <base-form-label slot="label" content="微信付款码支付配置" label="付款码支付" />
      <pay-config-select v-model="editForm.WX_SCAN" :pay-configs="payConfigs" placeholder="请关联微信付款码支付配置" />
    </el-form-item>
  </el-form>
</template>

<script>
import crud from '@/framework/mixin/crud'
import PayConfigSelect from '@/views/pay/components/PayConfigSelect.vue'
import { getPayConfigList } from '@/api/pay/config'

export default {
  name: 'WechatConfigForm',
  components: { PayConfigSelect },
  mixins: [crud],
  props: {
    value: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      editForm: {},
      payConfigs: []
    }
  },
  watch: {
    editForm: {
      handler(newVal, oldVal) {
        this.$emit('input', newVal)
      },
      deep: true
    },
    value: {
      handler(newVal, oldVal) {
        console.log(newVal, 'newVal')
        this.editForm = newVal
      },
      immediate: true,
      deep: true
    }
  },
  mounted() {
    this.getPayConfigList()
  },
  methods: {
    getPayConfigList() {
      getPayConfigList({ payType: '1' }).then(res => {
        this.payConfigs = res.data
      })
    },
    resetEditForm() {
      this.editForm = {
        WX_JSAPI: null,
        WX_APP: null,
        WX_H5: null,
        WX_NATIVE: null,
        WX_MINI: null,
        WX_SCAN: null
      }
      this.$refs.editForm && this.$refs.editForm.resetFields()
    }
  }
}
</script>
