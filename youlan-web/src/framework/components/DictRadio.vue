<template>
  <el-radio-group v-model="data">
    <template v-for="item in dict[dictType]">
      <el-radio v-if="!exclude.includes(item.value)" :key="item.value" disable-transitions :label="item.value">
        {{ item.name }}
      </el-radio>
    </template>
  </el-radio-group>
</template>

<script>
export default {
  name: 'DictRadio',
  props: {
    dictType: {
      type: String,
      default: null
    },
    value: {
      type: String,
      default: null
    },
    exclude: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      data: null
    }
  },
  watch: {
    value: {
      handler(newVal) {
        this.data = newVal
      },
      immediate: true
    },
    data(newVal) {
      this.$emit('input', newVal)
    }
  },
  created() {
    if (this.dictType) {
      this.$dict.loadDict(this.dictType)
    }
  },
  methods: {}
}
</script>
