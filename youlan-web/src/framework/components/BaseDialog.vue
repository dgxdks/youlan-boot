<template>
  <el-dialog
    :title="title"
    :visible.sync="visible"
    :append-to-body="appendToBody"
    :top="top"
    :width="width"
    :close-on-click-modal="closeOnClickModal"
  >
    <slot />
    <div slot="footer" class="dialog-footer">
      <el-button v-if="!confirmBtnDisabled" type="primary" @click="confirm">确 定</el-button>
      <el-button v-if="!cancelBtnDisabled" @click="cancel">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'BaseDialog',
  props: {
    open: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '操作'
    },
    width: {
      type: String,
      default: '600px'
    },
    closeOnClickModal: {
      type: Boolean,
      default: false
    },
    appendToBody: {
      type: Boolean,
      default: true
    },
    top: {
      type: String,
      default: '15vh'
    },
    confirmBtnDisabled: {
      type: Boolean,
      default: false
    },
    cancelBtnDisabled: {
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
    confirm() {
      this.$emit('confirm')
    },
    cancel() {
      this.visible = false
      this.$emit('cancel')
    },
    setTitle(title) {
      this.title = title
    }
  }
}
</script>
