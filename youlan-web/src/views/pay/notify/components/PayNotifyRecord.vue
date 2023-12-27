<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" :inline="true" :model="queryForm" label-width="68px" size="small">
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

    <el-table ref="table" v-loading="tableLoading" :data="recordList" :default-sort="defaultSort" @sort-change="handleSortChange">
      <el-table-column show-overflow-tooltip align="center" label="记录编号" prop="id" width="100px" />
      <el-table-column show-overflow-tooltip align="center" label="回调次数" prop="notifyTimes" width="180px" />
      <el-table-column align="center" label="回调状态" prop="notifyStatus">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.notifyStatus" dict-type="pay_notify_status" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="创建时间" prop="createTime" width="160px" sortable="custom" />
      <el-table-column show-overflow-tooltip align="center" label="请求体" prop="requestBody" width="240px" />
      <el-table-column show-overflow-tooltip align="center" label="响应体" prop="responseBody" width="240px" />
      <el-table-column show-overflow-tooltip align="center" label="错误信息" prop="errorMsg" width="240px" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作" width="160" fixed="right">
        <template slot-scope="scope">
          <base-detail-button v-has-perm="['pay:payNotify:load']" type="text" @click="handleDetail(scope.row)" />
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
          <el-descriptions-item label="回调次数">{{ editForm.notifyTimes }}</el-descriptions-item>
          <el-descriptions-item label="回调状态">
            <dict-tag v-model="editForm.notifyStatus" dict-type="pay_notify_status" />
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ editForm.createTime }}</el-descriptions-item>
        </el-descriptions>
        <el-descriptions :column="1" border direction="vertical" class="order-desc">
          <el-descriptions-item label="请求体">{{ editForm.requestBody }}</el-descriptions-item>
        </el-descriptions>
        <el-descriptions :column="1" border direction="vertical" class="order-desc">
          <el-descriptions-item label="响应体">{{ editForm.responseBody }}</el-descriptions-item>
        </el-descriptions>
        <el-descriptions :column="1" border direction="vertical" class="order-desc">
          <el-descriptions-item label="错误信息">{{ editForm.errorMsg }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </base-dialog>
  </div>
</template>

<script>
import crud from '@/framework/mixin/crud'
import { getPayNotifyRecordPageList } from '@/api/pay/notify'

export default {
  name: 'PayNotifyRecord',
  components: { },
  mixins: [crud],
  props: {
    notifyId: {
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
        notifyStatus: null,
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
  },
  methods: {
    // 查询支付记录
    getList() {
      this.openTableLoading()
      const queryForm = {
        ...this.queryForm,
        notifyId: this.notifyId
      }
      getPayNotifyRecordPageList(queryForm).then(res => {
        this.recordList = res.data.rows
        this.pageTotal = res.data.total
        this.closeTableLoading()
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
        notifyTimes: null,
        notifyStatus: null,
        createTime: null,
        requestBody: null,
        responseBody: null,
        errorMsg: null
      }
    },
    // 详细按钮
    handleDetail(row) {
      this.openEdit('回调记录详情')
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
