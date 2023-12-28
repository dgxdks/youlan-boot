<template>
  <el-select
    v-model="data"
    clearable
    :placeholder="placeholder"
    style="width: 100%"
    :disabled="disabled"
    @change="handleChange"
  >
    <el-option v-for="item in payChannels" :key="item.id" :label="item.name" :value="item.id">
      {{ item.name }}
    </el-option>
  </el-select>
</template>

<script>

export default {
  name: 'PayChannelSelect',
  props: {
    value: {
      type: [Number],
      default: null
    },
    disabled: {
      type: Boolean,
      default: false
    },
    placeholder: {
      type: String,
      default: null
    },
    payChannels: {
      type: Array,
      default: []
    },
    payType: {
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
      this.$emit('input', newVal || null)
    }
  },
  created() {
  },
  methods: {
    handleChange(data) {
      this.$emit('change', data)
    }
  }
}
</script>
