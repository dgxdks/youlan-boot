<template>
  <el-tree
    ref="tree"
    :check-strictly="checkStrictly"
    :data="treeList"
    :props="treeProps"
    node-key="id"
    :show-checkbox="showCheckBox"
    :default-expand-all="defaultExpandAll"
    @node-click="handleNodeClick"
    @check="handleCheck"
  />
</template>

<script>
import { getMenuTreeList } from '@/api/system/menu'
import tree from '@/framework/mixin/tree'

export default {
  name: 'MenuTree',
  mixins: [tree],
  data() {
    return {
      checkedKeys: [],
      treeProps: {
        children: 'children',
        label: 'menuName'
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      getMenuTreeList({}).then(res => {
        this.treeList = res
        // 刷新一次选中的key
        this.$nextTick(() => {
          this.setCheckedKeys(this.checkedKeys)
        })
      })
    }
  }
}
</script>
