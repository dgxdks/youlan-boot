<template>
  <el-date-picker
    v-model="range"
    :start-placeholder="startPlaceholder"
    :end-placeholder="endPlaceholder"
    :range-separator="rangeSeparator"
    type="daterange"
    :value-format="valueFormat"
    :picker-options="pickerOptions"
  />
</template>

<script>
import range_picker from '@/framework/mixin/range_picker'

export default {
  name: 'BaseDateRangePicker',
  mixins: [range_picker]
}
</script>
