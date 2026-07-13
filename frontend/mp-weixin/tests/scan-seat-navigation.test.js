const assert = require('assert');
const fs = require('fs');
const path = require('path');

const pagePath = path.resolve(__dirname, '../pages/scanSeat/scanSeat.js');
const page = fs.readFileSync(pagePath, 'utf8');

assert(
  page.includes("wx.redirectTo({\n            url: '/pages/index/index'"),
  '确认座位成功后必须跳转回点餐菜单页'
);

assert(
  !page.includes("wx.switchTab({\n            url: '/pages/index/index'"),
  '点餐菜单不是底部 Tab 页，不能使用 switchTab 跳转'
);

console.log('scan-seat-navigation tests passed');
