<script>
export default {
  name: 'BaseMenuButton',
  props: {},
  data() {
    return {}
  },
  watch: {},
  mounted() {
  },
  methods: {},
  render(createElement, context) {
    if (this.$array.isEmpty(this.$slots.default)) {
      return null
    }
    const itemNodes = this.$slots.default.map(item => {
      return createElement('el-dropdown-item', [item])
    })
    const dropdownMenuButton = createElement('el-button', {
      props: {
        icon: 'el-icon-d-arrow-right',
        size: 'mini',
        type: 'text'
      }
    }, '更多')
    const dropdownMenuNode = createElement('el-dropdown-menu', {
      props: {
        slot: 'dropdown'
      }
    }, itemNodes)
    return createElement('el-dropdown', {
      props: {
        size: 'mini'
      }
    }, [dropdownMenuButton, dropdownMenuNode])
  }
}
</script>
