<template>
  <main class="seat-embed" :class="`theme-${theme}`">
    <header class="embed-header">
      <div class="brand-block">
        <span class="brand-mark"><i class="el-icon-knife-fork" /></span>
        <div><h1>餐厅座位</h1><p>{{ lastUpdated ? `更新于 ${lastUpdated}` : '实时状态' }}</p></div>
      </div>
      <div class="embed-actions">
        <span v-if="demoMode" class="demo-badge">演示数据</span>
        <el-select v-model="selectedArea" size="small" class="area-select" aria-label="区域">
          <el-option label="全部区域" value="" />
          <el-option v-for="area in areas" :key="area" :label="area" :value="area" />
        </el-select>
        <el-tooltip content="刷新座位状态" placement="bottom">
          <el-button :loading="loading" icon="el-icon-refresh" size="small" circle aria-label="刷新" @click="loadSeats" />
        </el-tooltip>
      </div>
    </header>

    <div class="embed-summary" aria-label="座位状态统计">
      <span v-for="item in statuses" :key="item.value" :class="`status-${item.value}`"><i />{{ item.label }} <strong>{{ countFor(item.value) }}</strong></span>
    </div>

    <section v-if="errorMessage" class="embed-error">
      <i class="el-icon-warning-outline" />
      <strong>座位数据暂时无法加载</strong>
      <span>{{ errorMessage }}</span>
      <el-button size="small" @click="loadSeats">
        重新加载
      </el-button>
    </section>
    <div v-else v-loading="loading" class="embed-canvas">
      <SeatCanvas :seats="visibleSeats" :positions="positions" @select="selectSeat" />
    </div>

    <SeatDetailDrawer :visible="detail.visible" :seat="detail.seat" @close="detail.visible = false" />
  </main>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator'
import SeatCanvas from './components/SeatCanvas.vue'
import SeatDetailDrawer from './components/SeatDetailDrawer.vue'
import { getPublicSeatLayout } from '@/api/seat'
import { loadStoredLayout } from './layoutStorage'
import { mergeLayout, Seat, SeatPositionMap, SEAT_STATUSES } from './types'
import { isAllowedEmbedOrigin, postSeatEmbedMessage } from '@/utils/embedBridge'

const DEMO_SEATS: Seat[] = [
  { id: 901, seatNumber: 'A01', name: '靠窗桌', area: 'A区', capacity: 2, status: 0 },
  { id: 902, seatNumber: 'A02', name: '靠窗桌', area: 'A区', capacity: 2, status: 1 },
  { id: 903, seatNumber: 'A03', name: '四人桌', area: 'A区', capacity: 4, status: 2 },
  { id: 904, seatNumber: 'A04', name: '四人桌', area: 'A区', capacity: 4, status: 0 },
  { id: 905, seatNumber: 'B01', name: '卡座', area: 'B区', capacity: 6, status: 1 },
  { id: 906, seatNumber: 'B02', name: '卡座', area: 'B区', capacity: 6, status: 3 },
  { id: 907, seatNumber: 'B03', name: '圆桌', area: 'B区', capacity: 8, status: 0 },
  { id: 908, seatNumber: 'B04', name: '圆桌', area: 'B区', capacity: 8, status: 0 }
]

@Component({ name: 'SeatEmbed', components: { SeatCanvas, SeatDetailDrawer } })
export default class extends Vue {
  private seats: Seat[] = []
  private positions: SeatPositionMap = {}
  private statuses = SEAT_STATUSES
  private loading = false
  private errorMessage = ''
  private selectedArea = ''
  private lastUpdated = ''
  private detail: any = { visible: false, seat: { status: 0 } }
  private sizeTimer: number | null = null
  private refreshTimer: number | null = null
  private lastHeight = 0

  get demoMode() { return this.queryValue('demo') === '1' }
  get theme() { return this.queryValue('theme') === 'dark' ? 'dark' : 'light' }
  get areas() { return Array.from(new Set(this.seats.map(item => item.area || '未分区'))).sort() }
  get visibleSeats() { return this.selectedArea ? this.seats.filter(item => (item.area || '未分区') === this.selectedArea) : this.seats }

  mounted() {
    document.documentElement.classList.add('seat-embed-document')
    document.body.classList.add('seat-embed-document')
    this.selectedArea = this.queryValue('area')
    this.loadSeats()
    window.addEventListener('message', this.handleParentMessage)
    this.refreshTimer = window.setInterval(() => this.loadSeats(true), 30000)
    this.sizeTimer = window.setInterval(this.reportSize, 800)
    this.$nextTick(() => {
      postSeatEmbedMessage('seat-embed-ready', { area: this.selectedArea, demo: this.demoMode })
      this.reportSize()
    })
  }

  beforeDestroy() {
    document.documentElement.classList.remove('seat-embed-document')
    document.body.classList.remove('seat-embed-document')
    window.removeEventListener('message', this.handleParentMessage)
    if (this.refreshTimer) window.clearInterval(this.refreshTimer)
    if (this.sizeTimer) window.clearInterval(this.sizeTimer)
  }

  @Watch('selectedArea')
  private areaChanged(value: string) {
    postSeatEmbedMessage('seat-area-changed', { area: value })
  }

