<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" :inline="true" :model="queryForm" label-width="68px" size="small">
      <el-form-item label=" 通道名称" prop="name">
        <el-input
          v-model="queryForm.name"
          clearable
          placeholder="请输入支付通道名称"
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="配置状态" prop="status">
        <dict-select
          v-model="queryForm.status"
          dict-type="db_status"
          style="width: 240px"
          placeholder="支付通道状态"
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
        <base-update-button
          v-has-perm="['pay:payConfig:update']"
          plain
          :disabled="!tableSelectOne"
          @click="handleUpdate"
        />
      </el-col>
      <el-col :span="1.5">
        <base-remove-button
          v-has-perm="['pay:payConfig:remove']"
          plain
          :disabled="tableNoSelected"
          @click="handleDelete"
        />
      </el-col>
      <table-toolbar :query-show.sync="queryShow" @refresh="getList" />
    </el-row>

    <el-table v-loading="tableLoading" :data="channelList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55" />
      <el-table-column align="center" label="通道编号" prop="id" />
      <el-table-column show-overflow-tooltip align="center" label="通道名称" prop="name" />
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
      width="800px"
      @confirm="handleEditSubmit"
      @cancel="handleEditCancel"
    >
      <el-tabs v-model="editFormTab" tab-position="left" style="min-height: 400px">
        <el-tab-pane label="支付通道配置" name="channel">
          <el-form ref="editForm" :model="editForm" :rules="editRules" label-width="120px">
            <base-row-split2>
              <el-form-item label="通道名称" prop="name">
                <el-input v-model="editForm.name" placeholder="请输入支付配置名称" />
              </el-form-item>
              <el-form-item label="配置状态" prop="status">
                <dict-radio v-model="editForm.status" dict-type="db_status" />
              </el-form-item>
            </base-row-split2>
            <el-form-item label="备注" prop="remark">
              <el-input v-model="editForm.remark" placeholder="请输入内容" type="textarea" />
            </el-form-item>
            <el-form-item label="支付回调" prop="payNotifyUrl">
              <base-form-label slot="label" content="支付回调地址，必须是完成URL格式" label="支付回调" />
              <el-input v-model="editForm.payNotifyUrl" placeholder="请输入支付回调地址" />
            </el-form-item>
            <el-form-item label="退款回调" prop="refundNotifyUrl">
              <base-form-label slot="label" content="退款回调地址，必须是完成URL格式" label="退款回调" />
              <el-input v-model="editForm.refundNotifyUrl" placeholder="请输入退款回调地址" />
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="微信支付配置" name="wechat">
          <el-form label-width="120px">
            <el-form-item label="JSAPI支付" prop="status">
              <base-form-label slot="label" content="微信JSAPI支付配置" label="JSAPI支付" />
              <pay-config-select
                v-model="payChannelConfig.wechat.WX_JSAPI"
                :pay-configs="payConfig.wechat"
                placeholder="请关联微信JSAPI支付配置"
              />
            </el-form-item>
            <el-form-item label="APP支付" prop="status">
              <base-form-label slot="label" content="微信APP支付配置" label="APP支付" />
              <pay-config-select
                v-model="payChannelConfig.wechat.WX_APP"
                :pay-configs="payConfig.wechat"
                placeholder="请关联微信APP支付配置"
              />
            </el-form-item>
            <el-form-item label="H5支付" prop="status">
              <base-form-label slot="label" content="微信H5支付配置" label="H5支付" />
              <pay-config-select
                v-model="payChannelConfig.wechat.WX_H5"
                :pay-configs="payConfig.wechat"
                placeholder="请关联微信H5支付配置"
              />
            </el-form-item>
            <el-form-item label="Native支付" prop="status">
              <base-form-label slot="label" content="微信Native支付配置" label="Native支付" />
              <pay-config-select
                v-model="payChannelConfig.wechat.WX_NATIVE"
                :pay-configs="payConfig.wechat"
                placeholder="请关联微信Native支付配置"
              />
            </el-form-item>
            <el-form-item label="小程序支付" prop="status">
              <base-form-label slot="label" content="微信小程序支付配置" label="小程序支付" />
              <pay-config-select
                v-model="payChannelConfig.wechat.WX_MINI"
                :pay-configs="payConfig.wechat"
                placeholder="请关联微信小程序支付配置"
              />
            </el-form-item>
            <el-form-item label="付款码支付" prop="status">
              <base-form-label slot="label" content="微信付款码支付配置" label="付款码支付" />
              <pay-config-select
                v-model="payChannelConfig.wechat.WX_SCAN"
                :pay-configs="payConfig.wechat"
                placeholder="请关联微信付款码支付配置"
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </base-dialog>
  </div>
</template>

