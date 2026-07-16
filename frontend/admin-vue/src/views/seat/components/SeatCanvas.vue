<template>
  <div
    ref="canvas"
    class="seat-canvas"
    :class="{ 'is-editing': editable, 'is-empty': !seats.length }"
    role="region"
    aria-label="餐厅座位布局"
  >
    <div class="canvas-grid" aria-hidden="true" />
    <div v-if="!seats.length" class="empty-state">
      <i class="el-icon-tableware" />
      <span>当前条件下暂无座位</span>
    </div>
    <button
      v-for="seat in seats"
      :key="seat.id"
      class="seat-node"
      :class="[`status-${seat.status}`, { 'is-dragging': draggingId === seat.id }]"
      :style="positionStyle(seat.id)"
      :aria-label="`${seat.seatNumber}，${statusText(seat.status)}`"
      @click="handleSelect(seat)"
      @pointerdown="startDrag($event, seat)"
    >
      <span class="seat-node__topline">
        <strong>{{ seat.seatNumber }}</strong>
        <i :class="statusIcon(seat.status)" aria-hidden="true" />
      </span>
      <span class="seat-node__name">{{ seat.name || '未命名座位' }}</span>
      <span class="seat-node__meta">{{ seat.capacity || 0 }} 人</span>
      <span class="seat-node__status">{{ statusText(seat.status) }}</span>
    </button>
    <div v-if="editable" class="edit-hint">
      <i class="el-icon-rank" /> 拖动座位调整布局
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator'
import { clampPercent, Seat, SeatPositionMap, snapPercent, statusIcon, statusText } from '../types'

@Component({ name: 'SeatCanvas' })
export default class extends Vue {
  @Prop({ default: () => [] }) private seats!: Seat[]
  @Prop({ default: () => ({}) }) private positions!: SeatPositionMap
  @Prop({ default: false }) private editable!: boolean

  private draggingId: number | null = null
  private moved = false

  private statusText = statusText
  private statusIcon = statusIcon

  private positionStyle(id: number) {
    const position = this.positions[String(id)] || { x: 50, y: 50 }
    return { left: `${position.x}%`, top: `${position.y}%` }
  }

  private handleSelect(seat: Seat) {
    if (!this.moved) this.$emit('select', seat)
    this.moved = false
  }

  private startDrag(event: PointerEvent, seat: Seat) {
    if (!this.editable || event.button !== 0) return
    event.preventDefault()
    this.draggingId = seat.id
    this.moved = false
    window.addEventListener('pointermove', this.onDrag)
    window.addEventListener('pointerup', this.stopDrag, { once: true })
  }

  private onDrag(event: PointerEvent) {
    if (this.draggingId === null) return
    const canvas = this.$refs.canvas as HTMLElement
    const rect = canvas.getBoundingClientRect()
    const x = snapPercent(clampPercent(((event.clientX - rect.left) / rect.width) * 100))
    const y = snapPercent(clampPercent(((event.clientY - rect.top) / rect.height) * 100, 10))
    this.moved = true
    this.$emit('move', { id: this.draggingId, position: { x, y } })
  }

  private stopDrag() {
    this.draggingId = null
    window.removeEventListener('pointermove', this.onDrag)
  }

  beforeDestroy() {
    window.removeEventListener('pointermove', this.onDrag)
    window.removeEventListener('pointerup', this.stopDrag)
  }
}
</script>

<style lang="scss" scoped>
.seat-canvas { position: relative; width: 100%; min-height: 520px; overflow: hidden; border: 1px solid #d9e0dd; border-radius: 6px; background: #f5f7f6; }
.canvas-grid { position: absolute; inset: 0; background-image: linear-gradient(#dfe5e2 1px, transparent 1px), linear-gradient(90deg, #dfe5e2 1px, transparent 1px); background-size: 32px 32px; opacity: .48; }
.seat-node { position: absolute; width: 132px; height: 96px; padding: 10px 12px; transform: translate(-50%, -50%); border: 1px solid #cbd6d1; border-left-width: 5px; border-radius: 6px; background: #fff; color: #253b34; text-align: left; cursor: pointer; box-shadow: 0 2px 8px rgba(35, 55, 48, .08); transition: box-shadow .16s, transform .16s; touch-action: none; }
.seat-node:hover, .seat-node:focus { z-index: 2; outline: none; box-shadow: 0 7px 18px rgba(35, 55, 48, .18); transform: translate(-50%, -50%) translateY(-2px); }
.seat-node.is-dragging { z-index: 4; cursor: grabbing; box-shadow: 0 10px 24px rgba(35, 55, 48, .24); transition: none; }
.seat-node__topline { display: flex; align-items: center; justify-content: space-between; }.seat-node__topline strong { font-size: 19px; }.seat-node__name { display: block; margin-top: 3px; overflow: hidden; color: #52645e; font-size: 12px; text-overflow: ellipsis; white-space: nowrap; }.seat-node__meta, .seat-node__status { display: inline-block; margin-top: 10px; font-size: 11px; }.seat-node__status { float: right; padding: 2px 5px; border-radius: 3px; }
.status-0 { border-left-color: #21845a; }.status-0 .seat-node__status { color: #176644; background: #e5f5ec; }.status-1 { border-left-color: #db7b24; }.status-1 .seat-node__status { color: #9a4d0b; background: #fff0dc; }.status-2 { border-left-color: #397dbc; }.status-2 .seat-node__status { color: #245d91; background: #e8f2fc; }.status-3 { border-left-color: #7a838c; }.status-3 .seat-node__status { color: #555f68; background: #eceff1; }
.is-editing .seat-node { cursor: grab; }.edit-hint { position: absolute; right: 12px; bottom: 12px; padding: 7px 10px; border: 1px solid #d6dedb; border-radius: 4px; background: rgba(255, 255, 255, .94); color: #667871; font-size: 12px; }.empty-state { position: absolute; inset: 0; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 12px; color: #84928d; }.empty-state i { font-size: 34px; }
@media (max-width: 760px) { .seat-canvas { min-width: 680px; min-height: 460px; }.seat-node { width: 118px; height: 88px; }.edit-hint { display: none; } }
</style>
