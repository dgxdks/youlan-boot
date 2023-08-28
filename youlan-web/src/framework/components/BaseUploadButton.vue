<template>
  <el-button
    :icon="icon"
    :plain="plain"
    :size="size"
    :type="type"
    :disabled="disabled"
    @click="handleClick"
  >
    <template v-if="$slots.default && $slots.default.length > 0">
      <slot />
    </template>
    <template v-else>
      上传
    </template>
  </el-button>
</template>

<script>
import button from '@/framework/mixin/button'

export default {
  name: 'BaseUploadButton',
  mixins: [button],
  props: {
    icon: {
      type: String,
      default: 'el-icon-upload2'
    },
    type: {
      type: String,
      default: 'info'
    }
  }
}
</script>