<script>
import crud from '@/framework/mixin/crud'
import {
  addPayChannel,
  getPayChannelPageList,
  loadPayChannel,
  removePayChannel,
  updatePayChannel,
  updatePayChannelStatus
} from '@/api/pay/channel'
import PayConfigSelect from '@/views/pay/components/PayConfigSelect.vue'
import { getPayConfigList } from '@/api/pay/config'

export default {
  name: 'PayChannel',
  components: { PayConfigSelect },
  mixins: [crud],
  data() {
    return {
      // 通道表格数据
      channelList: [],
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        status: null
      },
      // 表单校验
      editRules: {
        name: [
          this.$validator.requiredRule('支付配置名称不能为空')
        ],
        payNotifyUrl: [
          this.$validator.requiredRule('支付回调地址不能为空')
        ],
        refundNotifyUrl: [
          this.$validator.requiredRule('退款回调地址不能为空')
        ]
      },
      editFormTab: 'channel',
      // 支付配置列表
      payConfigList: [],
      // 不同类型支付配置
      payConfig: {
        wechat: []
      },
      // 不同类型支付通道配置
      payChannelConfig: {
        wechat: {}
      }
    }
  },
  created() {
    this.getList()
    this.getPayConfigList()
  },
  methods: {
    // 查询通道列表
    getList() {
      this.openTableLoading()
      getPayChannelPageList(this.queryForm).then(res => {
        this.channelList = res.data.rows
        this.pageTotal = res.data.total
        this.closeTableLoading()
      })
    },
    // 获取支付配置列表
    getPayConfigList() {
      getPayConfigList({}).then(res => {
        this.payConfigList = res.data
        this.setPayConfig()
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
      // 刷新支付配置
      this.getPayConfigList()
      this.openEdit('添加通道')
    },
    // 修改按钮
    handleUpdate(row) {
      // 刷新支付配置
      this.getPayConfigList()
      const id = row.id || this.tableIds[0]
      loadPayChannel({ id }).then(res => {
        this.openEdit('修改通道')
        this.editForm = res.data
        this.setPayChannelConfig()
      })
    },
    // 删除按钮
    handleDelete(row) {
      const list = (row.id && [row.id]) || this.tableIds
      this.$modal.confirm('是否确认删除通道编号为"' + list + '"的数据项？').then(function() {
        return removePayChannel({ list })
      }).then(() => {
        this.getList()
        this.$modal.success('删除成功')
      }).catch(() => {
      })
    },
    // 表单重置
    resetEditForm() {
      this.editFormTab = 'channel'
      this.editForm = {
        name: null,
        remark: '',
        payNotifyUrl: null,
        refundNotifyUrl: null,
        status: '1'
      }
      this.payChannelConfig.wechat = {}
      this.$refs.editForm && this.$refs.editForm.resetFields()
    },
    // 提交按钮
    handleEditSubmit() {
      this.$refs.editForm.validate(valid => {
        if (valid) {
          // 转换通道支付配置
          const channelId = this.editForm.id
          const editForm = {
            ...this.editForm,
            payChannelConfigs: this.getPayChannelConfigs(channelId)
          }
          if (this.editForm.id) {
            updatePayChannel(editForm).then(res => {
              this.$modal.success('修改成功')
              this.closeEdit()
              this.getList()
            })
          } else {
            addPayChannel(editForm).then(res => {
              this.$modal.success('新增成功')
              this.closeEdit()
              this.getList()
            })
          }
        } else {
          this.$modal.error('支付通道配置校验未通过，请检查通道配置')
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
        updatePayChannelStatus(params).then(res => {
          this.$modal.success(`${confirmAction}成功`)
        }).catch(err => {
          console.log(err)
          this.$modal.error(`${confirmAction}失败`)
        })
      }).catch(() => {
        row.status = row.status === '1' ? '2' : '1'
      })
    },
    // 获取通道配置
    getPayChannelConfigs(channelId) {
      const payChannelConfig = {
        ...this.payChannelConfig.wechat
      }
      return Object.keys(payChannelConfig).filter(tradeType => {
        return !!payChannelConfig[tradeType]
      }).map(tradeType => {
        return { channelId, configId: payChannelConfig[tradeType], tradeType }
      })
    },
    // 设置通道配置
    setPayChannelConfig() {
      if (!this.editForm.payChannelConfigs) {
        return
      }
      const wxPayChannelConfig = {}
      this.editForm.payChannelConfigs.forEach(item => {
        if (item.payType === 'WECHAT') {
          wxPayChannelConfig[item.tradeType] = item.configId
        }
      })
      this.payChannelConfig.wechat = wxPayChannelConfig
    },
    // 设置支付配置
    setPayConfig() {
      this.payConfig.wechat = this.payConfigList.filter(item => item.type === 'WECHAT')
    }
  }
}
</script>
