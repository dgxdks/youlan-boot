import { mapGetters } from 'vuex'

export default {
  computed: {
    ...mapGetters(['dict'])
  },
  data() {
    if (this.$options && this.$array.isNotEmpty(this.$options.dictTypes)) {
      this.$dict.loadDictList(this.$options.dictTypes)
    }
    return {

    }
  }
}
