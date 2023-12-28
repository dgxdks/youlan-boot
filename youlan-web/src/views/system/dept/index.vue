<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" :inline="true" :model="queryForm" size="small">
      <el-form-item label="部门名称" prop="orgName">
        <el-input
          v-model="queryForm.orgName"
          clearable
          placeholder="请输入部门名称"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="orgStatus">
        <dict-select v-model="queryForm.orgStatus" dict-type="db_status" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <base-search-button @click="handleQuery" />
        <base-reset-button @click="handleResetQuery" />
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <base-add-button v-has-perm="['system:dept:add']" plain @click="handleAdd" />
      </el-col>
      <el-col :span="1.5">
        <base-add-button icon="el-icon-sort" type="info" plain @click="handleExpandAll">展开/折叠</base-add-button>
      </el-col>
      <table-toolbar :query-show.sync="queryShow" @refresh="getList" />
    </el-row>

    <el-table
      ref="queryTable"
      v-loading="tableLoading"
      :data="deptList"
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
      row-key="id"
    >
      <el-table-column label="部门名称" prop="orgName" width="260" />
      <el-table-column label="机构编码" prop="orgId" />
      <el-table-column label="机构类型" prop="orgType">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.orgType" dict-type="sys_org_type" />
        </template>
      </el-table-column>
      <el-table-column label="排序" prop="orgSort" width="200" />
      <el-table-column label="状态" prop="status" width="100">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.orgStatus" dict-type="db_status" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="创建时间" prop="createTime" width="200" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template slot-scope="scope">
          <base-update-button v-has-perm="['system:dept:update']" type="text" @click="handleUpdate(scope.row)" />
          <base-add-button v-has-perm="['system:dept:add']" type="text" @click="handleAdd(scope.row)" />
          <base-remove-button
            v-if="scope.row.parentOrgId !== 0"
            v-has-perm="['system:dept:remove']"
            type="text"
            @click="handleDelete(scope.row)"
          />
        </template>
      </el-table-column>
    </el-table>

    <!-- 部门编辑对话框 -->
    <base-dialog :title="editTitle" :open.sync="editOpen" width="600px" @confirm="handleEditSubmit" @cancel="handleEditCancel">
      <el-form ref="editForm" :model="editForm" :rules="editRules" label-width="80px">
        <el-form-item label="上级机构" prop="parentOrgId">
          <treeselect
            v-model="editForm.parentOrgId"
            :normalizer="deptNormalizer"
            :options="deptOptions"
            placeholder="选择上级机构"
          />
        </el-form-item>
        <base-row-split2>
          <el-form-item label="机构名称" prop="orgName">
            <el-input v-model="editForm.orgName" placeholder="请输入部门名称" />
          </el-form-item>
          <el-form-item label="显示排序" prop="orgSort">
            <el-input-number v-model="editForm.orgSort" :min="0" controls-position="right" />
          </el-form-item>
          <el-form-item label="负责人" prop="leader">
            <el-input v-model="editForm.leader" maxlength="20" placeholder="请输入负责人" />
          </el-form-item>
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="editForm.phone" maxlength="11" placeholder="请输入联系电话" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="editForm.email" maxlength="50" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="机构状态" prop="orgStatus">
            <dict-radio v-model="editForm.orgStatus" dict-type="db_status" />
          </el-form-item>
        </base-row-split2>
      </el-form>
    </base-dialog>
  </div>
</template>

<script>
import {
  addDept,
  getDeptTreeList,
  loadDept,
  removeDept,
  updateDept
} from '@/api/system/dept'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import crud from '@/framework/mixin/crud'

export default {
  name: 'Dept',
  components: { Treeselect },
  mixins: [crud],
  data() {
    return {
      // 表格树数据
      deptList: [],
      // 部门树选项
      deptOptions: [],
      // 查询参数
      queryForm: {
        orgName: null,
        orgStatus: null
      },
      // 表单校验
      editRules: {
        parentOrgId: [
          this.$validator.requiredRule('上级部门不能为空')
        ],
        orgName: [
          this.$validator.requiredRule('部门名称不能为空')
        ],
        orgSort: [
          this.$validator.requiredRule('显示排序不能为空')
        ],
        email: [
          this.$validator.emailRule('请输入正确的邮箱地址')
        ],
        phone: [
          this.$validator.mobileRule('请输入正确的手机号码')
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 部门列表
    getList() {
      this.openTableLoading()
      getDeptTreeList(this.queryForm).then(res => {
        this.deptList = res.data
        this.$nextTick(() => {
          this.tableExpandAll = false
          this.handleExpandAll()
        })
        this.closeTableLoading()
      })
    },
    // 表单重置
    resetEditForm() {
      this.editForm = {
        orgId: null,
        parentOrgId: null,
        orgName: null,
        orgSort: null,
        leader: null,
        phone: null,
        email: null,
        orgStatus: '1',
        orgType: '1'
      }
      this.$refs.editForm && this.$refs.editForm.resetFields()
    },
    // 搜索按钮
    handleQuery() {
      this.getList()
    },
    // 重置按钮
    handleResetQuery() {
      this.$refs.queryForm && this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    // 新增按钮
    handleAdd(row) {
      // 处理部门树
      getDeptTreeList({}).then(res => {
        this.deptOptions = res.data
      })
      this.openEdit('新增部门')
      if (row) {
        this.editForm.parentOrgId = row.orgId
      }
    },
    // 修改按钮
    handleUpdate(row) {
      // 处理部门树
      getDeptTreeList({ excludeOrgId: row.orgId }).then(res => {
        this.deptOptions = res.data
        if (this.deptOptions.length === 0) {
          this.deptOptions.push({ orgId: row.parentOrgId, orgName: row.orgName, children: [] })
        }
      })
      loadDept({ id: row.id }).then(res => {
        this.openEdit('修改部门')
        this.editForm = res.data
      })
    },
    // 展开/折叠按钮
    handleExpandAll() {
      this.tableExpandAll = !this.tableExpandAll
      this.deptList.forEach(menu => {
        this.$refs.queryTable.toggleRowExpansion(menu, this.tableExpandAll)
        // 展开/折叠两层部门
        if (this.$array.isNotEmpty(menu.children)) {
          menu.children.forEach(child => {
            this.$refs.queryTable.toggleRowExpansion(child, this.tableExpandAll)
          })
        }
      })
    },
    // 提交按钮
    handleEditSubmit() {
      this.$refs.editForm.validate(valid => {
        if (valid) {
          if (this.editForm.id) {
            updateDept(this.editForm).then(res => {
              this.$modal.success('修改成功')
              this.closeEdit()
              this.getList()
            })
          } else {
            addDept(this.editForm).then(res => {
              this.$modal.success('新增成功')
              this.closeEdit()
              this.getList()
            })
          }
        }
      })
    },
    // 取消按钮
    handleEditCancel() {
      this.closeEdit()
    },
    // 删除按钮
    handleDelete(row) {
      this.$modal.confirm('是否确认删除名称为"' + row.orgName + '"的数据项？').then(function() {
        return removeDept({ list: [row.orgId] })
      }).then(() => {
        this.getList()
        this.$modal.success('删除成功')
      }).catch(res => {
        console.log(res)
      })
    },
    // 转换部门数据结构
    deptNormalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children
      }
      return {
        id: node.orgId,
        label: node.orgName,
        children: node.children
      }
    }
  }
}
</script>
