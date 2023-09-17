<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" :inline="true" :model="queryForm" label-width="68px" size="small">
      <el-form-item label="字典名称" prop="typeKey">
        <el-select v-model="queryForm.typeKey" @change="handleTypeKeyChange">
          <el-option
            v-for="item in typeList"
            :key="item.id"
            :label="item.typeName"
            :value="item.typeKey"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="字典标签" prop="dataName">
        <el-input
          v-model="queryForm.dataName"
          clearable
          placeholder="请输入字典标签"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <dict-select v-model="queryForm.status" placeholder="数据状态" dict-type="db_status" clearable style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <base-search-button @click="handleQuery" />
        <base-reset-button @click="handleResetQuery" />
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <base-add-button v-has-perm="['system:dict:add']" plain @click="handleAdd" />
      </el-col>
      <el-col :span="1.5">
        <base-update-button v-has-perm="['system:dict:update']" plain :disabled="!tableSelectOne" @click="handleUpdate" />
      </el-col>
      <el-col :span="1.5">
        <base-remove-button v-has-perm="['system:dict:remove']" plain :disabled="tableNoSelected" @click="handleDelete" />
      </el-col>
      <el-col :span="1.5">
        <base-download-button v-has-perm="['system:dict:export']" plain @click="handleExport">导出</base-download-button>
      </el-col>
      <el-col :span="1.5">
        <base-close-button plain @click="handleClose" />
      </el-col>
      <right-toolbar :query-show.sync="queryShow" @refresh="getList" />
    </el-row>

    <el-table v-loading="tableLoading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55" />
      <el-table-column align="center" label="字典编码" prop="id" />
      <el-table-column align="center" label="字典标签" prop="dataName">
        <template slot-scope="scope">
          <span v-if="!scope.row.uiClass || scope.row.uiClass === 'default'">{{ scope.row.dataName }}</span>
          <el-tag v-else :type="scope.row.uiClass">{{ scope.row.dataName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="字典键值" prop="dataValue" />
      <el-table-column align="center" label="字典排序" prop="sort" />
      <el-table-column align="center" label="状态" prop="status">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.status" dict-type="db_status" />
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip align="center" label="备注" prop="remark" />
      <el-table-column align="center" label="创建时间" prop="createTime" width="160" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template slot-scope="scope">
          <base-update-button v-has-perm="['system:dict:update']" type="text" @click="handleUpdate(scope.row)" />
          <base-remove-button v-has-perm="['system:dict:remove']" type="text" @click="handleDelete(scope.row)" />
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

    <!-- 字典值编辑对话框 -->
    <base-dialog :title="editTitle" :open.sync="editOpen" width="680px" @confirm="handleEditSubmit" @cancel="handleEditCancel">
      <el-form ref="editForm" :model="editForm" :rules="editRules" label-width="80px">
        <base-row-split2>
          <el-form-item label="字典类型">
            <el-input v-model="editForm.typeKey" :disabled="true" />
          </el-form-item>
          <el-form-item label="UI样式" prop="uiClass">
            <dict-select v-model="editForm.uiClass" dict-type="ui_class" />
          </el-form-item>
          <el-form-item label="字典标签" prop="dataName">
            <el-input v-model="editForm.dataName" placeholder="请输入数据标签" />
          </el-form-item>
          <el-form-item label="字典键值" prop="dataValue">
            <el-input v-model="editForm.dataValue" placeholder="请输入数据键值" />
          </el-form-item>
          <el-form-item label="样式属性" prop="cssClass">
            <el-input v-model="editForm.cssClass" placeholder="请输入样式属性" />
          </el-form-item>
          <el-form-item label="显示排序" prop="sort">
            <el-input-number v-model="editForm.sort" :min="0" controls-position="right" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
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
import {
  addDictData,
  getDictDataPageList,
  loadDictData,
  removeDictData,
  updateDictData
} from '@/api/system/dict/data'
import { getDictTypeList } from '@/api/system/dict/type'
import crud from '@/framework/mixin/crud'

export default {
  name: 'Data',
  mixins: [crud],
  data() {
    return {
      // 字典表格数据
      dataList: [],
      // 类型数据字典
      typeList: [],
      // 默认字典类型
      defaultTypeKey: null,
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        dataName: null,
        status: null,
        typeKey: null
      },
      // 表单校验
      editRules: {
        dataName: [
          { required: true, message: '数据标签不能为空', trigger: 'blur' }
        ],
        dataValue: [
          { required: true, message: '数据键值不能为空', trigger: 'blur' }
        ],
        sort: [
          { required: true, message: '数据顺序不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    const typeKey = this.$route.params && this.$route.params.typeKey
    this.defaultTypeKey = typeKey
    this.queryForm.typeKey = typeKey
    this.getTypeList()
    this.getList()
  },
  methods: {
    // 查询字典类型列表
    getTypeList() {
      getDictTypeList({}).then(res => {
        this.typeList = res.data
      })
    },
    // 查询字典数据列表
    getList() {
      this.openTableLoading()
      getDictDataPageList(this.queryForm).then(res => {
        this.dataList = res.data.rows
        this.pageTotal = res.data.total
        this.closeTableLoading()
      })
    },
    // 搜索按钮
    handleQuery() {
      this.resetPageNum()
      this.getList()
    },
    // 重置搜索按钮
    handleResetQuery() {
      this.$refs.queryForm && this.$refs.queryForm.resetFields()
      this.queryForm.typeKey = this.defaultTypeKey
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.tableIds = selection.map(item => item.id)
    },
    // 新增按钮
    handleAdd() {
      this.openEdit('添加字典数据')
      this.editForm.typeKey = this.queryForm.typeKey
    },
    // 修改按钮
    handleUpdate(row) {
      const id = row.id || this.tableIds[0]
      loadDictData({ id }).then(res => {
        this.openEdit('修改字典数据')
        this.editForm = res.data
      })
    },
    // 删除按钮
    handleDelete(row) {
      const list = (row.id && [row.id]) || this.tableIds
      this.$modal.confirm('是否确认删除字典编码为"' + list + '"的数据项？').then(function() {
        return removeDictData({ list })
      }).then(() => {
        this.getList()
        this.$modal.success('删除成功')
        this.$dict.removeDict(this.queryForm.typeKey)
      }).catch(() => {
      })
    },
    // 表单重置
    resetEdit() {
      this.editForm = {
        id: null,
        dataName: null,
        dataValue: null,
        cssClass: null,
        uiClass: 'default',
        sort: 0,
        status: '1',
        isDefault: '2',
        remark: null
      }
      this.$refs.editForm && this.$refs.editForm.resetFields()
    },
    // 提交按钮
    handleEditSubmit() {
      this.$refs.editForm.validate(valid => {
        if (valid) {
          if (this.editForm.id) {
            updateDictData(this.editForm).then(res => {
              this.$modal.success('修改成功')
              this.closeEdit()
              this.getList()
            }).then(res => {
              this.$dict.refreshDict(this.editForm.typeKey)
            })
          } else {
            addDictData(this.editForm).then(res => {
              this.$modal.success('新增成功')
              this.closeEdit()
              this.getList()
            }).then(res => {
              this.$dict.refreshDict(this.editForm.typeKey)
            })
          }
        }
      })
    },
    // 取消按钮
    handleEditCancel() {
      this.closeEdit()
    },
    handleTypeKeyChange() {
      this.getList()
    },
    // 导出按钮
    handleExport() {
      this.$download.postAsName('/system/dictData/exportDictDataList', {}, this.queryForm, `data_${new Date().getTime()}.xlsx`)
    },
    // 返回按钮
    handleClose() {
      this.$tab.closePage()
    }
  }
}
</script>
