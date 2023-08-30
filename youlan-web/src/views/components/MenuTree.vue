<template>
  <el-tree
    ref="tree"
    :check-strictly="checkStrictly"
    :data="menuList"
    :props="menuProps"
    node-key="id"
    :show-checkbox="showCheckBox"
    :default-expand-all="defaultExpandAll"
    @node-click="handleNodeClick"
    @check="handleCheck"
  />
</template>

<script>
import { getMenuTreeList } from '@/api/system/menu'

export default {
  name: 'MenuTree',
  props: {
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
      default: true
    }
  },
  data() {
    return {
      menuList: [],
      menuProps: {
        children: 'children',
        label: 'menuName'
      }
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
  mounted() {
    this.getList()
  },
  methods: {
    getList() {
      getMenuTreeList({}).then(res => {
        this.menuList = res
      })
    },
    handleExpandAll(expanded) {
      this.$refs.tree.store._getAllNodes().forEach(node => {
        node.expanded = expanded
      })
    },
    handleCheckAll(checkAll) {
      if (checkAll) {
        this.$refs.tree.setCheckedNodes(this.menuList)
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
    }
  }
}
</script>
