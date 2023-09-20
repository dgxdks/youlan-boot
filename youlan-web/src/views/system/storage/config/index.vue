<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" :inline="true" :model="queryForm" label-width="68px" size="small">
      <el-form-item label=" 配置名称" prop="name">
        <el-input
          v-model="queryForm.name"
          clearable
          placeholder="请输入存储配置名称"
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="平台名称" prop="platform">
        <el-input
          v-model="queryForm.platform"
          clearable
          placeholder="请输入存储平台名称"
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="存储类型" prop="type">
        <dict-select v-model="queryForm.type" dict-type="sys_storage_type" style="width: 240px" placeholder="存储类型" clearable />
      </el-form-item>
      <el-form-item>
        <base-search-button @click="handleQuery" />
        <base-reset-button @click="resetQuery" />
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <base-add-button v-has-perm="['system:storageConfig:add']" plain @click="handleAdd" />
      </el-col>
      <el-col :span="1.5">
        <base-update-button v-has-perm="['system:storageConfig:update']" plain :disabled="!tableSelectOne" @click="handleUpdate" />
      </el-col>
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:storageConfig:remove']" plain :disabled="tableNoSelected" @click="handleDelete" />
      </el-col>
      <el-col :span="1.5">
        <base-download-button v-has-perm="['system:storageConfig:export']" plain @click="handleExport">导出</base-download-button>
      </el-col>
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:storageConfig:remove']" plain @click="handleRefreshCache">刷新缓存</base-remove-button>
      </el-col>
      <right-toolbar :query-show.sync="queryShow" @refresh="getList" />
    </el-row>

    <el-table v-loading="tableLoading" :data="configList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55" />
      <el-table-column align="center" label="配置编号" prop="id" />
      <el-table-column show-overflow-tooltip align="center" label="配置名称" prop="name" />
      <el-table-column show-overflow-tooltip align="center" label="平台名称" prop="platform" />
      <el-table-column show-overflow-tooltip align="center" label="存储类型" prop="type">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.type" dict-type="sys_storage_type" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="是否默认" prop="isDefault">
        <template slot-scope="scope">
          <base-switch v-model="scope.row.isDefault" @change="handleIsDefaultChange(scope.row)" />
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
          <base-update-button v-has-perm="['system:storageConfig:update']" type="text" @click="handleUpdate(scope.row)" />
          <base-remove-button v-has-perm="['system:storageConfig:remove']" type="text" @click="handleDelete(scope.row)" />
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
          <el-form-item label="配置名称" prop="name">
            <el-input v-model="editForm.name" placeholder="请输入存储配置名称" />
          </el-form-item>
          <el-form-item label="存储类型" prop="type">
            <base-form-label slot="label" content="支持本地存储、各家云存储、AmazonS3协议" label="存储类型" />
            <dict-select v-model="editForm.type" placeholder="请选择存储类型" dict-type="sys_storage_type" />
          </el-form-item>
          <el-form-item v-if="!!editForm.id" label="平台名称" prop="platform">
            <base-form-label slot="label" content="新增时会自动生成唯一平台名称" label="平台名称" />
            <el-input v-model="editForm.platform" :disabled="!!editForm.id" placeholder="请输入平台名称" />
          </el-form-item>
          <el-form-item label="基础路径" prop="basePath">
            <base-form-label slot="label" content="文件存储基础路径，以/结尾" label="基础路径" />
            <el-input v-model="editForm.basePath" placeholder="请输入基础路径" />
          </el-form-item>
          <el-form-item label="端点" prop="endPoint">
            <base-form-label slot="label" content="云存储中对应的endPoint" label="端点" />
            <el-input v-model="editForm.endPoint" :disabled="isLocalType" placeholder="请输入端点" />
          </el-form-item>
          <el-form-item label="桶名称" prop="bucketName">
            <base-form-label slot="label" content="云存储中对应的bucket" label="桶" />
            <el-input v-model="editForm.bucketName" :disabled="isLocalType" placeholder="请输入桶名称" />
          </el-form-item>
          <el-form-item label="存储域名" prop="domain">
            <base-form-label slot="label" content="云存储中对应的domain" label="存储域名" />
            <el-input v-model="editForm.domain" :disabled="isLocalType" placeholder="请输入存储域名" />
          </el-form-item>
          <el-form-item label="访问秘钥" prop="accessKey">
            <base-form-label slot="label" content="云存储中对应的accessKey" label="访问秘钥" />
            <el-input v-model="editForm.accessKey" :disabled="isLocalType" placeholder="请输入访问秘钥" />
          </el-form-item>
          <el-form-item label="私密秘钥" prop="secretKey">
            <base-form-label slot="label" content="云存储中对应的secretKey" label="私密秘钥" />
            <el-input v-model="editForm.secretKey" :disabled="isLocalType" placeholder="请输入私密秘钥" />
          </el-form-item>
          <el-form-item label="域名称" prop="region">
            <base-form-label slot="label" content="云存储中对应的region" label="域名称" />
            <el-input v-model="editForm.region" :disabled="isLocalType" placeholder="请输入域名称" />
          </el-form-item>
          <el-form-item label="访问控制" prop="fileAcl">
            <base-form-label slot="label" content="云存储中对应的ACL" label="访问控制" />
            <dict-select v-model="editForm.fileAcl" :disabled="isLocalType" dict-type="sys_storage_acl_type" />
          </el-form-item>
          <el-form-item label="是否HTTPS" prop="isHttps">
            <dict-radio v-model="editForm.isHttps" dict-type="db_yes_no" />
          </el-form-item>
          <el-form-item label="配置状态" prop="status">
            <dict-radio v-model="editForm.status" dict-type="db_status" />
          </el-form-item>
          <el-form-item label="是否默认" prop="isDefault">
            <dict-radio v-model="editForm.isDefault" dict-type="db_yes_no" />
          </el-form-item>
        </base-row-split2>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="editForm.remark" placeholder="请输入内容" type="textarea" />
        </el-form-item>
      </el-form>
    </base-dialog>
  </div>
