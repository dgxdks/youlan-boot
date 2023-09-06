<script>
export default {
  name: 'BaseRow',
  props: {
    gutter: {
      type: Number,
      default: 10
    },
    span: {
      type: Number,
      default: 1.5
    }
  },
  render(createElement, context) {
    let vNodes = this.$slots.default
    if (this.$array.isEmpty(vNodes)) {
      return null
    }
    // 过滤掉v-if=false的组件
    vNodes = vNodes.filter(vNode => {
      return vNode.data
    }).map(vNode => {
      return createElement('el-col', {
        props: {
          span: this.span
        }
      }, [vNode])
    })
    return createElement('el-row', {
      props: {
        gutter: this.gutter
      }
    }, vNodes)
  }
}
</script>
