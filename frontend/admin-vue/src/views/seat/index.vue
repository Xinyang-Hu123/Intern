<template>
  <div class="seat-container">
    <div class="container">
      <div class="tableBar">
        <label style="margin-right: 5px">区域：</label>
        <el-input v-model="query.areaName" placeholder="区域名称" style="width: 12%" clearable />
        <label style="margin-left: 10px">座位号：</label>
        <el-input v-model="query.seatCode" placeholder="座位编码" style="width: 12%" clearable />
        <label style="margin-left: 10px">状态：</label>
        <el-select v-model="query.status" placeholder="全部" style="width: 12%" clearable>
          <el-option label="空闲" value="AVAILABLE" />
          <el-option label="使用中" value="OCCUPIED" />
          <el-option label="停用" value="DISABLED" />
        </el-select>
        <el-button class="normal-btn continue" @click="fetchData" style="margin-left: 10px">查询</el-button>
        <el-button type="primary" style="float: right" @click="openAddDialog">+ 添加座位</el-button>
        <el-button type="info" style="float: right; margin-right: 10px" @click="toggleLayout">
          {{ layoutMode ? '返回列表' : '布局视图' }}
        </el-button>
      </div>

      <el-empty v-if="!layoutMode && !seatList.length && !loading"
        description="暂无座位数据，请先登录或使用查询功能"
        style="margin: 40px 0">
      </el-empty>
      <el-table v-if="!layoutMode && seatList.length" :data="seatList" stripe class="tableBox">
        <el-table-column prop="seatCode" label="座位编码" />
        <el-table-column prop="seatName" label="座位名称" />
        <el-table-column prop="areaName" label="区域" />
        <el-table-column prop="capacity" label="人数" />
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="qrVersion" label="二维码版本" width="100" />
        <el-table-column label="操作" width="320" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="mini" :type="scope.row.status==='DISABLED'?'success':'warning'"
              @click="toggleStatus(scope.row)">{{ scope.row.status==='DISABLED'?'启用':'停用' }}</el-button>
            <el-button size="mini" type="warning" @click="regenerateQr(scope.row)">重发二维码</el-button>
            <el-button size="mini" type="info" @click="openQrPreview(scope.row)">二维码预览</el-button>
            <el-button size="mini" type="danger" @click="confirmDelete(scope.row)">停用</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="layoutMode" class="layout-wrapper">
        <div class="layout-canvas" ref="canvasRef">
          <div v-for="seat in allSeats" :key="seat.id"
            class="seat-block" :class="'seat-' + seat.status"
            :style="{ left: seat.positionX + '%', top: seat.positionY + '%' }"
            draggable="true" @dragstart="onDragStart(seat)" @dragend="onDragEnd">
            <span class="s-code">{{ seat.seatCode }}</span>
            <span class="s-name">{{ seat.seatName }}</span>
            <el-tag size="mini" :type="getStatusType(seat.status)">{{ getStatusText(seat.status) }}</el-tag>
          </div>
        </div>
        <div class="layout-actions">
          <el-button type="primary" size="small" @click="saveLayout">保存布局</el-button>
          <el-button size="small" @click="layoutMode = false">返回列表</el-button>
        </div>
      </div>

      <el-card shadow="never" style="margin-top: 20px" v-if="stats">
        <div style="display:flex;gap:30px;justify-content:center">
          <div><span style="font-size:24px;font-weight:bold">{{ stats.totalSeats || 0 }}</span><br/>总座位</div>
          <div><span style="font-size:24px;font-weight:bold;color:#67c23a">{{ stats.availableSeats || 0 }}</span><br/>空闲</div>
          <div><span style="font-size:24px;font-weight:bold;color:#e6a23c">{{ stats.occupiedSeats || 0 }}</span><br/>使用中</div>
          <div><span style="font-size:24px;font-weight:bold;color:#909399">{{ stats.disabledSeats || 0 }}</span><br/>停用</div>
        </div>
      </el-card>

      <el-pagination v-if="!layoutMode"
        @size-change="handleSizeChange" @current-change="handlePageChange"
        :current-page="query.page" :page-sizes="[10, 20, 50]" :page-size="query.pageSize"
        layout="total, sizes, prev, pager, next" :total="total"
        style="margin-top: 20px; text-align: right" />
    </div>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form ref="seatForm" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="座位编码" prop="seatCode"><el-input v-model="form.seatCode" :disabled="!!form.id" placeholder="如 A01" /></el-form-item>
        <el-form-item label="座位名称" prop="seatName"><el-input v-model="form.seatName" placeholder="如 A区 1号桌" /></el-form-item>
        <el-form-item label="区域名称" prop="areaName"><el-input v-model="form.areaName" placeholder="如 A区" /></el-form-item>
        <el-form-item label="容纳人数" prop="capacity"><el-input-number v-model="form.capacity" :min="1" :max="50" /></el-form-item>
        <el-form-item label="排序" prop="sort"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="座位二维码" :visible.sync="qrPreviewVisible" width="360px" @close="closeQrPreview">
      <div class="qr-preview">
        <div class="qr-preview-name">{{ qrPreviewSeat.seatName }}</div>
        <img v-if="qrPreviewUrl" :src="qrPreviewUrl" :alt="qrPreviewSeat.seatName + '二维码'" class="qr-preview-image" />
      </div>
      <div slot="footer">
        <el-button @click="closeQrPreview">关闭</el-button>
        <el-button type="primary" :disabled="!qrPreviewUrl" @click="downloadQr">下载</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import { getSeatList, addSeat, editSeat, deleteSeat, changeSeatStatus, getAllSeats, getSeatStatistics, regenerateQrCode, downloadQrCode } from '@/api/seat'

