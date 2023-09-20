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
    console.log(vNodes)
    const leftNodes = []
    const rightNodes = []
    for (let i = 0; i < vNodes.length; i++) {
      const vNode = vNodes[i]
      i % 2 === 0 ? leftNodes.push(vNode) : rightNodes.push(vNode)
    }
    this.element = createElement('el-row', [
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
    return this.element
  }
}
</script>

<style scoped>

</style>
