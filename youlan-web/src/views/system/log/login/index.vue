<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" :inline="true" :model="queryForm" label-width="68px" size="small">
      <el-form-item label="登录地址" prop="loginIp">
        <el-input
          v-model="queryForm.loginIp"
          clearable
          placeholder="请输入登录地址"
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户名称" prop="userName">
        <el-input
          v-model="queryForm.userName"
          clearable
          placeholder="请输入用户名称"
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="登录状态" prop="loginStatus">
        <dict-select v-model="queryForm.loginStatus" dict-type="sys_login_log_status" style="width: 240px" placeholder="登录状态" clearable />
      </el-form-item>
      <el-form-item label="登录时间">
        <base-date-time-range-picker v-model="queryForm.loginTimeRange" style="width: 340px" />
      </el-form-item>
      <el-form-item>
        <base-search-button @click="handleQuery" />
        <base-reset-button @click="handleResetQuery" />
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:loginLog:remove']" plain :disabled="tableNoSelected" @click="handleDelete" />
      </el-col>
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:loginLog:remove']" plain @click="handleClean">清空</base-remove-button>
      </el-col>
      <el-col :span="1.5">
        <base-add-button v-has-perm="['system:loginLog:unlockUser']" plain icon="el-icon-unlock" :disabled="!tableSelectOne" @click="handleUnlock">解锁</base-add-button>
      </el-col>
      <el-col :span="1.5">
        <base-download-button v-has-perm="['system:loginLog:export']" plain @click="handleExport">导出</base-download-button>
      </el-col>
      <right-toolbar :query-show.sync="queryShow" @refresh="getList" />
    </el-row>

    <el-table
      ref="table"
      v-loading="tableLoading"
      :data="logList"
      :default-sort="defaultSort"
      @selection-change="handleSelectionChange"
      @sort-change="handleSortChange"
    >
      <el-table-column align="center" type="selection" width="55" />
      <el-table-column align="center" label="登录编号" prop="id" />
      <el-table-column show-overflow-tooltip align="center" label="用户名称" prop="userName" sortable="custom" />
      <el-table-column show-overflow-tooltip align="center" label="登录地址" prop="loginIp" width="130" />
      <el-table-column show-overflow-tooltip align="center" label="登录地点" prop="loginLocation" />
      <el-table-column show-overflow-tooltip align="center" label="用户代理" prop="userAgent" />
      <el-table-column align="center" label="登录状态" prop="loginStatus">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.loginStatus" dict-type="sys_login_log_status" />
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip align="center" label="登录信息" prop="loginMsg" />
      <el-table-column align="center" label="登录时间" prop="loginTime" width="160" sortable="custom" />
    </el-table>

    <pagination
      v-show="pageTotal>0"
      :limit.sync="queryForm.pageSize"
      :page.sync="queryForm.pageNum"
      :total="pageTotal"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { cleanLoginLogList, getLoginLogPageList, removeLoginLog, unlockLoginUser } from '@/api/system/log/login'
import crud from '@/framework/mixin/crud'

export default {
  name: 'LoginLog',
  mixins: [crud],
  data() {
    const defaultSort = { prop: 'loginTime', order: 'descending' }
    return {
      // 表格数据
      logList: [],
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        ipaddr: undefined,
        userName: undefined,
        status: undefined,
        sortList: [
          { column: defaultSort.prop, asc: this.orderStrIsAsc(defaultSort.order) }
        ]
      },
      defaultSort
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 查询登录日志列表
    getList() {
      this.openTableLoading()
      getLoginLogPageList(this.queryForm).then(res => {
        this.logList = res.rows
        this.pageTotal = res.total
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
      this.$modal.confirm('是否确认删除访问编号为"' + list + '"的数据项？').then(function() {
        return removeLoginLog({ list })
      }).then(() => {
        this.getList()
        this.$modal.success('删除成功')
      }).catch(() => {
      })
    },
    // 清空按钮
    handleClean() {
      this.$modal.confirm('是否确认清空所有登录日志数据项？').then(function() {
        return cleanLoginLogList()
      }).then(() => {
        this.getList()
        this.$modal.success('清空成功')
      }).catch(() => {
      })
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.tableIds = selection.map(item => {
        this.selectName = item.userName
        return item.id
      })
    },
    // 解锁按钮
    handleUnlock() {
      const userName = this.selectName
      this.$modal.confirm('是否确认解锁用户"' + userName + '"数据项?').then(function() {
        return unlockLoginUser({ userName })
      }).then(() => {
        this.$modal.success('用户' + userName + '解锁成功')
      }).catch(() => {
      })
    },
    // 导出按钮
    handleExport() {
      this.$download.postAsName('/system/loginLog/exportLoginLogList', {}, this.queryForm, `logininfor_${new Date().getTime()}.xlsx`, { timeout: 60 * 1000 })
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

