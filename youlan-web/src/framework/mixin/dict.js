import { ArrayUtil } from '../tools'
import { mapGetters } from 'vuex'

export default {
  computed: {
    ...mapGetters(['dict'])
  },
  data() {
    if (ArrayUtil.isNotEmpty(this.dictTypes)) {
      this.$dict.getDictList(this.dictTypes)
    }
    return {

    }
  }
}
