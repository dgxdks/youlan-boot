<template>
  <el-switch
    v-model="data"
    :disabled="disabled"
    :active-value="activeValue"
    :inactive-value="inactiveValue"
    @change="change"
  />
</template>

<script>
export default {
  name: 'StatusSwitch',
  props: {
    value: {
      type: String,
      default: null
    },
    activeValue: {
      type: String,
      default: '1'
    },
    inactiveValue: {
      type: String,
      default: '2'
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      data: '1'
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
  mounted() {
  },
  methods: {
    change(value) {
      this.$emit('change', value)
    }
  }
}
</script>
