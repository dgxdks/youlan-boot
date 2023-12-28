<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" :inline="true" :model="queryForm" label-width="68px" size="small">
      <el-form-item label="配置标识" prop="configId">
        <el-input
          v-model="queryForm.configId"
          clearable
          placeholder="请输入配置标识"
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="短信厂商" prop="supplier">
        <dict-select v-model="queryForm.supplier" dict-type="sms_supplier" style="width: 240px" placeholder="择短信厂商" clearable />
      </el-form-item>
      <el-form-item label="短信签名" prop="signature">
        <el-input
          v-model="queryForm.signature"
          clearable
          placeholder="请输入短信签名"
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="模版ID" prop="templateId">
        <el-input
          v-model="queryForm.templateId"
          clearable
          placeholder="请输入模版ID"
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="访问秘钥" prop="accessKeyId">
        <el-input
          v-model="queryForm.accessKeyId"
          clearable
          placeholder="请输入访问秘钥"
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="应用ID" prop="sdkAppId">
        <el-input
          v-model="queryForm.sdkAppId"
          clearable
          placeholder="请输入应用ID"
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <base-search-button @click="handleQuery" />
        <base-reset-button @click="resetQuery" />
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <base-add-button v-has-perm="['system:smsSupplier:add']" plain @click="handleAdd" />
      </el-col>
      <el-col :span="1.5">
        <base-update-button v-has-perm="['system:smsSupplier:update']" plain :disabled="!tableSelectOne" @click="handleUpdate" />
      </el-col>
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:smsSupplier:remove']" plain :disabled="tableNoSelected" @click="handleDelete" />
      </el-col>
      <el-col :span="1.5">
        <base-download-button v-has-perm="['system:smsSupplier:export']" plain @click="handleExport">导出</base-download-button>
      </el-col>
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:smsSupplier:remove']" plain @click="handleRefreshCache">刷新缓存</base-remove-button>
      </el-col>
      <table-toolbar :query-show.sync="queryShow" @refresh="getList" />
    </el-row>

    <el-table v-loading="tableLoading" :data="supplierList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55" />
      <el-table-column align="center" label="配置标识" prop="configId" />
      <el-table-column align="center" label="短信厂商" prop="supplier">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.supplier" dict-type="sms_supplier" />
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip align="center" label="访问秘钥" prop="accessKeyId" />
      <el-table-column show-overflow-tooltip align="center" label="私密秘钥" prop="accessKeySecret" />
      <el-table-column show-overflow-tooltip align="center" label="短信签名" prop="signature" />
      <el-table-column show-overflow-tooltip align="center" label="模版ID" prop="templateId" />
      <el-table-column align="center" label="状态" prop="status">
        <template slot-scope="scope">
          <base-switch v-model="scope.row.status" @change="handleStatusChange(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="创建时间" prop="createTime" width="160" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作" width="160">
        <template slot-scope="scope">
          <base-update-button v-has-perm="['system:smsSupplier:update']" type="text" @click="handleUpdate(scope.row)" />
          <base-remove-button v-has-perm="['system:smsSupplier:remove']" type="text" @click="handleDelete(scope.row)" />
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
    <base-dialog :title="editTitle" :open.sync="editOpen" width="700px" @confirm="handleEditSubmit" @cancel="handleEditCancel">
      <el-form ref="editForm" :model="editForm" :rules="editRules" label-width="100px">
        <base-row-split2>
          <el-form-item label="配置标识" prop="configId">
            <base-form-label slot="label" content="短信厂商全局唯一配置标识，创建后不可修改（建议使用英文）" label="配置标识" />
            <el-input v-model="editForm.configId" :disabled="!!editForm.id" placeholder="请输入配置标识" />
          </el-form-item>
          <el-form-item label="短信厂商" prop="supplier">
            <base-form-label slot="label" content="指定此配置是哪个厂商" label="短信厂商" />
            <dict-select v-model="editForm.supplier" placeholder="请选择短信厂商" dict-type="sms_supplier" @change="handleSupplierChange" />
          </el-form-item>
          <el-form-item label="访问秘钥" prop="accessKeyId">
            <base-form-label slot="label" content="不同厂商名称不一样，例如：accessKey、apiKey、sdkKey、appId" label="访问秘钥" />
            <el-input v-model="editForm.accessKeyId" placeholder="请输入访问秘钥" />
          </el-form-item>
          <el-form-item label="私密秘钥" prop="accessKeySecret">
            <base-form-label slot="label" content="不同厂商名称不一样，例如：accessSecret、apiSecret" label="私密秘钥" />
            <el-input v-model="editForm.accessKeySecret" placeholder="请输入私密秘钥" />
          </el-form-item>
          <el-form-item label="短信签名" prop="signature">
            <base-form-label slot="label" content="不同厂商对应的短信签名" label="短信签名" />
            <el-input v-model="editForm.signature" placeholder="请输入短信签名" />
          </el-form-item>
          <el-form-item label="模版ID" prop="templateId">
            <base-form-label slot="label" content="不同厂商对应的短信模版ID" label="模版ID" />
            <el-input v-model="editForm.templateId" placeholder="请输入模版ID" />
          </el-form-item>
          <el-form-item label="应用ID" prop="sdkAppId">
            <base-form-label slot="label" content="不同厂商要求一致，在腾讯中对应SmsSdkAppId（非必填）" label="应用ID" />
            <el-input v-model="editForm.sdkAppId" placeholder="请输入应用ID" />
          </el-form-item>
          <el-form-item label="随机权重" prop="weight">
            <base-form-label slot="label" content="负载均衡的权重值依赖于此，如不需要负载均衡可不进行配置" label="随机权重" />
            <el-input-number v-model="editForm.weight" placeholder="随机权重" />
          </el-form-item>
          <el-form-item label="重试间隔" prop="retryInterval">
            <base-form-label slot="label" content="短信自动重试间隔时间(单位：秒)" label="重试间隔" />
            <el-input-number v-model="editForm.retryInterval" placeholder="请输入重试间隔" />
          </el-form-item>
          <el-form-item label="重试次数" prop="maxRetries">
            <base-form-label slot="label" content="如果需要短信重试可调整此值，0则不重试" label="重试次数" />
            <el-input-number v-model="editForm.maxRetries" placeholder="请输入重试次数" />
          </el-form-item>
          <el-form-item label="配置状态" prop="status">
            <dict-radio v-model="editForm.status" dict-type="db_status" />
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="editForm.remark" placeholder="请输入内容" type="textarea" />
          </el-form-item>
        </base-row-split2>
        <el-form-item label="额外参数" prop="extraParams">
          <base-form-label slot="label" content="不同厂商包含一些非公共参数，可在此处进行指定" label="额外参数" />
          <vue-json-editor v-model="editForm.extraParams" mode="code" :modes="['code']" :show-btns="false" />
        </el-form-item>
      </el-form>
    </base-dialog>
  </div>
