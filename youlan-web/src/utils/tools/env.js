export default {
  isDevEnv() {
    return process.env.NODE_ENV === 'development'
  },
  isProdEnv() {
    return process.env.NODE_ENV === 'production'
  }
}
