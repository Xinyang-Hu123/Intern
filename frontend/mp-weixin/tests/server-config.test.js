const assert = require('assert');
const path = require('path');

const modulePath = path.resolve(__dirname, '../common/server-config.js');

function loadConfig(serverUrl) {
  delete require.cache[modulePath];
  global.wx = {
    getStorageSync(key) {
      return key === 'serverUrl' ? serverUrl : '';
    }
  };
  return require(modulePath);
}

assert.strictEqual(
  loadConfig('').getBaseUrl(),
  'http://10.143.6.18:8088',
  '本地真机调试未配置服务器地址时，应使用电脑的局域网后端地址'
);

assert.strictEqual(
  loadConfig('http://127.0.0.1:8088').getBaseUrl(),
  'http://10.143.6.18:8088',
  '手机端不能继续使用回环地址'
);

assert.strictEqual(
  loadConfig('http://192.168.1.20:8088/').getBaseUrl(),
  'http://192.168.1.20:8088',
  '真机配置的服务器地址应保留，并移除末尾斜杠'
);

assert.strictEqual(
  loadConfig('http://localhost:8080').getBaseUrl(),
  'http://10.143.6.18:8088',
  '旧的本机地址不能在手机端继续使用'
);

assert.strictEqual(
  loadConfig('http://192.168.31.16:8088').getBaseUrl(),
  'http://10.143.6.18:8088',
  '已失效的历史局域网地址不应继续阻断菜单和扫码'
);

assert.strictEqual(
  loadConfig('http://172.16.61.184:8088').getBaseUrl(),
  'http://10.143.6.18:8088',
  '切换热点后的旧地址不应继续阻断菜单和扫码'
);

assert.strictEqual(
  loadConfig('https://angry-pandas-stick.loca.lt').getBaseUrl(),
  'http://10.143.6.18:8088',
  '已失效的临时 HTTPS 地址不能继续阻断手机端菜单和扫码'
);

assert.strictEqual(
  loadConfig('https://chilly-ducks-double.loca.lt').getBaseUrl(),
  'http://10.143.6.18:8088',
  '已失效的临时 HTTPS 地址不能继续阻断手机端菜单和扫码'
);

console.log('server-config tests passed');
