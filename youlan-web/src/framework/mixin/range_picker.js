export default {
  props: {
    value: {
      type: Array,
      default: () => null
    },
    startPlaceholder: {
      type: String,
      default: '开始时间'
    },
    endPlaceholder: {
      type: String,
      default: '结束时间'
    },
    rangeSeparator: {
      type: String,
      default: '-'
    },
    valueFormat: {
      type: String,
      default: 'yyyy-MM-dd HH:mm:ss'
    },
    defaultTime: {
      type: Array,
      default: () => ['00:00:00', '23:59:59']
    },
    startDate: {
      type: Date,
      default: null
    },
    endDate: {
      type: Date,
      default: null
    },
    disabledDate: {
      type: Function,
      default: null
    }
  },
  data() {
    return {
      range: null,
      mixDate: null,
      maxDate: null
    }
  },
  computed: {
    pickerOptions() {
      return {
        disabledDate: this.disabledDate || this.handleDisabledDate
      }
    }
  },
  watch: {
    value: {
      handler(newVal) {
        if (this.$obj.isArray(newVal) && newVal.length === 2) {
          this.range = newVal
        } else {
          this.range = null
        }
      },
      immediate: true,
      deep: true
    },
    range: {
      handler(newVal) {
        this.$emit('input', newVal)
      },
      deep: true
    }
  },
  methods: {
    handleDisabledDate(date) {
      if (this.startDate && date.getTime() < this.startDate.getTime()) {
        return true
      }
      if (this.endDate && date.getTime() > this.endDate.getTime()) {
        return true
      }
    }
  }
}
