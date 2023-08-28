export default {
  data() {
    return {
      // 是否显示查询
      queryShow: true,
      // 查询参数名称宽度
      queryLabelWidth: '68px',
      // 查询参数
      queryForm: {
        pageNum: 1,
        pageSize: 10
      },
      // 分页总条数
      pageTotal: 0,
      // 遮罩层
      tableLoading: true,
      // 表格选中的ID
      tableIds: [],
      // 表格选中的数据是否可编辑
      tableCanEdit: false,
      // 表格选中的数据是否可删除
      tableCanRemove: false,
      // 弹出层标记
      editTitle: '',
      // 是否显示弹出层
      editOpen: false,
      // 表单数据
      editForm: {
      },
      // 表单校验规则
      editRules: []
    }
  },
  computed: {
    // 表格被选中
    tableSelected() {
      return this.tableIds && this.tableIds.length > 0
    },
    // 表格未被选中
    tableNoSelected() {
      return !this.tableIds || this.tableIds.length === 0
    },
    // 表格选中单行
    tableSelectOne() {
      return this.tableIds && this.tableIds.length === 1
    },
    // 表格选中多行
    tableSelectMulti() {
      return this.tableIds && this.tableIds.length > 1
    }
  },
  watch: {
  },
  methods: {
    // 重置页码
    resetPageNum() {
      this.queryForm.pageNum = 1
    },
    // 重置编辑表单
    resetEditForm() {
      this.editForm = {}
    },
    // 重置编辑
    resetEdit() {
      this.resetEditForm()
      this.editTitle = ''
    },
    // 打开编辑
    openEdit(editTitle) {
      this.resetEdit()
      this.editTitle = editTitle || ''
      this.editOpen = true
    },
    // 关闭编辑
    closeEdit() {
      this.editOpen = false
      this.resetEdit()
    },
    // 关闭表格加载状态
    closeTableLoading() {
      this.tableLoading = false
    },
    // 开启表格加载状态
    openTableLoading() {
      this.tableLoading = true
    }
  }
}
