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
      删除
    </template>
  </el-button>
</template>

<script>
import button from '@/framework/mixin/button'

export default {
  name: 'BaseRemoveButton',
  mixins: [button],
  props: {
    icon: {
      type: String,
      default: 'el-icon-delete'
    },
    type: {
      type: String,
      default: 'danger'
    }
  }
}
</script>
