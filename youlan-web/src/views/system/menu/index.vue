<template>
  <div class="app-container">
    <el-form v-show="queryShow" ref="queryForm" :inline="true" :model="queryForm" size="small">
      <el-form-item label="菜单名称" prop="menuName">
        <el-input
          v-model="queryForm.menuName"
          clearable
          placeholder="请输入菜单名称"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <dict-select v-model="queryForm.status" placeholder="菜单状态" clearable dict-type="db_status" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <base-search-button @click="handleQuery" />
        <base-reset-button @click="handleResetQuery" />
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <base-add-button v-has-perm="['system:menu:add']" plain @click="handleAdd(null)" />
      </el-col>
      <el-col :span="1.5">
        <base-add-button icon="el-icon-sort" type="info" plain @click="handleExpandAll">展开/折叠</base-add-button>
      </el-col>
      <right-toolbar :query-show.sync="queryShow" @refresh="getList" />
    </el-row>

    <el-table
      ref="queryTable"
      v-loading="tableLoading"
      :data="menuList"
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
      row-key="id"
    >
      <el-table-column show-overflow-tooltip label="菜单名称" prop="menuName" width="160" />
      <el-table-column align="center" label="图标" prop="menuIcon" width="100">
        <template slot-scope="scope">
          <svg-icon :icon-class="scope.row.menuIcon" />
        </template>
      </el-table-column>
      <el-table-column label="排序" prop="sort" width="60" />
      <el-table-column show-overflow-tooltip label="权限标识" prop="menuPerms" />
      <el-table-column show-overflow-tooltip label="组件路径" prop="componentPath" />
      <el-table-column label="状态" prop="status" width="80">
        <template slot-scope="scope">
          <dict-tag v-model="scope.row.status" dict-type="db_status" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="创建时间" prop="createTime" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template slot-scope="scope">
          <base-update-button v-has-perm="['system:menu:update']" type="text" @click="handleUpdate(scope.row)">修改</base-update-button>
          <base-add-button v-has-perm="['system:menu:add']" type="text" @click="handleAdd(scope.row)" />
          <base-remove-button v-has-perm="['system:menu:remove']" type="text" @click="handleDelete(scope.row)" />
        </template>
      </el-table-column>
    </el-table>

    <!-- 菜单编辑对话框 -->
    <base-dialog :title="editTitle" :open.sync="editOpen" width="680px" @confirm="handleEditSubmit" @cancel="handleEditCancel">
      <el-form ref="editForm" :model="editForm" :rules="editRules" label-width="100px">
        <el-form-item label="上级菜单" prop="parentId">
          <treeselect
            v-model="editForm.parentId"
            :normalizer="menuNormalizer"
            :options="menuOptions"
            :show-count="true"
            placeholder="选择上级菜单"
          />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <dict-radio v-model="editForm.menuType" dict-type="sys_menu_type" />
        </el-form-item>
        <el-form-item v-if="editForm.menuType !== '3'" label="菜单图标" prop="icon">
          <el-popover placement="bottom-start" trigger="click" width="460" @show="handleIconShow">
            <base-icon-select ref="iconSelect" v-model="editForm.menuIcon" />
            <el-input slot="reference" v-model="editForm.menuIcon" placeholder="点击选择图标" readonly>
              <svg-icon
                v-if="editForm.menuIcon"
                slot="prefix"
                :icon-class="editForm.menuIcon"
                style="width: 25px;"
              />
              <i v-else slot="prefix" class="el-icon-search el-input__icon" />
            </el-input>
          </el-popover>
        </el-form-item>
        <base-row-split2>
          <el-form-item label="菜单名称" prop="menuName">
            <el-input v-model="editForm.menuName" placeholder="请输入菜单名称" />
          </el-form-item>
          <el-form-item label="显示排序" prop="sort">
            <el-input-number v-model="editForm.sort" :min="0" controls-position="right" />
          </el-form-item>
          <el-form-item v-if="!isBtn(editForm)" prop="isFrame">
            <base-form-label slot="label" content="选择是外链则路由地址需要以`http(s)://`开头" label="是否外链" />
            <dict-radio v-model="editForm.isFrame" dict-type="db_yes_no" />
          </el-form-item>
          <el-form-item v-if="!isBtn(editForm)" prop="routePath">
            <base-form-label slot="label" content="访问的路由地址，如：`user`，如外网地址需内链访问则以`http(s)://`开头" label="路由地址" />
            <el-input v-model="editForm.routePath" placeholder="请输入路由地址" />
          </el-form-item>
          <el-form-item v-if="isMenu(editForm)" prop="componentPath">
            <base-form-label slot="label" content="访问的组件路径，如：`system/user/index`，默认在`views`目录下" label="组件路径" />
            <el-input v-model="editForm.componentPath" placeholder="请输入组件路径" />
          </el-form-item>
          <el-form-item v-if="isMenu(editForm)" prop="menuPerms">
            <base-form-label slot="label" content="控制器中定义的权限字符，如：@PreAuthorize(`@ss.hasPermi('system:user:list')`)" label="权限字符" />
            <el-input v-model="editForm.menuPerms" maxlength="100" placeholder="请输入权限标识" />
          </el-form-item>
          <el-form-item v-if="isMenu(editForm)" prop="routeQuery">
            <base-form-label slot="label" content="访问路由的默认传递参数，如：`{&quot;id&quot;: 1, &quot;name&quot;: &quot;ry&quot;}`" label="路由参数" />
            <el-input v-model="editForm.routeQuery" maxlength="255" placeholder="请输入路由参数" />
          </el-form-item>
          <el-form-item v-if="isMenu(editForm)" prop="routeCache">
            <base-form-label slot="label" content="选择是则会被`keep-alive`缓存，需要匹配组件的`name`和地址保持一致" label="是否缓存" />
            <dict-radio v-model="editForm.routeCache" dict-type="db_yes_no" />
          </el-form-item>
          <el-form-item v-if="!isBtn(editForm)" prop="visible">
            <base-form-label slot="label" content="选择隐藏则路由将不会出现在侧边栏，但仍然可以访问" label="显示状态" />
            <dict-radio v-model="editForm.visible" dict-type="db_yes_no" />
          </el-form-item>
          <el-form-item v-if="!isBtn(editForm)" prop="status">
            <base-form-label slot="label" content="选择停用则路由将不会出现在侧边栏，也不能被访问" label="菜单状态" />
            <dict-radio v-model="editForm.status" dict-type="db_status" />
          </el-form-item>
        </base-row-split2>
      </el-form>
    </base-dialog>
  </div>
