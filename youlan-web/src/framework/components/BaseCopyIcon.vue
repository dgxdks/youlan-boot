<template>
  <el-link
    v-clipboard:copy="value"
    v-clipboard:success="handleSuccess"
    v-clipboard:error="handleError"
    type="primary"
    :underline="false"
    icon="el-icon-document-copy"
  >
    <slot />
  </el-link>
</template>

<script>
export default {
  name: 'BaseCopyIcon',
  props: {
    value: {
      type: String,
      default: null
    },
    showMessage: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {}
  },
  watch: {},
  mounted() {
  },
  methods: {
    handleSuccess(context) {
      if (this.showMessage) {
        this.$modal.success('复制成功')
      }
      this.$emit('success', context)
    },
    handleError(context) {
      if (this.showMessage) {
        this.$modal.error('复制失败')
      }
      this.$emit('error', context)
    }
  }
}
</script>