  private queryValue(key: string) {
    const value: any = this.$route.query[key]
    return Array.isArray(value) ? value[0] || '' : value || ''
  }

  private async loadSeats(silent = false) {
    if (this.demoMode) {
      this.seats = DEMO_SEATS
      this.positions = mergeLayout(this.seats, loadStoredLayout())
      this.lastUpdated = this.timeText()
      this.errorMessage = ''
      return
    }
    if (!silent) this.loading = true
    try {
      const response: any = await getPublicSeatLayout()
      if (response.data.code !== 1) throw new Error(response.data.msg || '接口返回异常')
      this.seats = response.data.data || []
      this.positions = mergeLayout(this.seats, loadStoredLayout())
      this.lastUpdated = this.timeText()
      this.errorMessage = ''
      postSeatEmbedMessage('seat-status-changed', { seats: this.seats, updatedAt: new Date().toISOString() })
    } catch (_) {
      this.errorMessage = '请确认 /user/seat/layout 接口和 Nginx 代理已启动'
    } finally {
      this.loading = false
    }
  }

  private countFor(status: number) { return this.visibleSeats.filter(item => item.status === status).length }

  private selectSeat(seat: Seat) {
    this.detail = { visible: true, seat: { ...seat } }
    postSeatEmbedMessage('seat-selected', { seat })
  }

  private handleParentMessage(event: MessageEvent) {
    if (!isAllowedEmbedOrigin(event.origin, process.env.VUE_APP_EMBED_ORIGINS)) return
    const message = event.data || {}
    if (message.type === 'seat-embed-refresh') this.loadSeats()
    if (message.type === 'seat-embed-set-area' && typeof message.payload === 'string' && (message.payload === '' || this.areas.indexOf(message.payload) !== -1)) this.selectedArea = message.payload
  }

  private reportSize() {
    const height = document.documentElement.scrollHeight
    if (height === this.lastHeight) return
    this.lastHeight = height
    postSeatEmbedMessage('seat-embed-resize', { height })
  }

  private timeText() {
    return new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
  }
}
</script>

<style lang="scss" scoped>
.seat-embed { box-sizing: border-box; width: 100%; min-width: 320px; max-width: 100%; min-height: 100vh; overflow: hidden; padding: 18px; background: #eef1f0; color: #263b34; }.embed-header { display: flex; min-width: 0; align-items: center; justify-content: space-between; margin-bottom: 12px; }.brand-block { display: flex; min-width: 0; align-items: center; gap: 11px; }.brand-mark { display: flex; width: 38px; height: 38px; flex: 0 0 38px; align-items: center; justify-content: center; border-radius: 6px; background: #215d48; color: #fff; font-size: 19px; }.brand-block h1 { margin: 0; font-size: 19px; }.brand-block p { margin: 3px 0 0; color: #72827c; font-size: 11px; }.embed-actions { display: flex; flex: 0 0 auto; align-items: center; gap: 8px; }.area-select { width: 132px; }.demo-badge { padding: 3px 7px; border-radius: 3px; color: #8d520e; background: #fff0d7; font-size: 11px; }
.embed-summary { display: flex; flex-wrap: wrap; gap: 8px 18px; margin-bottom: 10px; padding: 9px 12px; border: 1px solid #d8dfdc; border-radius: 5px; background: #fff; }.embed-summary span { display: inline-flex; align-items: center; gap: 6px; color: #687871; font-size: 12px; }.embed-summary i { width: 8px; height: 8px; border-radius: 2px; background: #7a838c; }.embed-summary .status-0 i { background: #21845a; }.embed-summary .status-1 i { background: #db7b24; }.embed-summary .status-2 i { background: #397dbc; }.embed-summary strong { color: #263b34; }.embed-canvas { width: 100%; max-width: 100%; min-height: 520px; overflow-x: auto; }.embed-error { display: flex; min-height: 460px; flex-direction: column; align-items: center; justify-content: center; gap: 8px; border: 1px solid #d8dfdc; border-radius: 6px; background: #fff; color: #718079; }.embed-error > i { color: #d17a28; font-size: 34px; }.embed-error strong { color: #344940; }.embed-error span { margin-bottom: 6px; font-size: 12px; }
.theme-dark { background: #202825; color: #edf3f0; }.theme-dark .brand-mark { background: #5ba783; }.theme-dark .brand-block p { color: #9ca9a3; }.theme-dark .embed-summary { border-color: #3a4641; background: #29332f; }.theme-dark .embed-summary span, .theme-dark .embed-summary strong { color: #dbe4e0; }
@media (max-width: 620px) { .seat-embed { padding: 12px; }.embed-header { align-items: flex-start; gap: 12px; }.brand-block p { display: none; }.embed-actions { flex-wrap: wrap; justify-content: flex-end; }.demo-badge { display: none; }.area-select { width: 118px; }.embed-summary { gap: 7px 12px; } }
</style>

<style>
html, body { margin: 0; }
html.seat-embed-document, body.seat-embed-document, body.seat-embed-document #app { width: 100%; min-width: 0; max-width: 100%; overflow-x: hidden; }
</style>
