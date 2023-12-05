<template>
  <pre>
    <code class="hljs" v-html="highLightCode" />
  </pre>
</template>

<script>
import { LANGUAGE_JAVA } from '@/framework/tools/highlight'

export default {
  name: 'BaseHighLightCode',
  props: {
    value: {
      type: String,
      default: null
    },
    language: {
      type: String,
      default: LANGUAGE_JAVA
    }
  },
  computed: {
    highLightCode() {
      return this.$highlight.highlight(this.value, this.language)
    }
  }
}
</script>
