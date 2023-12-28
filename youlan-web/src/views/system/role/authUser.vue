<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" :inline="true" :model="queryForm" size="small">
      <el-form-item label="用户名称" prop="userName">
        <el-input
          v-model="queryForm.userName"
          clearable
          placeholder="请输入用户名称"
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机号码" prop="userMobile">
        <el-input
          v-model="queryForm.userMobile"
          clearable
          placeholder="请输入手机号码"
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <base-search-button @click="handleQuery" />
        <base-reset-button @click="handleResetQuery" />
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <base-add-button v-has-perm="['system:role:add']" plain @click="handleAddUser">添加用户</base-add-button>
      </el-col>
      <el-col :span="1.5">
        <base-remove-button
          v-has-perm="['system:role:remove']"
          plain
          :disabled="tableNoSelected"
          @click="handleCancelAuthUserBatch"
        >批量取消授权
        </base-remove-button>
      </el-col>
      <el-col :span="1.5">
        <base-close-button plain @click="handleClose">关闭</base-close-button>
      </el-col>
      <table-toolbar :query-show.sync="queryShow" @refresh="getList" />
    </el-row>

    <el-table v-loading="tableLoading" :data="userList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55" />
      <el-table-column show-overflow-tooltip label="用户名称" prop="userName" />
      <el-table-column show-overflow-tooltip label="用户昵称" prop="nickName" />
      <el-table-column show-overflow-tooltip label="邮箱" prop="email" />
      <el-table-column show-overflow-tooltip label="手机" prop="userMobile" />
      <el-table-column align="center" label="状态" prop="status">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.status" dict-type="db_status" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="创建时间" prop="createTime" width="160" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template slot-scope="scope">
          <base-close-button v-has-perm="['system:role:remove']" type="text" @click="handleCancelAuthUser(scope.row)">
            取消授权
          </base-close-button>
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
    <select-user ref="select" :role-id="queryForm.roleId" @addUser="handleQuery" />
  </div>
</template>

<script>
import { cancelAuthUser, getAuthUserPageList } from '@/api/system/role'
import selectUser from './selectUser'
import crud from '@/framework/mixin/crud'

export default {
  name: 'AuthUser',
  components: { selectUser },
  mixins: [crud],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 用户表格数据
      userList: [],
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        userName: null,
        userMobile: null,
        roleId: null
      }
    }
  },
  created() {
    const roleId = this.$route.params && this.$route.params.roleId
    if (roleId) {
      this.queryForm.roleId = roleId
      this.getList()
    }
  },
  methods: {
    // 列表查询
    getList() {
      this.openTableLoading()
      getAuthUserPageList(this.queryForm).then(res => {
        this.userList = res.data.rows
        this.pageTotal = res.data.total
        this.closeTableLoading()
      })
    },
    // 查询按钮
    handleQuery() {
      this.resetPageNum()
      this.getList()
    },
    // 查询重置按钮
    handleResetQuery() {
      this.$refs.queryForm && this.$refs.queryForm.resetFields()
      this.queryForm.orgId = null
      this.handleQuery()
    },
    // 关闭当前页按钮
    handleClose() {
      this.$tab.closeOpenPage({ name: 'Role' })
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.tableIds = selection.map(item => item.id)
    },
    // 添加用户按钮
    handleAddUser() {
      this.$refs.select.show()
    },
    // 取消授权按钮
    handleCancelAuthUser(row) {
      const roleId = this.queryForm.roleId
      this.$modal.confirm('确认要取消该用户"' + row.userName + '"角色吗？').then(function() {
        return cancelAuthUser({ userIds: [row.id], roleId })
      }).then(() => {
        this.getList()
        this.$modal.success('取消授权成功')
      }).catch(error => {
        console.log(error)
      })
    },
    // 批量取消授权按钮
    handleCancelAuthUserBatch(row) {
      const roleId = this.queryForm.roleId
      const _this = this
      this.$modal.confirm('是否取消选中用户授权数据项？').then(function() {
        return cancelAuthUser({ roleId, userIds: _this.tableIds })
      }).then(() => {
        this.getList()
        this.$modal.success('取消授权成功')
      }).catch(error => {
        console.log(error)
      })
    }
  }
}
</script>
