<template>
  <div>
    <template v-for="item in dict[dictType]">
      <template v-if="item.value === value">
        <span v-if="!item.ui || item.ui === 'default'" :key="item.value" :class="item.class">
          {{ item.name }}
        </span>
        <el-tag v-else :key="item.value" :type="item.ui" disable-transitions :class="item.class">
          {{ item.name }}
        </el-tag>
      </template>
    </template>
    <template v-if="noMatchAny && showDefault">
      {{ defaultTag }}
    </template>
  </div>
</template>

<script>
export default {
  name: 'DictTag',
  props: {
    value: {
      type: String,
      default: null
    },
    dictType: {
      type: String,
      default: null
    },
    showDefault: {
      type: Boolean,
      default: false
    },
    defaultTag: {
      type: String,
      default: '未知'
    }
  },
  data() {
    return {
    }
  },
  computed: {
    noMatchAny() {
      if (this.$store.getters.dict[this.dictType]) {
        return this.$store.getters.dict[this.dictType].filter(item => {
          return item.value === this.value
        }).length === 0
      }
      return false
    }
  },
  created() {
    if (this.dictType) {
      this.$dict.loadDict(this.dictType)
    }
  }
}
</script>
<style scoped>
.el-tag + .el-tag {
  margin-left: 10px;
}
</style>
