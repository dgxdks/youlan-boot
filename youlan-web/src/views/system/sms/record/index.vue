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
      <el-form-item label="模版ID" prop="templateId">
        <el-input
          v-model="queryForm.templateId"
          clearable
          placeholder="请输入模版ID"
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="短信类型" prop="smsType">
        <dict-select
          v-model="queryForm.smsType"
          dict-type="sms_type"
          clearable
          placeholder="短信类型"
          style="width: 240px"
        />
      </el-form-item>
      <el-form-item label="发送类型" prop="sendType">
        <dict-select
          v-model="queryForm.sendType"
          dict-type="sms_send_type"
          clearable
          placeholder="发送类型"
          style="width: 240px"
        />
      </el-form-item>
      <el-form-item label="发送状态" prop="sendStatus">
        <dict-select
          v-model="queryForm.sendStatus"
          dict-type="sms_send_status"
          clearable
          placeholder="发送状态"
          style="width: 240px"
        />
      </el-form-item>
      <el-form-item label="发送时间" prop="sendTimeRange">
        <base-date-time-range-picker v-model="queryForm.sendTimeRange" style="width: 340px" />
      </el-form-item>
      <el-form-item>
        <base-search-button @click="handleQuery" />
        <base-reset-button @click="handleResetQuery" />
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:smsRecord:remove']" plain :disabled="tableNoSelected" @click="handleDelete" />
      </el-col>
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:smsRecord:remove']" plain @click="handleClean">清空</base-remove-button>
      </el-col>
      <table-toolbar :query-show.sync="queryShow" @refresh="getList" />
    </el-row>

    <el-table
      ref="table"
      v-loading="tableLoading"
      :data="recordList"
      :default-sort="defaultSort"
      @sort-change="handleSortChange"
      @selection-change="handleSelectionChange"
    >
      <el-table-column align="center" type="selection" width="50" />
      <el-table-column show-overflow-tooltip align="center" label="手机号" prop="phone" />
      <el-table-column show-overflow-tooltip align="center" label="配置标识" prop="configId" />
      <el-table-column show-overflow-tooltip align="center" label="模版ID" prop="templateId" />
      <el-table-column show-overflow-tooltip align="center" label="短信类型" prop="smsType">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.smsType" dict-type="sms_type" />
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip align="center" label="发送类型" prop="sendType">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.sendType" dict-type="sms_send_type" />
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip align="center" label="发送状态" prop="sendStatus">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.sendStatus" dict-type="sms_send_status" />
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip align="center" label="发送批次" prop="sendBatch" />
      <el-table-column align="center" label="发送时间" prop="sendTime" width="160px" sortable="custom" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作" width="160">
        <template slot-scope="scope">
          <base-remove-button v-has-perm="['system:smsRecord:remove']" type="text" @click="handleDelete(scope.row)" />
          <base-detail-button v-has-perm="['system:smsRecord:load']" type="text" @click="handleDetail(scope.row)" />
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

    <!-- 短信记录详细 -->
    <base-drawer title="短信记录详情" :open.sync="editOpen" size="80%" wrapper-closable>
      <el-form ref="editForm" :model="editForm" label-width="140px" size="mini" label-position="left" style="padding: 10px">
        <base-row-split2>
          <el-form-item label="记录编号：" prop="id">
            {{ editForm.id }}
          </el-form-item>
          <el-form-item label="手机号：" prop="phone">
            {{ editForm.phone }}
          </el-form-item>
          <el-form-item label="配置标识：" prop="configId">
            {{ editForm.configId }}
          </el-form-item>
          <el-form-item label="模版ID：" prop="templateId">
            {{ editForm.templateId }}
          </el-form-item>
          <el-form-item label="短信类型：" prop="smsType">
            <dict-tag v-model="editForm.smsType" dict-type="sms_type" />
          </el-form-item>
          <el-form-item label="发送类型：" prop="sendType">
            <dict-tag v-model="editForm.sendType" dict-type="sms_send_type" />
          </el-form-item>
          <el-form-item label="发送状态：" prop="sendStatus">
            <dict-tag v-model="editForm.sendStatus" dict-type="sms_send_status" />
          </el-form-item>
          <el-form-item label="发送批次：" prop="sendBatch">
            {{ editForm.sendBatch }}
          </el-form-item>
          <el-form-item label="延迟时间(ms)：" prop="delayedTime">
            {{ editForm.delayedTime }}
          </el-form-item>
          <el-form-item label="发送时间：" prop="sendTime">
            {{ editForm.sendTime }}
          </el-form-item>
          <el-form-item label="响应时间：" prop="responseTime">
            {{ editForm.responseTime }}
          </el-form-item>
          <el-form-item label="消息内容：" prop="message">
            {{ editForm.message }}
          </el-form-item>
        </base-row-split2>
        <el-form-item label="响应数据：" prop="responseData">
          <el-input v-model="editForm.responseData" type="textarea" rows="4" />
        </el-form-item>
      </el-form>
    </base-drawer>
  </div>
</template>

<script>
import crud from '@/framework/mixin/crud'
import { cleanSmsRecordList, getSmsRecordPageList, removeSmsRecord } from '@/api/system/sms/record'

export default {
  name: 'StorageRecord',
  mixins: [crud],
  data() {
    const defaultSort = { prop: 'sendTime', order: 'descending' }
    return {
      // 表格数据
      recordList: [],
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        configId: null,
        templateId: null,
        smsType: null,
        sendType: null,
        sendStatus: null,
        sendTimeRange: null,
        sortList: [
          { column: defaultSort.prop, asc: this.orderStrIsAsc(defaultSort.order) }
        ]
      },
      // 默认排序
      defaultSort
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 查询短信记录
    getList() {
      this.openTableLoading()
      getSmsRecordPageList(this.queryForm).then(res => {
        this.recordList = res.data.rows
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
    // 删除按钮
    handleDelete(row) {
      const list = (row.id && [row.id]) || this.tableIds
      this.$modal.confirm('是否确认删除存储编号为"' + list + '"的数据项？').then(function() {
        return removeSmsRecord({ list })
      }).then(() => {
        this.getList()
        this.$modal.success('删除成功')
      }).catch(() => {
      })
    },
    // 清空按钮
    handleClean() {
      this.$modal.confirm('是否确认清空所有短信记录数据项？').then(function() {
        return cleanSmsRecordList()
      }).then(() => {
        this.getList()
        this.$modal.success('清空成功')
      }).catch(() => {
      })
    },
    // 详细按钮
    handleDetail(row) {
      this.openEdit('短信记录详情')
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

