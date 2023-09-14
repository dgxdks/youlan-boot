export default {
  props: {
    value: {
      type: [Array, String],
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
        this.range = newVal
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
