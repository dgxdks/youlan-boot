<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" :inline="true" :model="queryForm" label-width="68px" size="small">
      <el-form-item label="日志名称" prop="logName">
        <el-input
          v-model="queryForm.logName"
          clearable
          placeholder="请输入日志名称"
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作用户" prop="logBy">
        <el-input
          v-model="queryForm.logBy"
          clearable
          placeholder="请输入操作用户"
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="日志类型" prop="logType">
        <dict-select
          v-model="queryForm.logType"
          dict-type="sys_operation_log_type"
          placeholder="请选择日志类型"
          style="width: 240px"
        />
      </el-form-item>
      <el-form-item label="日志状态" prop="logStatus">
        <dict-select
          v-model="queryForm.logStatus"
          dict-type="sys_operation_log_status"
          clearable
          placeholder="操作状态"
          style="width: 220px"
        />
      </el-form-item>
      <el-form-item label="日志时间" prop="logTimeRange">
        <base-date-time-range-picker v-model="queryForm.logTimeRange" style="width: 340px" />
      </el-form-item>
      <el-form-item>
        <base-search-button @click="handleQuery" />
        <base-reset-button @click="handleResetQuery" />
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:operationLog:remove']" plain :disabled="tableNoSelected" @click="handleDelete" />
      </el-col>
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:operationLog:remove']" plain @click="handleClean">清空
        </base-remove-button>
      </el-col>
      <el-col :span="1.5">
        <base-download-button v-has-perm="['system:operationLog:export']" plain @click="handleExport">导出
        </base-download-button>
      </el-col>
      <table-toolbar :query-show.sync="queryShow" @refresh="getList" />
    </el-row>

    <el-table
      ref="table"
      v-loading="tableLoading"
      :data="logList"
      :default-sort="defaultSort"
      @sort-change="handleSortChange"
      @selection-change="handleSelectionChange"
    >
      <el-table-column align="center" type="selection" width="50" />
      <el-table-column align="center" label="日志编号" prop="id" />
      <el-table-column show-overflow-tooltip align="center" label="日志名称" prop="logName" />
      <el-table-column align="center" label="日志类型" prop="logType">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.logType" dict-type="sys_operation_log_type" />
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip align="center" label="操作人员" prop="logBy" width="110" sortable="custom" />
      <el-table-column show-overflow-tooltip align="center" label="来源IP" prop="sourceIp" width="130" />
      <el-table-column show-overflow-tooltip align="center" label="操作地点" prop="sourceLocation" />
      <el-table-column align="center" label="日志状态" prop="logStatus">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.logStatus" dict-type="sys_operation_log_status" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="日志时间" prop="logTime" width="160" sortable="custom" />
      <el-table-column show-overflow-tooltip align="center" label="消耗时间(ms)" prop="costTime" width="110" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template slot-scope="scope">
          <base-detail-button v-has-perm="['system:operationLog:list']" type="text" @click="handleDetail(scope.row)" />
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

    <!-- 操作日志详细 -->
    <base-dialog title="操作日志详情" :open.sync="editOpen" width="800px" confirm-btn-disabled close-on-click-modal>
      <el-form ref="editForm" :model="editForm" label-width="100px" size="mini">
        <base-row-split3>
          <el-form-item label="日志名称：">{{ editForm.logName }}</el-form-item>
          <el-form-item label="日志类型：">
            <dict-tag v-model="editForm.logType" dict-type="sys_operation_log_type" />
          </el-form-item>
          <el-form-item label="操作人员：">
            {{ editForm.logBy }}
          </el-form-item>
          <el-form-item label="来源IP：">
            {{ editForm.sourceIp }}
          </el-form-item>
          <el-form-item label="来源位置：">
            {{ editForm.sourceLocation }}
          </el-form-item>
          <el-form-item label="日志状态：">
            <dict-tag v-model="editForm.logStatus" dict-type="sys_operation_log_status" />
          </el-form-item>
          <el-form-item label="日志时间：">
            {{ editForm.logTime }}
          </el-form-item>
          <el-form-item label="操作方法：">
            {{ editForm.method }}
          </el-form-item>
          <el-form-item label="消耗时间">
            {{ editForm.costTime }}毫秒
          </el-form-item>
        </base-row-split3>
        <base-row-split2>
          <el-form-item label="请求地址：">{{ editForm.httpUrl }}</el-form-item>
          <el-form-item label="请求方法：">{{ editForm.httpMethod }}</el-form-item>
        </base-row-split2>

        <el-form-item label="请求参数：">{{ editForm.httpQuery }}</el-form-item>
        <el-form-item label="请求体：">{{ editForm.httpBody }}</el-form-item>
        <el-form-item label="响应体：">{{ editForm.httpResponse }}</el-form-item>
        <el-form-item label="异常信息：">{{ editForm.errorMsg }}</el-form-item>
      </el-form>
    </base-dialog>
  </div>
</template>

<script>
import { cleanOperationLogList, getOperationLogPageList, removeOperationLog } from '@/api/system/log/operation'
import crud from '@/framework/mixin/crud'

export default {
  name: 'OperationLog',
  mixins: [crud],
  data() {
    const defaultSort = { prop: 'logTime', order: 'descending' }
    return {
      // 表格数据
      logList: [],
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        logName: null,
        logBy: null,
        logType: null,
        logStatus: null,
        logTimeRange: null,
        sortList: [
          { column: defaultSort.prop, asc: this.orderStrIsAsc(defaultSort.order) }
        ]
      },
      // 默认排序
      defaultSort
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 查询操作日志
    getList() {
      this.openTableLoading()
      getOperationLogPageList(this.queryForm).then(res => {
        this.logList = res.data.rows
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
      this.$refs.table.sort(this.defaultSort.prop, this.defaultSort.order)
    },
    // 删除按钮
    handleDelete(row) {
      const list = (row.id && [row.id]) || this.tableIds
      this.$modal.confirm('是否确认删除日志编号为"' + list + '"的数据项？').then(function() {
        return removeOperationLog({ list })
      }).then(() => {
        this.getList()
        this.$modal.success('删除成功')
      }).catch(() => {
      })
    },
    // 清空按钮
    handleClean() {
      this.$modal.confirm('是否确认清空所有操作日志数据项？').then(function() {
        return cleanOperationLogList()
      }).then(() => {
        this.getList()
        this.$modal.success('清空成功')
      }).catch(() => {
      })
    },
    // 导出按钮
    handleExport() {
      this.$download.postAsName('/system/operationLog/exportOperationLogList', {}, this.queryForm, `operation_log_${new Date().getTime()}.xlsx`, { timeout: 60 * 1000 })
    },
    // 详细按钮
    handleDetail(row) {
      this.openEdit('日志详情')
      this.editForm = row
    },
    // 排序变化
    handleSortChange({ column, prop, order }) {
      this.queryForm.sortList = [
        { column: prop, asc: this.orderStrIsAsc(order) }
      ]
      this.getList()
    }
  }
}
</script>

