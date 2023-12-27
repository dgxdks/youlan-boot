<template>
  <!-- 导入表 -->
  <base-dialog title="导入表" :open.sync="visible" width="800px" top="5vh" @confirm="handleImportDBTable" @cancel="handleImportCancel">
    <el-form ref="queryForm" :model="queryForm" size="small" :inline="true">
      <el-form-item label="表名称" prop="tableName">
        <el-input
          v-model="queryForm.tableName"
          placeholder="请输入表名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="表描述" prop="tableComment">
        <el-input v-model="queryForm.tableComment" placeholder="请输入表描述" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <base-search-button @click="handleQuery" />
        <base-reset-button @click="handleResetQuery" />
      </el-form-item>
    </el-form>
    <el-row>
      <el-table ref="table" :data="dbTableList" height="260px" @row-click="handleRowClick" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="tableName" label="表名称" show-overflow-tooltip />
        <el-table-column prop="tableComment" label="表描述" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column prop="updateTime" label="更新时间" />
      </el-table>
      <pagination
        v-show="pageTotal>0"
        :total="pageTotal"
        :page.sync="queryForm.pageNum"
        :limit.sync="queryForm.pageSize"
        @pagination="getList"
      />
    </el-row>
  </base-dialog>
</template>

<script>
import { getDbTablePageList, importDbTableList } from '@/api/tools/generator'
import crud from '@/framework/mixin/crud'

export default {
  mixins: [crud],
  data() {
    return {
      // 遮罩层
      visible: false,
      // 表数据
      dbTableList: [],
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        tableName: null,
        tableComment: null
      }
    }
  },
  methods: {
    // 显示弹框
    show() {
      this.getList()
      this.visible = true
    },
    // 查询表数据
    getList() {
      this.openTableLoading()
      getDbTablePageList(this.queryForm).then(res => {
        this.dbTableList = res.data.rows
        this.pageTotal = res.data.total
        this.closeTableLoading()
      })
    },
    // 搜索按钮
    handleQuery() {
      this.resetPageNum()
      this.getList()
    },
    // 搜索重置按钮
    handleResetQuery() {
      this.$refs.queryForm && this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    // 导入按钮
    handleImportDBTable() {
      if (this.$array.isEmpty(this.tableIds)) {
        this.$modal.success('请选择要导入的表')
        return
      }
      this.$modal.loading('导入中，请稍等...')
      importDbTableList({ list: this.tableIds }).then(res => {
        this.$modal.loadingClose()
        this.$modal.success('导入成功')
        this.visible = false
        this.$emit('ok')
      }).catch(error => {
        console.log(error)
        this.$modal.loadingClose()
      })
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.tableIds = selection.map(item => item.tableName)
    },
    // 行点击
    handleRowClick(row) {
      this.$refs.table.toggleRowSelection(row)
    },
    // 导入取消
    handleImportCancel() {
      this.tableIds = []
    }
  }
}
</script>
