<template>
  <el-card>
    <el-tabs v-model="activeName">
      <el-tab-pane label="基础信息" name="basicInfo">
        <el-form ref="basicTableForm" :model="generatorTable" :rules="basicTableRules" label-width="150px">
          <base-row-split2>
            <el-form-item label="表名称" prop="tableName">
              <el-input v-model="generatorTable.tableName" placeholder="请输入表名称" />
            </el-form-item>
            <el-form-item label="表描述" prop="tableComment">
              <el-input v-model="generatorTable.tableComment" placeholder="请输入表描述" />
            </el-form-item>
            <el-form-item label="实体类名称" prop="entityName">
              <base-form-label slot="label" content="数据库实体类名称" label="实体类名称" />
              <el-input v-model="generatorTable.entityName" placeholder="请输入实体类名称" />
            </el-form-item>
            <el-form-item label="作者" prop="authorName">
              <base-form-label slot="label" content="如果填写作者会在类上方生成Java注解@author authorName" label="作者" />
              <el-input v-model="generatorTable.authorName" placeholder="请输入作者名称" />
            </el-form-item>
          </base-row-split2>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="generatorTable.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="字段信息" name="columnInfo">
        <el-table ref="dragTable" :data="generatorColumnList" :max-height="tableHeight" row-key="id">
          <el-table-column class-name="allowDrag" label="序号" min-width="5%" type="index" />
          <el-table-column show-overflow-tooltip label="字段列名" min-width="10%" prop="columnName" />
          <el-table-column label="字段描述" min-width="10%">
            <template slot-scope="scope">
              <el-input v-model="scope.row.columnComment" />
            </template>
          </el-table-column>
          <el-table-column
            show-overflow-tooltip
            label="字典类型"
            min-width="10%"
            prop="columnType"
          />
          <el-table-column label="Java类型" min-width="11%">
            <template slot-scope="scope">
              <dict-select v-model="scope.row.javaType" dict-type="tools_generator_java_type" />
            </template>
          </el-table-column>
          <el-table-column label="Java属性" min-width="10%">
            <template slot-scope="scope">
              <el-input v-model="scope.row.javaField" />
            </template>
          </el-table-column>
          <el-table-column label="编辑" min-width="5%">
            <template slot-scope="scope">
              <el-checkbox v-model="scope.row.isEdit" false-label="2" true-label="1" />
            </template>
          </el-table-column>
          <el-table-column label="列表" min-width="5%">
            <template slot-scope="scope">
              <el-checkbox v-model="scope.row.isTable" false-label="2" true-label="1" />
            </template>
          </el-table-column>
          <el-table-column label="查询" min-width="5%">
            <template slot-scope="scope">
              <el-checkbox v-model="scope.row.isQuery" false-label="2" true-label="1" />
            </template>
          </el-table-column>
          <el-table-column label="查询方式" min-width="10%">
            <template slot-scope="scope">
              <dict-select v-model="scope.row.queryType" dict-type="tools_generator_query_type" />
            </template>
          </el-table-column>
          <el-table-column label="必填" min-width="5%">
            <template slot-scope="scope">
              <el-checkbox v-model="scope.row.isRequired" false-label="0" true-label="1" />
            </template>
          </el-table-column>
          <el-table-column label="显示类型" min-width="12%">
            <template slot-scope="scope">
              <dict-select v-model="scope.row.componentType" dict-type="tools_generator_component_type" />
            </template>
          </el-table-column>
          <el-table-column label="字典类型" min-width="12%">
            <template slot-scope="scope">
              <el-select v-model="scope.row.typeKey">
                <el-option
                  v-for="item in typeList"
                  :key="item.id"
                  :label="item.typeName"
                  :value="item.typeKey"
                />
              </el-select>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="生成信息" name="generatorInfo">
        <el-form ref="generatorForm" :model="generatorTable" :rules="generatorTableRules" label-width="150px">
          <base-row-split2>
            <el-form-item prop="templateType">
              <span slot="label">生成模板</span>
              <el-select v-model="generatorTable.templateType" @change="handleTemplateChange">
                <el-option label="单表（增删改查）" value="1" />
                <el-option label="树表（增删改查）" value="2" />
              </el-select>
            </el-form-item>
            <el-form-item prop="packageName">
              <base-form-label slot="label" content="代码生成在哪个包路径下，例如 com.youlan" label="包路径" />
              <el-input v-model="generatorTable.packageName" />
            </el-form-item>
            <el-form-item prop="moduleName">
              <base-form-label slot="label" content="英文的模块名称，例如 system，包路径为com.youlan，则最终代码会生成在com.youlan.system下" label="模块名称" />
              <el-input v-model="generatorTable.moduleName" />
            </el-form-item>
            <el-form-item prop="bizName">
              <base-form-label slot="label" content="英文的业务名称, 例如 user，则方法和接口路径的命名都会包含此名称，例如 getUserList" label="业务名称" />
              <el-input v-model="generatorTable.bizName" />
            </el-form-item>
            <el-form-item prop="featureName">
              <base-form-label slot="label" content="中英文的功能名称，例如 用户，则方法注释等文字性描述都会包含此名称" label="生成功能名" />
              <el-input v-model="generatorTable.featureName" />
            </el-form-item>
            <el-form-item prop="parentMenuId">
              <base-form-label slot="label" content="分配到指定菜单目录下，例如 系统管理" label="上级菜单" />
              <el-cascader
                ref="menu"
                v-model="generatorTable.parentMenuId"
                :options="menuOptions"
                :props="menuProps"
                clearable
                style="width: 100%"
                @change="handleMenuChange"
              />
            </el-form-item>
            <el-form-item prop="generatorType">
              <base-form-label slot="label" content="默认为zip压缩包下载，也可以自定义生成路径" label="生成代码方式" />
              <el-radio v-model="generatorTable.generatorType" label="1">zip压缩包</el-radio>
              <el-radio v-model="generatorTable.generatorType" label="2">自定义路径</el-radio>
            </el-form-item>
          </base-row-split2>
          <el-form-item v-if="generatorTable.generatorType === '2'" prop="generatorType">
            <base-form-label slot="label" content="建议填写磁盘绝对路径，若不填写，则会生成到当前项目下" label="自定义路径" />
            <el-input v-model="generatorTable.generatorPath">
              <el-dropdown slot="append">
                <el-button type="primary">
                  最近路径快速选择
                  <i class="el-icon-arrow-down el-icon--right" />
                </el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item @click.native="generatorTable.genPath = '/'">恢复默认的生成基础路径</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </el-input>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
    <el-form label-width="100px">
      <el-form-item style="text-align: center;margin-left:-100px;margin-top:10px;">
        <el-button type="primary" @click="handleSubmitForm()">提交</el-button>
        <el-button @click="handleClose()">返回</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import { loadTable } from '@/api/tools/generator'
