<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" :inline="true" :model="queryForm" label-width="68px" size="small">
      <el-form-item label="参数名称" prop="configName">
        <el-input
          v-model="queryForm.configName"
          clearable
          placeholder="请输入参数名称"
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="参数键名" prop="configKey">
        <el-input
          v-model="queryForm.configKey"
          clearable
          placeholder="请输入参数键名"
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="系统内置" prop="configType">
        <dict-select
          v-model="queryForm.configType"
          dict-type="db_yes_no"
          clearable
          placeholder="系统内置"
          style="width: 240px"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTimeRange">
        <base-date-range-picker v-model="queryForm.createTimeRange" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <base-search-button @click="handleQuery" />
        <base-reset-button @click="resetQuery" />
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <base-add-button v-has-perm="['system:config:add']" plain @click="handleAdd" />
      </el-col>
      <el-col :span="1.5">
        <base-update-button v-has-perm="['system:config:update']" plain :disabled="!tableSelectOne" @click="handleUpdate" />
      </el-col>
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:config:remove']" plain :disabled="tableNoSelected" @click="handleDelete" />
      </el-col>
      <el-col :span="1.5">
        <base-download-button v-has-perm="['system:config:export']" plain @click="handleExport">导出
        </base-download-button>
      </el-col>
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:config:remove']" plain @click="handleRefreshCache">刷新缓存
        </base-remove-button>
      </el-col>
      <table-toolbar :query-show.sync="queryShow" @refresh="getList" />
    </el-row>

    <el-table v-loading="tableLoading" :data="configList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55" />
      <el-table-column align="center" label="参数主键" prop="id" />
      <el-table-column show-overflow-tooltip align="center" label="参数名称" prop="configName" />
      <el-table-column show-overflow-tooltip align="center" label="参数键名" prop="configKey" />
      <el-table-column show-overflow-tooltip align="center" label="参数键值" prop="configValue" />
      <el-table-column align="center" label="系统内置" prop="configType">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.configType" dict-type="db_yes_no" />
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip align="center" label="备注" prop="remark" />
      <el-table-column align="center" label="创建时间" prop="createTime" width="160" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template slot-scope="scope">
          <base-update-button v-has-perm="['system:config:update']" type="text" @click="handleUpdate(scope.row)" />
          <base-remove-button v-has-perm="['system:config:remove']" type="text" @click="handleDelete(scope.row)" />
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

    <!-- 参数配置编辑对话框 -->
    <base-dialog :title="editTitle" :open.sync="editOpen" width="600px" @confirm="handleEditSubmit" @cancel="handleEditCancel">
      <el-form ref="editForm" :model="editForm" :rules="editRules" label-width="100px">
        <el-form-item label="参数名称" prop="configName">
          <el-input v-model="editForm.configName" placeholder="请输入参数名称" />
        </el-form-item>
        <el-form-item label="参数键名" prop="configKey">
          <el-input v-model="editForm.configKey" placeholder="请输入参数键名" />
        </el-form-item>
        <el-form-item label="参数键值" prop="configValue">
          <el-input v-model="editForm.configValue" placeholder="请输入参数键值" />
        </el-form-item>
        <el-form-item label="系统内置" prop="configType">
          <dict-radio v-model="editForm.configType" dict-type="db_yes_no" />
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
  addConfig,
  getConfigPageList,
  loadConfig,
  refreshConfigCache,
  removeConfig,
  updateConfig
} from '@/api/system/config'
import crud from '@/framework/mixin/crud'

export default {
  name: 'Config',
  mixins: [crud],
  data() {
    return {
      // 参数表格数据
      configList: [],
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        type: null
      },
      // 表单校验
      editRules: {
        configName: [
          this.$validator.requiredRule('参数名称不能为空')
        ],
        configKey: [
          this.$validator.requiredRule('参数键名不能为空')
        ],
        configValue: [
          this.$validator.requiredRule('参数键值不能为空')
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 查询参数列表
    getList() {
      this.openTableLoading()
      getConfigPageList(this.queryForm).then(res => {
        this.configList = res.data.rows
        this.pageTotal = res.data.total
        this.closeTableLoading()
      })
    },
    // 搜索按钮
    handleQuery() {
      this.resetPageNum()
      this.getList()
    },
    // 搜索重置按钮
    resetQuery() {
      this.$refs.queryForm && this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    // 新增按钮
    handleAdd() {
      this.openEdit('添加参数')
    },
    // 修改按钮
    handleUpdate(row) {
      const id = row.id || this.tableIds[0]
      loadConfig({ id }).then(res => {
        this.openEdit('修改参数')
        this.editForm = res.data
      })
    },
    // 删除按钮
    handleDelete(row) {
      const list = (row.id && [row.id]) || this.tableIds
      this.$modal.confirm('是否确认删除参数编号为"' + list + '"的数据项？').then(function() {
        return removeConfig({ list })
      }).then(() => {
        this.getList()
        this.$modal.success('删除成功')
      }).catch(() => {
      })
    },
    // 表单重置
    resetEditForm() {
      this.editForm = {
        configId: null,
        configName: null,
        configKey: null,
        configValue: null,
        configType: '1',
        remark: null
      }
      this.$refs.editForm && this.$refs.editForm.resetFields()
    },
    // 提交按钮
    handleEditSubmit() {
      this.$refs.editForm.validate(valid => {
        if (valid) {
          if (this.editForm.id) {
            updateConfig(this.editForm).then(res => {
              this.$modal.success('修改成功')
              this.closeEdit()
              this.getList()
            })
          } else {
            addConfig(this.editForm).then(res => {
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
    // 导出按钮
    handleExport() {
      this.$download.postAsName('/system/config/exportConfigList', {}, this.queryForm, `config_${new Date().getTime()}.xlsx`)
    },
    // 刷新缓存按钮
    handleRefreshCache() {
      this.$modal.loading('刷新缓存中，请稍等...')
      refreshConfigCache().then(() => {
        this.$modal.loadingClose()
        this.$modal.success('刷新成功')
      }).catch(error => {
        console.log(error)
        this.$modal.loadingClose()
      })
    }
  }
}
</script>
