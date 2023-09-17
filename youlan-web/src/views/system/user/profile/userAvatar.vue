<template>
  <div>
    <div class="user-info-head" @click="handleEditAvatar">
      <img :src="options.avatar" title="点击上传头像" class="img-circle img-lg" alt="">
    </div>
    <el-dialog title="修改头像" :visible.sync="open" width="800px" append-to-body>
      <el-row>
        <el-col :xs="24" :md="12" :style="{height: '350px'}">
          <vue-cropper
            ref="cropper"
            :img="options.avatar"
            :info="options.info"
            :auto-crop="options.autoCrop"
            :auto-crop-width="options.autoCropWidth"
            :auto-crop-height="options.autoCropHeight"
            :fixed-box="options.fixedBox"
            :output-type="options.outputType"
            @realTime="handleRealTime"
          />
        </el-col>
        <el-col :xs="24" :md="12" :style="{height: '350px'}">
          <div class="avatar-upload-preview">
            <img :src="previews.url" :style="previews.img" alt="">
          </div>
        </el-col>
      </el-row>
      <br>
      <el-row>
        <el-col :lg="2" :sm="3" :xs="3">
          <el-upload
            action="#"
            accept=".jpg, .jpeg, .png, .gif"
            :http-request="requestUpload"
            :show-file-list="false"
            :before-upload="beforeUpload"
          >
            <el-button size="small" type="danger">更换头像</el-button>
          </el-upload>
        </el-col>
        <el-col :lg="{span: 1, offset: 2}" :sm="2" :xs="2">
          <el-button icon="el-icon-plus" size="small" @click="handleChangeScale(1)" />
        </el-col>
        <el-col :lg="{span: 1, offset: 1}" :sm="2" :xs="2">
          <el-button icon="el-icon-minus" size="small" @click="handleChangeScale(-1)" />
        </el-col>
        <el-col :lg="{span: 1, offset: 1}" :sm="2" :xs="2">
          <el-button icon="el-icon-refresh-left" size="small" @click="handleRotateLeft" />
        </el-col>
        <el-col :lg="{span: 1, offset: 1}" :sm="2" :xs="2">
          <el-button icon="el-icon-refresh-right" size="small" @click="handleRotateRight" />
        </el-col>
        <el-col :lg="{span: 2, offset: 6}" :sm="2" :xs="2">
          <el-button type="primary" size="small" @click="handleAvatarUpdate">确认更换</el-button>
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script>
import { VueCropper } from 'vue-cropper'

export default {
  components: { VueCropper },
  props: {
    user: {
      type: Object,
      default: {}
    }
  },
  data() {
    const avatar = this.$store.getters.avatar
    return {
      open: false,
      options: {
        name: avatar,
        avatar: avatar,
        info: true,
        autoCrop: true,
        autoCropWidth: 200,
        autoCropHeight: 200,
        fixedBox: true,
        outputType: 'png'
      },
      previews: {}
    }
  },
  methods: {
    // 编辑头像
    handleEditAvatar() {
      this.open = true
    },
    // 刷新组件
    refresh() {
      this.$refs.cropper.refresh()
    },
    // 覆盖默认的上传行为
    requestUpload() {
    },
    // 向左旋转
    handleRotateLeft() {
      this.$refs.cropper.rotateLeft()
    },
    // 向右旋转
    handleRotateRight() {
      this.$refs.cropper.rotateRight()
    },
    // 图片缩放
    handleChangeScale(num) {
      num = num || 1
      this.$refs.cropper.changeScale(num)
    },
    // 上传预处理
    beforeUpload(file) {
      if (file.type.indexOf('image/') === -1) {
        this.$modal.error('文件格式错误，请上传图片类型,如：JPG，PNG后缀的文件。')
      } else {
        this.$download.saveFileAsUrl(file).then(res => {
          this.options.avatar = res.data
          this.options.name = file.name
        })
      }
    },
    // 上传图片
    handleAvatarUpdate() {
      this.$refs.cropper.getCropBlob(data => {
        const formData = new FormData()
        formData.append('file', data, this.options.name)
        this.$upload.upload('/system/user/profile/uploadUserAvatar', formData).then(res => {
          this.$modal.success('更换成功')
          this.open = false
        }).catch(error => {
          console.log(error)
          this.$modal.error('更换失败')
        })
      })
    },
    // 实时预览
    handleRealTime(data) {
      this.previews = data
    }
  }
}
</script>
<style scoped lang="scss">
.user-info-head {
  position: relative;
  display: inline-block;
  height: 120px;
}

.user-info-head:hover:after {
  content: '+';
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  color: #eee;
  background: rgba(0, 0, 0, 0.5);
  font-size: 24px;
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  cursor: pointer;
  line-height: 110px;
  border-radius: 50%;
}
</style>
