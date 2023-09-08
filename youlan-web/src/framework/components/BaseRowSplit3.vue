<script>
export default {
  name: 'RowSplit3',
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
    const middleNodes = []
    const rightNodes = []
    for (let i = 0; i < vNodes.length; i++) {
      const vNode = vNodes[i]
      const index = i % 3
      if (index === 0) {
        leftNodes.push(vNode)
      } else if (index === 1) {
        middleNodes.push(vNode)
      } else if (index === 2) {
        rightNodes.push(vNode)
      }
    }
    return createElement('el-row', [
      createElement('el-col', {
        props: {
          span: 8
        }
      }, leftNodes),
      createElement('el-col', {
        props: {
          span: 8
        }
      }, middleNodes),
      createElement('el-col', {
        props: {
          span: 8
        }
      }, rightNodes)
    ])
  }
}
</script>

<style scoped>

</style>