import Sortable from 'sortablejs'
import { getDictTypeList } from '@/api/system/dict/type'
import { getMenuTreeList } from '@/api/system/menu'

export default {
  name: 'GenEdit',
  data() {
    return {
      // 选中选项卡的 name
      activeName: 'columnInfo',
      // 表格的高度
      tableHeight: document.documentElement.scrollHeight - 245 + 'px',
      // 字典信息
      dictOptions: [],
      // 菜单信息
      menuOptions: [],
      // 菜单属性
      menuProps: {
        label: 'menuName',
        value: 'id',
        checkStrictly: true,
        emitPath: false
      },
      // 表列信息
      generatorColumnList: [],
      // 表详细信息
      generatorTable: {
        tableName: null,
        tableComment: null,
        entityName: null,
        authorName: null,
        remark: null,
        templateType: null,
        packageName: null,
        moduleName: null,
        bizName: null,
        generatorPath: null
      },
      // 基础信息校验规则
      basicTableRules: {
        tableName: [
          { required: true, message: '请输入表名称', trigger: 'blur' }
        ],
        tableComment: [
          { required: true, message: '请输入表描述', trigger: 'blur' }
        ],
        entityName: [
          { required: true, message: '请输入实体类名称', trigger: 'blur' }
        ]
      },
      // 生成信息校验规则
      generatorTableRules: {
        templateType: [
          this.$validator.requiredRule('必须指定生成模版')
        ],
        packageName: [
          this.$validator.requiredRule('必须指定包路径'),
          this.$validator.patternRule(/^([a-zA-Z_]\w*)+([.][a-zA-Z_]\w*)*$/, '不符合Java包路径规范', ['blur', 'change'])
        ],
        moduleName: [
          this.$validator.requiredRule('必须指定模块名称'),
          this.$validator.patternRule(/[a-zA-Z]*/, '只允许包含大小写英文字母')
        ],
        bizName: [
          this.$validator.requiredRule('必须指定业务名称')
        ],
        featureName: [
          this.$validator.requiredRule('必须指定功能名称')
        ],
        generatorType: [
          this.$validator.requiredRule('生成类型必须指定')
        ]
      },
      // 字典列表
      typeList: []
    }
  },
  created() {
    const tableId = this.$route.params && this.$route.params.tableId
    if (tableId) {
      // 获取表详细信息
      loadTable({ id: tableId }).then(res => {
        this.generatorColumnList = res.generatorColumnList
        this.generatorTable = res.generatorTable
      })
      // 查询字典类型列表
      getDictTypeList({}).then(res => {
        this.typeList = res
      })
      // 查询菜单下拉列表
      getMenuTreeList({ menuTypeList: ['1'] }).then(res => {
        this.menuOptions = this.formatMenuOptions(res)
      })
    }
  },
  mounted() {
    const el = this.$refs.dragTable.$el.querySelectorAll('.el-table__body-wrapper > table > tbody')[0]
    Sortable.create(el, {
      handle: '.allowDrag',
      onEnd: evt => {
        const targetRow = this.columns.splice(evt.oldIndex, 1)[0]
        this.columns.splice(evt.newIndex, 0, targetRow)
        for (const index in this.columns) {
          this.columns[index].sort = parseInt(index) + 1
        }
      }
    })
  },
  methods: {
    /** 提交按钮 */
    submitForm() {
      const basicForm = this.$refs.basicInfo.$refs.basicInfoForm
      const genForm = this.$refs.genInfo.$refs.genInfoForm
      Promise.all([basicForm, genForm].map(this.getFormPromise)).then(res => {
        const validateResult = res.every(item => !!item)
        if (validateResult) {
          const genTable = Object.assign({}, basicForm.model, genForm.model)
          genTable.columns = this.columns
          genTable.params = {
            treeCode: genTable.treeCode,
            treeName: genTable.treeName,
            treeParentCode: genTable.treeParentCode,
            parentMenuId: genTable.parentMenuId
          }
          updateGenTable(genTable).then(res => {
            this.$modal.msgSuccess(res.msg)
            if (res.code === 200) {
              this.close()
            }
          })
        } else {
          this.$modal.msgError('表单校验未通过，请重新检查提交内容')
        }
      })
    },
    getFormPromise(form) {
      return new Promise(resolve => {
        form.validate(res => {
          resolve(res)
        })
      })
    },
    // 模版类型变更
    handleTemplateChange(value) {

    },
    // 关闭按钮
    handleClose() {
      const obj = { path: '/tools/generator', query: { t: Date.now(), pageNum: this.$route.query.pageNum }}
      this.$tab.closeOpenPage(obj)
    },
    // 菜单选择
    handleMenuChange(value) {
      this.$refs.menu.dropDownVisible = false
    },
    // 格式化菜单
    formatMenuOptions(menuOptions) {
      if (this.$array.isEmpty(menuOptions)) {
        return menuOptions
      }
      menuOptions.forEach(menu => {
        if (this.$array.isEmpty(menu.children)) {
          menu.children = null
        } else {
          this.formatMenuOptions(menu.children)
        }
      })
      return menuOptions
    }
  }
}
</script>