@Component({
  name: 'SeatManagement'
})
export default class extends Vue {
  private query: any = { areaName: '', seatCode: '', status: '', page: 1, pageSize: 10 }
  private seatList: any[] = []
  private allSeats: any[] = []
  private total = 0
  private dialogVisible = false
  private dialogTitle = '新增座位'
  private form: any = { capacity: 4, sort: 0 }
  private rules: any = {
    seatCode: [{ required: true, message: '请输入座位编码', trigger: 'blur' }],
    seatName: [{ required: true, message: '请输入座位名称', trigger: 'blur' }],
    areaName: [{ required: true, message: '请输入区域名称', trigger: 'blur' }],
    capacity: [{ required: true, message: '请输入容纳人数', trigger: 'blur' }]
  }
  private layoutMode = false
  private stats: any = null
  private draggingSeat: any = null
  private qrPreviewVisible = false
  private qrPreviewUrl = ''
  private qrPreviewSeat: any = {}

  private loading = true

  created() {
    this.fetchData()
    this.loadAllSeats()
    this.loadStats()
  }

  beforeDestroy() {
    this.revokeQrPreviewUrl()
  }

  getStatusType(s: string) {
    if (s === 'AVAILABLE') return 'success'
    if (s === 'OCCUPIED') return 'warning'
    return 'info'
  }

  getStatusText(s: string) {
    if (s === 'AVAILABLE') return '空闲'
    if (s === 'OCCUPIED') return '使用中'
    return '停用'
  }

  async fetchData() {
    try {
      const result = this.getResult(await getSeatList(this.query))
      if (result && result.code === 1) {
        this.seatList = result.data.records || []
        this.total = result.data.total || 0
        this.loading = false
      }
    } catch (err) {
      console.error('获取座位列表失败:', err)
      this.$message.error('获取座位列表失败，请检查登录状态')
    }
  }

  async loadAllSeats() {
    try {
      const result = this.getResult(await getAllSeats())
      if (result && result.code === 1) this.allSeats = result.data || []
    } catch (err) {
      console.error('获取座位数据失败:', err)
    }
  }

  async loadStats() {
    try {
      const result = this.getResult(await getSeatStatistics())
      if (result && result.code === 1) this.stats = result.data
    } catch (err) {
      console.error('获取统计数据失败:', err)
    }
  }

  handleSizeChange(size: number) { this.query.pageSize = size; this.fetchData() }
  handlePageChange(page: number) { this.query.page = page; this.fetchData() }

  openAddDialog() {
    this.dialogTitle = '新增座位'
    this.form = { capacity: 4, sort: 0 }
    this.dialogVisible = true
  }

  openEditDialog(row: any) {
    this.dialogTitle = '编辑座位'
    this.form = Object.assign({}, row)
    this.dialogVisible = true
  }

  submitForm() {
    // using this directly
    (this.$refs.seatForm as any).validate(async (valid: boolean) => {
      if (!valid) return
      try {
        const response: any = this.form.id ? await editSeat(this.form) : await addSeat(this.form)
        const result = this.getResult(response)
        this.$message.success('操作成功')
        this.dialogVisible = false
        this.fetchData()
        this.loadAllSeats()
        this.loadStats()
        if (!this.form.id && result && result.code === 1 && result.data && result.data.id) {
          await this.openQrPreview(result.data)
        }
      } catch (err) {
        console.error('座位操作失败:', err)
        this.$message.error((err && err.response && err.response.data && err.response.data.msg) || (err && err.message) || '操作失败，请检查登录状态')
      }
    })
  }

  async toggleStatus(row: any) {
    // using this directly
    const ns = row.status === 'DISABLED' ? 'AVAILABLE' : 'DISABLED'
    try {
      await changeSeatStatus({ id: row.id, status: ns })
      this.$message.success('操作成功')
      this.fetchData()
      this.loadAllSeats()
      this.loadStats()
    } catch (err) {
      console.error('\xe4\xbd\xa7\xe4\xbd\x8d\xe6\x93\x8d\xe4\xbd\x9c\xe5\xa4\xb1\xe8\xb4\xb1:', err)
      this.$message.error('\xe6\x93\x8d\xe4\xbd\x9c\xe5\xa4\xb1\xe8\xb4\xb1\xef\xbc\x8c\xe8\xaf\xb7\xe6\xa3\x80\xe6\x9f\xa5\xe7\xb9\xbb\xe5\xbd\x95\xe7\x8a\xb6\xe6\x80\x81')
      }
  }

