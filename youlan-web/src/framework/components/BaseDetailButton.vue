<template>
  <el-button
    :icon="icon"
    :plain="plain"
    :size="size"
    :type="type"
    :disabled="disabled"
    :style="style"
    @click="handleClick"
  >
    <template v-if="$slots.default && $slots.default.length > 0">
      <slot />
    </template>
    <template v-else>
      详情
    </template>
  </el-button>
</template>

<script>
import button from '@/framework/mixin/button'

export default {
  name: 'BaseDetailButton',
  mixins: [button],
  props: {
    icon: {
      type: String,
      default: 'el-icon-view'
    },
    type: {
      type: String,
      default: 'info'
    }
  }
}
</script>
