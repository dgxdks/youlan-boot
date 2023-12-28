<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" :inline="true" :model="queryForm" label-width="68px" size="small">
      <el-form-item label="退款单号" prop="id">
        <el-input
          v-model="queryForm.id"
          clearable
          placeholder="请输入退款订单号"
          style="width: 220px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="支付单号" prop="orderId">
        <el-input
          v-model="queryForm.orderId"
          clearable
          placeholder="请输入支付订单号"
          style="width: 220px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商户退款号" prop="mchRefundId" label-width="85px">
        <el-input
          v-model="queryForm.mchRefundId"
          clearable
          placeholder="请输入商户退款号"
          style="width: 220px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商户单号" prop="mchOrderId">
        <el-input
          v-model="queryForm.mchOrderId"
          clearable
          placeholder="请输入商户订单号"
          style="width: 220px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="退款状态" prop="refundStatus">
        <dict-select
          v-model="queryForm.refundStatus"
          dict-type="pay_status"
          placeholder="请选择退款状态"
          style="width: 220px"
        />
      </el-form-item>
      <el-form-item label="支付通道" prop="channelId">
        <pay-channel-select
          v-model="queryForm.channelId"
          :pay-channels="payChannelList"
          placeholder="请选择支付通道"
          style="width: 220px"
        />
      </el-form-item>
      <el-form-item label="支付配置" prop="configId">
        <pay-config-select
          v-model="queryForm.configId"
          :pay-configs="payConfigList"
          placeholder="请选择支付配置"
          style="width: 220px"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTimeRange">
        <base-date-time-range-picker v-model="queryForm.createTimeRange" style="width: 340px" />
      </el-form-item>
      <el-form-item>
        <base-search-button @click="handleQuery" />
        <base-reset-button @click="handleResetQuery" />
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <table-toolbar :query-show.sync="queryShow" @refresh="getList" />
    </el-row>

    <el-table ref="table" v-loading="tableLoading" :data="orderList" :default-sort="defaultSort" @sort-change="handleSortChange">
      <el-table-column show-overflow-tooltip align="center" label="退款单号" prop="id" width="180px" />
      <el-table-column show-overflow-tooltip align="center" label="商户退款号" prop="mchRefundId" width="180px" />
      <el-table-column show-overflow-tooltip align="center" label="支付单号" prop="orderId" width="180px" />
      <el-table-column show-overflow-tooltip align="center" label="商户单号" prop="mchOrderId" width="180px" />
      <el-table-column align="center" label="退款状态" prop="refundStatus">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.refundStatus" dict-type="pay_refund_status" />
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip align="center" label="退款金额" prop="refundAmount" width="100px" />
      <el-table-column show-overflow-tooltip align="center" label="创建时间" prop="createTime" width="160px" sortable="custom" />
      <el-table-column align="center" label="支付时间" prop="successTime" width="160px" />
      <el-table-column show-overflow-tooltip align="center" label="支付通道" prop="channelName" width="180px" />
      <el-table-column show-overflow-tooltip align="center" label="支付配置" prop="configName" width="180px" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作" width="210" fixed="right">
        <template slot-scope="scope">
          <base-detail-button v-has-perm="['pay:payRefundOrder:load']" type="text" @click="handleDetail(scope.row)" />
          <base-remove-button v-has-perm="['pay:payRefundOrder:remove']" type="text" @click="handleDelete(scope.row)" />
          <base-menu-button v-has-perm="['pay:payOrder:list']">
            <base-text-button
              v-has-perm="['pay:payRefundOrder:update']"
              icon="el-icon-refresh"
              color="#606266"
              @click="handleSyncPayRefundOrder(scope.row)"
            >退款同步
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

    <!-- 退款订单详细 -->
    <base-drawer title="退款订单详情" :open.sync="editOpen" size="80%" wrapper-closable>
      <div style="padding: 10px">
        <el-descriptions :column="3" class="order-desc" border>
          <el-descriptions-item label="退款单号">{{ editForm.id }}</el-descriptions-item>
          <el-descriptions-item label="商户退款号">{{ editForm.mchRefundId }}</el-descriptions-item>
          <el-descriptions-item label="退款状态">
            <dict-tag v-model="editForm.refundStatus" dict-type="pay_refund_status" />
          </el-descriptions-item>
          <el-descriptions-item label="退款金额">{{ editForm.refundAmount }}</el-descriptions-item>
          <el-descriptions-item label="外部退款号">{{ editForm.outRefundNo }}</el-descriptions-item>
          <el-descriptions-item label="退款号">{{ editForm.refundNo }}</el-descriptions-item>
          <el-descriptions-item label="支付单号">{{ editForm.orderId }}</el-descriptions-item>
          <el-descriptions-item label="商户单号">{{ editForm.mchOrderId }}</el-descriptions-item>
          <el-descriptions-item label="外部交易号">{{ editForm.outTradeNo }}</el-descriptions-item>
          <el-descriptions-item label="交易号">{{ editForm.tradeNo }}</el-descriptions-item>
          <el-descriptions-item label="交易类型">
            <dict-tag v-model="editForm.tradeType" dict-type="pay_trade_type" />
          </el-descriptions-item>
          <el-descriptions-item label="通道ID">
            <el-tag type="primary">{{ editForm.channelId }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="通道名称">
            <el-tag type="primary">{{ editForm.channelName }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="配置ID">
            <el-tag v-if="editForm.configId" type="info">{{ editForm.configId }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="配置名称">
            <el-tag v-if="editForm.configName" type="info">{{ editForm.configName }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="退款时间">{{ editForm.successTime }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ editForm.createTime }}</el-descriptions-item>
          <el-descriptions-item label="修改时间">{{ editForm.updateTime }}</el-descriptions-item>
          <el-descriptions-item label="错误码">{{ editForm.errorCode }}</el-descriptions-item>
          <el-descriptions-item label="退款原因">{{ editForm.refundReason }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ editForm.remark }}</el-descriptions-item>
        </el-descriptions>
        <el-descriptions :column="1" border direction="vertical" class="order-desc">
          <el-descriptions-item label="错误信息">{{ editForm.errorMsg }}</el-descriptions-item>
        </el-descriptions>
        <el-descriptions :column="1" border direction="vertical" class="order-desc">
          <el-descriptions-item label="响应数据">{{ editForm.rawData }}</el-descriptions-item>
        </el-descriptions>
        <el-descriptions :column="1" border direction="vertical" class="order-desc">
          <el-descriptions-item label="回调地址">{{ editForm.notifyUrl }}</el-descriptions-item>
        </el-descriptions>
        <el-descriptions :column="1" border direction="vertical" class="order-desc">
          <el-descriptions-item label="回调数据">{{ editForm.notifyRawData }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </base-drawer>
  </div>
</template>

<script>
import crud from '@/framework/mixin/crud'
import PayConfigSelect from '@/views/pay/components/PayConfigSelect.vue'
import { getPayConfigList } from '@/api/pay/config'
import { getPayChannelList } from '@/api/pay/channel'
import PayChannelSelect from '@/views/pay/components/PayChannelSelect.vue'
import { getPayRefundOrderPageList, removePayRefundOrder, syncPayRefundOrder } from '@/api/pay/refund'

export default {
  name: 'PayRefundOrder',
  components: { PayChannelSelect, PayConfigSelect },
  mixins: [crud],
  data() {
    const defaultSort = { prop: 'createTime', order: 'descending' }
    return {
      // 表格数据
      orderList: [],
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        id: null,
        orderId: null,
        mchRefundId: null,
        mchOrderId: null,
        refundStatus: null,
        channelId: null,
        configId: null,
        createTimeRange: [],
        sortList: [
          { column: defaultSort.prop, asc: this.orderStrIsAsc(defaultSort.order) }

        ]
      },
      // 默认排序
      defaultSort,
      // 支付配置列表
      payConfigList: [],
      // 支付通道列表
      payChannelList: []
    }
  },
  created() {
    this.getList()
    this.getPayConfigList()
    this.getPayChannelList()
  },
  methods: {
    // 查询退款订单
    getList() {
      this.openTableLoading()
      getPayRefundOrderPageList(this.queryForm).then(res => {
        this.orderList = res.data.rows
        this.pageTotal = res.data.total
        this.closeTableLoading()
      })
    },
    // 查询支付配置
    getPayConfigList() {
      getPayConfigList({}).then(res => {
        this.payConfigList = res.data
      })
    },
    // 查询支付通道
    getPayChannelList() {
      getPayChannelList({}).then(res => {
        this.payChannelList = res.data
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
      this.$refs.table.sort(this.defaultSort.prop, this.defaultSort.order)
    },
    resetEdit() {
      this.editForm = {
        id: null,
        mchRefundId: null,
        refundStatus: null,
        refundAmount: null,
        outRefundNo: null,
        refundNo: null,
        orderId: null,
        mchOrderId: null,
        outTradeNo: null,
        tradeNo: null,
        tradeType: null,
        refundReason: null,
        channelId: null,
        channelName: null,
        configId: null,
        configName: null,
        successTime: null,
        createTime: null,
        updateTime: null,
        errorCode: null,
        remark: null,
        errorMsg: null,
        rawData: null,
        notifyUrl: null,
        notifyRawData: null
      }
    },
    // 详细按钮
    handleDetail(row) {
      this.openEdit('退款订单详情')
      this.editForm = row
    },
    // 排序变化
    handleSortChange({ column, prop, order }) {
      this.queryForm.sortList = [
        { column: prop, asc: this.orderStrIsAsc(order) }
      ]
      this.getList()
    },
    // 删除按钮
    handleDelete(row) {
      const list = (row.id && [row.id]) || this.tableIds
      this.$modal.confirm('是否确认删除退款单号为"' + list + '"的数据项？').then(function() {
        return removePayRefundOrder({ list })
      }).then(() => {
        this.getList()
        this.$modal.success('删除成功')
      }).catch(() => {
      })
    },
    // 退款订单同步
    handleSyncPayRefundOrder(row) {
      const _this = this
      this.$modal.confirm('是否确认同步退款单号为"' + row.id + '"的数据项？').then(function() {
        _this.openTableLoading('订单同步中...')
        return syncPayRefundOrder({ id: row.id })
      }).then(() => {
        this.getList()
        this.$modal.success('同步成功')
        _this.$modal.closeTableLoading()
      }).catch(() => {
        _this.$modal.closeTableLoading()
      })
    }
  }
}
</script>

<style lang="scss">
.order-desc {
  margin-top: 5px;
}
</style>
