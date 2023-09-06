<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--机构树-->
      <el-col :span="4" :xs="24">
        <org-tree @nodeClick="handleOrgClick" />
      </el-col>
      <!--用户数据-->
      <el-col :span="20" :xs="24">
        <el-form
          v-show="queryShow"
          ref="queryForm"
          :model="queryForm"
          inline
          :label-width="queryLabelWidth"
          size="small"
        >
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
          <el-form-item label="状态" prop="status">
            <dict-select
              v-model="queryForm.status"
              dict-type="db_status"
              clearable
              placeholder="用户状态"
              style="width: 240px"
            />
          </el-form-item>
          <el-form-item label="创建时间" prop="createTimeRange">
            <base-date-range-picker v-model="queryForm.createTimeRange" style="width: 240px" />
          </el-form-item>
          <base-search-button @click="handleQuery" />
          <base-reset-button @click="handleResetQuery" />
        </el-form>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <base-add-button v-has-perm="['system:user:add']" plain @click="handleAdd('用户新增')" />
          </el-col>
          <el-col :span="1.5">
            <base-update-button v-has-perm="['system:user:update']" plain :disabled="!tableSelectOne" @click="handleUpdate" />
          </el-col>
          <el-col :span="1.5">
            <base-remove-button v-has-perm="['system:user:remove']" plain :disabled="tableNoSelected" @click="handleDelete" />
          </el-col>
          <el-col :span="1.5">
            <base-upload-button v-has-perm="['system:user:import']" plain @click="handleImport">导入</base-upload-button>
          </el-col>
          <el-col :span="1.5">
            <base-download-button v-has-perm="['system:user:export']" plain @click="handleExport">导出</base-download-button>
          </el-col>
          <right-toolbar :columns.sync="columns" :query-show.sync="queryShow" @refresh="getList" />
        </el-row>
        <el-table v-loading="tableLoading" :data="userList" @selection-change="handleSelectionChange">
          <el-table-column align="center" type="selection" :selectable="tableSelectEnabled" width="50" />
          <el-table-column v-if="columns.id.show" key="id" align="center" label="用户编号" prop="id" />
          <el-table-column
            v-if="columns.userName.show"
            key="userName"
            :show-overflow-tooltip="true"
            align="center"
            label="用户名称"
            prop="userName"
          />
          <el-table-column
            v-if="columns.nickName.show"
            key="nickName"
            :show-overflow-tooltip="true"
            align="center"
            label="用户昵称"
            prop="nickName"
          />
          <el-table-column
            v-if="columns.orgName.show"
            key="orgName"
            :show-overflow-tooltip="true"
            align="center"
            label="组织机构"
            prop="orgName"
          />
          <el-table-column
            v-if="columns.userMobile.show"
            key="userMobile"
            align="center"
            label="手机号码"
            prop="userMobile"
            width="120"
          />
          <el-table-column v-if="columns.status.show" key="status" align="center" label="状态">
            <template slot-scope="scope">
              <base-switch v-model="scope.row.status" :disabled="$auth.isAdmin(scope.row.id)" @change="handleStatusChange(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column v-if="columns.createTime.show" align="center" label="创建时间" prop="createTime" width="160" />
          <el-table-column
            align="center"
            class-name="small-padding fixed-width"
            label="操作"
            width="160"
          >
            <template v-if="!$auth.isAdmin(scope.row.id)" slot-scope="scope">
              <base-update-button v-has-perm="['system:user:update']" type="text" @click="handleUpdate(scope.row)" />
              <base-remove-button v-has-perm="['system:user:remove']" type="text" @click="handleDelete(scope.row)" />
              <base-column-menu>
                <base-text-button
                  v-has-perm="['system:user:updatePasswd']"
                  icon="el-icon-key"
                  color="#606266"
                  @click="handleResetPwd(scope.row)"
                >重置密码</base-text-button>
                <base-text-button
                  v-has-perm="['system:user:update']"
                  icon="el-icon-circle-check"
                  color="#606266"
                  @click="handleAuthRole(scope.row)"
                >分配角色</base-text-button>
              </base-column-menu>
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
      </el-col>
    </el-row>

    <!-- 用户编辑对话框 -->
    <base-dialog :title="editTitle" :open.sync="editOpen" @confirm="handleEditSubmit" @cancel="handleEditCancel">
      <el-form ref="editForm" :model="editForm" :rules="editRules" label-width="80px">
        <base-row-split2>
          <el-form-item label="用户昵称" prop="nickName">
            <el-input v-model="editForm.nickName" maxlength="30" placeholder="请输入用户昵称" />
          </el-form-item>
          <el-form-item label="组织机构" prop="orgId">
            <org-select v-model="editForm.orgId" />
          </el-form-item>
          <el-form-item label="手机号码" prop="userMobile">
            <el-input v-model="editForm.userMobile" maxlength="11" placeholder="请输入手机号码" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="editForm.email" maxlength="50" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item v-if="!editForm.id" label="用户名称" prop="userName">
            <el-input v-model="editForm.userName" maxlength="30" placeholder="请输入用户名称" />
          </el-form-item>
          <el-form-item v-if="!editForm.id" label="用户密码" prop="userPassword">
            <el-input v-model="editForm.userPassword" maxlength="20" placeholder="请输入用户密码" show-password type="password" />
          </el-form-item>
          <el-form-item label="用户性别">
            <dict-select v-model="editForm.sex" dict-type="sys_user_sex" placeholder="请选择性别" />
          </el-form-item>
          <el-form-item label="状态">
            <dict-radio v-model="editForm.status" dict-type="db_status" />
          </el-form-item>
          <el-form-item label="岗位">
            <post-select v-model="editForm.postIdList" multiple />
          </el-form-item>
          <el-form-item label="角色">
            <role-select v-model="editForm.roleIdList" multiple />
          </el-form-item>
        </base-row-split2>
        <el-form-item label="备注">
          <el-input v-model="editForm.remark" placeholder="请输入内容" type="textarea" />
        </el-form-item>
      </el-form>
    </base-dialog>

    <!-- 用户导入对话框 -->
    <base-dialog :title="upload.title" :open.sync="upload.open" width="400px" @confirm="handleImportConfirm">
      <file-upload-drag
        ref="upload"
        :action="upload.action"
        :accept="upload.accept"
        :form-data="{cover: upload.cover}"
        @onError="handleImportError"
        @onSuccess="handleImportSuccess"
      >
        <template slot="tip">
          <el-checkbox v-model="upload.cover" />
          是否更新已经存在的用户数据
        </template>
        <span>仅允许导入xls、xlsx格式文件。</span>
        <el-link
          :underline="false"
          style="font-size:12px;vertical-align: baseline;"
          type="primary"
          @click="handleDownloadTemplate"
        >下载模板
        </el-link>
      </file-upload-drag>
    </base-dialog>
  </div>
