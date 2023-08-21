<template>
  <el-upload
    ref="upload"
    :headers="headers"
    :disabled="disabled"
    :action="action"
    :accept="accept"
    :auto-upload="autoUpload"
    :http-request="httpRequest"
    :on-remove="onRemove"
    :on-error="onError"
    :on-success="onSuccess"
    :on-exceed="onExceed"
    :file-list="fileList"
    :limit="limit"
    :before-upload="beforeUpload"
    drag
  >
    <i class="el-icon-upload" />
    <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
    <template slot="tip" class="el-upload__tip text-center">
      <slot name="tip" />
    </template>
  </el-upload>
</template>

<script>
export default {
  name: 'FileUploadDrag',
  props: {
    value: {
      type: [String, Array],
      default: null
    },
    accept: {
      type: String,
      default: '.xlsx, .xls'
    },
    action: {
      type: String,
      default: '/system/storage/upload'
    },
    autoUpload: {
      type: Boolean,
      default: false
    },
    disabled: {
      type: Boolean,
      default: false
    },
    headers: {
      type: Object,
      default: () => {
      }
    },
    limit: {
      type: Number,
      default: 1
    },
    platform: {
      type: String,
      default: null
    },
    paramsData: {
      type: Object,
      default: () => {}
    },
    formData: {
      type: Object,
      default: () => {
      }
    },
    fileName: {
      type: String,
      default: 'file'
    },
    fileSize: {
      type: Number,
      default: 5 * 1024 * 1024
    }
  },
  data() {
    return {
      fileList: []
    }
  },
  watch: {
    value: {
      handler(newVal) {
        if (!newVal) {
          return
        }
        if (!this.$obj.isString(newVal)) {
          this.fileList = newVal
          return
        }
        this.fileList = newVal.split(',').map(item => {
          return {
            name: item,
            url: item
          }
        })
      },
      immediate: true,
      deep: true
    }
  },
  mounted() {

  },
  methods: {
    updateValue() {
      const value = this.fileList.map(item => item.url).join(',')
      this.$emit('input', value)
    },
    submit() {
      this.$refs.upload.submit()
    },
    onRemove(file, fileList) {
      this.fileList = fileList
      this.updateValue()
    },
    onSuccess(response, file, fileList) {
      this.$modal.loadingClose()
      this.fileList = fileList.map(item => {
        return {
          ...item,
          ...item.response
        }
      })
      this.updateValue()
    },
    onError(error, file, fileList) {
      this.$modal.error(error)
    },
    onExceed(files, fileList) {

    },
    beforeUpload(file) {
      if (file.size && file.size > this.fileSize) {
        this.$modal.error(`文件上传大小不能超过${this.fileSize / 1024 / 1024}MB`)
      }
      this.$modal.loading('正在上传中, 请稍等...')
    },
    httpRequest(context) {
      const { action } = context
      const formParams = {
        platform: this.$str.isNotBlank(this.platform) ? this.platform : null,
        [this.fileName]: context.file,
        ...this.formData
      }
      this.$upload.upload(action, formParams, this.paramsData, this.headers).then(res => {
        context.onSuccess(res)
      }).catch(error => {
        context.onError(error)
      })
    }
  }
}
</script>
