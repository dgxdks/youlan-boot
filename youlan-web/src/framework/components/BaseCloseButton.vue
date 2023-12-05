<template>
  <el-button
    :icon="icon"
    :plain="plain"
    :size="size"
    :type="type"
    :style="style"
    :disabled="disabled"
    @click="handleClick"
  >
    <template v-if="$slots.default && $slots.default.length > 0">
      <slot />
    </template>
    <template v-else>
      关闭
    </template>
  </el-button>
</template>

<script>
import button from '@/framework/mixin/button'

export default {
  name: 'BaseCloseButton',
  mixins: [button],
  props: {
    icon: {
      type: String,
      default: 'el-icon-circle-close'
    },
    type: {
      type: String,
      default: 'warning'
    }
  }
}
</script>
