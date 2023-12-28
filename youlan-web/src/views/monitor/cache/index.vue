<template>
  <div class="app-container">
    <el-row>
      <el-col :span="24" class="card-box">
        <el-card>
          <el-descriptions direction="horizontal" :column="4" border>
            <div slot="title">
              <el-icon name="el-icon-monitor" class="el-icon-monitor" />
              基本信息
            </div>
            <el-descriptions-item label="Redis版本">{{ cacheMonitorInfo.redis_version }}</el-descriptions-item>
            <el-descriptions-item label="运行模式">{{ cacheMonitorInfo.redis_mode }}</el-descriptions-item>
            <el-descriptions-item label="Redis版本">{{ cacheMonitorInfo.tcp_port }}</el-descriptions-item>
            <el-descriptions-item label="客户端数">{{ cacheMonitorInfo.connected_clients }}</el-descriptions-item>
            <el-descriptions-item label="运行时间(天)">{{ cacheMonitorInfo.uptime_in_days }}</el-descriptions-item>
            <el-descriptions-item label="使用内存">{{ cacheMonitorInfo.used_memory_human }}</el-descriptions-item>
            <el-descriptions-item label="使用CPU">{{ cacheMonitorInfo.used_cpu_user_children }}</el-descriptions-item>
            <el-descriptions-item label="内存配置">{{ cacheMonitorInfo.maxmemory_human }}</el-descriptions-item>
            <el-descriptions-item label="AOF是否开启">{{ getAofEnabled(cacheMonitorInfo) }}</el-descriptions-item>
            <el-descriptions-item label="RDB是否成功">{{ cacheMonitorInfo.rdb_last_bgsave_status }}</el-descriptions-item>
            <el-descriptions-item label="Key数量">{{ cacheMonitorInfo.dbSize }}</el-descriptions-item>
            <el-descriptions-item label="网络入口/出口">{{ getInputOutputKbps(cacheMonitorInfo) }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="12" class="card-box">
        <el-card>
          <div slot="header"><span><i class="el-icon-pie-chart" /> 命令统计</span></div>
          <div class="el-table el-table--enable-row-hover el-table--medium">
            <div ref="commandStats" style="height: 420px" />
          </div>
        </el-card>
      </el-col>

      <el-col :span="12" class="card-box">
        <el-card>
          <div slot="header"><span><i class="el-icon-odometer" /> 内存信息</span></div>
          <div class="el-table el-table--enable-row-hover el-table--medium">
            <div ref="usedMemory" style="height: 420px" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getCacheMonitorInfo } from '@/api/monitor/cache'
import * as echarts from 'echarts'

export default {
  name: 'Cache',
  data() {
    return {
      // 统计命令信息
      commandStats: null,
      // 使用内存
      usedMemory: null,
      // 缓存监控信息
      cacheMonitorInfo: {
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 查缓存询信息
    getList() {
      this.$modal.loading('正在加载缓存监控数据，请稍候...')
      getCacheMonitorInfo().then(res => {
        this.cacheMonitorInfo = res.data
        this.$modal.loadingClose()
        this.commandStats = echarts.init(this.$refs.commandStats, 'macarons')
        this.commandStats.setOption({
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b} : {c} ({d}%)'
          },
          series: [
            {
              name: '命令',
              type: 'pie',
              roseType: 'radius',
              radius: [15, 95],
              center: ['50%', '38%'],
              data: res.data.commandstats,
              animationEasing: 'cubicInOut',
              animationDuration: 1000
            }
          ]
        })
        this.usedMemory = echarts.init(this.$refs.usedMemory, 'macarons')
        this.usedMemory.setOption({
          tooltip: {
            formatter: '{b} <br/>{a} : ' + this.cacheMonitorInfo.used_memory_human
          },
          series: [
            {
              name: '峰值',
              type: 'gauge',
              min: 0,
              max: 1000,
              detail: {
                formatter: this.cacheMonitorInfo.used_memory_human
              },
              data: [
                {
                  value: parseFloat(this.cacheMonitorInfo.used_memory_human),
                  name: '内存消耗'
                }
              ]
            }
          ]
        })
      })
    },
    getInputOutputKbps(cacheMonitorInfo) {
      if (!cacheMonitorInfo) {
        return ''
      }
      const inputKbps = cacheMonitorInfo.instantaneous_input_kbps || '0'
      const outputKbps = cacheMonitorInfo.instantaneous_output_kbps || '0'
      return `${inputKbps}kbps/${outputKbps}kbps`
    },
    getAofEnabled(cacheMonitorInfo) {
      if (!cacheMonitorInfo) {
        return ''
      }
      return cacheMonitorInfo.aof_enabled === '0' ? '否' : '是'
    }
  }
}
</script>
