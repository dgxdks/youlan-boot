<template>
  <el-drawer
    :title="title"
    :visible.sync="visible"
    :direction="direction"
    :size="size"
    append-to-body
    :wrapper-closable="wrapperClosable"
    show-close
  >
    <slot />
  </el-drawer>
</template>

<script>
export default {
  name: 'BaseDrawer',
  props: {
    title: {
      type: String,
      default: null
    },
    open: {
      type: Boolean,
      default: false
    },
    direction: {
      type: String,
      default: 'rtl'
    },
    size: {
      type: String,
      default: '50%'
    },
    wrapperClosable: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      visible: false
    }
  },
  watch: {
    open(newVal, oldVal) {
      this.visible = newVal
    },
    visible(newVal, oldVal) {
      this.$emit('update:open', newVal)
    }
  },
  mounted() {
  },
  methods: {

  }
}
</script>
