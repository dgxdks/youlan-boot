<template>
  <el-select v-model="data" :placeholder="placeholder" style="width: 100%" :disabled="disabled" :clearable="clearable" @change="handleChange">
    <el-option v-for="item in dict[dictType]" :key="item.value" :label="item.name" :value="item.value">
      {{ item.name }}
    </el-option>
  </el-select>
</template>

<script>
import { StrUtil } from '@/framework/tools'

export default {
  name: 'DictSelect',
  props: {
    value: {
      type: [String],
      default: null
    },
    dictType: {
      type: String,
      default: null
    },
    placeholder: {
      type: String,
      default: ''
    },
    clearable: {
      type: Boolean,
      default: true
    },
    disabled: {
      type: Boolean,
      default: false
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
    if (StrUtil.isNotBlank(this.dictType)) {
      this.$dict.loadDict(this.dictType)
    }
  },
  methods: {
    handleChange(data) {
      this.$emit('change', data)
    }
  }
}
</script>
