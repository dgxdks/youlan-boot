<template>
  <el-radio-group v-model="data">
    <el-radio v-for="item in dict[dictType]" :key="item.value" :label="item.value">
      {{ item.name }}
    </el-radio>
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
      this.$dict.getDict(this.dictType)
    }
  },
  methods: {}
}
</script>
