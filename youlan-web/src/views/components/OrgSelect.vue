<template>
  <tree-select
    v-model="orgId"
    label="orgName"
    :options="orgOptions"
    :show-count="showCount"
    :placeholder="placeholder"
    :normalizer="orgNormalizer"
  />
</template>

<script>
import TreeSelect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { getOrgTreeList } from '@/api/system/org'

export default {
  name: 'OrgSelect',
  components: { TreeSelect },
  props: {
    value: {
      type: [Number, Array],
      default: null
    },
    placeholder: {
      type: String,
      default: '请选择组织机构'
    },
    showCount: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      orgId: null,
      orgOptions: []
    }
  },
  watch: {
    orgId: {
      handler(newVal) {
        this.$emit('input', newVal)
      },
      deep: true
    },
    value: {
      handler(newVal) {
        this.orgId = newVal
      },
      immediate: true,
      deep: true
    }
  },
  mounted() {
    this.getOrgTreeList()
  },
  methods: {
    getOrgTreeList() {
      getOrgTreeList({}).then(res => {
        this.orgOptions = res.data
      })
    },
    orgNormalizer(node) {
      return {
        id: node.orgId,
        label: node.orgName,
        children: this.$array.isNotEmpty(node.children) ? node.children : undefined
      }
    }
  }
}
</script>

<style scoped>

</style>
