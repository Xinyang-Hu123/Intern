<template>
  <div class="seat-page">
    <div class="toolbar">
      <el-input v-model="filters.seatNumber" clearable placeholder="桌号" class="filter-input" @keyup.enter.native="loadSeats" />
      <el-select v-model="filters.status" clearable placeholder="全部状态" class="filter-select" @change="loadSeats">
        <el-option v-for="item in statuses" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-button type="primary" class="continue" @click="loadSeats">查询</el-button>
      <el-button @click="resetFilters">重置</el-button>
      <div class="toolbar-actions">
        <el-button type="primary" @click="openCreate">新增座位</el-button>
        <el-button :loading="loading" icon="el-icon-refresh" @click="refresh">刷新</el-button>
      </div>
    </div>

    <div class="summary" aria-label="座位状态统计">
      <button v-for="item in statuses" :key="item.value" class="summary-item" :class="statusClass(item.value)" @click="filterByStatus(item.value)">
        <span>{{ item.label }}</span><strong>{{ countFor(item.value) }}</strong>
      </button>
    </div>

    <section class="layout-section">
      <div class="section-heading">
        <h2>餐厅座位图</h2>
        <span>{{ seats.length }} 个座位</span>
      </div>
      <div v-if="seats.length" class="seat-grid">
        <button v-for="seat in seats" :key="seat.id" class="seat-tile" :class="statusClass(seat.status)" @click="showDetail(seat)">
          <span class="seat-number">{{ seat.seatNumber }}</span>
          <span class="seat-name">{{ seat.name || '未命名座位' }}</span>
          <span class="seat-meta">{{ seat.area || '未分区' }} · {{ seat.capacity || 0 }} 人</span>
          <span class="seat-status">{{ statusText(seat.status) }}</span>
        </button>
      </div>
      <el-empty v-else description="暂无符合条件的座位" :image-size="96" />
    </section>

    <section class="table-section">
      <div class="section-heading"><h2>座位列表</h2></div>
      <el-table :data="seats" stripe v-loading="loading">
        <el-table-column prop="seatNumber" label="桌号" min-width="90" />
        <el-table-column prop="name" label="名称" min-width="120" />
        <el-table-column prop="area" label="区域" min-width="100" />
        <el-table-column prop="capacity" label="容量" min-width="80"><template slot-scope="scope">{{ scope.row.capacity }} 人</template></el-table-column>
        <el-table-column label="状态" min-width="110"><template slot-scope="scope"><span class="status-tag" :class="statusClass(scope.row.status)">{{ statusText(scope.row.status) }}</span></template></el-table-column>
        <el-table-column prop="updateTime" label="更新时间" min-width="160" />
        <el-table-column label="操作" width="240" fixed="right"><template slot-scope="scope">
          <el-button type="text" size="small" @click="showDetail(scope.row)">详情</el-button>
          <el-button type="text" size="small" @click="openEdit(scope.row)">编辑</el-button>
          <el-dropdown trigger="click" @command="changeStatus(scope.row, $event)">
            <el-button type="text" size="small">状态<i class="el-icon-arrow-down el-icon--right" /></el-button>
            <el-dropdown-menu slot="dropdown"><el-dropdown-item v-for="item in statuses" :key="item.value" :command="item.value" :disabled="scope.row.status === item.value">{{ item.label }}</el-dropdown-item></el-dropdown-menu>
          </el-dropdown>
          <el-button type="text" size="small" class="danger" @click="remove(scope.row)">删除</el-button>
        </template></el-table-column>
      </el-table>
    </section>

    <el-dialog :title="dialog.title" :visible.sync="dialog.visible" width="480px" @closed="resetDialog">
      <el-form ref="seatForm" :model="dialog.form" :rules="rules" label-width="90px">
        <el-form-item label="桌号" prop="seatNumber"><el-input v-model.trim="dialog.form.seatNumber" maxlength="32" /></el-form-item>
        <el-form-item label="座位名称" prop="name"><el-input v-model.trim="dialog.form.name" maxlength="64" /></el-form-item>
        <el-form-item label="区域" prop="area"><el-input v-model.trim="dialog.form.area" maxlength="64" /></el-form-item>
        <el-form-item label="容纳人数" prop="capacity"><el-input-number v-model="dialog.form.capacity" :min="1" :max="100" controls-position="right" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="dialog.form.status"><el-option v-for="item in statuses" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item label="备注"><el-input v-model.trim="dialog.form.remark" type="textarea" :rows="3" maxlength="255" show-word-limit /></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="dialog.visible = false">取消</el-button><el-button type="primary" :loading="saving" @click="save">保存</el-button></span>
    </el-dialog>

    <el-dialog title="座位详情" :visible.sync="detail.visible" width="400px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="桌号">{{ detail.seat.seatNumber }}</el-descriptions-item>
        <el-descriptions-item label="名称">{{ detail.seat.name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="区域">{{ detail.seat.area || '-' }}</el-descriptions-item>
        <el-descriptions-item label="容量">{{ detail.seat.capacity || 0 }} 人</el-descriptions-item>
        <el-descriptions-item label="状态"><span class="status-tag" :class="statusClass(detail.seat.status)">{{ statusText(detail.seat.status) }}</span></el-descriptions-item>
        <el-descriptions-item label="备注">{{ detail.seat.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import { createSeat, deleteSeat, getSeatList, updateSeat, updateSeatStatus } from '@/api/seat'

const emptyForm = () => ({ id: null, seatNumber: '', name: '', area: '', capacity: 4, status: 0, remark: '' })

@Component({ name: 'SeatManagement' })
export default class extends Vue {
  private seats: any[] = []
  private loading = false
  private saving = false
  private socket: WebSocket | null = null
  private refreshTimer: number | null = null
  private filters: any = { seatNumber: '', status: null }
  private statuses = [
    { value: 0, label: '空闲' }, { value: 1, label: '使用中' },
    { value: 2, label: '已预订' }, { value: 3, label: '清洁中' }
  ]
  private dialog: any = { title: '新增座位', visible: false, form: emptyForm() }
  private detail: any = { visible: false, seat: {} }

  get rules() {
    return {
      seatNumber: [{ required: true, message: '请输入桌号', trigger: 'blur' }],
      capacity: [{ required: true, message: '请输入容纳人数', trigger: 'change' }]
    }
  }

  mounted() {
    this.loadSeats()
    this.connectSocket()
    this.refreshTimer = window.setInterval(this.loadSeats, 30000)
  }

  beforeDestroy() {
    if (this.socket) this.socket.close()
    if (this.refreshTimer) window.clearInterval(this.refreshTimer)
  }

  private async loadSeats() {
    this.loading = true
    try {
      const response: any = await getSeatList(this.filters)
      this.seats = response.data.data || []
    } finally {
      this.loading = false
    }
  }

  private refresh() { this.loadSeats() }
  private resetFilters() { this.filters = { seatNumber: '', status: null }; this.loadSeats() }
  private filterByStatus(status: number) { this.filters.status = status; this.loadSeats() }
  private countFor(status: number) { return this.seats.filter(item => item.status === status).length }
  private statusText(status: number) { const item = this.statuses.find(item => item.value === status); return item ? item.label : '未知' }
  private statusClass(status: number) { return `status-${status}` }

  private openCreate() { this.dialog = { title: '新增座位', visible: true, form: emptyForm() } }
  private openEdit(seat: any) { this.dialog = { title: '编辑座位', visible: true, form: { ...emptyForm(), ...seat } } }
  private resetDialog() { const form: any = this.$refs.seatForm; if (form) form.clearValidate() }
  private showDetail(seat: any) { this.detail = { visible: true, seat: { ...seat } } }

  private save() {
    const form: any = this.$refs.seatForm
    form.validate(async (valid: boolean) => {
      if (!valid) return
      this.saving = true
      try {
        if (this.dialog.form.id) await updateSeat(this.dialog.form)
        else await createSeat(this.dialog.form)
        this.$message.success('保存成功')
        this.dialog.visible = false
        await this.loadSeats()
      } finally {
        this.saving = false
      }
    })
  }

  private async changeStatus(seat: any, status: number) {
    await updateSeatStatus(seat.id, status)
    this.$message.success('状态已更新')
    this.loadSeats()
  }

  private remove(seat: any) {
    this.$confirm(`确认删除座位「${seat.seatNumber}」吗？`, '删除座位', { type: 'warning' }).then(async () => {
      await deleteSeat(seat.id)
      this.$message.success('删除成功')
      this.loadSeats()
    }).catch(() => undefined)
  }

  private connectSocket() {
    const base = process.env.VUE_APP_SOCKET_URL || 'ws://localhost:8088/ws/'
    this.socket = new WebSocket(`${base}seat-management`)
    this.socket.onmessage = (event) => {
      try {
        const payload = JSON.parse(event.data)
        if (payload.type === 'seat-status-changed') this.loadSeats()
      } catch (_) { /* Ignore unrelated legacy WebSocket notifications. */ }
    }
  }
}
</script>

<style lang="scss" scoped>
.seat-page { padding: 20px; color: #273b35; }
.toolbar { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.filter-input { width: 180px; }.filter-select { width: 140px; }.toolbar-actions { margin-left: auto; }
.summary { display: grid; grid-template-columns: repeat(4, minmax(130px, 1fr)); gap: 12px; margin-bottom: 20px; }
.summary-item { display: flex; align-items: center; justify-content: space-between; min-height: 58px; padding: 12px 16px; border: 1px solid #dce5e1; border-radius: 6px; background: #fff; color: #40574f; cursor: pointer; text-align: left; }
.summary-item strong { font-size: 22px; }.layout-section, .table-section { margin-bottom: 22px; }
.section-heading { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 12px; }.section-heading h2 { margin: 0; font-size: 17px; }.section-heading span { color: #7b8c85; font-size: 13px; }
.seat-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(165px, 1fr)); gap: 12px; }
.seat-tile { min-height: 148px; padding: 15px; border: 1px solid #dce5e1; border-left-width: 5px; border-radius: 6px; background: #fff; color: #273b35; cursor: pointer; text-align: left; transition: box-shadow .2s; }.seat-tile:hover { box-shadow: 0 5px 14px rgba(26, 71, 55, .14); }
.seat-number { display: block; font-size: 23px; font-weight: 700; }.seat-name { display: block; margin-top: 5px; font-size: 14px; }.seat-meta { display: block; margin-top: 14px; color: #72847d; font-size: 12px; }.seat-status { display: inline-block; margin-top: 8px; font-size: 12px; }
.status-tag { display: inline-block; padding: 3px 8px; border-radius: 3px; font-size: 12px; }.status-0 { border-color: #26a269; }.status-0 .seat-status, .status-tag.status-0 { color: #177549; background: #e9f8ef; }.status-1 { border-color: #e08a2e; }.status-1 .seat-status, .status-tag.status-1 { color: #a85513; background: #fff3df; }.status-2 { border-color: #4e88c7; }.status-2 .seat-status, .status-tag.status-2 { color: #2165a7; background: #eaf3ff; }.status-3 { border-color: #7f8793; }.status-3 .seat-status, .status-tag.status-3 { color: #5a6470; background: #eff2f5; }
.danger { color: #d14343; }
@media (max-width: 760px) { .toolbar { flex-wrap: wrap; }.toolbar-actions { width: 100%; margin-left: 0; }.summary { grid-template-columns: repeat(2, minmax(120px, 1fr)); } }
</style>
