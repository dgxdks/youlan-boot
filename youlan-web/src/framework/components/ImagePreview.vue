<template>
  <el-image
    :src="`${realSrc}`"
    fit="cover"
    :style="`width:${realWidth};height:${realHeight};`"
    :preview-src-list="realSrcList"
  >
    <div slot="error" class="image-slot">
      <i class="el-icon-picture-outline" />
    </div>
  </el-image>
</template>

<script>

export default {
  name: 'ImagePreview',
  props: {
    src: {
      type: String,
      default: ''
    },
    width: {
      type: [Number, String],
      default: ''
    },
    height: {
      type: [Number, String],
      default: ''
    }
  },
  computed: {
    realSrc() {
      if (!this.src) {
        return
      }
      const real_src = this.src.split(',')[0]
      return this.$download.parseSrcUrl(real_src)
    },
    realSrcList() {
      if (!this.src) {
        return
      }
      return this.src.split(',').map(item => {
        return this.$download.parseSrcUrl(item)
      })
    },
    realWidth() {
      return typeof this.width === 'string' ? this.width : `${this.width}px`
    },
    realHeight() {
      return typeof this.height === 'string' ? this.height : `${this.height}px`
    }
  }
}
</script>

<style lang="scss" scoped>
.el-image {
  border-radius: 5px;
  background-color: #ebeef5;
  box-shadow: 0 0 5px 1px #ccc;

  ::v-deep .el-image__inner {
    transition: all 0.3s;
    cursor: pointer;

    &:hover {
      transform: scale(1.2);
    }
  }

  ::v-deep .image-slot {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    color: #909399;
    font-size: 30px;
  }
}
</style>
