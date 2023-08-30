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
      <el-form-item label="权限字符" prop="roleKey">
        <el-input
          v-model="queryForm.roleKey"
          clearable
          placeholder="请输入权限字符"
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <dict-select v-model="queryForm.status" dict-type="db_status" placeholder="角色状态" />
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
        <base-update-button v-has-perm="['system:role:edit']" plain :disabled="!tableSelectOne" @click="handleUpdate" />
      </el-col>
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:role:remove']" plain :disabled="tableNoSelected" @click="handleDelete" />
      </el-col>
      <el-col :span="1.5">
        <base-download-button v-has-perm="['system:role:export']" plain @click="handleExport">导出</base-download-button>
      </el-col>
      <right-toolbar :query-show.sync="queryShow" @refresh="getList" />
    </el-row>

    <el-table v-loading="tableLoading" :data="roleList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" :selectable="tableSelectEnabled" width="55" />
      <el-table-column label="角色编号" prop="id" width="120" />
      <el-table-column :show-overflow-tooltip="true" label="角色名称" prop="roleName" width="150" />
      <el-table-column :show-overflow-tooltip="true" label="角色字符" prop="roleStr" width="150" />
      <el-table-column label="显示顺序" prop="sort" width="100" />
      <el-table-column align="center" label="状态" width="100">
        <template slot-scope="scope">
          <base-switch v-model="scope.row.status" :disabled="$auth.isAdminRole(scope.row.id)" @change="handleStatusChange(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="创建时间" prop="createTime" width="180" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template v-if="!$auth.isAdminRole(scope.row.id)" slot-scope="scope">
          <base-update-button v-has-perm="['system:role:edit']" type="text" @click="handleUpdate(scope.row)" />
          <base-remove-button v-has-perm="['system:role:remove']" type="text" @click="handleDelete(scope.row)" />
          <base-column-menu v-has-perm="['system:role:edit']">
            <base-text-button
              v-has-perm="['system:role:edit']"
              icon="el-icon-circle-check"
              color="#606266"
              @click="handleDataScope(scope.row)"
            >数据权限</base-text-button>
            <base-text-button
              v-has-perm="['system:role:edit']"
              icon="el-icon-user"
              color="#606266"
              @click="handleAuthUser(scope.row)"
            >分配用户</base-text-button>
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

    <!-- 角色编辑对话框 -->
    <base-dialog :title="editTitle" :open.sync="editOpen" @confirm="handleEditSubmit" @cancel="handleEditCancel">
      <el-form ref="form" :model="editForm" :rules="editRules" label-width="100px">
        <row-split2>
          <el-form-item label="角色名称" prop="roleName">
            <el-input v-model="editForm.roleName" placeholder="请输入角色名称" />
          </el-form-item>
          <el-form-item prop="roleStr">
            <base-form-label slot="label" label="角色字符" content="控制器中定义的权限字符，如：@SaCheckRole('admin')" />
            <el-input v-model="editForm.roleStr" placeholder="请输入权限字符" />
          </el-form-item>
          <el-form-item label="角色顺序" prop="sort">
            <el-input-number v-model="editForm.sort" :min="0" controls-position="right" />
          </el-form-item>
          <el-form-item label="状态">
            <dict-radio v-model="editForm.status" dict-type="db_status" />
          </el-form-item>
        </row-split2>
        <el-form-item label="备注">
          <el-input v-model="editForm.remark" placeholder="请输入内容" type="textarea" />
        </el-form-item>
        <el-form-item label="菜单权限">
          <el-checkbox v-model="menuExpandAll">展开/折叠</el-checkbox>
          <el-checkbox v-model="menuCheckAll">全选/全不选
          </el-checkbox>
          <el-checkbox v-model="menuCheckStrictly">父子联动
          </el-checkbox>
          <menu-tree
            :expand-all="menuExpandAll"
            :default-expand-all="menuExpandAll"
            :check-all="menuCheckAll"
            :check-strictly="!menuCheckStrictly"
            class="tree-border"
          />
        </el-form-item>
      </el-form>
    </base-dialog>

    <!-- 数据权限编辑对话框 -->
    <el-dialog :title="dataScope.title" :visible.sync="dataScope.open" append-to-body width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="角色名称">
          <el-input v-model="editForm.roleName" :disabled="true" />
        </el-form-item>
        <el-form-item label="权限字符">
          <el-input v-model="editForm.roleKey" :disabled="true" />
        </el-form-item>
        <el-form-item label="权限范围">
          <el-select v-model="editForm.dataScope" @change="dataScopeSelectChange">
            <el-option
              v-for="item in dataScopeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-show="editForm.dataScope == 2" label="数据权限">
          <el-checkbox v-model="deptExpand" @change="handleCheckedTreeExpand($event, 'dept')">展开/折叠</el-checkbox>
          <el-checkbox v-model="deptNodeAll" @change="handleCheckedTreeNodeAll($event, 'dept')">全选/全不选
          </el-checkbox>
          <el-checkbox v-model="editForm.deptCheckStrictly" @change="handleCheckedTreeConnect($event, 'dept')">父子联动
          </el-checkbox>
          <el-tree
            ref="dept"
            :check-strictly="!editForm.deptCheckStrictly"
            :data="deptOptions"
            :props="defaultProps"
            class="tree-border"
            default-expand-all
            empty-text="加载中，请稍候"
            node-key="id"
            show-checkbox
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitDataScope">确 定</el-button>
        <el-button @click="cancelDataScope">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  addRole,
  dataScope,
  deptTreeSelect, getRolePageList, loadRole, removeRole,
  updateRole, updateRoleStatus
} from '@/api/system/role'
import { roleMenuTreeselect } from '@/api/system/menu'
import crud from '@/framework/mixin/crud'
import { addUser, updateUser } from '@/api/system/user'
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
      // 是否直接选中菜单
      menuCheckStrictly: true,
      deptExpand: true,
      deptNodeAll: false,
      // 数据范围选项
      dataScopeOptions: [
        {
          value: '1',
          label: '全部数据权限'
        },
        {
          value: '2',
          label: '自定数据权限'
        },
        {
          value: '3',
          label: '本部门数据权限'
        },
        {
          value: '4',
          label: '本部门及以下数据权限'
        },
        {
          value: '5',
          label: '仅本人数据权限'
        }
      ],
      // 菜单列表
      menuOptions: [],
      // 部门列表
      deptOptions: [],
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        roleName: null,
        roleStr: null,
        status: null,
        createTimeRange: null
      },
      // 表单参数
      defaultProps: {
        children: 'children',
        label: 'label'
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
        this.roleList = res.rows
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
      this.openEdit('新增角色')
    },
    // 修改按钮
    handleUpdate(row) {
      const id = row.id || this.tableIds[0]
      loadRole({ id }).then(res => {
        this.openEdit('修改角色')
        this.editForm = {
          ...res
        }
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
        roleName: null,
        roleStr: null,
        sort: 0,
        status: '1',
        menuIds: [],
        deptIds: [],
        menuCheckStrictly: true,
        deptCheckStrictly: true,
        remark: null
      }
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
    getDeptAllCheckedKeys() {
      // 目前被选中的部门节点
      const checkedKeys = this.$refs.dept.getCheckedKeys()
      // 半选中的部门节点
      const halfCheckedKeys = this.$refs.dept.getHalfCheckedKeys()
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys)
      return checkedKeys
    },
    /** 根据角色ID查询菜单树结构 */
    getRoleMenuTreeselect(roleId) {
      return roleMenuTreeselect(roleId).then(response => {
        this.menuOptions = response.menus
        return response
      })
    },
    /** 根据角色ID查询部门树结构 */
    getDeptTree(roleId) {
      return deptTreeSelect(roleId).then(response => {
        this.deptOptions = response.depts
        return response
      })
    },
    // 角色状态修改
    handleStatusChange(row) {
      const confirmAction = row.status === '1' ? '停用' : '启用'
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
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 取消按钮（数据权限）
    cancelDataScope() {
      this.openDataScope = false
      this.reset()
    },
    // 表单重置
    reset() {
      if (this.$refs.menu !== undefined) {
        this.$refs.menu.setCheckedKeys([])
      }
      this.menuExpandAll = false
      this.menuNodeAll = false
      this.deptExpand = true
      this.deptNodeAll = false
      this.form = {
        roleId: undefined,
        roleName: undefined,
        roleKey: undefined,
        roleSort: 0,
        status: '0',
        menuIds: [],
        deptIds: [],
        menuCheckStrictly: true,
        deptCheckStrictly: true,
        remark: undefined
      }
      this.resetForm('form')
    },

    // 更多操作触发
    handleCommand(command, row) {
      switch (command) {
        case 'handleDataScope':
          this.handleDataScope(row)
          break
        case 'handleAuthUser':
          this.handleAuthUser(row)
          break
        default:
          break
      }
    },
    // 树权限（展开/折叠）
    handleCheckedTreeExpand(value, type) {
      if (type === 'menu') {
        const treeList = this.menuOptions
        for (let i = 0; i < treeList.length; i++) {
          this.$refs.menu.store.nodesMap[treeList[i].id].expanded = value
        }
      } else if (type === 'dept') {
        const treeList = this.deptOptions
        for (let i = 0; i < treeList.length; i++) {
          this.$refs.dept.store.nodesMap[treeList[i].id].expanded = value
        }
      }
    },
    // 树权限（全选/全不选）
    handleCheckedTreeNodeAll(value, type) {
      if (type === 'menu') {
        this.$refs.menu.setCheckedNodes(value ? this.menuOptions : [])
      } else if (type === 'dept') {
        this.$refs.dept.setCheckedNodes(value ? this.deptOptions : [])
      }
    },
    // 树权限（父子联动）
    handleCheckedTreeConnect(value, type) {
      if (type === 'menu') {
        this.editForm.menuCheckStrictly = !!value
      } else if (type === 'dept') {
        this.editForm.deptCheckStrictly = !!value
      }
    },
    /** 选择角色权限范围触发 */
    dataScopeSelectChange(value) {
      if (value !== '2') {
        this.$refs.dept.setCheckedKeys([])
      }
    },
    /** 分配数据权限操作 */
    handleDataScope(row) {
      this.reset()
      const deptTreeSelect = this.getDeptTree(row.roleId)
      getRole(row.roleId).then(response => {
        this.form = response.data
        this.openDataScope = true
        this.$nextTick(() => {
          deptTreeSelect.then(res => {
            this.$refs.dept.setCheckedKeys(res.checkedKeys)
          })
        })
        this.title = '分配数据权限'
      })
    },
    /** 分配用户操作 */
    handleAuthUser: function(row) {
      const roleId = row.roleId
      this.$router.push('/system/role-auth/user/' + roleId)
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.editForm.roleId !== undefined) {
            this.editForm.menuIds = this.getMenuAllCheckedKeys()
            updateRole(this.form).then(response => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            this.editForm.menuIds = this.getMenuAllCheckedKeys()
            addRole(this.form).then(response => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 提交按钮（数据权限） */
    submitDataScope: function() {
      if (this.editForm.roleId !== undefined) {
        this.editForm.deptIds = this.getDeptAllCheckedKeys()
        dataScope(this.form).then(response => {
          this.$modal.msgSuccess('修改成功')
          this.openDataScope = false
          this.getList()
        })
      }
    },

    /** 导出按钮操作 */
    handleExport() {
      this.download('system/role/export', {
        ...this.queryParams
      }, `role_${new Date().getTime()}.xlsx`)
    },
    tableSelectEnabled(row, index) {
      return !this.$auth.isAdminRole(row.id)
    }
  }
}
</script>
