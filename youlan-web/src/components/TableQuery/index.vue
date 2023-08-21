<template>
  <el-form v-show="show" ref="form" inline :label-width="labelWidth" :size="size">
    <el-form-item v-for="param in queryList" :key="param.prop" :label="param.label" :prop="param.prop">
      <slot :name="param.prop" />
    </el-form-item>
  </el-form>
</template>

<script>
import { ArrayUtil } from '@/utils/tools'

export default {
  name: 'TableQuery',
  props: {
    size: {
      type: String,
      default: 'small'
    },
    show: {
      type: Boolean,
      default: true
    },
    labelWidth: {
      type: String,
      default: '68px'
    }
  },
  data() {
    return {
      queryList: []
    }
  },
  watch: {},
  created() {
    this.getQueryParams()
  },
  methods: {
    getQueryParams() {
      const queryParams = []
      for (const name in this.$slots) {
        // 如果未获取到VNode数组直接跳过
        if (ArrayUtil.isEmpty(this.$slots[name])) {
          continue
        }
        // 只取第一个VNode提取查询参数信息
        queryParams.push({
          prop: name,
          label: this.getLabelFromVNode(this.$slots[name][0])
        })
      }
      this.queryList = queryParams
    },
    getLabelFromVNode(vNode) {
      if (vNode.data && vNode.data.attrs) {
        const attrs = vNode.data.attrs
        const attrsLabel = attrs.plabel || attrs.label
        if (attrsLabel) {
          return attrsLabel
        }
      }
      if (vNode.componentOptions && vNode.componentOptions.propsData) {
        const propsData = vNode.componentOptions.propsData
        const propsDataLabel = propsData.plabel || propsData.label
        if (propsDataLabel) {
          return propsDataLabel
        }
      }
      return ''
    }
  }
}
</script>
