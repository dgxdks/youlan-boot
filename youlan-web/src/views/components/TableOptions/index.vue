<template>
  <el-row v-show="show" :gutter="gutter" class="mb8">
    <el-col v-for="option in optionList" :key="option" :span="span">
      <slot :name="option" />
    </el-col>
    <slot />
  </el-row>
</template>

<script>
export default {
  name: 'TableOptions',
  props: {
    gutter: {
      type: Number,
      default: 10
    },
    span: {
      type: Number,
      default: 1.5
    },
    show: {
      type: Boolean,
      default: true
    },
    showToolbar: {
      type: Boolean,
      default: true
    },
    showQuery: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      showSearch: true,
      optionList: []
    }
  },
  watch: {
    showSearch(newVal) {
      this.$emit('update:showQuery', newVal)
    }
  },
  created() {
    this.getColList()
  },
  methods: {
    getColList() {
      const optionList = []
      for (const name in this.$slots) {
        if (name !== 'default') {
          // 动态生成的内容需要排除掉匿名插槽
          optionList.push(name)
        }
      }
      this.optionList = optionList
    },
    queryTable() {
      this.$emit('doQuery')
    }
  }
}
</script>
