function resolveApiBase() {
  let base = 'http://localhost:8088'
  try {
    base = wx.getStorageSync('serverUrl') || base
  } catch (_) {}

  base = String(base).replace(/\/+$/, '')
  if (base === 'http://localhost:8080' || base === 'http://127.0.0.1:8080') {
    return base.replace(':8080', ':8088')
  }
  return base || 'http://localhost:8088'
}

const API_BASE = resolveApiBase()

function isLocalBackend() {
  return /^http:\/\/(localhost|127\.0\.0\.1)(:\d+)?(?:\/|$)/.test(API_BASE)
}

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
    if (isLocalBackend()) {
      this.loginForCartAccess().then(() => this.loadCart()).catch(error => {
        this.setData({ loading: false, error: error.message })
      })
      return
    }
    this.loadCart()
  },

  loginForCartAccess() {
    const loginCode = isLocalBackend() ? 'local-acceptance-user' : ''
    const codePromise = loginCode ? Promise.resolve(loginCode) : new Promise(resolve => {
      wx.login({
        success: loginResult => resolve(loginResult.code || 'local-acceptance-user'),
        fail: () => resolve('local-acceptance-user')
      })
    })

    return codePromise.then(code => new Promise((resolve, reject) => {
      wx.request({
        url: `${API_BASE}/user/user/login`,
        method: 'POST',
        data: { code },
        success: response => {
          const result = response.data || {}
          const token = result.data && result.data.token
          if (result.code === 1 && token) {
            wx.setStorageSync('token', token)
            resolve(token)
            return
          }
          reject(new Error(result.msg || '登录初始化失败，请返回菜单后重试。'))
        },
        fail: () => reject(new Error('无法连接服务，请确认本地后端已启动。'))
      })
    }))
  },

  request(url, method, data, retried) {
    const token = wx.getStorageSync('token') || ''
    if (!token && !retried) {
      return this.loginForCartAccess().then(() => this.request(url, method, data, true))
    }

    return new Promise((resolve, reject) => {
      wx.request({
        url: `${API_BASE}${url}`,
        method,
        data,
        header: token ? { authentication: token } : {},
        success: response => {
          if (response.statusCode === 401 && !retried) {
            wx.removeStorageSync('token')
            this.loginForCartAccess()
              .then(() => this.request(url, method, data, true))
              .then(resolve)
              .catch(reject)
            return
          }
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
