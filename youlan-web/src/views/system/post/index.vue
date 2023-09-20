<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" :inline="true" :model="queryForm" label-width="68px" size="small">
      <el-form-item label="岗位编码" prop="postCode">
        <el-input
          v-model="queryForm.postCode"
          clearable
          placeholder="请输入岗位编码"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="岗位名称" prop="postName">
        <el-input
          v-model="queryForm.postName"
          clearable
          placeholder="请输入岗位名称"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <dict-select v-model="queryForm.status" placeholder="岗位状态" dict-type="db_status" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <base-search-button @click="handleQuery" />
        <base-reset-button @click="handleResetQuery" />
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <base-add-button v-has-perm="['system:post:add']" plain @click="handleAdd" />
      </el-col>
      <el-col :span="1.5">
        <base-update-button v-has-perm="['system:post:update']" :disabled="!tableSelectOne" plain @click="handleUpdate" />
      </el-col>
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:post:remove']" :disabled="tableNoSelected" plain @click="handleDelete" />
      </el-col>
      <el-col :span="1.5">
        <base-download-button v-has-perm="['system:post:export']" plain @click="handleExport">导出</base-download-button>
      </el-col>
      <right-toolbar :query-show.sync="queryShow" @refresh="getList" />
    </el-row>

    <el-table v-loading="tableLoading" :data="postList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55" />
      <el-table-column align="center" label="岗位编号" prop="id" />
      <el-table-column align="center" label="岗位编码" prop="postCode" />
      <el-table-column align="center" label="岗位名称" prop="postName" />
      <el-table-column align="center" label="岗位排序" prop="sort" />
      <el-table-column align="center" label="状态" prop="status">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.status" dict-type="db_status" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="创建时间" prop="createTime" width="160" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template slot-scope="scope">
          <base-update-button v-has-perm="['system:post:update']" type="text" @click="handleUpdate(scope.row)" />
          <base-remove-button v-has-perm="['system:post:remove']" type="text" @click="handleDelete(scope.row)" />
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

    <!-- 岗位编辑对话框 -->
    <base-dialog :title="editTitle" :open.sync="editOpen" width="500px" @confirm="handleEditSubmit" @cancel="handleEditCancel">
      <el-form ref="editForm" :model="editForm" :rules="editRules" label-width="80px">
        <el-form-item label="岗位名称" prop="postName">
          <el-input v-model="editForm.postName" placeholder="请输入岗位名称" />
        </el-form-item>
        <el-form-item label="岗位编码" prop="postCode">
          <el-input v-model="editForm.postCode" placeholder="请输入编码名称" />
        </el-form-item>
        <el-form-item label="岗位顺序" prop="sort">
          <el-input-number v-model="editForm.sort" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="岗位状态" prop="status">
          <dict-radio v-model="editForm.status" dict-type="db_status" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="editForm.remark" placeholder="请输入内容" type="textarea" />
        </el-form-item>
      </el-form>
    </base-dialog>
  </div>
</template>

<script>
import { addPost, getPostPageList, loadPost, removePost, updatePost } from '@/api/system/post'
import crud from '@/framework/mixin/crud'

export default {
  name: 'Post',
  mixins: [crud],
  data() {
    return {
      // 岗位表格数据
      postList: [],
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        postCode: null,
        postName: null,
        status: null
      },
      // 表单校验
      editRules: {
        postName: [
          this.$validator.requiredRule('岗位名称不能为空')
        ],
        postCode: [
          this.$validator.requiredRule('岗位编码不能为空')
        ],
        sort: [
          this.$validator.requiredRule('岗位顺序不能为空')
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 查询岗位列表
    getList() {
      this.loading = true
      this.openTableLoading()
      getPostPageList(this.queryForm).then(res => {
        this.postList = res.data.rows
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
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.tableIds = selection.map(item => item.id)
    },
    // 表单重置
    resetEditForm() {
      this.editForm = {
        id: null,
        postCode: null,
        postName: null,
        sort: 0,
        status: '1',
        remark: null
      }
      this.$refs.editForm && this.$refs.editForm.resetFields()
    },
    // 新增按钮
    handleAdd() {
      this.openEdit('添加岗位')
    },
    // 修改按钮
    handleUpdate(row) {
      const id = row.id || this.tableIds[0]
      loadPost({ id }).then(res => {
        this.openEdit('修改岗位')
        this.editForm = res.data
      })
    },
    // 删除按钮
    handleDelete(row) {
      const list = (row.id && [row.id]) || this.tableIds
      this.$modal.confirm('是否确认删除岗位编号为"' + list + '"的数据项？').then(function() {
        return removePost({ list })
      }).then(() => {
        this.getList()
        this.$modal.success('删除成功')
      }).catch(() => {
      })
    },
    // 提交按钮
    handleEditSubmit() {
      this.$refs.editForm.validate(valid => {
        if (valid) {
          if (this.editForm.id) {
            updatePost(this.editForm).then(res => {
              this.$modal.success('修改成功')
              this.closeEdit()
              this.getList()
            })
          } else {
            addPost(this.editForm).then(res => {
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
      this.$download.postAsName('/system/post/exportPostList', {}, this.queryForm, `post_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
