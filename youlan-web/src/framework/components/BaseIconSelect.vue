<template>
  <div class="icon-body">
    <el-input v-model="icon" class="icon-search" clearable placeholder="请输入图标名称" @clear="handleFilterIcon" @input="handleFilterIcon">
      <i slot="suffix" class="el-icon-search el-input__icon" />
    </el-input>
    <div class="icon-list">
      <div class="list-container">
        <div v-for="(item, index) in iconList" :key="index" class="icon-item-wrapper" @click="handleChange(item)">
          <div :class="['icon-item', { active: icon === item }]">
            <svg-icon :icon-class="item" class-name="icon" style="height: 25px;width: 16px;" />
            <span>{{ item }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import icons from '@/framework/icons'
export default {
  name: 'BaseIconSelect',
  props: {
    value: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      icon: '',
      iconList: icons
    }
  },
  watch: {
    value: {
      handler(newVal) {
        this.icon = newVal
      },
      immediate: true
    },
    icon: {
      handler(newVal) {
        this.$emit('input', newVal)
      }
    }
  },
  methods: {
    handleFilterIcon() {
      this.iconList = icons
      if (this.icon) {
        this.iconList = this.iconList.filter(item => item.includes(this.icon))
      }
    },
    handleChange(name) {
      this.icon = name
      this.$emit('change', name)
      document.body.click()
    },
    reset() {
      this.icon = null
      this.iconList = icons
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .icon-body {
    width: 100%;
    padding: 10px;
    .icon-search {
      position: relative;
      margin-bottom: 5px;
    }
    .icon-list {
      height: 200px;
      overflow: auto;
      .list-container {
        display: flex;
        flex-wrap: wrap;
        .icon-item-wrapper {
          width: calc(100% / 3);
          height: 25px;
          line-height: 25px;
          cursor: pointer;
          display: flex;
          .icon-item {
            display: flex;
            max-width: 100%;
            height: 100%;
            padding: 0 5px;
            &:hover {
              background: #ececec;
              border-radius: 5px;
            }
            .icon {
              flex-shrink: 0;
            }
            span {
              display: inline-block;
              vertical-align: -0.15em;
              fill: currentColor;
              padding-left: 2px;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }
          .icon-item.active {
            background: #ececec;
            border-radius: 5px;
          }
        }
      }
    }
  }
</style>
