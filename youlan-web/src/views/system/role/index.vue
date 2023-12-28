<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" inline :model="queryForm" size="small">
      <el-form-item label="角色名称" prop="roleName">
        <el-input
          v-model="queryForm.roleName"
          clearable
          placeholder="请输入角色名称"
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="权限字符" prop="roleStr">
        <el-input
          v-model="queryForm.roleStr"
          clearable
          placeholder="请输入权限字符"
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <dict-select v-model="queryForm.status" dict-type="db_status" placeholder="角色状态" style="width: 240px" />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTimeRange">
        <base-date-range-picker v-model="queryForm.createTimeRange" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <base-search-button @click="handleQuery" />
        <base-reset-button @click="handleResetQuery" />
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <base-add-button v-has-perm="['system:role:add']" plain @click="handleAdd" />
      </el-col>
      <el-col :span="1.5">
        <base-update-button v-has-perm="['system:role:update']" plain :disabled="!tableSelectOne" @click="handleUpdate" />
      </el-col>
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:role:remove']" plain :disabled="tableNoSelected" @click="handleDelete" />
      </el-col>
      <el-col :span="1.5">
        <base-download-button v-has-perm="['system:role:export']" plain @click="handleExport">导出</base-download-button>
      </el-col>
      <table-toolbar :query-show.sync="queryShow" @refresh="getList" />
    </el-row>

    <el-table v-loading="tableLoading" :data="roleList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" :selectable="tableSelectEnabled" width="55" />
      <el-table-column label="角色编号" prop="id" width="120" />
      <el-table-column show-overflow-tooltip label="角色名称" prop="roleName" width="150" />
      <el-table-column show-overflow-tooltip label="角色字符" prop="roleStr" width="150" />
      <el-table-column label="显示顺序" prop="sort" width="100" />
      <el-table-column align="center" label="状态" width="100">
        <template slot-scope="scope">
          <base-switch
            v-model="scope.row.status"
            :disabled="$auth.isAdminRole(scope.row.id)"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column align="center" label="创建时间" prop="createTime" width="160" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template v-if="!$auth.isAdminRole(scope.row.id)" slot-scope="scope">
          <base-update-button v-has-perm="['system:role:update']" type="text" @click="handleUpdate(scope.row)" />
          <base-remove-button v-has-perm="['system:role:remove']" type="text" @click="handleDelete(scope.row)" />
          <base-menu-button v-has-perm="['system:role:update']">
            <base-text-button
              v-has-perm="['system:role:update']"
              icon="el-icon-circle-check"
              color="#606266"
              @click="handleDataScope(scope.row)"
            >数据权限
            </base-text-button>
            <base-text-button
              v-has-perm="['system:role:update']"
              icon="el-icon-user"
              color="#606266"
              @click="handleAuthUser(scope.row)"
            >分配用户
            </base-text-button>
            <base-text-button
              v-has-perm="['system:role:remove']"
              icon="el-icon-delete"
              color="#606266"
              @click="handleRefreshRole(scope.row)"
            >刷新缓存
            </base-text-button>
          </base-menu-button>
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

    <!-- 角色编辑对话框 -->
    <base-dialog :title="editTitle" :open.sync="editOpen" @confirm="handleEditSubmit" @cancel="handleEditCancel">
      <el-form ref="editForm" :model="editForm" :rules="editRules" label-width="100px">
        <base-row-split2>
          <el-form-item label="角色名称" prop="roleName">
            <el-input v-model="editForm.roleName" placeholder="请输入角色名称" />
          </el-form-item>
          <el-form-item prop="roleStr">
            <base-form-label
              slot="label"
              label="角色字符"
              content="控制器中定义的权限字符，如：@SaCheckRole('admin')，新增后不支持修改"
            />
            <el-input v-model="editForm.roleStr" placeholder="请输入权限字符" :disabled="$obj.isNotEmpty(editForm.id)" />
          </el-form-item>
          <el-form-item label="角色顺序" prop="sort">
            <el-input-number v-model="editForm.sort" :min="0" controls-position="right" />
          </el-form-item>
          <el-form-item label="状态">
            <dict-radio v-model="editForm.status" dict-type="db_status" />
          </el-form-item>
        </base-row-split2>
        <el-form-item label="备注">
          <el-input v-model="editForm.remark" placeholder="请输入内容" type="textarea" />
        </el-form-item>
        <el-form-item label="菜单权限">
          <el-checkbox v-model="menuExpandAll">展开/折叠</el-checkbox>
          <el-checkbox v-model="menuCheckAll">全选/全不选</el-checkbox>
          <menu-tree
            ref="menu"
            :expand-all="menuExpandAll"
            :default-expand-all="menuExpandAll"
            :check-all="menuCheckAll"
            class="tree-border"
          />
        </el-form-item>
      </el-form>
    </base-dialog>

    <!-- 数据权限编辑对话框 -->
    <base-dialog
      :title="dataScope.title"
      :open.sync="dataScope.open"
      width="500px"
      @confirm="handleDataScopeConfirm"
      @cancel="handleDataScopeCancel"
    >
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="角色名称">
          <el-input v-model="editForm.roleName" :disabled="true" />
        </el-form-item>
        <el-form-item label="角色字符">
          <el-input v-model="editForm.roleStr" :disabled="true" />
        </el-form-item>
        <el-form-item label="权限范围">
          <dict-select
            v-model="editForm.roleScope"
            dict-type="sys_data_scope"
            style="width: 240px"
            @change="handleDataScopeChange"
          />
        </el-form-item>
        <!--自定义数据权限展示-->
        <el-form-item v-show="editForm.roleScope === '2'" label="数据权限">
          <el-checkbox v-model="orgExpandAll">展开/折叠</el-checkbox>
          <el-checkbox @change="handleOrgCheckAll">全选/全不选</el-checkbox>
          <org-tree
            ref="org"
            show-check-box
            :expand-all="orgExpandAll"
            :default-expand-all="orgExpandAll"
            :check-all="orgCheckAll"
            :search-enabled="false"
            :check-strictly="orgCheckStrictly"
            class="tree-border"
          />
        </el-form-item>
      </el-form>
    </base-dialog>
  </div>