</template>

<script>
import crud from '@/framework/mixin/crud'
import {
  addStorageConfig,
  getStorageConfigPageList,
  loadStorageConfig,
  refreshStorageConfigCache,
  removeStorageConfig,
  updateStorageConfig,
  updateStorageConfigIsDefault,
  updateStorageConfigStatus
} from '@/api/system/storage/config'

export default {
  name: 'StorageConfig',
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
        platform: null,
        type: null
      },
      // 表单校验
      editRules: {
        name: [
          this.$validator.requiredRule('存储配置名称不能为空')
        ],
        type: [
          this.$validator.requiredRule('存储类型必须选择')
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
      getStorageConfigPageList(this.queryForm).then(res => {
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
      loadStorageConfig({ id }).then(res => {
        this.openEdit('修改配置')
        this.editForm = res.data
      })
    },
    // 删除按钮
    handleDelete(row) {
      const list = (row.id && [row.id]) || this.tableIds
      this.$modal.confirm('是否确认删除配置编号为"' + list + '"的数据项？').then(function() {
        return removeStorageConfig({ list })
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
        platform: null,
        type: 'LOCAL',
        domain: null,
        basePath: null,
        accessKey: null,
        secretKey: null,
        bucketName: null,
        endPoint: null,
        region: null,
        isDefault: '2',
        isHttps: '1',
        fileAcl: 'default',
        status: '1'
      }
      this.$refs.editForm && this.$refs.editForm.resetFields()
    },
    // 提交按钮
    handleEditSubmit() {
      this.$refs.editForm.validate(valid => {
        if (valid) {
          if (this.editForm.id) {
            updateStorageConfig(this.editForm).then(res => {
              this.$modal.success('修改成功')
              this.closeEdit()
              this.getList()
            })
          } else {
            addStorageConfig(this.editForm).then(res => {
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
      this.$download.postAsName('/system/storageConfig/exportStorageConfigList', {}, this.queryForm, `storage_config_${new Date().getTime()}.xlsx`)
    },
    // 刷新缓存按钮
    handleRefreshCache() {
      this.$modal.loading('刷新缓存中，请稍等...')
      refreshStorageConfigCache({}).then(() => {
        this.$modal.loadingClose()
        this.$modal.success('刷新成功')
      }).catch(error => {
        console.log(error)
        this.$modal.loadingClose()
      })
    },
    // 是否默认变更
    handleIsDefaultChange(row) {
      const confirmAction = row.isDefault === '1' ? '设置默认' : '取消默认'
      const confirmText = `确认要将"${row.name}"${confirmAction}吗？`
      this.$modal.confirm(confirmText).then(() => {
        const params = {
          id: row.id,
          isDefault: row.isDefault
        }
        updateStorageConfigIsDefault(params).then(res => {
          this.$modal.success(`${confirmAction}成功`)
          this.getList()
        }).catch(err => {
          console.log(err)
          this.$modal.error(`${confirmAction}失败`)
        })
      }).catch(() => {
        row.isDefault = row.isDefault === '1' ? '2' : '1'
      })
    },
    // 状态变更
    handleStatusChange(row) {
      const confirmAction = row.status === '1' ? '启用' : '停用'
      const confirmText = `确认要${confirmAction}"${row.roleName}"吗？`
      this.$modal.confirm(confirmText).then(() => {
        const params = {
          id: row.id,
          status: row.status
        }
        updateStorageConfigStatus(params).then(res => {
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
