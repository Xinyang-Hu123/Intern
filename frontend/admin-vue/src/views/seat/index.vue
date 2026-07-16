<template>
  <div class="seat-page">
    <header class="page-header">
      <div>
        <h1>座位管理</h1>
        <p>按区域查看桌台状态与餐厅布局</p>
      </div>
      <div class="connection-state" :class="{ online: socketConnected }">
        <span />{{ socketConnected ? '实时连接' : '定时刷新' }}
      </div>
    </header>

    <div class="toolbar">
      <el-select v-model="filters.area" clearable placeholder="全部区域" class="filter-select">
        <el-option v-for="area in areas" :key="area" :label="area" :value="area" />
      </el-select>
      <el-select v-model="filters.status" clearable placeholder="全部状态" class="filter-select">
        <el-option v-for="item in statuses" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-input v-model.trim="filters.keyword" clearable placeholder="搜索桌号或名称" prefix-icon="el-icon-search" class="filter-input" />
      <el-button @click="resetFilters">
        重置
      </el-button>
      <div class="toolbar-actions">
        <el-radio-group v-model="viewMode" size="small" aria-label="座位视图">
          <el-radio-button label="layout">
            <i class="el-icon-menu" /> 布局
          </el-radio-button>
          <el-radio-button label="list">
            <i class="el-icon-tickets" /> 列表
          </el-radio-button>
        </el-radio-group>
        <el-tooltip content="刷新座位状态" placement="top">
          <el-button :loading="loading" icon="el-icon-refresh" circle aria-label="刷新" @click="loadSeats" />
        </el-tooltip>
        <el-button type="primary" icon="el-icon-plus" @click="openCreate">
          新增座位
        </el-button>
      </div>
    </div>

    <div class="summary" aria-label="座位状态统计">
      <button v-for="item in statuses" :key="item.value" class="summary-item" :class="`status-${item.value}`" @click="filterByStatus(item.value)">
        <i :class="item.icon" /><span>{{ item.label }}</span><strong>{{ countFor(item.value) }}</strong>
      </button>
    </div>

    <section v-if="viewMode === 'layout'" v-loading="loading" class="layout-section">
      <div class="section-heading">
        <div><h2>{{ filters.area || '餐厅全景' }}</h2><span>{{ filteredSeats.length }} 个座位</span></div>
        <div class="layout-actions">
          <template v-if="layoutEditing">
            <span class="local-note"><i class="el-icon-info" /> 布局保存在当前浏览器</span>
            <el-button @click="cancelLayout">
              取消
            </el-button>
            <el-button type="primary" :disabled="!layoutDirty" @click="saveLayout">
              保存布局
            </el-button>
          </template>
          <el-button v-else icon="el-icon-rank" @click="startLayoutEdit">
            调整布局
          </el-button>
        </div>
      </div>
      <div class="canvas-scroll">
        <SeatCanvas :seats="filteredSeats" :positions="positions" :editable="layoutEditing" @select="showDetail" @move="moveSeat" />
      </div>
    </section>

    <section v-else class="table-section">
      <el-table v-loading="loading" :data="filteredSeats" stripe empty-text="暂无符合条件的座位">
        <el-table-column prop="seatNumber" label="桌号" min-width="90" />
        <el-table-column prop="name" label="名称" min-width="120" />
        <el-table-column prop="area" label="区域" min-width="100" />
        <el-table-column prop="capacity" label="容量" min-width="80">
          <template slot-scope="scope">
            {{ scope.row.capacity || 0 }} 人
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="110">
          <template slot-scope="scope">
            <span class="status-tag" :class="`status-${scope.row.status}`"><i :class="statusIcon(scope.row.status)" />{{ statusText(scope.row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" min-width="168" />
        <el-table-column label="操作" width="190" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="showDetail(scope.row)">
              详情
            </el-button>
            <el-button type="text" size="small" @click="openEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="text" size="small" class="danger" @click="remove(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog :title="dialog.title" :visible.sync="dialog.visible" width="480px" @closed="resetDialog">
      <el-form ref="seatForm" :model="dialog.form" :rules="rules" label-width="90px">
        <el-form-item label="桌号" prop="seatNumber">
          <el-input v-model.trim="dialog.form.seatNumber" maxlength="32" />
        </el-form-item>
        <el-form-item label="座位名称" prop="name">
          <el-input v-model.trim="dialog.form.name" maxlength="64" />
        </el-form-item>
        <el-form-item label="区域" prop="area">
          <el-input v-model.trim="dialog.form.area" maxlength="64" />
        </el-form-item>
        <el-form-item label="容纳人数" prop="capacity">
          <el-input-number v-model="dialog.form.capacity" :min="1" :max="100" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="dialog.form.status">
            <el-option v-for="item in statuses" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model.trim="dialog.form.remark" type="textarea" :rows="3" maxlength="255" show-word-limit />
        </el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="dialog.visible = false">取消</el-button><el-button type="primary" :loading="saving" @click="save">保存</el-button></span>
    </el-dialog>

    <SeatDetailDrawer :visible="detail.visible" :seat="detail.seat" editable @close="detail.visible = false" @edit="openEdit" @status="changeStatus" @remove="remove" />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import SeatCanvas from './components/SeatCanvas.vue'
import SeatDetailDrawer from './components/SeatDetailDrawer.vue'
import { createSeat, deleteSeat, getSeatList, updateSeat, updateSeatStatus } from '@/api/seat'
import { loadStoredLayout, saveStoredLayout } from './layoutStorage'
import { mergeLayout, Seat, SeatPosition, SeatPositionMap, SEAT_STATUSES, statusIcon, statusText } from './types'

const emptyForm = () => ({ id: null, seatNumber: '', name: '', area: '', capacity: 4, status: 0, remark: '' })

@Component({ name: 'SeatManagement', components: { SeatCanvas, SeatDetailDrawer } })
export default class extends Vue {
  private seats: Seat[] = []
  private loading = false
  private saving = false
  private socket: WebSocket | null = null
  private socketConnected = false
  private reconnectTimer: number | null = null
  private refreshTimer: number | null = null
  private viewMode = 'layout'
  private layoutEditing = false
  private layoutDirty = false
  private layoutSnapshot: SeatPositionMap = {}
  private positions: SeatPositionMap = {}
  private filters: any = { keyword: '', area: '', status: null }
  private statuses = SEAT_STATUSES
  private dialog: any = { title: '新增座位', visible: false, form: emptyForm() }
  private detail: any = { visible: false, seat: { status: 0 } }

  private statusText = statusText
  private statusIcon = statusIcon

  get areas() {
    return Array.from(new Set(this.seats.map(item => item.area || '未分区'))).sort()
  }

  get filteredSeats() {
    const keyword = this.filters.keyword.toLowerCase()
    return this.seats.filter(seat => {
      const matchesArea = !this.filters.area || (seat.area || '未分区') === this.filters.area
      const matchesStatus = this.filters.status === null || this.filters.status === '' || seat.status === this.filters.status
      const content = `${seat.seatNumber || ''} ${seat.name || ''}`.toLowerCase()
      return matchesArea && matchesStatus && (!keyword || content.indexOf(keyword) !== -1)
    })
  }

  get rules() {
    return {
      seatNumber: [{ required: true, message: '请输入桌号', trigger: 'blur' }],
      capacity: [{ required: true, message: '请输入容纳人数', trigger: 'change' }]
    }
  }

  mounted() {
    this.loadSeats()
    this.connectSocket()
    this.refreshTimer = window.setInterval(() => this.loadSeats(true), 30000)
    window.addEventListener('beforeunload', this.handleBeforeUnload)
  }

  beforeDestroy() {
    this.cleanupConnections()
    window.removeEventListener('beforeunload', this.handleBeforeUnload)
  }

  beforeRouteLeave(_: any, __: any, next: any) {
    if (!this.layoutDirty) return next()
    this.$confirm('当前布局还未保存，确认离开吗？', '未保存的布局', { type: 'warning' })
      .then(() => next()).catch(() => next(false))
  }

  private async loadSeats(silent = false) {
    if (!silent) this.loading = true
    try {
      const response: any = await getSeatList()
      this.seats = response.data.data || []
      this.positions = mergeLayout(this.seats, loadStoredLayout())
      if (this.detail.visible) {
        const current = this.seats.find(item => item.id === this.detail.seat.id)
        if (current) this.detail.seat = { ...current }
      }
    } catch (_) {
      if (!silent) this.$message.error('座位数据加载失败，请稍后重试')
    } finally {
      this.loading = false
    }
  }

  private resetFilters() { this.filters = { keyword: '', area: '', status: null } }
  private filterByStatus(status: number) { this.filters.status = this.filters.status === status ? null : status }
  private countFor(status: number) { return this.seats.filter(item => item.status === status).length }
  private showDetail(seat: Seat) { if (!this.layoutEditing) this.detail = { visible: true, seat: { ...seat } } }

  private startLayoutEdit() {
    this.layoutSnapshot = JSON.parse(JSON.stringify(this.positions))
    this.layoutEditing = true
    this.layoutDirty = false
  }

  private moveSeat(payload: { id: number, position: SeatPosition }) {
    this.positions = { ...this.positions, [String(payload.id)]: payload.position }
    this.layoutDirty = true
  }

  private saveLayout() {
    saveStoredLayout(this.positions)
    this.layoutEditing = false
    this.layoutDirty = false
    this.$message.success('布局已保存到当前浏览器')
  }

  private cancelLayout() {
    this.positions = JSON.parse(JSON.stringify(this.layoutSnapshot))
    this.layoutEditing = false
    this.layoutDirty = false
  }

  private openCreate() { this.dialog = { title: '新增座位', visible: true, form: emptyForm() } }
  private openEdit(seat: Seat) { this.detail.visible = false; this.dialog = { title: '编辑座位', visible: true, form: { ...emptyForm(), ...seat } } }
  private resetDialog() { const form: any = this.$refs.seatForm; if (form) form.clearValidate() }

  private save() {
    const form: any = this.$refs.seatForm
    form.validate(async (valid: boolean) => {
      if (!valid) return
      this.saving = true
      try {
        if (this.dialog.form.id) await updateSeat(this.dialog.form)
        else await createSeat(this.dialog.form)
        this.$message.success('座位已保存')
        this.dialog.visible = false
        await this.loadSeats()
      } catch (_) {
        this.$message.error('保存失败，请检查桌号是否重复')
      } finally {
        this.saving = false
      }
    })
  }

  private async changeStatus(seat: Seat, status: number) {
    if (seat.status === 1 && status !== 1) {
      try { await this.$confirm('该座位正在使用，请确认用餐已结束。', '更改使用中座位', { type: 'warning' }) } catch (_) { return }
    }
    try {
      await updateSeatStatus(seat.id, status)
      this.$message.success('状态已更新')
      await this.loadSeats(true)
    } catch (_) {
      this.$message.error('状态更新失败')
    }
  }

  private async remove(seat: Seat) {
    try {
      await this.$confirm(`确认删除座位「${seat.seatNumber}」吗？`, '删除座位', { type: 'warning' })
      await deleteSeat(seat.id)
      this.detail.visible = false
      this.$message.success('座位已删除')
      await this.loadSeats()
    } catch (error) {
      if (error !== 'cancel' && error !== 'close') this.$message.error('删除失败，该座位可能已被业务引用')
    }
  }

  private connectSocket() {
    if (!process.env.VUE_APP_SOCKET_URL) return
    try {
      this.socket = new WebSocket(`${process.env.VUE_APP_SOCKET_URL}seat-management`)
      this.socket.onopen = () => { this.socketConnected = true }
      this.socket.onmessage = event => {
        try {
          const payload = JSON.parse(event.data)
          if (['seat-status-changed', 'SEAT_STATUS_CHANGED', 'DINING_SESSION_CLOSED'].indexOf(payload.type || payload.eventType) !== -1) this.loadSeats(true)
        } catch (_) { /* Other legacy WebSocket messages are ignored. */ }
      }
      this.socket.onclose = () => { this.socketConnected = false; this.scheduleReconnect() }
      this.socket.onerror = () => { this.socketConnected = false }
    } catch (_) { this.scheduleReconnect() }
  }

  private scheduleReconnect() {
    if (this.reconnectTimer || !process.env.VUE_APP_SOCKET_URL) return
    this.reconnectTimer = window.setTimeout(() => {
      this.reconnectTimer = null
      this.connectSocket()
      this.loadSeats(true)
    }, 5000)
  }

  private cleanupConnections() {
    if (this.socket) { this.socket.onclose = null; this.socket.close() }
    if (this.reconnectTimer) window.clearTimeout(this.reconnectTimer)
    if (this.refreshTimer) window.clearInterval(this.refreshTimer)
  }

  private handleBeforeUnload(event: BeforeUnloadEvent) {
    if (!this.layoutDirty) return
    event.preventDefault()
    event.returnValue = ''
  }
}
</script>

<style lang="scss" scoped>
.seat-page { min-height: calc(100vh - 84px); padding: 22px; background: #f2f4f3; color: #263c35; }.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 18px; }.page-header h1 { margin: 0; font-size: 22px; }.page-header p { margin: 5px 0 0; color: #71817b; font-size: 13px; }.connection-state { display: flex; align-items: center; gap: 7px; color: #7b8783; font-size: 12px; }.connection-state span { width: 8px; height: 8px; border-radius: 50%; background: #9aa3a0; }.connection-state.online { color: #1d7450; }.connection-state.online span { background: #279568; box-shadow: 0 0 0 3px #dcefe6; }
.toolbar { display: flex; align-items: center; gap: 10px; margin-bottom: 14px; }.filter-select { width: 145px; }.filter-input { width: 220px; }.toolbar-actions { display: flex; align-items: center; gap: 9px; margin-left: auto; }
.summary { display: grid; grid-template-columns: repeat(4, minmax(130px, 1fr)); gap: 10px; margin-bottom: 14px; }.summary-item { display: grid; grid-template-columns: 28px 1fr auto; align-items: center; min-height: 58px; padding: 10px 14px; border: 1px solid #dbe1df; border-radius: 6px; background: #fff; color: #52635d; cursor: pointer; text-align: left; }.summary-item i { font-size: 18px; }.summary-item strong { color: #263c35; font-size: 20px; }.summary-item.status-0 { border-left: 4px solid #21845a; }.summary-item.status-1 { border-left: 4px solid #db7b24; }.summary-item.status-2 { border-left: 4px solid #397dbc; }.summary-item.status-3 { border-left: 4px solid #7a838c; }
.layout-section, .table-section { padding: 16px; border: 1px solid #dce2e0; border-radius: 6px; background: #fff; }.section-heading { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }.section-heading > div:first-child { display: flex; align-items: baseline; gap: 10px; }.section-heading h2 { margin: 0; font-size: 16px; }.section-heading span { color: #7b8984; font-size: 12px; }.layout-actions { display: flex; align-items: center; gap: 8px; }.local-note { margin-right: 4px; color: #75827e; }.canvas-scroll { overflow-x: auto; }.status-tag { display: inline-flex; align-items: center; gap: 5px; padding: 3px 7px; border-radius: 3px; font-size: 12px; }.status-tag.status-0 { color: #176644; background: #e5f5ec; }.status-tag.status-1 { color: #9a4d0b; background: #fff0dc; }.status-tag.status-2 { color: #245d91; background: #e8f2fc; }.status-tag.status-3 { color: #555f68; background: #eceff1; }.danger { color: #c43c3c; }
@media (max-width: 980px) { .toolbar { flex-wrap: wrap; }.toolbar-actions { width: 100%; margin-left: 0; }.summary { grid-template-columns: repeat(2, minmax(130px, 1fr)); }.local-note { display: none; } }
@media (max-width: 560px) { .seat-page { padding: 14px; }.page-header { align-items: flex-start; }.connection-state { margin-top: 5px; }.filter-input, .filter-select { flex: 1 1 140px; width: auto; }.toolbar-actions { flex-wrap: wrap; }.summary { grid-template-columns: 1fr 1fr; }.section-heading { align-items: flex-start; gap: 10px; }.layout-actions { flex-wrap: wrap; justify-content: flex-end; } }
</style>