</template>

<script>
import crud from '@/framework/mixin/crud'
import VueJsonEditor from 'vue-json-editor'
import {
  addSmsSupplier,
  getSmsSupplierExtraParams,
  getSmsSupplierPageList, loadSmsSupplier,
  refreshSmsSupplierCache, removeSmsSupplier,
  updateSmsSupplier, updateSmsSupplierStatus
} from '@/api/system/sms/supplier'

export default {
  name: 'SmsSupplier',
  components: { VueJsonEditor },
  mixins: [crud],
  data() {
    return {
      // 厂商表格数据
      supplierList: [],
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        configId: null,
        supplier: null,
        accessKeyId: null,
        sdkAppId: null,
        signature: null,
        templateId: null
      },
      // 表单校验
      editRules: {
        configId: [
          this.$validator.requiredRule('配置标识不能为空')
        ],
        supplier: [
          this.$validator.requiredRule('短信厂商不能为空')
        ],
        accessKeyId: [
          this.$validator.requiredRule('访问秘钥不能为空')
        ],
        accessKeySecret: [
          this.$validator.requiredRule('私密秘钥不能为空')
        ],
        signature: [
          this.$validator.requiredRule('短信签名不能为空')
        ],
        templateId: [
          this.$validator.requiredRule('模版ID不能为空')
        ]
      }
    }
  },
  computed: {
    isLocalType() {
      return this.editForm.type === 'LOCAL'
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 查询配置列表
    getList() {
      this.openTableLoading()
      getSmsSupplierPageList(this.queryForm).then(res => {
        this.supplierList = res.data.rows
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
      this.openEdit('添加短信厂商')
    },
    // 修改按钮
    handleUpdate(row) {
      const id = row.id || this.tableIds[0]
      loadSmsSupplier({ id }).then(res => {
        this.openEdit('修改短信厂商')
        this.editForm = {
          ...res.data,
          extraParams: this.$json.toJsonObject(res.data.extraParams)
        }
      })
    },
    // 删除按钮
    handleDelete(row) {
      const list = (row.id && [row.id]) || this.tableIds
      this.$modal.confirm('是否确认删除短信厂商编号为"' + list + '"的数据项？').then(function() {
        return removeSmsSupplier({ list })
      }).then(() => {
        this.getList()
        this.$modal.success('删除成功')
      }).catch(() => {
      })
    },
    // 监听短信厂商选择
    handleSupplierChange(supplier) {
      if (this.$str.isBlank(supplier)) {
        this.editForm.extraParams = {}
      }
      getSmsSupplierExtraParams({ supplier }).then(res => {
        this.editForm.extraParams = this.$json.toJsonObject(res.data)
      }).catch(error => {
        console.log(error)
        this.$modal.error('获取厂商额外参数异常')
      })
    },
    // 表单重置
    resetEditForm() {
      this.editForm = {
        configId: null,
        supplier: null,
        accessKeyId: null,
        accessKeySecret: null,
        sdkAppId: null,
        signature: null,
        templateId: null,
        weight: 1,
        retryInterval: 5,
        maxRetries: 0,
        extraParams: {},
        status: '1',
        remark: null
      }
      this.$refs.editForm && this.$refs.editForm.resetFields()
    },
    // 提交按钮
    handleEditSubmit() {
      this.$refs.editForm.validate(valid => {
        if (valid) {
          // 转换extraParams格式
          const submitEditForm = {
            ...this.editForm,
            extraParams: this.$json.toJsonStr(this.editForm.extraParams)
          }
          if (this.editForm.id) {
            updateSmsSupplier(submitEditForm).then(res => {
              this.$modal.success('修改成功')
              this.closeEdit()
              this.getList()
            })
          } else {
            addSmsSupplier(submitEditForm).then(res => {
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
      this.$download.postAsName('/system/smsSupplier/exportSmsSupplierList', {}, this.queryForm, `sms_supplier_${new Date().getTime()}.xlsx`)
    },
    // 刷新缓存按钮
    handleRefreshCache() {
      this.$modal.loading('刷新缓存中，请稍等...')
      refreshSmsSupplierCache({}).then(() => {
        this.$modal.loadingClose()
        this.$modal.success('刷新成功')
      }).catch(error => {
        console.log(error)
        this.$modal.loadingClose()
      })
    },
    // 状态变更
    handleStatusChange(row) {
      const confirmAction = row.status === '1' ? '启用' : '停用'
      const confirmText = `确认要${confirmAction}"${row.configId}"吗？`
      this.$modal.confirm(confirmText).then(() => {
        const params = {
          id: row.id,
          status: row.status
        }
        updateSmsSupplierStatus(params).then(res => {
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
