<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" :inline="true" :model="queryForm" label-width="68px" size="small">
      <el-form-item label="支付单号" prop="orderId">
        <el-input
          v-model="queryForm.orderId"
          clearable
          placeholder="请输入支付订单号"
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商户单号" prop="mchOrderId">
        <el-input
          v-model="queryForm.mchOrderId"
          clearable
          placeholder="请输入商户订单号"
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="回调类型" prop="notifyType">
        <dict-select
          v-model="queryForm.notifyType"
          dict-type="pay_notify_type"
          placeholder="请选择回调类型"
          style="width: 240px"
        />
      </el-form-item>
      <el-form-item label="回调状态" prop="notifyStatus">
        <dict-select
          v-model="queryForm.notifyStatus"
          dict-type="pay_notify_status"
          placeholder="请选择回调状态"
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

    <el-table ref="table" v-loading="tableLoading" :data="notifyList" :default-sort="defaultSort" @sort-change="handleSortChange">
      <el-table-column show-overflow-tooltip align="center" label="回调编号" prop="id" width="180px" />
      <el-table-column show-overflow-tooltip align="center" label="回调单号" prop="orderId" width="180px" />
      <el-table-column show-overflow-tooltip align="center" label="商户单号" prop="mchOrderId" width="180px" />
      <el-table-column align="center" label="回调类型" prop="notifyType" width="100px">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.notifyType" dict-type="pay_notify_type" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="回调状态" prop="notifyStatus" width="100px">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.notifyStatus" dict-type="pay_notify_status" />
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip align="center" label="回调次数" prop="notifyTimes" width="80px" />
      <el-table-column show-overflow-tooltip align="center" label="创建时间" prop="createTime" width="160px" sortable="custom" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作" width="210" fixed="right">
        <template slot-scope="scope">
          <base-detail-button v-has-perm="['pay:payNotify:load']" type="text" @click="handleDetail(scope.row)" />
          <base-menu-button v-has-perm="['pay:payNotify:list']">
            <base-text-button
              v-has-perm="['pay:payNotify:load']"
              icon="el-icon-s-order"
              color="#606266"
              @click="handlePayNotifyRecord(scope.row)"
            >回调记录
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

    <!-- 支付回调详细 -->
    <base-dialog title="支付回调详情" :open.sync="editOpen" width="80%">
      <div style="padding: 10px">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="回调编号">{{ editForm.id }}</el-descriptions-item>
          <el-descriptions-item label="回调类型">
            <dict-tag v-model="editForm.notifyType" dict-type="pay_notify_type" show-default />
          </el-descriptions-item>
          <el-descriptions-item label="回调单号">{{ editForm.orderId }}</el-descriptions-item>
          <el-descriptions-item label="回调状态">
            <dict-tag v-model="editForm.notifyStatus" dict-type="pay_notify_status" show-default />
          </el-descriptions-item>
          <el-descriptions-item label="回调次数">
            <el-tag v-if="editForm.notifyTimes" type="info">{{ editForm.notifyTimes }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ editForm.createTime }}</el-descriptions-item>
          <el-descriptions-item label="修改时间">{{ editForm.updateTime }}</el-descriptions-item>
          <el-descriptions-item label="下次回调时间">{{ editForm.nextNotifyTime }}</el-descriptions-item>
          <el-descriptions-item label="最后回调时间">{{ editForm.lastNotifyTime }}</el-descriptions-item>
        </el-descriptions>
        <el-descriptions :column="1" border direction="vertical" class="order-desc">
          <el-descriptions-item label="回调地址">{{ editForm.notifyUrl }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </base-dialog>
    <base-dialog title="支付回调记录" :open.sync="notifyRecord.open" width="80%" @cancel="handlePayNotifyRecordCancel">
      <pay-notify-record ref="notifyRecord" :notify-id="notifyRecord.notifyId" />
    </base-dialog>
  </div>
</template>

<script>
import crud from '@/framework/mixin/crud'
import { getPayNotifyPageList } from '@/api/pay/notify'
import PayNotifyRecord from '@/views/pay/notify/components/PayNotifyRecord.vue'

export default {
  name: 'PayOrder',
  components: { PayNotifyRecord },
  mixins: [crud],
  data() {
    const defaultSort = { prop: 'createTime', order: 'descending' }
    return {
      // 表格数据
      notifyList: [],
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        orderId: null,
        mchOrderId: null,
        notifyType: null,
        notifyStatus: null,
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
      payChannelList: [],
      // 回调记录
      notifyRecord: {
        open: false,
        notifyId: null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 查询支付回调
    getList() {
      this.openTableLoading()
      getPayNotifyPageList(this.queryForm).then(res => {
        this.notifyList = res.data.rows
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
    handleResetQuery() {
      this.$refs.queryForm && this.$refs.queryForm.resetFields()
      this.$refs.table.sort(this.defaultSort.prop, this.defaultSort.order)
    },
    resetEdit() {
      this.editForm = {
        id: null,
        notifyType: null,
        orderId: null,
        mchOrderId: null,
        notifyStatus: null,
        notifyTimes: null,
        createTime: null,
        updateTime: null,
        nextNotifyTime: null,
        lastNotifyTime: null
      }
    },
    // 详细按钮
    handleDetail(row) {
      this.openEdit('支付回调详情')
      this.editForm = row
    },
    // 排序变化
    handleSortChange({ column, prop, order }) {
      this.queryForm.sortList = [
        { column: prop, asc: this.orderStrIsAsc(order) }
      ]
      this.getList()
    },
    // 支付回调记录
    handlePayNotifyRecord(row) {
      this.notifyRecord.notifyId = row.id
      this.notifyRecord.open = true
      this.$nextTick(() => {
        this.$refs.notifyRecord && this.$refs.notifyRecord.getList()
      })
    },
    // 支付回调记录取消
    handlePayNotifyRecordCancel() {
      this.$refs.notifyRecord && this.$refs.notifyRecord.handleResetQueryForm()
    }
  }
}
</script>

<style lang="scss">
</style>
