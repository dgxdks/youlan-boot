<template>
  <!-- 授权用户 -->
  <base-dialog title="选择用户" :open.sync="open" width="800px" top="5vh" @confirm="handleAuthUserConfirm">
    <el-form ref="queryForm" :model="queryForm" size="small" :inline="true">
      <el-form-item label="用户名称" prop="userName">
        <el-input
          v-model="queryForm.userName"
          placeholder="请输入用户名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机号码" prop="userMobile">
        <el-input
          v-model="queryForm.userMobile"
          placeholder="请输入手机号码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <base-search-button @click="handleQuery" />
        <base-reset-button @click="handleResetQuery" />
      </el-form-item>
    </el-form>
    <el-row>
      <el-table
        ref="table"
        v-loading="tableLoading"
        :data="userList"
        height="260px"
        @row-click="handleRowClick"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" :selectable="tableSelectEnabled" />
        <el-table-column label="用户名称" prop="userName" show-overflow-tooltip />
        <el-table-column label="用户昵称" prop="nickName" show-overflow-tooltip />
        <el-table-column label="邮箱" prop="email" show-overflow-tooltip />
        <el-table-column label="手机" prop="userMobile" show-overflow-tooltip />
        <el-table-column label="状态" align="center" prop="status">
          <template slot-scope="scope">
            <dict-tag v-model="scope.row.status" dict-type="db_status" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
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
import { addAuthUser, getUnAuthUserPageList } from '@/api/system/role'
import crud from '@/framework/mixin/crud'

export default {
  mixins: [crud],
  props: {
    // 角色编号
    roleId: {
      type: [Number, String],
      default: null
    }
  },
  data() {
    return {
      // 遮罩层
      open: false,
      // 未授权用户数据
      userList: [],
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        roleId: null,
        userName: null,
        userMobile: null
      }
    }
  },
  methods: {
    // 显示弹框
    show() {
      this.queryForm.roleId = this.roleId
      this.getList()
      this.open = true
    },
    // 表格行选中
    handleRowClick(row) {
      if (this.$auth.isAdmin(row.id)) {
        return
      }
      this.$refs.table.toggleRowSelection(row)
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.tableIds = selection.map(item => item.id)
    },
    // 查询表数据
    getList() {
      this.openTableLoading()
      getUnAuthUserPageList(this.queryForm).then(res => {
        this.userList = res.data.rows
        this.pageTotal = res.data.total
        this.closeTableLoading()
      })
    },
    // 搜索按钮
    handleQuery() {
      this.queryForm.pageNum = 1
      this.getList()
    },
    // 重置按钮
    handleResetQuery() {
      this.$refs.queryForm && this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    // 授权用户提交
    handleAuthUserConfirm() {
      if (this.$array.isEmpty(this.tableIds)) {
        this.$modal.error('请选择要分配的用户')
        return
      }
      addAuthUser({ roleId: this.roleId, userIds: this.tableIds }).then(res => {
        this.$modal.success('分配用户成功')
        this.open = false
        this.$emit('addUser')
      })
    },
    tableSelectEnabled(row, index) {
      return !this.$auth.isAdminRole(row.id)
    }
  }
}
</script>