</template>

<script>
import {
  addUser,
  getUserPageList, loadUser, removeUser, resetUserPasswd,
  updateUser, updateUserStatus
} from '@/api/system/user'
import FileUploadDrag from '@/views/components/FileUploadDrag.vue'
import crud from '@/framework/mixin/crud'

export default {
  name: 'User',
  components: { FileUploadDrag },
  mixins: [crud],
  data() {
    return {
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        userName: null,
        userMobile: null,
        status: null,
        createTimeRange: null,
        orgId: null
      },
      // 默认密码
      initPassword: null,
      // 用户表格数据
      userList: null,
      // 用户导入参数
      upload: {
        // 是否显示弹出层
        open: false,
        // 弹出层标题
        title: '用户导入',
        // 是否更新已经存在的用户数据
        cover: false,
        // 上传的地址
        action: '/system/user/importUserList',
        // 接受格式
        accept: '.xls, .xlsx'
      },
      // 列信息
      columns: {
        id: { label: '用户编号', show: true },
        userName: { label: '用户名称', show: true },
        nickName: { label: '用户昵称', show: true },
        orgName: { label: '组织机构', show: true },
        userMobile: { label: '手机号码', show: true },
        status: { label: '状态', show: true },
        createTime: { label: '创建时间', show: true }
      },
      // 表单校验
      editRules: {
        orgId: [
          this.$validator.requiredRule('机构组织不能为空')
        ],
        userName: [
          this.$validator.requiredRule('用户名称不能为空'),
          this.$validator.sizeRule(2, 20, '用户名称长度必须介于 2 和 20 之间')
        ],
        nickName: [
          this.$validator.requiredRule('用户昵称不能为空')
        ],
        userPassword: [
          this.$validator.requiredRule('用户密码不能为空'),
          this.$validator.sizeRule(5, 20, '用户密码长度必须介于 5 和 20 之间')
        ],
        email: [
          this.$validator.emailRule('请输入正确的邮箱地址')
        ],
        userMobile: [
          this.$validator.mobileRule('请输入正确的手机号码')
        ]
      }
    }
  },
  created() {
    this.getList()
    this.$config.getInitPassword().then(res => {
      res.configValue && (this.initPassword = res.configValue)
    })
  },
  methods: {
    // 列表查询
    getList() {
      this.openTableLoading()
      getUserPageList(this.queryForm).then(res => {
        this.userList = res.rows
        this.pageTotal = res.total
        this.closeTableLoading()
      })
    },
    // 查询按钮
    handleQuery() {
      this.resetPageNum()
      this.getList()
    },
    // 查询充值按钮
    handleResetQuery() {
      this.$refs.queryForm && this.$refs.queryForm.resetFields()
      this.queryForm.orgId = null
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.tableIds = selection.map(item => item.id)
    },
    // 新增按钮
    handleAdd() {
      this.openEdit('新增用户')
    },
    // 修改按钮
    handleUpdate(row) {
      const id = row.id || this.tableIds[0]
      loadUser({ id }).then(res => {
        this.openEdit('修改用户')
        this.editForm = {
          ...res
        }
      })
    },
    // 删除按钮
    handleDelete(row) {
      const list = (row.id && [row.id]) || this.tableIds
      this.$modal.confirm('是否确认删除用户编号为"' + list + '"的数据项？').then(res => {
        return removeUser({ list })
      }).then(() => {
        this.getList()
        this.$modal.success('删除成功')
      }).catch(error => {
        console.log(error)
      })
    },
    // 编辑提交按钮
    handleEditSubmit() {
      this.$refs.editForm.validate(valid => {
        if (valid) {
          if (this.editForm.id) {
            updateUser(this.editForm).then(res => {
              this.$modal.success('修改成功')
              this.closeEdit()
              this.getList()
            })
          } else {
            this.editForm.userPassword = this.$crypto.aesEncrypt(this.editForm.userPassword)
            addUser(this.editForm).then(res => {
              this.$modal.success('新增成功')
              this.closeEdit()
              this.getList()
            })
          }
        }
      })
    },
    // 编辑取消按钮
    handleEditCancel() {
      this.closeEdit()
    },
    // 重置表单数据
    resetEditForm() {
      this.editForm = {
        id: null,
        orgId: null,
        userMobile: null,
        email: null,
        userName: null,
        userPassword: this.initPassword,
        sex: null,
        status: '1',
        postIdList: null,
        roleIdList: null
      }
      this.$refs.editForm && this.$refs.editForm.resetFields()
    },
    // 用户状态修改
    handleStatusChange(row) {
      const confirmAction = row.status === '1' ? '停用' : '启用'
      const confirmText = `确认要${confirmAction}"${row.userName}"用户吗？`
      this.$modal.confirm(confirmText).then(() => {
        const params = {
          id: row.id,
          status: row.status
        }
        updateUserStatus(params).then(res => {
          this.$modal.success(`${confirmAction}成功`)
        }).catch(err => {
          console.log(err)
          this.$modal.error(`${confirmAction}失败`)
        })
      }).catch(() => {
        row.status = row.status === '1' ? '2' : '1'
      })
    },
    // 用户密码重置
    handleResetPwd(row) {
      this.$prompt('请输入"' + row.userName + '"的新密码', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        closeOnClickModal: false,
        inputPattern: /^.{5,20}$/,
        inputErrorMessage: '用户密码长度必须介于 5 和 20 之间'
      }).then(({ value }) => {
        const data = {
          id: row.id,
          userPassword: this.$crypto.aesEncrypt(value)
        }
        resetUserPasswd(data).then(response => {
          this.$modal.success('重置成功')
        })
      }).catch(() => {
      })
    },
    // 用户角色分配
    handleAuthRole(row) {
      this.$router.push('/system/user-auth/role/' + row.id)
    },
    // 机构树点击
    handleOrgClick(data) {
      this.queryForm.orgId = data.orgId
      this.handleQuery()
    },
    // 导出按钮
    handleExport() {
      this.$download.postAsName('/system/user/exportUserList', {}, { ...this.queryForm }, `user_${new Date().getTime()}.xlsx`).catch(error => {
        console.log(error)
        this.$modal.error('导出失败')
      })
    },
    // 导入按钮
    handleImport() {
      this.upload.title = '用户导入'
      this.upload.open = true
    },
    // 下载导入模版
    handleDownloadTemplate() {
      this.$download.postAsName('/system/user/downloadUserTemplate', {}, {}, `user_template_${new Date().getTime()}.xlsx`).then(error => {
        console.log(error)
        this.$modal.error('下载模版失败')
      })
    },
    // 导入确认按钮
    handleImportConfirm() {
      this.$refs.upload.submit()
    },
    handleImportError() {
      this.$modal.error('用户导入失败')
      this.upload.open = false
      this.$refs.upload.clear()
    },
    handleImportSuccess(res) {
      this.$refs.upload.clear()
      this.upload.open = false
      this.alertImportMsg(res)
      this.getList()
    },
    alertImportMsg(msg) {
      this.$alert('<div style=\'overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;\'>' + msg + '</div>', '导入结果', { dangerouslyUseHTMLString: true })
    },
    tableSelectEnabled(row, index) {
      return !this.$auth.isAdminRole(row.id)
    }
  }
}
</script>
