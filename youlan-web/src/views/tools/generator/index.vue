<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" :inline="true" :model="queryForm" label-width="68px" size="small">
      <el-form-item label="表名称" prop="tableName">
        <el-input
          v-model="queryForm.tableName"
          clearable
          placeholder="请输入表名称"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="表描述" prop="tableComment">
        <el-input
          v-model="queryForm.tableComment"
          clearable
          placeholder="请输入表描述"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTimeRange">
        <base-date-time-range-picker v-model="queryForm.createTimeRange" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <base-search-button @click="handleQuery" />
        <base-reset-button @click="handleResetQuery" />
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <base-download-button v-has-perm="['tools:generator:code']" plain type="primary" @click="handleGenerateCode">生成</base-download-button>
      </el-col>
      <el-col :span="1.5">
        <base-upload-button v-has-perm="['tools:generator:update']" plain type="info" @click="handleImportDbTable">导入</base-upload-button>
      </el-col>
      <el-col :span="1.5">
        <base-update-button v-has-perm="['tools:generator:update']" :disabled="!tableSelectOne" plain @click="handleUpdate" />
      </el-col>
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['tools:generator:remove']" :disabled="tableNoSelected" plain @click="handleDelete" />
      </el-col>
      <table-toolbar :query-show.sync="queryShow" @refresh="getList" />
    </el-row>

    <el-table v-loading="tableLoading" :data="tableList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55" />
      <el-table-column align="center" label="序号" type="index" width="50" prop="id" />
      <el-table-column show-overflow-tooltip align="center" label="表名称" prop="tableName" width="160" />
      <el-table-column show-overflow-tooltip align="center" label="表描述" prop="tableComment" width="160" />
      <el-table-column show-overflow-tooltip align="center" label="实体名称" prop="entityName" width="160" />
      <el-table-column align="center" label="创建时间" prop="createTime" width="160" />
      <el-table-column align="center" label="更新时间" prop="updateTime" width="160" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template slot-scope="scope">
          <el-button
            v-has-perm="['tools:generator:preview']"
            icon="el-icon-view"
            size="small"
            type="text"
            @click="handlePreviewCode(scope.row)"
          >预览
          </el-button>
          <el-button
            v-has-perm="['tools:generator:update']"
            icon="el-icon-edit"
            size="small"
            type="text"
            @click="handleUpdate(scope.row)"
          >编辑
          </el-button>
          <el-button
            v-has-perm="['tools:generator:remove']"
            icon="el-icon-delete"
            size="small"
            type="text"
            @click="handleDelete(scope.row)"
          >删除
          </el-button>
          <el-button
            v-has-perm="['tools:generator:update']"
            icon="el-icon-refresh"
            size="small"
            type="text"
            @click="handleSyncDbTable(scope.row)"
          >同步
          </el-button>
          <el-button
            v-has-perm="['tools:generator:code']"
            icon="el-icon-download"
            size="small"
            type="text"
            @click="handleGenerateCode(scope.row)"
          >生成
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="pageTotal>0"
      :limit.sync="queryForm.pageSize"
      :page.sync="queryForm.pageNum"
      :total="pageTotal"
      @pagination="getList"
    />
    <!-- 预览界面 -->
    <el-dialog
      :title="preview.title"
      :visible.sync="preview.open"
      append-to-body
      class="scrollbar"
      top="5vh"
      width="80%"
    >
      <el-tabs v-model="preview.activeVm">
        <el-tab-pane
          v-for="item in preview.codes"
          :key="item.codeName"
          :label="getVmFileName(item.vmName)"
          :name="getVmFileName(item.vmName)"
        >
          <base-copy-icon v-model="item.codeContent" show-message style="float:right">复制</base-copy-icon>
          <base-high-light-code v-model="item.codeContent" :language="getVmLanguage(item.vmName)" />
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
    <import-table ref="import" @ok="handleQuery" />
  </div>
</template>

<script>
import {
  getTablePageList,
  previewCode,
  removeTable,
  syncDbTable,
  writeCode
} from '@/api/tools/generator'
import importTable from './importTable'
import crud from '@/framework/mixin/crud'

export default {
  name: 'Generator',
  components: { importTable },
  mixins: [crud],
  data() {
    return {
      // 选中表数组
      tableNames: [],
      // 表数据
      tableList: [],
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        tableName: null,
        tableComment: null,
        createTimeRange: null
      },
      // 预览参数
      preview: {
        open: false,
        title: '代码预览',
        codes: {},
        activeVm: null
      },
      // 当前路由路径
      currentPath: null
    }
  },
  watch: {
    $route(to, from) {
      // 返回当前组件时刷新表格
      if (to.path === this.currentPath) {
        this.getList()
      }
    }
  },
  created() {
    this.currentPath = this.$route.path
    this.getList()
  },
  methods: {
    // 查询表集合
    getList() {
      this.openTableLoading()
      getTablePageList(this.queryForm).then(res => {
        this.tableList = res.data.rows
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
    // 生成代码操作
    handleGenerateCode(row) {
      const list = (row.id && [row.id]) || this.tableIds
      if (this.$array.isEmpty(list)) {
        this.$modal.error('请选择要生成的数据')
        return
      }
      // 指定路径写入
      if (row.generatorType === '2') {
        writeCode(row.tableName).then(response => {
          this.$modal.success('成功生成到自定义路径：' + row.genPath)
        })
        // zip包下载
      } else {
        this.$download.post('/tools/generator/downloadCode', {}, { list }, 'youlan.zip')
      }
    },
    // 预览按钮
    handlePreviewCode(row) {
      previewCode({ id: row.id }).then(res => {
        this.preview.codes = res.data
        this.preview.open = true
        this.preview.activeVm = this.getActiveVm(this.preview.codes)
      })
    },
    // 同步数据库操作
    handleSyncDbTable(row) {
      const tableName = row.tableName
      const id = row.id
      this.$modal.confirm('确认要强制同步"' + tableName + '"表结构吗？').then(() => {
        this.$modal.loading('正在同步中，请稍等...')
        return syncDbTable({ id })
      }).then(() => {
        this.$modal.loadingClose()
        this.$modal.success('同步成功')
      }).catch(error => {
        console.log(error)
        this.$modal.loadingClose()
      })
    },
    // 打开导入表弹窗
    handleImportDbTable() {
      this.$refs.import.show()
    },
    // 修改按钮
    handleUpdate(row) {
      const id = row.id || this.tableIds[0]
      const tableName = row.tableName || this.tableList.find(table => this.tableIds.includes(table.id)).tableName
      this.$tab.openPage('修改[' + tableName + ']生成配置', '/tools/gen-edit/index/' + id)
    },
    // 删除按钮
    handleDelete(row) {
      const list = (row.id && [row.id]) || this.tableIds
      this.$modal.confirm('是否确认删除表编号为"' + list + '"的数据项？').then(function() {
        return removeTable({ list })
      }).then(() => {
        this.getList()
        this.$modal.success('删除成功')
      }).catch(() => {
      })
    },
    // 获取vm文件名称
    getVmFileName(vmName) {
      return vmName.substring(vmName.lastIndexOf('/') + 1, vmName.indexOf('.vm'))
    },
    // 获取要激活显示的vm
    getActiveVm(codes) {
      return (this.$array.isNotEmpty(codes) && this.getVmFileName(codes[0].vmName)) || 'entity.java'
    },
    // 获取vm文件对应语言
    getVmLanguage(vmName) {
      const vmFileName = this.getVmFileName(vmName)
      return vmFileName.substring(vmFileName.indexOf('.') + 1)
    }
  }
}
</script>
