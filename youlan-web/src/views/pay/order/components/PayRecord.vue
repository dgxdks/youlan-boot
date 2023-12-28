<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" :inline="true" :model="queryForm" label-width="68px" size="small">
      <el-form-item label="外部单号" prop="outTradeNo">
        <el-input
          v-model="queryForm.outTradeNo"
          clearable
          placeholder="请输入外部交易订单号"
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="交易单号" prop="tradeNo">
        <el-input
          v-model="queryForm.tradeNo"
          clearable
          placeholder="请输入交易单号"
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="支付状态" prop="payStatus">
        <dict-select
          v-model="queryForm.payStatus"
          dict-type="pay_status"
          placeholder="请选择支付状态"
          style="width: 240px"
        />
      </el-form-item>
      <el-form-item label="交易类型" prop="tradeType">
        <dict-select
          v-model="queryForm.tradeType"
          dict-type="pay_trade_type"
          placeholder="请选择交易类型"
          style="width: 240px"
        />
      </el-form-item>
      <el-form-item label="支付配置" prop="configId">
        <pay-config-select
          v-model="queryForm.configId"
          :pay-configs="payConfigList"
          placeholder="请选择支付配置"
          style="width: 240px"
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

    <el-table ref="table" v-loading="tableLoading" :data="recordList" :default-sort="defaultSort" @sort-change="handleSortChange">
      <el-table-column show-overflow-tooltip align="center" label="记录编号" prop="id" width="100px" />
      <el-table-column show-overflow-tooltip align="center" label="外部单号" prop="outTradeNo" width="180px" />
      <el-table-column show-overflow-tooltip align="center" label="交易单号" prop="tradeNo" width="240px" />
      <el-table-column align="center" label="支付状态" prop="payStatus">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.payStatus" dict-type="pay_status" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="交易类型" prop="tradeType" width="180px">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.tradeType" dict-type="pay_trade_type" />
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip align="center" label="支付配置" prop="configName" width="180px" />
      <el-table-column align="center" label="创建时间" prop="createTime" width="160px" sortable="custom" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作" width="160" fixed="right">
        <template slot-scope="scope">
          <base-detail-button v-has-perm="['pay:payOrder:load']" type="text" @click="handleDetail(scope.row)" />
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

    <base-dialog title="支付记录详情" :open.sync="editOpen" width="60%" close-on-click-modal confirm-btn-disabled>
      <div style="padding: 10px">
        <el-descriptions :column="2" class="order-desc" border>
          <el-descriptions-item label="记录编号">{{ editForm.id }}</el-descriptions-item>
          <el-descriptions-item label="外部单号">{{ editForm.outTradeNo }}</el-descriptions-item>
          <el-descriptions-item label="交易单号">{{ editForm.tradeNo }}</el-descriptions-item>
          <el-descriptions-item label="支付单号">{{ editForm.orderId }}</el-descriptions-item>
          <el-descriptions-item label="支付状态">
            <dict-tag v-model="editForm.payStatus" dict-type="pay_status" />
          </el-descriptions-item>
          <el-descriptions-item label="交易类型">
            <dict-tag v-model="editForm.tradeType" dict-type="pay_trade_type" show-default />
          </el-descriptions-item>
          <el-descriptions-item label="配置ID">
            <el-tag v-if="editForm.configId" type="info">{{ editForm.configId }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="配置名称">
            <el-tag v-if="editForm.configName" type="info">{{ editForm.configName }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="客户端IP">{{ editForm.clientIp }}</el-descriptions-item>
          <el-descriptions-item label="错误码">{{ editForm.errorCode }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ editForm.createTime }}</el-descriptions-item>
          <el-descriptions-item label="修改时间">{{ editForm.updateTime }}</el-descriptions-item>
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
    </base-dialog>
  </div>
</template>

<script>
import crud from '@/framework/mixin/crud'
import PayConfigSelect from '@/views/pay/components/PayConfigSelect.vue'
import { getPayConfigList } from '@/api/pay/config'
import { getPayRecordPageList } from '@/api/pay/record'

export default {
  name: 'PayRecord',
  components: { PayConfigSelect },
  mixins: [crud],
  props: {
    orderId: {
      type: [Number, String],
      default: null
    }
  },
  data() {
    const defaultSort = { prop: 'createTime', order: 'descending' }
    return {
      // 表格数据
      recordList: [],
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        outTradeNo: null,
        tradeNo: null,
        payStatus: null,
        tradeType: null,
        configId: null,
        createTimeRange: [],
        sortList: [
          { column: defaultSort.prop, asc: this.orderStrIsAsc(defaultSort.order) }

        ]
      },
      // 默认排序
      defaultSort,
      // 支付配置列表
      payConfigList: []
    }
  },
  created() {
    this.getPayConfigList()
  },
  methods: {
    // 查询支付记录
    getList() {
      this.openTableLoading()
      const queryForm = {
        ...this.queryForm,
        orderId: this.orderId
      }
      getPayRecordPageList(queryForm).then(res => {
        this.recordList = res.data.rows
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
    // 搜索表单重置
    handleResetQueryForm() {
      this.$refs.queryForm && this.$refs.queryForm.resetFields()
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
        outTradeNo: null,
        tradeNo: null,
        orderId: null,
        payStatus: null,
        tradeType: null,
        configId: null,
        configName: null,
        clientIp: null,
        errorCode: null,
        createTime: null,
        updateTime: null,
        errorMsg: null,
        rawData: null,
        notifyUrl: null,
        notifyRawData: null
      }
    },
    // 详细按钮
    handleDetail(row) {
      this.openEdit('支付记录详情')
      this.editForm = row
    },
    // 排序变化
    handleSortChange({ column, prop, order }) {
      this.queryForm.sortList = [
        { column: prop, asc: this.orderStrIsAsc(order) }
      ]
      this.getList()
    }
  }
}
</script>

<style lang="scss">
.order-desc {
  margin-top: 5px;
}
</style>
