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
    <template slot="tip">
      <div slot="tip" class="el-upload__tip text-center">
        <div slot="tip" class="el-upload__tip">
          <slot name="tip" />
        </div>
        <slot />
      </div>
    </template>
  </el-upload>
</template>

<script>
import upload from '@/mixin/upload'

export default {
  name: 'FileUploadDrag',
  mixins: [upload],
  props: {
    accept: {
      type: String,
      default: '.xlsx, .xls'
    }
  }
}
</script>
