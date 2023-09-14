<template>
  <el-date-picker
    v-model="range"
    :start-placeholder="startPlaceholder"
    :end-placeholder="endPlaceholder"
    :range-separator="rangeSeparator"
    :type="type"
    :value-format="valueFormat"
    :picker-options="pickerOptions"
    :default-time="defaultTime"
    :placeholder="placeholder"
  />
</template>

<script>

import range_picker from '@/framework/mixin/range_picker'

export default {
  name: 'BaseDateTimeRangePicker',
  mixins: [range_picker],
  props: {
    type: {
      type: String,
      default: 'datetimerange'
    },
    placeholder: {
      type: String,
      default: '请选择时间'
    },
    defaultTime: {
      type: [String, Array],
      default: function() {
        if (this.type === 'datetimerange') {
          return ['00:00:00', '23:59:59']
        } else {
          return '00:00:00'
        }
      }
    }
  }
}
</script>
