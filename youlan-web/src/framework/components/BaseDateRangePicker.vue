<template>
  <el-date-picker
    v-model="range"
    :start-placeholder="startPlaceholder"
    :end-placeholder="endPlaceholder"
    :range-separator="rangeSeparator"
    :type="type"
    :value-format="valueFormat"
    :picker-options="pickerOptions"
    :placeholder="placeholder"
  />
</template>

<script>
import range_picker from '@/framework/mixin/range_picker'

export default {
  name: 'BaseDateRangePicker',
  mixins: [range_picker],
  props: {
    type: {
      type: String,
      default: 'daterange'
    },
    placeholder: {
      type: String,
      default: '请选择日期'
    }
  }
}
</script>
