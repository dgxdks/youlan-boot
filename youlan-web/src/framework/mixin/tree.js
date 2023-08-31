export default {
  props: {
    size: {
      type: String,
      default: 'small'
    },
    showCheckBox: {
      type: Boolean,
      default: true
    },
    defaultExpandAll: {
      type: Boolean,
      default: false
    },
    expandAll: {
      type: Boolean,
      default: false
    },
    checkAll: {
      type: Boolean,
      default: false
    },
    checkStrictly: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      treeList: [],
      treeProps: {}
    }
  },
  watch: {
    expandAll: {
      handler(newVal) {
        this.handleExpandAll(newVal)
      }
    },
    checkAll: {
      handler(newVal) {
        this.handleCheckAll(newVal)
      }
    }
  },
  methods: {
    handleExpandAll(expanded) {
      this.$refs.tree.store._getAllNodes().forEach(node => {
        node.expanded = expanded
      })
    },
    handleCheckAll(checkAll) {
      if (checkAll) {
        this.$refs.tree.setCheckedNodes(this.treeList)
      } else {
        this.$refs.tree.setCheckedNodes([])
      }
    },
    handleNodeClick(data, node, component) {
      this.$emit('nodeClick', data, node, component)
    },
    handleCheck(data, context) {
      // const { checkedNodes, checkedKeys, halfCheckedNodes, halfCheckedKeys } = context
      this.$emit('check', data, context)
    },
    getNode(key) {
      return this.$refs.tree.getNode(key)
    },
    getCheckedNodes() {
      return this.$refs.tree.getCheckedNodes()
    },
    getCheckedKeys() {
      return this.$refs.tree.getCheckedKeys()
    },
    getHalfCheckedNodes() {
      return this.$refs.tree.getHalfCheckedNodes()
    },
    getHalfCheckedKeys() {
      return this.$refs.tree.getHalfCheckedKeys()
    },
    setCheckedNodes(nodes) {
      this.$refs.tree.setCheckedNodes(nodes)
    },
    setCheckedKeys(keys) {
      // 避免包含父节点ID则子节点就会全被选中的问题
      this.checkedKeys = keys
      const filterKeys = keys.filter(id => {
        if (this.$refs.tree.getNode(id)) {
          return this.$array.isEmpty(this.$refs.tree.getNode(id).childNodes)
        }
        return false
      })
      this.$refs.tree.setCheckedKeys(filterKeys)
    }
  }

}
