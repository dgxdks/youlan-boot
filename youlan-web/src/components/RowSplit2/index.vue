<template>
  <el-row>
    <el-col :span="12">
      <slot name="1" />
    </el-col>
    <el-col :span="12">
      <slot name="2" />
    </el-col>
  </el-row>
</template>

<script>
export default {
  name: 'RowSplit2'
}
</script>

<style scoped>

</style>
