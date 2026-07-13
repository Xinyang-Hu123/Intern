const assert = require('assert');
const fs = require('fs');
const path = require('path');

const pagePath = path.resolve(__dirname, '../pages/scanSeat/scanSeat.js');
const page = fs.readFileSync(pagePath, 'utf8');

assert(
  page.includes("'content-type': 'text/plain;charset=UTF-8'"),
  '扫码解析接口必须以纯文本发送二维码内容，不能按 JSON 发送'
);

assert(
  page.includes('res.data && res.data.code === 1'),
  '扫码接口异常响应时页面应安全处理，不应直接读取空响应'
);

console.log('scan-seat-request tests passed');
