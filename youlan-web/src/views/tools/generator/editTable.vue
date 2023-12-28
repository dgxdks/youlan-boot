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
          <el-table-column show-overflow-tooltip label="列类型" min-width="10%" prop="columnType" />
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
              <el-select v-model="scope.row.typeKey" clearable>
                <el-option
                  v-for="item in typeList"
                  :key="item.id"
                  :label="item.typeKey"
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
              <el-select v-model="generatorTable.templateType" style="width: 100%" @change="handleTemplateChange">
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
            <el-form-item prop="icon">
              <base-form-label slot="label" content="指定要生成的菜单对应的图标" label="菜单图标" />
              <el-popover placement="bottom-start" trigger="click" width="460" @show="handleIconShow">
                <base-icon-select ref="iconSelect" v-model="generatorTable.menuIcon" />
                <el-input slot="reference" v-model="generatorTable.menuIcon" placeholder="点击选择图标" readonly>
                  <svg-icon
                    v-if="generatorTable.menuIcon"
                    slot="prefix"
                    :icon-class="generatorTable.menuIcon"
                    style="width: 25px;"
                  />
                  <i v-else slot="prefix" class="el-icon-search el-input__icon" />
                </el-input>
              </el-popover>
            </el-form-item>
            <el-form-item prop="entityDto">
              <base-form-label slot="label" content="实体类DTO只包含参与页面新增修改的字段，例如 数据新增、数据编辑，数据库实体类字段较多时建议使用" label="实体类DTO" />
              <dict-radio v-model="generatorTable.entityDto" dict-type="db_yes_no" />
            </el-form-item>
            <el-form-item prop="entityPageDto">
              <base-form-label slot="label" content="实体类分页DTO只包含参与页面列表相关查询的字段，例如 分页查询、列表查询、列表导出，数据库实体类字段较多时建议使用" label="实体类PageDTO" />
              <dict-radio v-model="generatorTable.entityPageDto" dict-type="db_yes_no" />
            </el-form-item>
            <el-form-item prop="entityVo">
              <base-form-label slot="label" content="实体类VO只包含参与接口返回数据的字段，例如 各种查询返回的数据、Excel表格导出，数据库实体类字段较多时建议使用" label="实体类VO" />
              <dict-radio v-model="generatorTable.entityVo" dict-type="db_yes_no" />
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
          <template v-if="generatorTable.templateType === '2'">
            <h4 class="form-header">树表信息</h4>
            <base-row-split2>
              <el-form-item>
                <base-form-label slot="label" content="树结构表列名，一般都是主键或者能够保证唯一的列， 如：org_id" label="树表列名" />
                <el-select v-model="generatorTable.columnName" clearable placeholder="请选择树表列名" style="width: 100%;">
                  <el-option
                    v-for="column in generatorColumnList"
                    :key="column.columnName"
                    :label="column.columnName + '：' + column.columnComment"
                    :value="column.columnName"
                  />
                </el-select>
              </el-form-item>
              <el-form-item>
                <base-form-label slot="label" content="树结构表指向父级的列名称， 如：子parent_org_id -> 父org_id" label="树表父列名" />
                <el-select v-model="generatorTable.parentColumnName" clearable placeholder="请选择树表父列名" style="width: 100%;">
                  <el-option
                    v-for="column in generatorColumnList"
                    :key="column.columnName"
                    :label="column.columnName + '：' + column.columnComment"
                    :value="column.columnName"
                  />
                </el-select>
              </el-form-item>
              <el-form-item>
                <base-form-label slot="label" content="如果需要对表数据进行排序排序则可指定排序列，列对应的Java类型需要实现Comparable接口，默认升序， 如：org_sort" label="树表排序列名" />
                <el-select v-model="generatorTable.sortColumnName" clearable placeholder="树表排序列名" style="width: 100%;">
                  <el-option
                    v-for="column in generatorColumnList"
                    :key="column.columnName"
                    :label="column.columnName + '：' + column.columnComment"
                    :value="column.columnName"
                  />
                </el-select>
              </el-form-item>
            </base-row-split2>
          </template>
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
import { loadTable, updateTale } from '@/api/tools/generator'
import Sortable from 'sortablejs'
import { getDictTypeList } from '@/api/system/dict/type'
import { getMenuTreeList } from '@/api/system/menu'

