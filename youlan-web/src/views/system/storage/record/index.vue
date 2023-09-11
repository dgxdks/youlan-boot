<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" :inline="true" :model="queryForm" label-width="68px" size="small">
      <el-form-item label="对象ID" prop="objectId">
        <el-input
          v-model="queryForm.objectId"
          clearable
          placeholder="请输入文件对象ID"
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="文件名称" prop="fileName">
        <el-input
          v-model="queryForm.fileName"
          clearable
          placeholder="请输入文件名称"
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="拓展名称" prop="ext">
        <el-input
          v-model="queryForm.ext"
          clearable
          placeholder="请输入拓展名称"
          style="width: 200px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="平台名称" prop="platform">
        <el-input
          v-model="queryForm.platform"
          clearable
          placeholder="请输入平台名称"
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
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
      <el-col :span="1.5">
        <base-upload-button type="primary" plain @click="handleFileUpload">文件上传</base-upload-button>
      </el-col>
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:storageRecord:remove']" plain :disabled="tableNoSelected" @click="handleDelete" />
      </el-col>
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:storageRecord:remove']" plain @click="handleClean">清空</base-remove-button>
      </el-col>
      <right-toolbar :query-show.sync="queryShow" @refresh="getList" />
    </el-row>

    <el-table ref="table" v-loading="tableLoading" :data="logList" :default-sort="defaultSort" @sort-change="handleSortChange" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="50" />
      <el-table-column show-overflow-tooltip align="center" label="平台名称" prop="platform" />
      <el-table-column show-overflow-tooltip align="center" label="对象ID" prop="objectId" />
      <el-table-column show-overflow-tooltip align="center" label="文件名称" prop="fileName" />
      <el-table-column show-overflow-tooltip align="center" label="原始名称" prop="originalFileName" />
      <el-table-column show-overflow-tooltip align="center" label="文件大小" prop="size">
        <template v-if="scope.row.size >= 0" slot-scope="scope">
          {{ scope.row.size }}字节
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip align="center" label="拓展名称" prop="ext" />
      <el-table-column align="center" label="创建时间" prop="createTime" width="160px" sortable="custom" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作" width="160">
        <template slot-scope="scope">
          <base-download-button v-has-perm="['system:storageRecord:downloadFile']" type="text" @click="handleDownload(scope.row)" />
          <base-remove-button v-has-perm="['system:storageRecord:remove']" type="text" @click="handleDelete(scope.row)" />
          <base-detail-button v-has-perm="['system:storageRecord:load']" type="text" @click="handleDetail(scope.row)" />
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

    <!-- 存储记录详细 -->
    <base-drawer title="存储记录详情" :open.sync="editOpen" size="80%" wrapper-closable>
      <el-form ref="editForm" :model="editForm" label-width="100px" size="mini" label-position="left" style="padding: 10px">
        <base-row-split2>
          <el-form-item label="平台名称：" prop="platform">
            {{ editForm.platform }}
          </el-form-item>
          <el-form-item label="文件名称：" prop="fileName">
            {{ editForm.fileName }}
          </el-form-item>
          <el-form-item label="对象ID：" prop="objectId">
            {{ editForm.objectId }}
          </el-form-item>
          <el-form-item label="对象类型：" prop="objectType">
            {{ editForm.objectType }}
          </el-form-item>
          <el-form-item label="文件地址：" prop="url">
            {{ editForm.url }}
          </el-form-item>
          <el-form-item label="文件大小：" prop="size">
            <template v-if="editForm.size > 0">
              {{ editForm.size }}字节
            </template>
          </el-form-item>
          <el-form-item label="拓展名称：" prop="ext">
            {{ editForm.ext }}
          </el-form-item>
          <el-form-item label="原始名称：" prop="originalFileName">
            {{ editForm.originalFileName }}
          </el-form-item>
          <el-form-item label="创建时间：" prop="createTime">
            {{ editForm.createTime }}
          </el-form-item>
          <el-form-item label="基础路径：" prop="basePath">
            {{ editForm.basePath }}
          </el-form-item>
          <el-form-item label="MIME类型：" prop="contentType">
            {{ editForm.contentType }}
          </el-form-item>
          <el-form-item label="存储路径：" prop="path">
            {{ editForm.path }}
          </el-form-item>
          <el-form-item label="缩略图大小：" prop="thSize">
            <template v-if="editForm.thSize > 0">
              {{ editForm.thSize }}字节
            </template>
          </el-form-item>
          <el-form-item label="缩略图MIME类型：" prop="thContentType" label-width="180">
            {{ editForm.thContentType }}
          </el-form-item>
          <el-form-item label="附加属性：" prop="attr">
            {{ editForm.attr }}
          </el-form-item>
          <el-form-item label="文件ACL：" prop="fileAcl">
            {{ editForm.fileAcl }}
          </el-form-item>
          <el-form-item label="缩略图文件ACL：" prop="thFileAcl" label-width="160">
            {{ editForm.thFileAcl }}
          </el-form-item>
          <el-form-item label="缩略图地址：" prop="thUrl">
            {{ editForm.thUrl }}
          </el-form-item>
          <el-form-item v-if="hasImageContentType(editForm)" label="图片预览">
            <image-preview :src="editForm.fullUrl" :width="150" :height="150" />
          </el-form-item>
          <el-form-item v-if="hasImageContentType(editForm)" label="缩略图预览">
            <image-preview :src="editForm.thFullUrl" :width="150" :height="150" />
          </el-form-item>
        </base-row-split2>
      </el-form>
    </base-drawer>
    <base-dialog :title="fileUpload.title" :open.sync="fileUpload.open" width="400px" @confirm="handleUploadFileSubmit" @cancel="handleUploadFileCancel">
      <file-upload-drag
        ref="fileUpload"
        :limit="1"
        @onError="handleFileUploadError"
        @onSuccess="handleFileUploadSuccess"
      />
    </base-dialog>
  </div>
