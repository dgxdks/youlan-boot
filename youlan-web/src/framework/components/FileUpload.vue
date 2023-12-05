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
    :on-change="onChange"
    :file-list="fileList"
    :limit="limit"
    :before-upload="beforeUpload"
    :multiple="multiple"
  >
    <el-button size="mini" type="primary">选取文件</el-button>
    <template v-if="showTips" slot="tip">
      <div slot="tip" class="el-upload__tip ">
        <div slot="tip" class="el-upload__tip">
          <slot v-if="$slots.tip" name="tip" />
          <template v-else>
            请上传
            <template v-if="fileSize"> 大小不超过 <b style="color: #f56c6c">{{ fileSize }}MB</b></template>
            <template v-if="accept"> 格式为 <b style="color: #f56c6c">{{ accept }}</b></template>
            的文件
          </template>
        </div>
        <slot />
      </div>
    </template>
  </el-upload>
</template>

<script>

import upload from '@/framework/mixin/upload'

export default {
  name: 'FileUpload',
  mixins: [upload]
}
</script>

<style lang="scss" scoped>
.upload-file-uploader {
  margin-bottom: 5px;
}

.upload-file-list .el-upload-list__item {
  border: 1px solid #e4e7ed;
  line-height: 2;
  margin-bottom: 10px;
  position: relative;
}

.upload-file-list .ele-upload-list__item-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: inherit;
}

.ele-upload-list__item-content-action .el-link {
  margin-right: 10px;
}
</style>
