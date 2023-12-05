<template>
  <div class="component-upload-image">
    <el-upload
      ref="upload"
      :auto-upload="autoUpload"
      :action="action"
      :before-upload="beforeUpload"
      :class="{hide: fileList.length >= limit}"
      :accept="accept"
      :file-list="fileList"
      :headers="headers"
      :limit="limit"
      :on-error="onError"
      :on-exceed="onExceed"
      :on-preview="onPreview"
      :on-remove="onRemove"
      :on-success="onSuccess"
      :on-change="onChange"
      :http-request="httpRequest"
      :show-file-list="true"
      list-type="picture-card"
      multiple
    >
      <i slot="default" class="el-icon-plus" />
      <div slot="file" slot-scope="{file}">
        <img class="el-upload-list__item-thumbnail" :src="file.url" alt="">
        <span class="el-upload-list__item-actions">
          <span
            v-if="showPreview"
            class="el-upload-list__item-preview"
            @click="handlePreview(file)"
          >
            <i class="el-icon-zoom-in" />
          </span>
          <span
            v-if="showDownload"
            class="el-upload-list__item-delete"
            @click="handleDownload(file)"
          >
            <i class="el-icon-download" />
          </span>
          <span
            v-if="showRemove"
            class="el-upload-list__item-delete"
            @click="handleRemove(file)"
          >
            <i class="el-icon-delete" />
          </span>
        </span>
      </div>
    </el-upload>

    <!-- 上传提示 -->
    <div v-if="isShowTip" slot="tip" class="el-upload__tip">
      请上传
      <template v-if="fileSize"> 大小不超过 <b style="color: #f56c6c">{{ fileSize }}MB</b></template>
      <template v-if="accept"> 格式为 <b style="color: #f56c6c">{{ accept }}</b></template>
      的文件
    </div>

    <el-dialog :visible.sync="previewImageOpen" append-to-body title="图片预览" width="800">
      <img :src="previewImageUrl" style="display: block; max-width: 100%; margin: 0 auto" alt="">
    </el-dialog>
  </div>
</template>

<script>

import upload from '@/framework/mixin/upload'

export default {
  name: 'ImageUpload',
  mixins: [upload],
  props: {
    // 文件类型, 例如['png', 'jpg', 'jpeg']
    accept: {
      type: String,
      default: '.png, .jpg, .jpeg'
    },
    // 是否显示提示
    isShowTip: {
      type: Boolean,
      default: true
    },
    // 是否展示删除
    showRemove: {
      type: Boolean,
      default: true
    },
    // 是否展示预览
    showPreview: {
      type: Boolean,
      default: true
    },
    // 是否展示下载按钮
    showDownload: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      previewImageUrl: null,
      previewImageOpen: false
    }
  },
  methods: {
    // 下载
    handleDownload(file) {
      this.$download.saveUrlAsFile(file.url)
    },
    // 预览
    handlePreview(file) {
      this.previewImageUrl = file.url
      this.previewImageOpen = true
    },
    // 删除
    handleRemove(file) {
      this.removeFile(file)
    }
  }
}
</script>
<style lang="scss" scoped>
// .el-upload--picture-card 控制加号部分
::v-deep.hide .el-upload--picture-card {
  display: none;
}

// 去掉动画效果
::v-deep .el-list-enter-active,
::v-deep .el-list-leave-active {
  transition: all 0s;
}

::v-deep .el-list-enter, .el-list-leave-active {
  opacity: 0;
  transform: translateY(0);
}
</style>