</template>

<script>
import {
  addRole,
  getRolePageList,
  loadRole,
  refreshRoleCache,
  removeRole,
  updateRole,
  updateRoleScope,
  updateRoleStatus
} from '@/api/system/role'
import crud from '@/framework/mixin/crud'
import MenuTree from '@/views/components/MenuTree.vue'

export default {
  name: 'Role',
  components: { MenuTree },
  mixins: [crud],
  data() {
    return {
      // 角色表格数据
      roleList: [],
      // 数据权限参数
      dataScope: {
        // 对话框标记
        title: '数据权限',
        // 是否显示弹出层
        open: false
      },
      // 是否展开全部菜单
      menuExpandAll: false,
      // 是否选中全部菜单
      menuCheckAll: false,
      // 是否展开全部机构
      orgExpandAll: true,
      // 是否选中全部机构
      orgCheckAll: false,
      // 是否父子不相互关联
      orgCheckStrictly: true,
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        roleName: null,
        roleStr: null,
        status: null,
        createTimeRange: null
      },
      // 表单校验
      editRules: {
        roleName: [
          this.$validator.requiredRule('角色名称不能为空')
        ],
        roleStr: [
          this.$validator.requiredRule('权限字符不能为空')
        ],
        roleSort: [
          this.$validator.requiredRule('角色顺序不能为空')
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 列表查询
    getList() {
      this.openTableLoading()
      getRolePageList(this.queryForm).then(res => {
        this.roleList = res.data.rows
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.tableIds = selection.map(item => item.id)
    },
    // 新增按钮
    handleAdd() {
      this.openEdit('新增角色')
    },
    // 修改按钮
    handleUpdate(row) {
      const id = row.id || this.tableIds[0]
      loadRole({ id }).then(res => {
        this.openEdit('修改角色')
        this.editForm = {
          ...res.data
        }
        this.$nextTick(() => {
          this.$refs.menu.setCheckedKeys(this.editForm.menuIdList)
        })
      })
    },
    // 删除按钮
    handleDelete(row) {
      const list = (row.id && [row.id]) || this.tableIds
      this.$modal.confirm('是否确认删除角色编号为"' + list + '"的数据项？').then(res => {
        return removeRole({ list })
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
          this.editForm.menuIdList = this.getMenuAllCheckedKeys()
          if (this.editForm.id) {
            updateRole(this.editForm).then(res => {
              this.$modal.success('修改成功')
              this.closeEdit()
              this.getList()
            })
          } else {
            addRole(this.editForm).then(res => {
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
        roleName: null,
        roleStr: null,
        roleScope: null,
        sort: 0,
        status: '1',
        menuIdList: [],
        orgIdList: [],
        remark: null
      }
      this.menuCheckAll = false
      this.menuExpandAll = false
      this.orgExpandAll = true
      this.orgCheckAll = false
      // 置空选中菜单节点
      this.$refs.menu && this.$refs.menu.setCheckedKeys([])
      // 折叠菜单树
      this.$refs.menu && this.$refs.menu.handleExpandAll(false)
      this.$refs.editForm && this.$refs.editForm.resetFields()
    },
    // 所有菜单节点数据
    getMenuAllCheckedKeys() {
      // 目前被选中的菜单节点
      const checkedKeys = this.$refs.menu.getCheckedKeys()
      // 半选中的菜单节点
      const halfCheckedKeys = this.$refs.menu.getHalfCheckedKeys()
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys)
      return checkedKeys
    },
    // 所有部门节点数据
    getOrgAllCheckedKeys() {
      // 目前被选中的菜单节点
      const checkedKeys = this.$refs.org.getCheckedKeys()
      // 半选中的菜单节点
      const halfCheckedKeys = this.$refs.org.getHalfCheckedKeys()
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys)
      return checkedKeys
    },
    // 角色状态修改
    handleStatusChange(row) {
      const confirmAction = row.status === '1' ? '启用' : '停用'
      const confirmText = `确认要${confirmAction}"${row.roleName}"吗？`
      this.$modal.confirm(confirmText).then(() => {
        const params = {
          id: row.id,
          status: row.status
        }
        updateRoleStatus(params).then(res => {
          this.$modal.success(`${confirmAction}成功`)
        }).catch(err => {
          console.log(err)
          this.$modal.error(`${confirmAction}失败`)
        })
      }).catch(() => {
        row.status = row.status === '1' ? '2' : '1'
      })
    },
    // 数据权限编辑按钮
    handleDataScope(row) {
      loadRole({ id: row.id }).then(res => {
        this.dataScope.open = true
        this.resetEdit()
        this.editForm = {
          ...res.data
        }
        this.$nextTick(() => {
          this.$refs.org.setCheckedKeys(this.editForm.orgIdList)
        })
      })
    },
    // 全选/全不选按钮
    handleOrgCheckAll(orgCheckAll) {
      this.orgCheckStrictly = false
      // 重新渲染后设置全选或全不选
      this.$nextTick(() => {
        this.orgCheckAll = orgCheckAll
        this.orgCheckStrictly = true
      })
    },
    // 数据权限提交按钮
    handleDataScopeConfirm() {
      if (!this.editForm.id) {
        return
      }
      this.editForm.orgIdList = this.getOrgAllCheckedKeys()
      updateRoleScope(this.editForm).then(res => {
        this.dataScope.open = false
        this.resetEdit()
        this.$modal.success('修改成功')
        this.getList()
      })
    },
    // 数据权限取消按钮
    handleDataScopeCancel() {
      this.dataScope.open = false
      this.resetEdit()
    },
    // 数据权限变更
    handleDataScopeChange(data) {
      // 选中不是自定义权限的时候要将机构数据恢复至默认
      if (data !== '2') {
        this.$refs.org.setCheckedKeys(this.editForm.orgIdList)
      }
    },
    // 分配用户按钮
    handleAuthUser(row) {
      this.$router.push('/system/role-auth/user/' + row.id)
    },
    // 刷新缓存
    handleRefreshRole(row) {
      refreshRoleCache({ id: row.id }).then(res => {
        this.$modal.success('刷新成功')
      })
    },
    // 导出按钮
    handleExport() {
      this.$download.postAsName('/system/role/exportRoleList', {}, this.queryForm, `role_${new Date().getTime()}.xlsx`)
    },
    tableSelectEnabled(row, index) {
      return !this.$auth.isAdminRole(row.id)
    }
  }
}
</script>
