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
    <slot />
  </el-button>
</template>

<script>
import button from '@/framework/mixin/button'

export default {
  name: 'BaseTextButton',
  mixins: [button],
  props: {
    icon: {
      type: String,
      default: null
    },
    type: {
      type: String,
      default: 'text'
    },
    size: {
      type: String,
      default: 'mini'
    },
    color: {
      type: String,
      default: null
    }
  },
  computed: {
    style() {
      return {
        color: this.color
      }
    }
  }
}
</script>
