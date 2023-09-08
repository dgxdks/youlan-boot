<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" :inline="true" :model="queryForm" label-width="80px" size="small">
      <el-form-item label="字典名称" prop="typeName">
        <el-input
          v-model="queryForm.typeName"
          clearable
          placeholder="请输入字典名称"
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="字典类型" prop="typeKey">
        <el-input
          v-model="queryForm.typeKey"
          clearable
          placeholder="请输入字典类型"
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <dict-select v-model="queryForm.status" placeholder="字典状态" style="width: 240px" dict-type="db_status" />
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
        <base-add-button v-has-perm="['system:dict:add']" plain @click="handleAdd" />
      </el-col>
      <el-col :span="1.5">
        <base-update-button v-has-perm="['system:dict:update']" plain :disabled="!tableSelectOne" @click="handleUpdate" />
      </el-col>
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:dict:remove']" plain :disabled="tableNoSelected" @click="handleDelete" />
      </el-col>
      <el-col :span="1.5">
        <base-download-button v-has-perm="['system:dict:export']" plain @click="handleExport">导出</base-download-button>
      </el-col>
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:dict:remove']" plain @click="handleRefreshCache">刷新缓存</base-remove-button>
      </el-col>
      <right-toolbar :query-show.sync="queryShow" @refresh="getList" />
    </el-row>

    <el-table v-loading="tableLoading" :data="typeList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55" />
      <el-table-column align="center" label="字典编号" prop="id" />
      <el-table-column :show-overflow-tooltip="true" align="center" label="字典名称" prop="typeName" />
      <el-table-column :show-overflow-tooltip="true" align="center" label="字典类型">
        <template slot-scope="scope">
          <router-link :to="'/system/dict-data/type/' + scope.row.typeKey" class="link-type">
            <span>{{ scope.row.typeKey }}</span>
          </router-link>
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态" prop="status">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.status" dict-type="db_status" />
        </template>
      </el-table-column>
      <el-table-column :show-overflow-tooltip="true" align="center" label="备注" prop="remark" />
      <el-table-column align="center" label="创建时间" prop="createTime" width="180" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template slot-scope="scope">
          <base-update-button v-has-perm="['system:dict:update']" type="text" @click="handleUpdate(scope.row)" />
          <base-remove-button v-has-perm="['system:dict:remove']" type="text" @click="handleDelete(scope.row)" />
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

    <!-- 字典类型对话框 -->
    <base-dialog :title="editTitle" :open.sync="editOpen" width="500px" @confirm="handleEditSubmit" @cancel="handleEditCancel">
      <el-form ref="editForm" :model="editForm" :rules="editRules" label-width="100px">
        <el-form-item label="字典名称" prop="typeName">
          <el-input v-model="editForm.typeName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="字典类型" prop="typeKey">
          <base-form-label slot="label" content="字典类型必须以字母开头，且只能为（小写字母，数字，下滑线）" label="字典类型" />
          <el-input v-model="editForm.typeKey" placeholder="请输入字典类型" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <dict-radio v-model="editForm.status" dict-type="db_status" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="editForm.remark" placeholder="请输入内容" type="textarea" />
        </el-form-item>
      </el-form>
    </base-dialog>
  </div>
</template>

<script>
import {
  addDictType,
  getDictTypePageList,
  loadDictType,
  refreshDictCache,
  removeDictType,
  updateDictType
} from '@/api/system/dict/type'
import crud from '@/framework/mixin/crud'

export default {
  name: 'Dict',
  mixins: [crud],
  data() {
    return {
      // 字典表格数据
      typeList: [],
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        typeName: null,
        typeKey: null,
        status: null,
        createTimeRange: null
      },
      // 表单校验
      editRules: {
        typeName: [
          this.$validator.requiredRule('字典名称不能为空')
        ],
        typeKey: [
          this.$validator.requiredRule('字典类型不能为空'),
          this.$validator.patternRule(/^[a-z][a-z0-9_]*$/, '字典类型必须以字母开头，且只能为（小写字母，数字，下滑线）', ['blur', 'change'])
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 查询字典类型列表
    getList() {
      this.openTableLoading()
      getDictTypePageList(this.queryForm).then(res => {
        this.typeList = res.rows
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
      this.handleQuery()
    },
    // 新增按钮
    handleAdd() {
      this.openEdit('添加字典类型')
    },
    // 修改按钮
    handleUpdate(row) {
      const id = row.id || this.tableIds[0]
      loadDictType({ id }).then(res => {
        this.openEdit('修改字典类型')
        this.editForm = res
      })
    },
    // 表单重置
    resetEdit() {
      this.editForm = {
        id: null,
        dictName: null,
        dictType: null,
        status: '1',
        remark: null
      }
      this.$refs.editForm && this.$refs.editForm.resetFields()
    },
    // 提交按钮
    handleEditSubmit() {
      this.$refs.editForm.validate(valid => {
        if (valid) {
          if (this.editForm.id) {
            updateDictType(this.editForm).then(res => {
              this.$modal.success('修改成功')
              this.closeEdit()
              this.getList()
            })
          } else {
            addDictType(this.editForm).then(res => {
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
      const list = (row.id && [row.id]) || this.tableIds
      this.$modal.confirm('是否确认删除字典编号为"' + list + '"的数据项？').then(function() {
        return removeDictType({ list })
      }).then(() => {
        this.getList()
        this.$modal.success('删除成功')
      }).catch(error => {
        console.log(error)
      })
    },
    // 导出按钮
    handleExport() {
      this.$download.postAsName('/system/dictType/exportDictTypeList', {}, this.queryForm, `type_${new Date().getTime()}.xlsx`)
    },
    // 刷新缓存按钮
    handleRefreshCache() {
      this.$modal.loading('正在刷新中，请稍等...')
      refreshDictCache().then(res => {
        this.$modal.loadingClose()
        this.$modal.success('刷新成功')
      }).then(res => {
        this.$dict.refreshDict()
      }).catch(error => {
        this.$modal.loadingClose()
        console.log(error)
      })
    }
  }
}
</script>
