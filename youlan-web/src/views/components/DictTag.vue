<template>
  <div>
    <template v-for="item in dict[dictType]">
      <template v-if="values.includes(item.value)">
        <span v-if="$str.isNotBlank(item.cssClass)" :key="item.value" :class="item.cssClass">
          {{ item.name }}
        </span>
        <el-tag v-else :key="item.value" :disable-transitions="true" :type="item.ui" :class="item.cssClass">
          {{ item.name }}
        </el-tag>
      </template>
    </template>
    <!--    <template v-for="(item, index) in options">-->
    <!--      <template v-if="values.includes(item.value)">-->
    <!--        <span-->
    <!--          v-if="item.raw.listClass === 'default' || item.raw.listClass === ''"-->
    <!--          :key="item.value"-->
    <!--          :index="DictTag"-->
    <!--          :class="item.raw.cssClass"-->
    <!--        >{{ item.label + " " }}</span>-->
    <!--        <el-tag-->
    <!--          v-else-->
    <!--          :key="item.value"-->
    <!--          :disable-transitions="true"-->
    <!--          :index="DictTag"-->
    <!--          :type="item.raw.listClass == 'primary' ? '' : item.raw.listClass"-->
    <!--          :class="item.raw.cssClass"-->
    <!--        >-->
    <!--          {{ item.label + " " }}-->
    <!--        </el-tag>-->
    <!--      </template>-->
    <!--    </template>-->
    <!--    <template v-if="unmatch && showValue">-->
    <!--      {{ unmatchArray | handleArray }}-->
    <!--    </template>-->
  </div>
</template>

<script>
export default {
  name: 'DictTag',
  filters: {
    handleArray(array) {
      if (array.length === 0) return ''
      return array.reduce((pre, cur) => {
        return pre + ' ' + cur
      })
    }
  },
  props: {
    value: {
      type: [Number, String, Array],
      default: null
    },
    dictType: {
      type: String,
      default: null
    },
    dictDataList: {
      type: [],
      default: null
    },
    // 当未找到匹配的数据时，显示value
    showValue: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      unmatchArray: [] // 记录未匹配的项
    }
  },
  computed: {
    values() {
      if (this.$obj.isNotEmpty(this.value)) {
        return this.$obj.isArray(this.value) ? this.value : [String(this.value)]
      } else {
        return []
      }
    }
    // unmatch() {
    //   this.unmatchArray = []
    //   if (this.value !== null && typeof this.value !== 'undefined') {
    //     // 传入值为非数组
    //     if (!Array.isArray(this.value)) {
    //       if (this.options.some((v) => v.value == this.value)) return false
    //       this.unmatchArray.push(this.value)
    //       return true
    //     }
    //     // 传入值为Array
    //     this.value.forEach((item) => {
    //       if (!this.options.some((v) => v.value == item)) { this.unmatchArray.push(item) }
    //     })
    //     return true
    //   }
    //   // 没有value不显示
    //   return false
    // }
  },
  created() {
    // 未指定数据字典则主动加载
    if (!this.dictDataList && this.dictType) {
      this.$dict.loadDict(this.dictType)
    }
  }
}
</script>
<style scoped>
.el-tag + .el-tag {
  margin-left: 10px;
}
</style>
