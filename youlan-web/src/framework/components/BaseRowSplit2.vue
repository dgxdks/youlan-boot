<script>
export default {
  name: 'RowSplit2',
  render(createElement, context) {
    const vNodes = this.$slots.default
    if (this.$array.isEmpty(vNodes)) {
      return null
    }
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