</template>

<script>
import { addMenu, getMenuTreeList, loadMenu, removeMenu, updateMenu } from '@/api/system/menu'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import crud from '@/framework/mixin/crud'

export default {
  name: 'Menu',
  components: { Treeselect },
  mixins: [crud],
  data() {
    return {
      // 查询参数
      queryForm: {
        menuName: null,
        status: null
      },
      // 菜单表格树数据
      menuList: [],
      // 菜单树选项
      menuOptions: [],
      // 表单校验
      editRules: {
        menuName: [
          { required: true, message: '菜单名称不能为空', trigger: 'blur' }
        ],
        sort: [
          { required: true, message: '菜单顺序不能为空', trigger: 'blur' }
        ],
        routePath: [
          { required: true, message: '路由地址不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getMenuTreeList()
  },
  methods: {
    // 列表查询
    getList() {
      this.openTableLoading()
      // 加载表格数据
      getMenuTreeList(this.queryForm).then(res => {
        this.menuList = res.data
        this.tableExpandAll = false
        this.$nextTick(() => {
          this.handleExpandAll()
        })
        this.closeTableLoading()
      })
    },
    // 搜索按钮
    handleQuery() {
      this.getList()
    },
    // 搜索重置按钮
    handleResetQuery() {
      this.$refs.queryForm && this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    // 表单重置
    resetEditForm() {
      this.editForm = {
        id: null,
        parentId: 0,
        menuName: null,
        menuIcon: null,
        menuType: '1',
        menuPerms: null,
        componentPath: null,
        sort: 0,
        isFrame: '2',
        routeCache: '1',
        visible: '1',
        status: '1'
      }
      this.$refs.editForm && this.$refs.editForm.resetFields()
    },
    // 新增按钮
    handleAdd(row) {
      this.openEdit('新增菜单')
      if (row) {
        this.editForm.parentId = row.id
      }
    },
    // 修改按钮
    handleUpdate(row) {
      loadMenu({ id: row.id }).then(res => {
        this.openEdit('修改菜单')
        this.editForm = {
          ...res.data
        }
      })
    },
    // 删除按钮
    handleDelete(row) {
      this.$modal.confirm('是否确认删除名称为"' + row.menuName + '"的数据项？').then(function() {
        return removeMenu({ list: [row.id] })
      }).then(() => {
        this.getList()
        this.$modal.success('删除成功')
      }).catch(error => {
        console.log(error)
      })
    },
    // 提交按钮
    handleEditSubmit() {
      this.$refs.editForm.validate(valid => {
        if (valid) {
          if (this.editForm.id) {
            updateMenu(this.editForm).then(res => {
              this.$modal.success('修改成功')
              this.getList()
              this.closeEdit()
            })
          } else {
            addMenu(this.editForm).then(res => {
              this.$modal.success('新增成功')
              this.getList()
              this.closeEdit()
            })
          }
        }
      })
    },
    // 取消按钮
    handleEditCancel() {
      this.closeEdit()
    },
    // 展开/折叠按钮
    handleExpandAll() {
      this.tableExpandAll = !this.tableExpandAll
      this.menuList.forEach(menu => {
        this.$refs.queryTable.toggleRowExpansion(menu, this.tableExpandAll)
      })
    },
    // 菜单数据转换
    menuNormalizer(node) {
      if (this.$array.isEmpty(node.children)) {
        delete node.children
      }
      return {
        id: node.id,
        label: node.menuName,
        children: node.children
      }
    },
    // 加载全部菜单
    getMenuTreeList() {
      getMenuTreeList({}).then(res => {
        this.menuOptions = [{ id: 0, menuName: '主类目', children: res.data }]
      })
    },
    // 菜单是否目录类型
    isDir(menu) {
      return menu && menu.menuType === '1'
    },
    // 菜单是否菜单类型
    isMenu(menu) {
      return menu && menu.menuType === '2'
    },
    // 菜单是否按钮类型
    isBtn(menu) {
      return menu && menu.menuType === '3'
    },
    // icon组件显示
    handleIconShow() {
      this.$refs.iconSelect && this.$refs.iconSelect.reset()
    }
  }
}
</script>