export default {
  name: 'GenEdit',
  data() {
    return {
      // 表ID
      tableId: null,
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
        generatorPath: null,
        columnName: null,
        parentColumnName: null,
        sortColumnName: null,
        entityDto: null,
        entityPageDto: null,
        entityVo: null,
        menuIcon: null,
        parentMenuId: null
      },
      // 基础信息校验规则
      basicTableRules: {
        tableName: [
          this.$validator.requiredRule('请输入表名称')
        ],
        tableComment: [
          this.$validator.requiredRule('请输入描述')
        ],
        entityName: [
          this.$validator.requiredRule('请输入实体类名称')
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
        ],
        entityDto: [
          this.$validator.requiredRule('请指定是否使用实体类DTO')
        ],
        entityPageDto: [
          this.$validator.requiredRule('请指定是否使用实体类分页DTO')
        ],
        entityVo: [
          this.$validator.requiredRule('请指定是否使用实体类VO')
        ]
      },
      // 字典列表
      typeList: []
    }
  },
  created() {
    this.tableId = this.$route.params && this.$route.params.tableId
    if (this.tableId) {
      this.getTable()
      this.getDictList()
      this.getMenuList()
    }
  },
  mounted() {
    const el = this.$refs.dragTable.$el.querySelectorAll('.el-table__body-wrapper tbody')[0]
    const _this = this
    Sortable.create(el, {
      animation: 150,
      sort: true,
      draggable: '.el-table__row', // 设置可拖拽行的类名(el-table自带的类名)
      forceFallback: true,
      onEnd({ newIndex, oldIndex }) {
        const currRow = _this.generatorColumnList.splice(oldIndex, 1)[0]
        _this.generatorColumnList.splice(newIndex, 0, currRow)
      }
    })
  },
  methods: {
    // 获取表详细信息
    getTable() {
      loadTable({ id: this.tableId }).then(res => {
        this.generatorColumnList = res.data.generatorColumnList
        this.generatorTable = res.data.generatorTable
      })
    },
    // 查询字典类型列表
    getDictList() {
      getDictTypeList({}).then(res => {
        this.typeList = res.data
      })
    },
    // 查询菜单下拉列表
    getMenuList() {
      getMenuTreeList({ menuTypeList: ['1'] }).then(res => {
        this.menuOptions = this.formatMenuOptions(res.data)
      })
    },
    // 提交按钮
    handleSubmitForm() {
      const validateAll = [this.$refs.basicTableForm, this.$refs.generatorForm].map(form => {
        return new Promise(resolve => {
          form.validate(res => {
            resolve(res)
          })
        })
      })
      Promise.all(validateAll).then(res => {
        const validateResult = res.every(item => item === true)
        if (!validateResult) {
          this.$modal.error('表单校验未通过，请重新检查提交内容')
          return
        }
        this.$modal.loading('更新表信息中，请稍等...')
        const data = {
          generatorTable: this.generatorTable,
          generatorColumnList: this.generatorColumnList
        }
        updateTale(data).then(res => {
          this.$modal.loadingClose()
          this.getTable()
        }).catch(error => {
          this.$modal.loadingClose()
          console.log(error)
        })
      })
    },
    // 模版类型变更
    handleTemplateChange(value) {
      // 如果选回单表则清空树表已选结果
      if (value === '1') {
        this.generatorTable.columnName = null
        this.generatorTable.parentColumnName = null
      }
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
    },
    // icon组件显示
    handleIconShow() {
      this.$refs.iconSelect && this.$refs.iconSelect.reset()
    }
  }
}
</script>
