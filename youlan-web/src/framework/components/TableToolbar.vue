<template>
  <div class="top-right-btn" :style="style">
    <el-row>
      <el-tooltip v-if="queryEnabled" class="item" effect="dark" :content="queryContent" placement="top">
        <el-button size="mini" circle icon="el-icon-search" @click="handleSearch" />
      </el-tooltip>
      <el-tooltip v-if="refreshEnabled" class="item" effect="dark" content="刷新" placement="top">
        <el-button size="mini" circle icon="el-icon-refresh" @click="handleRefresh" />
      </el-tooltip>
      <el-tooltip v-if="columnEnabled || columns" class="item" effect="dark" content="显隐列" placement="top">
        <el-button size="mini" circle icon="el-icon-menu" @click="handleColumn" />
      </el-tooltip>
    </el-row>
    <el-dialog title="显示/隐藏" :visible.sync="columnOpen" append-to-body>
      <el-transfer
        v-model="columnValue"
        :titles="['显示', '隐藏']"
        :data="columnData"
        @change="handleColumnChange"
      />
    </el-dialog>
  </div>
</template>
<script>
export default {
  name: 'TableToolbar',
  props: {
    queryEnabled: {
      type: Boolean,
      default: true
    },
    queryShow: {
      type: Boolean,
      default: true
    },
    refreshEnabled: {
      type: Boolean,
      default: true
    },
    columnEnabled: {
      type: Boolean,
      default: true
    },
    columns: {
      type: Object,
      default: () => {}
    },
    search: {
      type: Boolean,
      default: true
    },
    gutter: {
      type: Number,
      default: 10
    }
  },
  data() {
    return {
      // 是否显示弹出层
      columnOpen: false,
      // 显隐值
      columnValue: [],
      // 显隐列
      columnData: []
    }
  },
  computed: {
    style() {
      const style = {}
      if (this.gutter) {
        style.marginRight = `${this.gutter / 2}px`
      }
      return style
    },
    queryContent() {
      return this.queryShow ? '隐藏搜索' : '显示搜索'
    }
  },
  created() {
    this.initColumns()
  },
  methods: {
    // 搜索
    handleSearch() {
      this.$emit('update:queryShow', !this.queryShow)
    },
    // 刷新
    handleRefresh() {
      this.$emit('refresh')
    },
    // 初始化列
    initColumns() {
      const columns = {}
      for (const columnKey in this.columns) {
        const columnConf = this.columns[columnKey]
        const key = columnKey
        if (this.$obj.isString(columnConf)) {
          columns[key] = { key, label: columnConf, show: true }
        }
        if (this.$obj.isObject(columnConf)) {
          const show = this.$obj.isEmpty(columnConf.show) ? true : columnConf.show
          const label = columnConf.label
          columns[key] = { key, show, label }
        }
      }
      this.columnData = Object.keys(columns).map(key => columns[key])
      this.$emit('update:columns', columns)
    },
    // 右侧列表元素变化
    handleColumnChange(data) {
      for (const columnKey in this.columns) {
        // eslint-disable-next-line vue/no-mutating-props
        this.columns[columnKey].show = !data.includes(columnKey)
      }
    },
    // 打开显隐列dialog
    handleColumn() {
      this.columnOpen = true
    }
  }
}
</script>
<style lang="scss" scoped>
::v-deep .el-transfer__button {
  border-radius: 50%;
  padding: 12px;
  display: block;
  margin-left: 0px;
}
::v-deep .el-transfer__button:first-child {
  margin-bottom: 10px;
}
</style>
