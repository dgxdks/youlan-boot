export default {
  getNowTime() {
    return new Date().getTime()
  },
  getNowDate() {
    return new Date()
  },
  // 比较日期大小 date1大返回>0
  compareDate(date1, date2) {
    return date1.getTime() - date2.getTime()
  },
  offsetDate(date, offset) {
    return new Date(date.getTime() + offset)
  },
  // 天偏移
  offsetDay(date, offset) {
    return this.offsetDate(date, offset * 24 * 60 * 60 * 1000)
  },
  // 小时偏移
  offsetHour(date, offset) {
    return this.offsetDate(date, offset * 60 * 60 * 1000)
  },
  // 秒偏移
  offsetSeconds(date, offset) {
    return this.offsetDate(date, offset * 1000)
  },
  // 分钟偏移
  offsetMinute(date, offset) {
    return this.offsetDate(date, offset * 60 * 1000)
  },
  // 格式化时间
  formatDateTime(date, format = 'yyyy-MM-dd HH:mm:ss') {
    const o = {
      'M+': date.getMonth() + 1, // 月份
      'd+': date.getDate(), // 日
      'h+': date.getHours() % 12 === 0 ? 12 : date.getHours() % 12, // 小时
      'H+': date.getHours(), // 小时
      'm+': date.getMinutes(), // 分
      's+': date.getSeconds(), // 秒
      'q+': Math.floor((date.getMonth() + 3) / 3), // 季度
      S: date.getMilliseconds(), // 毫秒
      a: date.getHours() < 12 ? '上午' : '下午', // 上午/下午
      A: date.getHours() < 12 ? 'AM' : 'PM' // AM/PM
    }
    if (/(y+)/.test(format)) {
      format = format.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length))
    }
    for (const k in o) {
      if (new RegExp('(' + k + ')').test(format)) {
        format = format.replace(
          RegExp.$1,
          RegExp.$1.length === 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length)
        )
      }
    }
    return format
  },
  // 指定日期当日起始时间
  beginOfDay(date) {
    const beginDate = new Date(date.getTime())
    beginDate.setHours(0, 0, 0, 0)
    return beginDate
  },
  // 指定日期当日结束时间
  endOfDay(date) {
    const endDate = new Date(date.getTime())
    endDate.setHours(23, 59, 59, 999)
    return endDate
  }
}
