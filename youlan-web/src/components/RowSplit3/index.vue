<template>
  <el-row>
    <el-col :span="8">
      <slot name="1" />
    </el-col>
    <el-col :span="8">
      <slot name="2" />
    </el-col>
    <el-col :span="8">
      <slot name="3" />
    </el-col>
  </el-row>
</template>

<script>
export default {
  name: 'RowSplit3'
}
</script>

<style scoped>

</style>
