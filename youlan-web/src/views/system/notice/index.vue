<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" :inline="true" :model="queryForm" label-width="68px" size="small">
      <el-form-item label="通知标题" prop="title">
        <el-input
          v-model="queryForm.title"
          clearable
          placeholder="请输入通知标题"
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="通知类型" prop="type">
        <dict-select v-model="queryForm.type" dict-type="sys_notice_type" clearable placeholder="通知类型" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <base-search-button @click="handleQuery" />
        <base-reset-button @click="resetQuery" />
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <base-add-button v-has-perm="['system:notice:add']" plain @click="handleAdd" />
      </el-col>
      <el-col :span="1.5">
        <base-update-button v-has-perm="['system:notice:update']" plain :disabled="!tableSelectOne" @click="handleUpdate" />
      </el-col>
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:notice:remove']" plain :disabled="tableNoSelected" @click="handleDelete" />
      </el-col>
      <table-toolbar :query-show.sync="queryShow" @refresh="getList" />
    </el-row>

    <el-table v-loading="tableLoading" :data="noticeList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55" />
      <el-table-column align="center" label="通知编号" prop="id" />
      <el-table-column show-overflow-tooltip align="center" label="通知标题" prop="title" />
      <el-table-column align="center" label="通知类型" prop="type">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.type" dict-type="sys_notice_type" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态" prop="status">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.status" dict-type="db_status" />
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip align="center" label="通知内容" prop="content" />
      <el-table-column show-overflow-tooltip align="center" label="备注" prop="remark" />
      <el-table-column show-overflow-tooltip align="center" label="创建用户" prop="createBy" />
      <el-table-column align="center" label="创建时间" prop="createTime" width="160" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template slot-scope="scope">
          <base-update-button v-has-perm="['system:notice:update']" type="text" @click="handleUpdate(scope.row)" />
          <base-remove-button v-has-perm="['system:notice:remove']" type="text" @click="handleDelete(scope.row)" />
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

    <!-- 参数配置编辑对话框 -->
    <base-dialog :title="editTitle" :open.sync="editOpen" width="800px" @confirm="handleEditSubmit" @cancel="handleEditCancel">
      <el-form ref="editForm" :model="editForm" :rules="editRules" label-width="100px">
        <base-row-split2>
          <el-form-item label="通知标题" prop="title">
            <el-input v-model="editForm.title" placeholder="请输入参数名称" />
          </el-form-item>
          <el-form-item label="通知类型" prop="type">
            <dict-select v-model="editForm.content" dict-type="sys_notice_type" />
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="editForm.remark" placeholder="请输入内容" type="textarea" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <dict-radio v-model="editForm.status" dict-type="db_status" />
          </el-form-item>
        </base-row-split2>
        <el-form-item label="通知内容" prop="content">
          <editor v-model="editForm.content" :min-height="200" />
        </el-form-item>
      </el-form>
    </base-dialog>
  </div>
</template>

<script>
import crud from '@/framework/mixin/crud'
import { addNotice, getNoticePageList, loadNotice, removeNotice, updateNotice } from '@/api/system/notice'

export default {
  name: 'Notice',
  mixins: [crud],
  data() {
    return {
      // 通知公告表格数据
      noticeList: [],
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        type: null
      },
      // 表单校验
      editRules: {
        title: [
          this.$validator.requiredRule('通知标题不能为空')
        ],
        type: [
          this.$validator.requiredRule('通知类型不能为空')
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 查询参数列表
    getList() {
      this.openTableLoading()
      getNoticePageList(this.queryForm).then(res => {
        this.noticeList = res.data.rows
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
      this.openEdit('添加参数')
    },
    // 修改按钮
    handleUpdate(row) {
      const id = row.id || this.tableIds[0]
      loadNotice({ id }).then(res => {
        this.openEdit('修改参数')
        this.editForm = res.data
      })
    },
    // 删除按钮
    handleDelete(row) {
      const list = (row.id && [row.id]) || this.tableIds
      this.$modal.confirm('是否确认删除参数编号为"' + list + '"的数据项？').then(function() {
        return removeNotice({ list })
      }).then(() => {
        this.getList()
        this.$modal.success('删除成功')
      }).catch(() => {
      })
    },
    // 表单重置
    resetEditForm() {
      this.editForm = {
        title: null,
        type: '1',
        content: null,
        remark: null,
        status: '1'
      }
      this.$refs.editForm && this.$refs.editForm.resetFields()
    },
    // 提交按钮
    handleEditSubmit() {
      this.$refs.editForm.validate(valid => {
        if (valid) {
          if (this.editForm.id) {
            updateNotice(this.editForm).then(res => {
              this.$modal.success('修改成功')
              this.closeEdit()
              this.getList()
            })
          } else {
            addNotice(this.editForm).then(res => {
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
    }
  }
}
</script>
