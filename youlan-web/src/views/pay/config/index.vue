<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" :inline="true" :model="queryForm" label-width="68px" size="small">
      <el-form-item label=" 配置名称" prop="name">
        <el-input
          v-model="queryForm.name"
          clearable
          placeholder="请输入支付配置名称"
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="支付类型" prop="type">
        <dict-select
          v-model="queryForm.type"
          dict-type="pay_type"
          style="width: 240px"
          placeholder="支付类型"
          clearable
        />
      </el-form-item>
      <el-form-item label="配置状态" prop="status">
        <dict-select
          v-model="queryForm.status"
          dict-type="db_status"
          style="width: 240px"
          placeholder="支付配置状态"
          clearable
        />
      </el-form-item>
      <el-form-item>
        <base-search-button @click="handleQuery" />
        <base-reset-button @click="resetQuery" />
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <base-add-button v-has-perm="['pay:payConfig:add']" plain @click="handleAdd" />
      </el-col>
      <el-col :span="1.5">
        <base-update-button v-has-perm="['pay:payConfig:update']" plain :disabled="!tableSelectOne" @click="handleUpdate" />
      </el-col>
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['pay:payConfig:remove']" plain :disabled="tableNoSelected" @click="handleDelete" />
      </el-col>
      <table-toolbar :query-show.sync="queryShow" @refresh="getList" />
    </el-row>

    <el-table v-loading="tableLoading" :data="configList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55" />
      <el-table-column align="center" label="配置编号" prop="id" />
      <el-table-column show-overflow-tooltip align="center" label="配置名称" prop="name" />
      <el-table-column show-overflow-tooltip align="center" label="支付类型" prop="type">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.type" dict-type="pay_type" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态" prop="status">
        <template slot-scope="scope">
          <base-switch v-model="scope.row.status" @change="handleStatusChange(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip align="center" label="备注" prop="remark" />
      <el-table-column align="center" label="创建时间" prop="createTime" width="160" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作" width="160">
        <template slot-scope="scope">
          <base-update-button v-has-perm="['pay:payConfig:update']" type="text" @click="handleUpdate(scope.row)" />
          <base-remove-button v-has-perm="['pay:payConfig:remove']" type="text" @click="handleDelete(scope.row)" />
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

    <!-- 存储配置编辑对话框 -->
    <base-dialog
      :title="editTitle"
      :open.sync="editOpen"
      width="850px"
      @confirm="handleEditSubmit"
      @cancel="handleEditCancel"
    >
      <el-form ref="editForm" :model="editForm" :rules="editRules" label-width="120px">
        <base-row-split2>
          <el-form-item label="配置名称" prop="name">
            <el-input v-model="editForm.name" placeholder="请输入支付配置名称" />
          </el-form-item>
          <el-form-item label="支付类型" prop="type">
            <dict-select v-model="editForm.type" placeholder="请选择支付类型" dict-type="pay_type" />
          </el-form-item>
          <el-form-item label="配置状态" prop="status">
            <dict-radio v-model="editForm.status" dict-type="db_status" />
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="editForm.remark" placeholder="请输入内容" type="textarea" />
          </el-form-item>
        </base-row-split2>
        <wechat-params-form v-if="editForm.type === 'WECHAT'" ref="paramsForm" :params.sync="editForm.params" />
      </el-form>
    </base-dialog>
  </div>
</template>

<script>
import crud from '@/framework/mixin/crud'
import {
  addPayConfig,
  getPayConfigPageList,
  loadPayConfig,
  removePayConfig,
  updatePayConfig,
  updatePayConfigStatus
} from '@/api/pay/config'
import WechatParamsForm from '@/views/pay/config/components/WechatParamsForm.vue'

export default {
  name: 'PayConfig',
  components: { WechatParamsForm },
  mixins: [crud],
  data() {
    return {
      // 配置表格数据
      configList: [],
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        type: null,
        status: null
      },
      // 表单校验
      editRules: {
        name: [
          this.$validator.requiredRule('支付配置名称不能为空')
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 查询配置列表
    getList() {
      this.openTableLoading()
      getPayConfigPageList(this.queryForm).then(res => {
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
      this.openEdit('添加配置')
    },
    // 修改按钮
    handleUpdate(row) {
      const id = row.id || this.tableIds[0]
      loadPayConfig({ id }).then(res => {
        this.openEdit('修改配置')
        this.editForm = res.data
      })
    },
    // 删除按钮
    handleDelete(row) {
      const list = (row.id && [row.id]) || this.tableIds
      this.$modal.confirm('是否确认删除配置编号为"' + list + '"的数据项？').then(function() {
        return removePayConfig({ list })
      }).then(() => {
        this.getList()
        this.$modal.success('删除成功')
      }).catch(() => {
      })
    },
    // 表单重置
    resetEditForm() {
      this.editForm = {
        name: null,
        type: 'WECHAT',
        remark: '',
        params: null,
        payNotifyUrl: null,
        refundNotifyUrl: null,
        status: '1'
      }
      this.$refs.editForm && this.$refs.editForm.resetFields()
    },
    // 提交按钮
    handleEditSubmit() {
      const validateAll = [this.$refs.editForm, this.$refs.paramsForm].map(form => {
        return new Promise(resolve => {
          form.validate(res => {
            resolve(res)
          })
        })
      })
      Promise.all(validateAll).then(res => {
        const validateResult = res.every(item => item === true)
        // 验证失败或者配置参数为空直接返回
        if (!validateResult || !this.editForm.params) {
          this.$modal.error('支付配置校验未通过，请重新检查提交内容')
          return
        }
        if (this.editForm.id) {
          updatePayConfig(this.editForm).then(res => {
            this.$modal.success('修改成功')
            this.closeEdit()
            this.getList()
          })
        } else {
          addPayConfig(this.editForm).then(res => {
            this.$modal.success('新增成功')
            this.closeEdit()
            this.getList()
          })
        }
      })
    },
    // 取消按钮
    handleEditCancel() {
      this.closeEdit()
    },
    // 状态变更
    handleStatusChange(row) {
      const confirmAction = row.status === '1' ? '启用' : '停用'
      const confirmText = `确认要${confirmAction}"${row.name}"吗？`
      this.$modal.confirm(confirmText).then(() => {
        const params = {
          id: row.id,
          status: row.status
        }
        updatePayConfigStatus(params).then(res => {
          this.$modal.success(`${confirmAction}成功`)
        }).catch(err => {
          console.log(err)
          this.$modal.error(`${confirmAction}失败`)
        })
      }).catch(() => {
        row.status = row.status === '1' ? '2' : '1'
      })
    }
  }
}
</script>
