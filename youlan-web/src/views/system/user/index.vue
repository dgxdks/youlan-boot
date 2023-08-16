<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--部门数据-->
      <el-col :span="4" :xs="24">
        <org-tree />
      </el-col>
      <!--用户数据-->
      <el-col :span="20" :xs="24">
        <table-query>
          <el-input
            slot="userName"
            v-model="queryParams.userName"
            label="用户名称"
            clearable
            placeholder="请输入用户名称"
            style="width: 240px"
            @keyup.enter.native="handleQuery"
          />
          <el-input
            slot="userMobile"
            v-model="queryParams.userMobile"
            label="手机号码"
            clearable
            placeholder="请输入手机号码"
            style="width: 240px"
            @keyup.enter.native="handleQuery"
          />
          <dict-select
            slot="status"
            v-model="queryParams.status"
            label="状态"
            dict-type="web_common_status"
            clearable
            placeholder="用户状态"
            style="width: 240px"
          />
          <el-date-picker
            slot="createTimeRange"
            v-model="queryParams.createTimeRange"
            label="创建时间"
            end-placeholder="结束日期"
            range-separator="-"
            start-placeholder="开始日期"
            style="width: 240px"
            type="daterange"
            value-format="yyyy-MM-dd"
          />
          <el-button slot="button" icon="el-icon-search" size="mini" type="primary" @click="handleQuery">搜索</el-button>
          <el-button slot="button" icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </table-query>
        <table-options>
          <el-button
            slot="add"
            v-hasPerm="['system:user:add']"
            icon="el-icon-plus"
            plain
            size="mini"
            type="primary"
            @click="handleAdd"
          >新增
          </el-button>
          <el-button
            slot="update"
            v-hasPerm="['system:user:update']"
            :disabled="single"
            icon="el-icon-edit"
            plain
            size="mini"
            type="success"
            @click="handleUpdate"
          >修改
          </el-button>
          <el-button
            slot="remove"
            v-hasPerm="['system:user:remove']"
            :disabled="multiple"
            icon="el-icon-delete"
            plain
            size="mini"
            type="danger"
            @click="handleDelete"
          >删除
          </el-button>
          <el-button
            slot="import"
            v-hasPerm="['system:user:import']"
            icon="el-icon-upload2"
            plain
            size="mini"
            type="info"
            @click="handleImport"
          >导入
          </el-button>
          <el-button
            slot="export"
            v-hasPerm="['system:user:export']"
            icon="el-icon-download"
            plain
            size="mini"
            type="warning"
            @click="handleExport"
          >导出
          </el-button>
          <right-toolbar :columns="columns" :show-search.sync="showSearch" @queryTable="getList" />
        </table-options>

        <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
          <el-table-column align="center" type="selection" width="50" />
          <el-table-column v-if="columns[0].visible" key="id" align="center" label="用户编号" prop="id" />
          <el-table-column
            v-if="columns[1].visible"
            key="userName"
            :show-overflow-tooltip="true"
            align="center"
            label="用户名称"
            prop="userName"
          />
          <el-table-column
            v-if="columns[2].visible"
            key="nickName"
            :show-overflow-tooltip="true"
            align="center"
            label="用户昵称"
            prop="nickName"
          />
          <el-table-column
            v-if="columns[3].visible"
            key="orgName"
            :show-overflow-tooltip="true"
            align="center"
            label="组织机构"
            prop="orgName"
          />
          <el-table-column
            v-if="columns[4].visible"
            key="userMobile"
            align="center"
            label="手机号码"
            prop="userMobile"
            width="120"
          />
          <el-table-column v-if="columns[5].visible" key="status" align="center" label="状态">
            <template slot-scope="scope">
              <base-switch v-model="scope.row.status" @change="handleStatusChange(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column v-if="columns[6].visible" align="center" label="创建时间" prop="createTime" width="160" />
          <el-table-column
            align="center"
            class-name="small-padding fixed-width"
            label="操作"
            width="160"
          >
            <template v-if="scope.row.userId !== 1" slot-scope="scope">
              <el-button
                v-hasPerm="['system:user:edit']"
                icon="el-icon-edit"
                size="mini"
                type="text"
                @click="handleUpdate(scope.row)"
              >修改
              </el-button>
              <el-button
                v-hasPerm="['system:user:remove']"
                icon="el-icon-delete"
                size="mini"
                type="text"
                @click="handleDelete(scope.row)"
              >删除
              </el-button>
              <el-dropdown
                v-hasPerm="['system:user:resetPwd', 'system:user:edit']"
                size="mini"
                @command="(command) => handleCommand(command, scope.row)"
              >
                <el-button icon="el-icon-d-arrow-right" size="mini" type="text">更多</el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item
                    v-hasPerm="['system:user:resetPwd']"
                    command="handleResetPwd"
                    icon="el-icon-key"
                  >重置密码
                  </el-dropdown-item>
                  <el-dropdown-item
                    v-hasPerm="['system:user:edit']"
                    command="handleAuthRole"
                    icon="el-icon-circle-check"
                  >分配角色
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="total>0"
          :limit.sync="queryParams.pageSize"
          :page.sync="queryParams.pageNum"
          :total="total"
          @pagination="getList"
        />
      </el-col>
    </el-row>

    <!-- 添加或修改用户配置对话框 -->
    <base-dialog :title="title" :open.sync="open" @confirm="submitForm" @cancel="cancel">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <row-split2>
          <el-form-item slot="1" label="用户昵称" prop="nickName">
            <el-input v-model="form.nickName" maxlength="30" placeholder="请输入用户昵称" />
          </el-form-item>
          <el-form-item slot="2" label="组织机构" prop="orgId">
            <org-select v-model="form.orgId" />
          </el-form-item>
          <el-form-item slot="1" label="手机号码" prop="phonenumber">
            <el-input v-model="form.phonenumber" maxlength="11" placeholder="请输入手机号码" />
          </el-form-item>
          <el-form-item slot="2" label="邮箱" prop="email">
            <el-input v-model="form.email" maxlength="50" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item v-if="form.userId == undefined" slot="1" label="用户名称" prop="userName">
            <el-input v-model="form.userName" maxlength="30" placeholder="请输入用户名称" />
          </el-form-item>
          <el-form-item v-if="form.userId == undefined" slot="2" label="用户密码" prop="password">
            <el-input
              v-model="form.password"
              maxlength="20"
              placeholder="请输入用户密码"
              show-password
              type="password"
            />
          </el-form-item>
          <el-form-item slot="1" label="用户性别">
            <dict-select v-model="form.sex" dict-type="sys_user_sex" placeholder="请选择性别" />
          </el-form-item>
          <el-form-item slot="2" label="状态">
            <dict-radio v-model="form.status" dict-type="web_common_status" />
          </el-form-item>
          <el-form-item slot="1" label="岗位">
            <el-select v-model="form.postIds" multiple placeholder="请选择岗位">
              <el-option
                v-for="item in postOptions"
                :key="item.postId"
                :disabled="item.status == 1"
                :label="item.postName"
                :value="item.postId"
              />
            </el-select>
          </el-form-item>
          <el-form-item slot="2" label="角色">
            <el-select v-model="form.roleIds" multiple placeholder="请选择角色">
              <el-option
                v-for="item in roleOptions"
                :key="item.roleId"
                :disabled="item.status == 1"
                :label="item.roleName"
                :value="item.roleId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="form.remark" placeholder="请输入内容" type="textarea" />
          </el-form-item>
        </row-split2>
      </el-form>
    </base-dialog>

    <!-- 用户导入对话框 -->
    <base-dialog :title="upload.title" :open.sync="upload.open" width="400px" @confirm="submitFileForm">
      <el-upload
        ref="upload"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :auto-upload="false"
        :disabled="upload.isUploading"
        :headers="upload.headers"
        :limit="1"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        accept=".xlsx, .xls"
        drag
      >
        <i class="el-icon-upload" />
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div slot="tip" class="el-upload__tip text-center">
          <div slot="tip" class="el-upload__tip">
            <el-checkbox v-model="upload.updateSupport" />
            是否更新已经存在的用户数据
          </div>
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link
            :underline="false"
            style="font-size:12px;vertical-align: baseline;"
            type="primary"
            @click="importTemplate"
          >下载模板
          </el-link>
        </div>
      </el-upload>
    </base-dialog>
  </div>
