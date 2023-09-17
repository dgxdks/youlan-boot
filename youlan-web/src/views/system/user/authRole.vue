<template>
  <div class="app-container">
    <h4 class="form-header h4">基本信息</h4>
    <el-form ref="form" :model="user" label-width="80px">
      <el-row>
        <el-col :offset="2" :span="8">
          <el-form-item label="用户昵称" prop="nickName">
            <el-input v-model="user.nickName" disabled />
          </el-form-item>
        </el-col>
        <el-col :offset="2" :span="8">
          <el-form-item label="登录账号" prop="userName">
            <el-input v-model="user.userName" disabled />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <h4 class="form-header h4">角色信息</h4>
    <el-table
      ref="table"
      v-loading="tableLoading"
      :data="roleList"
      :row-key="handleGetRowKey"
      @row-click="handleClickRow"
      @selection-change="handleSelectionChange"
    >
      <el-table-column :reserve-selection="true" type="selection" width="55" />
      <el-table-column align="center" label="角色编号" prop="id" />
      <el-table-column align="center" label="角色名称" prop="roleName" />
      <el-table-column align="center" label="权限字符" prop="roleStr" />
      <el-table-column align="center" label="创建时间" prop="createTime" width="160" />
    </el-table>
    <pagination
      v-show="pageTotal>0"
      :limit.sync="queryForm.pageSize"
      :page.sync="queryForm.pageNum"
      :total="pageTotal"
      @pagination="getRoleList"
    />
    <el-form label-width="100px">
      <el-form-item style="text-align: center;margin-left:-120px;margin-top:30px;">
        <el-button type="primary" @click="handleSubmit()">提交</el-button>
        <el-button @click="handleClose">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { getAuthRolePageList, loadUser, updateAuthRole } from '@/api/system/user'
import crud from '@/framework/mixin/crud'

export default {
  name: 'AuthRole',
  mixins: [crud],
  data() {
    return {
      queryForm: {
        pageNum: 1,
        pageSize: 10
      },
      userId: null,
      // 用户信息
      user: {
        roleIdList: []
      },
      roleList: []
    }
  },
  created() {
    this.userId = this.$route.params && this.$route.params.userId
    this.getUser()
  },
  methods: {
    getUser() {
      loadUser({ id: this.userId }).then(res => {
        this.user = res.data
        this.getRoleList()
      })
    },
    getRoleList() {
      this.openTableLoading()
      getAuthRolePageList(this.queryForm).then(res => {
        this.roleList = res.data.rows
        this.pageTotal = res.data.total
        this.closeTableLoading()
        this.$nextTick(() => {
          this.roleList.forEach(role => {
            if (this.user.roleIdList.includes(role.id)) {
              this.$refs.table.toggleRowSelection(role)
            }
          })
        })
      })
    },
    // 单击选中行数据
    handleClickRow(row) {
      this.$refs.table.toggleRowSelection(row)
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.tableIds = selection.map((item) => item.id)
    },
    // 保存选中的数据编号
    handleGetRowKey(row) {
      return row.id
    },
    // 提交按钮
    handleSubmit() {
      updateAuthRole({ userId: this.userId, roleIds: this.tableIds }).then(res => {
        this.$modal.success('授权成功')
        this.handleClose()
      })
    },
    // 关闭按钮
    handleClose() {
      this.$tab.closeOpenPage({ path: '/system/user' })
    }
  }
}
</script>
