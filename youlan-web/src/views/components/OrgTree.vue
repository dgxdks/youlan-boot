<template>
  <div>
    <div v-if="searchEnabled">
      <el-input
        v-model="orgName"
        :size="size"
        clearable
        placeholder="请输入组织机构名称"
        prefix-icon="el-icon-search"
        style="margin-bottom: 20px"
      />
    </div>
    <div>
      <el-tree
        ref="tree"
        :data="treeList"
        :expand-on-click-node="false"
        :filter-node-method="filterNode"
        :props="treeProps"
        :show-checkbox="showCheckBox"
        :default-expand-all="defaultExpandAll"
        highlight-current
        node-key="orgId"
        @node-click="handleNodeClick"
      />
    </div>
  </div>

</template>

<script>
import { getOrgTreeList } from '@/api/system/org'
import tree from '@/framework/mixin/tree'

export default {
  name: 'OrgTree',
  mixins: [tree],
  props: {
    searchEnabled: {
      type: Boolean,
      default: true
    },
    showCheckBox: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      orgName: null,
      treeProps: {
        children: 'children',
        label: 'orgName'
      }
    }
  },
  watch: {
    orgName(newVal, oldVal) {
      this.$refs.tree.filter(newVal)
    }
  },
  mounted() {
    this.getList()
  },
  methods: {
    getList() {
      getOrgTreeList({}).then(res => {
        this.treeList = res
      })
    },
    filterNode(value, data) {
      if (!value) {
        return true
      }
      return data.orgName.indexOf(value) !== -1
    }
  }
}
</script>

<style scoped>

</style>
