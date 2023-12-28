<template>
  <div class="app-container">
    <el-form ref="queryForm" :inline="true" :model="queryForm" label-width="68px" size="small">
      <el-form-item label="用户名称" prop="userName">
        <el-input
          v-model="queryForm.userName"
          clearable
          placeholder="请输入用户名称"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <base-search-button @click="handleQuery" />
        <base-reset-button @click="handleResetQuery" />
      </el-form-item>

    </el-form>
    <el-table v-loading="tableLoading" :data="onlineList">
      <el-table-column show-overflow-tooltip align="center" label="会话编号" prop="tokenValue" />
      <el-table-column show-overflow-tooltip align="center" label="用户名称" prop="userName" />
      <el-table-column align="center" label="组织机构" prop="orgName" />
      <el-table-column show-overflow-tooltip align="center" label="主机" prop="loginIp" />
      <el-table-column show-overflow-tooltip align="center" label="登录地点" prop="loginLocation" />
      <el-table-column align="center" label="浏览器" prop="browser" />
      <el-table-column align="center" label="操作系统" prop="os" />
      <el-table-column align="center" label="登录时间" prop="loginTime" width="160" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template slot-scope="scope">
          <base-remove-button v-has-perm="['monitor:onlineUser:kickout']" type="text" @click="handleKickout(scope.row)">
            强踢
          </base-remove-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      :limit.sync="queryForm.pageSize"
      :page.sync="queryForm.pageNum"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { getOnlineUserPageList, kickoutOnlineUser } from '@/api/monitor/online'
import crud from '@/framework/mixin/crud'

export default {
  name: 'OnlineUser',
  mixins: [crud],
  data() {
    return {
      // 在线用户数据
      onlineList: [],
      pageNum: 1,
      pageSize: 10,
      // 查询参数
      queryForm: {
        userName: null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 查询登录日志列表
    getList() {
      this.openTableLoading()
      getOnlineUserPageList(this.queryForm).then(res => {
        this.onlineList = res.data.rows
        this.pageTotal = res.data.total
        this.closeTableLoading()
      })
    },
    // 搜索按钮
    handleQuery() {
      this.resetPageNum()
      this.getList()
    },
    // 重置按钮
    handleResetQuery() {
      this.$refs.queryForm && this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    // 强踢按钮
    handleKickout(row) {
      this.$modal.confirm('是否确认强踢名称为"' + row.userName + '"的用户？').then(function() {
        return kickoutOnlineUser({ tokenValue: row.tokenValue })
      }).then(() => {
        this.getList()
        this.$modal.success('强退成功')
      }).catch(() => {
      })
    }
  }
}
</script>