  confirmDelete(row: any) {
    // using this directly
    this.$confirm('该座位将被停用，是否继续？', '提示').then(async () => {
      try {
        await deleteSeat({ id: row.id })
        this.$message.success('已停用')
        this.fetchData()
        this.loadAllSeats()
        this.loadStats()
      } catch (err) {
        console.error('座位操作失败:', err)
        this.$message.error((err && err.response && err.response.data && err.response.data.msg) || (err && err.message) || '操作失败，请检查登录状态')
      }
    }).catch(() => {})
  }

  async regenerateQr(row: any) {
    // using this directly
    try {
      await regenerateQrCode(row.id)
      this.$message.success('二维码已重新生成')
      this.fetchData()
    } catch (err) {
      console.error('\xe4\xbd\xa7\xe4\xbd\x8d\xe6\x93\x8d\xe4\xbd\x9c\xe5\xa4\xb1\xe8\xb4\xb1:', err)
      this.$message.error('\xe6\x93\x8d\xe4\xbd\x9c\xe5\xa4\xb1\xe8\xb4\xb1\xef\xbc\x8c\xe8\xaf\xb7\xe6\xa3\x80\xe6\x9f\xa5\xe7\xb9\xbb\xe5\xbd\x95\xe7\x8a\xb6\xe6\x80\x81')
      }
  }

  async openQrPreview(row: any) {
    try {
      this.revokeQrPreviewUrl()
      const response: any = await downloadQrCode(row.id)
      this.qrPreviewSeat = Object.assign({}, row)
      this.qrPreviewUrl = URL.createObjectURL(response.data)
      this.qrPreviewVisible = true
    } catch (err) {
      console.error('获取座位二维码失败:', err)
      this.$message.error('获取座位二维码失败')
    }
  }

  closeQrPreview() {
    this.qrPreviewVisible = false
    this.revokeQrPreviewUrl()
    this.qrPreviewSeat = {}
  }

  downloadQr() {
    if (!this.qrPreviewUrl) return
    const link = document.createElement('a')
    link.href = this.qrPreviewUrl
    link.download = this.qrPreviewSeat.seatCode + '-二维码.png'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }

  private revokeQrPreviewUrl() {
    if (this.qrPreviewUrl) {
      URL.revokeObjectURL(this.qrPreviewUrl)
      this.qrPreviewUrl = ''
    }
  }

  private getResult(response: any) {
    return response && response.data && typeof response.data.code !== 'undefined'
      ? response.data
      : response
  }

  toggleLayout() { this.layoutMode = !this.layoutMode }

  onDragStart(seat: any) { this.draggingSeat = seat }
  onDragEnd(e: any) {
    // using this directly
    if (!this.draggingSeat) return
    const canvas = this.$refs.canvasRef as HTMLElement
    if (canvas) {
      const target = e.target as HTMLElement
      const rect = target.getBoundingClientRect()
      const cRect = canvas.getBoundingClientRect()
      this.draggingSeat.positionX = parseFloat(((rect.left + rect.width / 2 - cRect.left) / cRect.width * 100).toFixed(2))
      this.draggingSeat.positionY = parseFloat(((rect.top + rect.height / 2 - cRect.top) / cRect.height * 100).toFixed(2))
    }
  }

  async saveLayout() {
    // using this directly
    for (const s of this.allSeats) {
      await editSeat({ id: s.id, positionX: s.positionX, positionY: s.positionY })
    }
    this.$message.success('布局已保存')
  }
}
</script>

<style scoped lang="scss">
.seat-container { padding: 20px; }
.layout-wrapper { position: relative; margin-top: 20px; }
.layout-canvas {
  position: relative; width: 100%; height: 500px; border: 2px dashed #dcdfe6;
  border-radius: 8px; background: #fafafa; overflow: hidden;
}
.seat-block {
  position: absolute; width: 110px; height: 70px; border-radius: 8px;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  cursor: grab; user-select: none; transform: translate(-50%, -50%); gap: 4px;
  background: #f0f9ff; border: 2px solid #409eff;
}
.seat-block.seat-AVAILABLE { background: #f0f9ff; border-color: #67c23a; }
.seat-block.seat-OCCUPIED { background: #fdf6ec; border-color: #e6a23c; }
.seat-block.seat-DISABLED { background: #f4f4f5; border-color: #909399; }
.s-code { font-weight: bold; font-size: 14px; }
.s-name { font-size: 11px; color: #666; }
.layout-actions { margin-top: 10px; text-align: right; }
.qr-preview { text-align: center; }
.qr-preview-name { margin-bottom: 12px; }
.qr-preview-image { display: block; max-width: 100%; margin: 0 auto; }
</style>