</template>

<script>
import crud from '@/framework/mixin/crud'
import { cleanStorageRecordList, getStorageRecordPageList, removeStorageRecord } from '@/api/system/storage/record'

export default {
  name: 'StorageRecord',
  mixins: [crud],
  data() {
    const defaultSort = { prop: 'createTime', order: 'descending' }
    return {
      // 表格数据
      logList: [],
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        objectId: null,
        fileName: null,
        originalFileName: null,
        platform: null,
        ext: null,
        createTimeRange: [],
        sortList: [
          { column: defaultSort.prop, asc: this.orderStrIsAsc(defaultSort.order) }
        ]
      },
      // 默认排序
      defaultSort,
      imageUpload: {
        open: false,
        title: '图片上传'
      },
      fileUpload: {
        open: false,
        title: '文件上传'
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 查询操作日志
    getList() {
      this.openTableLoading()
      getStorageRecordPageList(this.queryForm).then(res => {
        this.logList = res.rows
        this.pageTotal = res.total
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
      this.$modal.confirm('是否确认删除日志编号为"' + list + '"的数据项？').then(function() {
        return removeStorageRecord({ list })
      }).then(() => {
        this.getList()
        this.$modal.success('删除成功')
      }).catch(() => {
      })
    },
    // 清空按钮
    handleClean() {
      this.$modal.confirm('是否确认清空所有操作日志数据项？').then(function() {
        return cleanStorageRecordList()
      }).then(() => {
        this.getList()
        this.$modal.success('清空成功')
      }).catch(() => {
      })
    },
    // 导出按钮
    handleExport() {
      this.$download.postAsName('/system/operationLog/exportOperationLogList', {}, this.queryForm, `operation_log_${new Date().getTime()}.xlsx`, { timeout: 60 * 1000 })
    },
    // 详细按钮
    handleDetail(row) {
      this.openEdit('存储记录详情')
      this.editForm = row
    },
    // 排序变化
    handleSortChange({ column, prop, order }) {
      this.queryForm.sortList = [
        { column: prop, asc: this.orderStrIsAsc(order) }
      ]
      this.getList()
    },
    // 下载文件
    handleDownload(row) {
      this.$download.saveUrlAsFile(row.url)
    },
    // 文件上传
    handleFileUpload() {
      this.fileUpload.open = true
    },
    // 文件上传成功
    handleFileUploadSuccess() {
      this.$refs.fileUpload.clear()
      this.$modal.success('文件上传成功')
      this.fileUpload.open = false
      this.getList()
    },
    // 文件上传失败
    handleFileUploadError() {
      this.$refs.fileUpload.clear()
      this.$modal.error('文件上传失败')
      this.fileUpload.open = false
    },
    // 文件上传确认
    handleUploadFileSubmit() {
      this.$refs.fileUpload.submit()
    },
    // 文件上传取消
    handleUploadFileCancel() {
      this.$refs.fileUpload.clear()
    },
    // 是否图片文件
    hasImageContentType(row) {
      return row.contentType && row.contentType.includes('image')
    }
  }
}
</script>

