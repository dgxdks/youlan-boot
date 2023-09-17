<template>
  <el-select v-model="roleIds" :multiple="multiple" :placeholder="placeholder">
    <el-option
      v-for="item in roleOptions"
      :key="item.id"
      :disabled="item.status === '2'"
      :label="item.roleName"
      :value="item.id"
    />
  </el-select>
</template>

<script>
import { getRoleList } from '@/api/system/role'

export default {
  name: 'Role',
  props: {
    value: {
      type: [Number, Array],
      default: null
    },
    placeholder: {
      type: String,
      default: '请选择角色'
    },
    multiple: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      roleOptions: [],
      roleIds: null
    }
  },
  watch: {
    value: {
      handler(newVal) {
        this.roleIds = newVal
      },
      deep: true,
      immediate: true
    },
    roleIds: {
      handler(newVal) {
        this.$emit('input', newVal)
      },
      deep: true
    }
  },
  mounted() {
    this.getList()
  },
  methods: {
    getList() {
      getRoleList({}).then(res => {
        this.roleOptions = res.data
      })
    }
  }
}
</script>
