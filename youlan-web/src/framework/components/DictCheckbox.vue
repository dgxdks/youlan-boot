<template>
  <el-checkbox-group v-model="data" :min="min" :max="max">
    <template v-for="item in dict[dictType]">
      <el-checkbox v-if="!exclude.includes(item.value)" :key="item.value" :label="item.value">
        {{ item.name }}
      </el-checkbox>
    </template>

  </el-checkbox-group>
</template>

<script>
export default {
  name: 'DictCheckbox',
  props: {
    dictType: {
      type: [String, Array],
      default: null
    },
    value: {
      type: String,
      default: null
    },
    exclude: {
      type: Array,
      default: () => []
    },
    min: {
      type: Number,
      default: 0
    },
    max: {
      type: Number,
      default: Number.MAX_VALUE
    }
  },
  data() {
    return {
      data: []
    }
  },
  watch: {
    value: {
      handler(newVal) {
        if (this.$obj.isString(newVal)) {
          this.data = newVal.split(',')
        }
      },
      immediate: true
    },
    data(newVal) {
      if (newVal && this.$obj.isArray(newVal)) {
        this.$emit('input', newVal.join(','))
      }
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
