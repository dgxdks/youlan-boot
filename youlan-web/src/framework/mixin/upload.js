export default {
  props: {
    value: {
      type: String,
      default: null
    },
    files: {
      type: Array,
      default: () => []
    },
    accept: {
      type: String,
      default: null
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
    },
    multiple: {
      type: Boolean,
      default: false
    },
    timeout: {
      type: Number,
      default: 60 * 1000
    }
  },
  data() {
    return {
      fileList: []
    }
  },
  watch: {
    value: {
      handler(urls) {
        this.mergeUrls(urls)
      },
      immediate: true,
      deep: true
    }
  },
  mounted() {

  },
  methods: {
    mergeUrls(urls) {
      if (this.$str.isBlank(urls)) {
        return
      }
      urls.split(',').map(url => {
        const file = this.fileList.some(file => file.url === url)
        if (file) {
          return file
        } else {
          return {
            name: url,
            url: url
          }
        }
      })
    },
    updateValue() {
      this.$emit('input', this.fileList.map(item => item.url).join(','))
    },
    submit() {
      this.$refs.upload && this.$refs.upload.submit()
    },
    clear() {
      this.fileList = []
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
      this.$emit('onSuccess', response, file, fileList)
    },
    onError(error, file, fileList) {
      this.$modal.loadingClose()
      this.$emit('onError', error, file, fileList)
      this.$modal.error(error)
    },
    onExceed(files, fileList) {
      this.$modal.loadingClose()
      this.$emit('onExceed', files, fileList)
    },
    beforeUpload(file) {
      if (file.size && file.size > this.fileSize) {
        this.$modal.error(`文件上传大小不能超过${this.fileSize / 1024 / 1024}MB`)
      }
      this.$modal.loading('正在上传中, 请稍等...')
    },
    httpRequest(context) {
      const { action } = context
      const formData = new FormData()
      formData.append('platform', this.$str.isNotBlank(this.platform) ? this.platform : null)
      formData.append(this.fileName, context.file)
      for (const key of this.formData) {
        formData.append(key, this.formData[key])
      }
      this.$upload.upload(action, formData, this.paramsData, this.headers, { timeout: this.timeout }).then(res => {
        context.onSuccess(res)
      }).catch(error => {
        console.log(error)
        context.onError(error)
      })
    }
  }
}
