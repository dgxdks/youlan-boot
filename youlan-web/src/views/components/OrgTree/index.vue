<template>
  <div>
    <div>
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
        :data="orgTreeList"
        :expand-on-click-node="false"
        :filter-node-method="filterNode"
        :props="props"
        default-expand-all
        highlight-current
        node-key="orgId"
        @node-click="handleNodeClick"
      />
    </div>
  </div>

</template>

<script>
import { getOrgTreeList } from '@/api/system/org'

export default {
  name: 'OrgTree',
  props: {
    size: {
      type: String,
      default: 'small'
    }
  },
  data() {
    return {
      orgName: null,
      orgTreeList: null,
      props: {
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
    this.getOrgTreeList()
  },
  methods: {
    getOrgTreeList() {
      getOrgTreeList({}).then(res => {
        this.orgTreeList = res
      })
    },
    handleNodeClick(data) {
      this.$emit('nodeClick', data)
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
