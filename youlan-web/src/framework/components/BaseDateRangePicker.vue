<template>
  <el-date-picker
    v-model="range"
    :start-placeholder="startPlaceholder"
    :end-placeholder="endPlaceholder"
    :range-separator="rangeSeparator"
    type="daterange"
    :value-format="valueFormat"
  />
</template>

<script>
export default {
  name: 'BaseDateRangePicker',
  props: {
    value: {
      type: Array,
      default: () => null
    },
    startPlaceholder: {
      type: String,
      default: '开始日期'
    },
    endPlaceholder: {
      type: String,
      default: '结束日期'
    },
    rangeSeparator: {
      type: String,
      default: '-'
    },
    valueFormat: {
      type: String,
      default: 'yyyy-MM-dd HH:mm:ss'
    }
  },
  data() {
    return {
      range: null
    }
  },
  watch: {
    value: {
      handler(newVal) {
        if (this.$obj.isArray(newVal) && newVal.length === 2) {
          this.range = newVal
        } else {
          this.range = null
        }
      },
      immediate: true,
      deep: true
    },
    range: {
      handler(newVal) {
        this.$emit('input', newVal)
      },
      deep: true
    }
  },
  mounted() {
  },
  methods: {}
}
</script>
