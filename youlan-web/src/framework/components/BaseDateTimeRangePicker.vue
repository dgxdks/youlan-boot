<template>
  <el-date-picker
    v-model="range"
    :start-placeholder="startPlaceholder"
    :end-placeholder="endPlaceholder"
    :range-separator="rangeSeparator"
    type="datetimerange"
    :value-format="valueFormat"
    :picker-options="pickerOptions"
    :default-time="defaultTime"
  />
</template>

<script>

import range_picker from '@/framework/mixin/range_picker'

export default {
  name: 'BaseDateTimeRangePicker',
  mixins: [range_picker]
}
</script>
