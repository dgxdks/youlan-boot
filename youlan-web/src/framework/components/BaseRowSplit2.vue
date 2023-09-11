<script>
export default {
  name: 'BaseRowSplit2',
  render(createElement, context) {
    let vNodes = this.$slots.default
    if (this.$array.isEmpty(vNodes)) {
      return null
    }
    // 过滤掉v-if=false的组件
    vNodes = vNodes.filter(vNode => {
      return vNode.data
    })
    const leftNodes = []
    const rightNodes = []
    for (let i = 0; i < vNodes.length; i++) {
      const vNode = vNodes[i]
      i % 2 === 0 ? leftNodes.push(vNode) : rightNodes.push(vNode)
    }
    return createElement('el-row', [
      createElement('el-col', {
        props: {
          span: 12
        }
      }, leftNodes),
      createElement('el-col', {
        props: {
          span: 12
        }
      }, rightNodes)
    ])
  }
}
</script>

<style scoped>

</style>