</template>

<script>
import {
  addUser,
  changeUserStatus,
  delUser,
  deptTreeSelect,
  getUserPageList,
  resetUserPwd,
  updateUser
} from '@/api/system/user'

export default {
  name: 'User',
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 用户表格数据
      userList: null,
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 部门名称
      deptName: undefined,
      // 默认密码
      initPassword: undefined,
      // 日期范围
      dateRange: [],
      // 岗位选项
      postOptions: [],
      // 角色选项
      roleOptions: [],
      // 表单参数
      form: {},
      // 用户导入参数
      upload: {
        // 是否显示弹出层（用户导入）
        open: false,
        // 弹出层标题（用户导入）
        title: '',
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的用户数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: this.$store.state.tokenHeaders,
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + '/system/user/importData'
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        userMobile: undefined,
        status: undefined,
        deptId: undefined
      },
      // 列信息
      columns: [
        { key: 0, label: `用户编号`, visible: true },
        { key: 1, label: `用户名称`, visible: true },
        { key: 2, label: `用户昵称`, visible: true },
        { key: 3, label: `部门`, visible: true },
        { key: 4, label: `手机号码`, visible: true },
        { key: 5, label: `状态`, visible: true },
        { key: 6, label: `创建时间`, visible: true }
      ],
      // 表单校验
      rules: {
        userName: [
          { required: true, message: '用户名称不能为空', trigger: 'blur' },
          { min: 2, max: 20, message: '用户名称长度必须介于 2 和 20 之间', trigger: 'blur' }
        ],
        nickName: [
          { required: true, message: '用户昵称不能为空', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '用户密码不能为空', trigger: 'blur' },
          { min: 5, max: 20, message: '用户密码长度必须介于 5 和 20 之间', trigger: 'blur' }
        ],
        email: [
          {
            type: 'email',
            message: '请输入正确的邮箱地址',
            trigger: ['blur', 'change']
          }
        ],
        phonenumber: [
          {
            pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
            message: '请输入正确的手机号码',
            trigger: 'blur'
          }
        ]
      }
    }
  },
  watch: {
    // // 根据名称筛选部门树
    // deptName(val) {
    //   this.$refs.tree.filter(val)
    // }
  },
  created() {
    this.getList()
    // this.getDeptTree()
    // this.getConfigKey('sys.user.initPassword').then(response => {
    //   this.initPassword = response.msg
    // })
  },
  methods: {
    getList() {
      this.loading = true
      getUserPageList(this.addDateRange(this.queryParams, this.dateRange)).then(res => {
        this.userList = res.rows
        this.total = res.total
        this.loading = false
      }
      )
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
    },
    /** 查询部门下拉树结构 */
    getDeptTree() {
      deptTreeSelect().then(response => {
        this.deptOptions = response.data
      })
    },
    handleStatusChange(row) {
      const text = row.status === '0' ? '启用' : '停用'
      this.$modal.confirm('确认要"' + text + '""' + row.userName + '"用户吗？').then(function() {
        return changeUserStatus(row.userId, row.status)
      }).then(() => {
        this.$modal.msgSuccess(text + '成功')
      }).catch(function() {
        row.status = row.status === '0' ? '1' : '0'
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        userId: undefined,
        deptId: undefined,
        userName: undefined,
        nickName: undefined,
        password: undefined,
        phonenumber: undefined,
        email: undefined,
        sex: undefined,
        status: '1',
        remark: undefined,
        postIds: [],
        roleIds: []
      }
      this.resetForm('form')
    },

    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = []
      this.resetForm('queryForm')
      this.queryParams.deptId = undefined
      this.$refs.tree.setCurrentKey(null)
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.userId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    // 更多操作触发
    handleCommand(command, row) {
      switch (command) {
        case 'handleResetPwd':
          this.handleResetPwd(row)
          break
        case 'handleAuthRole':
          this.handleAuthRole(row)
          break
        default:
          break
      }
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const userId = row.userId || this.ids
      getUser(userId).then(response => {
        this.form = response.data
        this.postOptions = response.posts
        this.roleOptions = response.roles
        this.$set(this.form, 'postIds', response.postIds)
        this.$set(this.form, 'roleIds', response.roleIds)
        this.open = true
        this.title = '修改用户'
        this.form.password = ''
      })
    },
    /** 重置密码按钮操作 */
    handleResetPwd(row) {
      this.$prompt('请输入"' + row.userName + '"的新密码', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        closeOnClickModal: false,
        inputPattern: /^.{5,20}$/,
        inputErrorMessage: '用户密码长度必须介于 5 和 20 之间'
      }).then(({ value }) => {
        resetUserPwd(row.userId, value).then(response => {
          this.$modal.msgSuccess('修改成功，新密码是：' + value)
        })
      }).catch(() => {
      })
    },
    /** 分配角色操作 */
    handleAuthRole: function(row) {
      const userId = row.userId
      this.$router.push('/system/user-auth/role/' + userId)
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.userId !== undefined) {
            updateUser(this.form).then(response => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addUser(this.form).then(response => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const userIds = row.userId || this.ids
      this.$modal.confirm('是否确认删除用户编号为"' + userIds + '"的数据项？').then(function() {
        return delUser(userIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {
      })
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/user/export', {
        ...this.queryParams
      }, `user_${new Date().getTime()}.xlsx`)
    },
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = '用户导入'
      this.upload.open = true
    },
    /** 下载模板操作 */
    importTemplate() {
      this.download('system/user/importTemplate', {}, `user_template_${new Date().getTime()}.xlsx`)
    },
    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false
      this.upload.isUploading = false
      this.$refs.upload.clearFiles()
      this.$alert('<div style=\'overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;\'>' + response.msg + '</div>', '导入结果', { dangerouslyUseHTMLString: true })
      this.getList()
    },
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit()
    }
  }
}
</script>
