let API_BASE = 'http://localhost:8088'
try {
  API_BASE = wx.getStorageSync('serverUrl') || API_BASE
  if (API_BASE === 'http://localhost:8080') API_BASE = 'http://localhost:8088'
} catch (_) {}

Page({
  data: {
    loading: true,
    submitting: false,
    seatNumber: '',
    cartItems: [],
    totalQuantity: 0,
    totalAmount: 0,
    remark: '',
    tablewareNumber: 0,
    error: ''
  },

  onLoad() {
    const seatNumber = wx.getStorageSync('seatNumber') || ''
    this.setData({ seatNumber })
    if (!seatNumber) {
      this.setData({ loading: false, error: '未识别到桌号，请重新扫码后点餐。' })
      return
    }
    this.loadCart()
  },

  request(url, method, data) {
    const token = wx.getStorageSync('token') || ''
    return new Promise((resolve, reject) => {
      wx.request({
        url: `${API_BASE}${url}`,
        method,
        data,
        header: token ? { authentication: token } : {},
        success: response => {
          const result = response.data || {}
          if (result.code === 1) {
            resolve(result)
          } else {
            reject(new Error(result.msg || '请求失败，请稍后重试。'))
          }
        },
        fail: () => reject(new Error('无法连接服务，请确认本地后端已启动。'))
      })
    })
  },

  loadCart() {
    this.setData({ loading: true, error: '' })
    this.request('/user/shoppingCart/list', 'GET')
      .then(result => {
        const cartItems = result.data || []
        const totals = cartItems.reduce((summary, item) => ({
          quantity: summary.quantity + Number(item.number || 0),
          amount: summary.amount + Number(item.amount || 0) * Number(item.number || 0)
        }), { quantity: 0, amount: 0 })
        this.setData({
          cartItems,
          totalQuantity: totals.quantity,
          totalAmount: Number(totals.amount.toFixed(2)),
          loading: false
        })
      })
      .catch(error => this.setData({ loading: false, error: error.message }))
  },

  updateRemark(event) {
    this.setData({ remark: event.detail.value })
  },

  decreaseTableware() {
    this.setData({ tablewareNumber: Math.max(0, this.data.tablewareNumber - 1) })
  },

  increaseTableware() {
    this.setData({ tablewareNumber: Math.min(10, this.data.tablewareNumber + 1) })
  },

  submitOrder() {
    if (this.data.submitting) return
    if (!this.data.seatNumber) {
      wx.showToast({ title: '桌号已失效，请重新扫码', icon: 'none' })
      return
    }
    if (!this.data.cartItems.length) {
      wx.showToast({ title: '请先添加菜品', icon: 'none' })
      return
    }

    this.setData({ submitting: true })
    this.request('/user/order/submit', 'POST', {
      seatNumber: this.data.seatNumber,
      payMethod: 1,
      amount: this.data.totalAmount,
      remark: this.data.remark,
      tablewareNumber: this.data.tablewareNumber,
      tablewareStatus: 0,
      packAmount: 0
    }).then(result => {
      const order = result.data || {}
      wx.redirectTo({
        url: `/pages/dineInSuccess/index?orderId=${order.id || ''}&orderNumber=${encodeURIComponent(order.orderNumber || '')}&seatNumber=${encodeURIComponent(this.data.seatNumber)}`
      })
    }).catch(error => {
      this.setData({ submitting: false })
      wx.showToast({ title: error.message, icon: 'none' })
    })
  },

  goBack() {
    wx.navigateBack({
      fail: () => wx.reLaunch({ url: '/pages/index/index' })
    })
  }
})
