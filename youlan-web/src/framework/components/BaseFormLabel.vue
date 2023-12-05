<template>
  <span>
    <el-tooltip v-if="content" :content="content" :placement="placement">
      <i :class="icon" />
    </el-tooltip>
    {{ label }}
  </span>
</template>

<script>
export default {
  name: 'BaseFormLabel',
  props: {
    label: {
      type: String,
      default: '未知'
    },
    content: {
      type: String,
      default: '未知'
    },
    placement: {
      type: String,
      default: 'top'
    },
    icon: {
      type: String,
      default: 'el-icon-question'
    }
  }
}
</script>
