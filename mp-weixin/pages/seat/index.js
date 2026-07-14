let API_BASE = 'http://localhost:8088'
try {
  API_BASE = wx.getStorageSync('serverUrl') || API_BASE
  if (API_BASE === 'http://localhost:8080') API_BASE = 'http://localhost:8088'
} catch (_) {}

Page({
  data: {
    loading: true,
    scannedSeatNumber: '',
    selectedSeat: null,
    seats: [],
    error: ''
  },

  onLoad(options) {
    const scene = decodeURIComponent(options.scene || options.seatNumber || '')
    this.setData({ scannedSeatNumber: scene })
    this.loadLayout(scene)
  },

  loadLayout(scannedSeatNumber) {
    const token = wx.getStorageSync('token')
    wx.request({
      url: `${API_BASE}/user/seat/layout`,
      header: token ? { token } : {},
      success: (response) => {
        const seats = response.data && response.data.data ? response.data.data : []
        const selectedSeat = seats.find(item => item.seatNumber === scannedSeatNumber) || null
        this.setData({ seats, selectedSeat, loading: false })
        if (scannedSeatNumber) this.verifyScannedSeat(scannedSeatNumber, token)
      },
      fail: () => this.setData({ loading: false, error: '无法连接服务，请确认本地后端已启动。' })
    })
  },

  verifyScannedSeat(seatNumber, token) {
    wx.request({
      url: `${API_BASE}/user/seat/scan/${encodeURIComponent(seatNumber)}`,
      header: token ? { token } : {},
      success: (response) => {
        if (response.data && response.data.code === 1) {
          this.setData({ selectedSeat: response.data.data, error: '' })
        } else {
          this.setData({ error: (response.data && response.data.msg) || '该座位当前不可用。' })
        }
      },
      fail: () => this.setData({ error: '座位校验失败，请稍后重试。' })
    })
  },

  selectSeat(event) {
    const seat = event.currentTarget.dataset.seat
    if (seat.status !== 0) {
      wx.showToast({ title: '该座位当前不可用', icon: 'none' })
      return
    }
    this.setData({ selectedSeat: seat, error: '' })
  },

  startOrdering() {
    const seat = this.data.selectedSeat
    if (!seat || seat.status !== 0) {
      wx.showToast({ title: '请选择空闲座位', icon: 'none' })
      return
    }
    wx.setStorageSync('seatNumber', seat.seatNumber)
    wx.reLaunch({ url: '/pages/index/index' })
  }
})
