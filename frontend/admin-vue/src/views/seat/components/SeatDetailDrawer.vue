<template>
  <el-drawer
    :visible="visible"
    :with-header="false"
    size="390px"
    custom-class="seat-detail-drawer"
    @close="$emit('close')"
  >
    <div class="drawer-header">
      <div>
        <span class="eyebrow">{{ seat.area || '未分区' }}</span>
        <h2>{{ seat.seatNumber || '-' }}</h2>
        <p>{{ seat.name || '未命名座位' }}</p>
      </div>
      <el-button icon="el-icon-close" circle title="关闭" @click="$emit('close')" />
    </div>

    <div class="status-panel" :class="`status-${seat.status}`">
      <i :class="statusIcon(seat.status)" />
      <div><small>当前状态</small><strong>{{ statusText(seat.status) }}</strong></div>
    </div>

    <dl class="detail-list">
      <div><dt>建议容量</dt><dd>{{ seat.capacity || 0 }} 人</dd></div>
      <div><dt>更新时间</dt><dd>{{ seat.updateTime || '-' }}</dd></div>
      <div><dt>备注</dt><dd>{{ seat.remark || '-' }}</dd></div>
    </dl>

    <section class="qr-section">
      <div class="section-title">
        <h3>桌号二维码</h3><span>{{ seat.qrCode ? '已生成' : '暂未接入' }}</span>
      </div>
      <div v-if="seat.qrCode" class="qr-content">
        <img :src="seat.qrCode" :alt="`${seat.seatNumber}二维码`">
        <a :href="seat.qrCode" target="_blank" rel="noopener">查看原图 <i class="el-icon-top-right" />
        </a>
      </div>
      <div v-else class="feature-empty">
        <i class="el-icon-picture-outline" />后端暂未提供二维码地址
      </div>
    </section>

    <section>
      <div class="section-title">
        <h3>当前用餐会话</h3><span>待接入</span>
      </div>
      <div class="feature-empty">
        <i class="el-icon-document" />当前接口未返回用餐会话和关联订单
      </div>
    </section>

    <div v-if="editable" class="drawer-actions">
      <el-button icon="el-icon-edit" @click="$emit('edit', seat)">
        编辑
      </el-button>
      <el-dropdown trigger="click" @command="$emit('status', seat, $event)">
        <el-button>更改状态<i class="el-icon-arrow-down el-icon--right" /></el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item v-for="item in statuses" :key="item.value" :command="item.value" :disabled="seat.status === item.value">
            {{ item.label }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <el-button class="danger" icon="el-icon-delete" @click="$emit('remove', seat)">
        删除
      </el-button>
    </div>
  </el-drawer>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator'
import { Seat, SEAT_STATUSES, statusIcon, statusText } from '../types'

@Component({ name: 'SeatDetailDrawer' })
export default class extends Vue {
  @Prop({ default: false }) private visible!: boolean
  @Prop({ default: () => ({ status: 0 }) }) private seat!: Seat
  @Prop({ default: false }) private editable!: boolean

  private statuses = SEAT_STATUSES
  private statusText = statusText
  private statusIcon = statusIcon
}
</script>

<style lang="scss">
.seat-detail-drawer { overflow: auto; }
.seat-detail-drawer .el-drawer__body { padding: 22px; }
.drawer-header { display: flex; align-items: flex-start; justify-content: space-between; }.drawer-header h2 { margin: 4px 0 0; color: #20352e; font-size: 28px; }.drawer-header p { margin: 4px 0 0; color: #677871; }.eyebrow { color: #71827b; font-size: 12px; }
.status-panel { display: flex; align-items: center; gap: 12px; margin: 22px 0; padding: 14px; border-left: 4px solid; border-radius: 5px; background: #f3f5f4; }.status-panel > i { font-size: 24px; }.status-panel small, .status-panel strong { display: block; }.status-panel small { color: #71827b; }.status-panel strong { margin-top: 2px; }.status-panel.status-0 { border-color: #21845a; color: #176644; background: #eaf6ef; }.status-panel.status-1 { border-color: #db7b24; color: #944b0d; background: #fff3e4; }.status-panel.status-2 { border-color: #397dbc; color: #285e8f; background: #edf5fc; }.status-panel.status-3 { border-color: #7a838c; color: #555f68; background: #f0f2f3; }
.detail-list { margin: 0 0 22px; border-top: 1px solid #e2e7e5; }.detail-list > div { display: grid; grid-template-columns: 92px 1fr; gap: 12px; padding: 12px 0; border-bottom: 1px solid #e2e7e5; }.detail-list dt { color: #71827b; }.detail-list dd { margin: 0; color: #283d36; text-align: right; word-break: break-word; }
.section-title { display: flex; align-items: baseline; justify-content: space-between; margin-top: 22px; }.section-title h3 { margin: 0 0 10px; font-size: 15px; }.section-title span { color: #8a9792; font-size: 12px; }.feature-empty { padding: 18px; border: 1px dashed #d3dbd8; border-radius: 5px; color: #7a8983; font-size: 12px; text-align: center; }.feature-empty i { margin-right: 6px; }.qr-content { display: flex; align-items: center; gap: 16px; }.qr-content img { width: 96px; height: 96px; object-fit: contain; border: 1px solid #e1e6e4; }.qr-content a { color: #267253; }
.drawer-actions { position: sticky; bottom: -22px; display: flex; gap: 8px; margin: 26px -22px -22px; padding: 14px 22px; border-top: 1px solid #e1e6e4; background: #fff; }.drawer-actions .danger { margin-left: auto; color: #c43c3c; }
@media (max-width: 560px) { .seat-detail-drawer { width: 92% !important; } }
</style>
