<template>
  <el-select v-model="postIds" :multiple="multiple" :placeholder="placeholder">
    <el-option
      v-for="item in postOptions"
      :key="item.id"
      :disabled="item.status === '2'"
      :label="item.postName"
      :value="item.id"
    />
  </el-select>
</template>

<script>
import { getPostList } from '@/api/system/post'

export default {
  name: 'PostSelect',
  props: {
    value: {
      type: [Number, Array],
      default: null
    },
    placeholder: {
      type: String,
      default: '请选择岗位'
    },
    multiple: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      postOptions: [],
      postIds: null
    }
  },
  watch: {
    value: {
      handler(newVal) {
        this.postIds = newVal
      },
      deep: true,
      immediate: true
    },
    postIds: {
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
      getPostList({}).then(res => {
        this.postOptions = res
      })
    }
  }
}
</script>
