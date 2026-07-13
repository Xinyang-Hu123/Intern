const assert = require('assert');
const fs = require('fs');
const path = require('path');

const vendorPath = path.resolve(__dirname, '../common/vendor.js');
const vendorContent = fs.readFileSync(vendorPath, 'utf8');

assert(
  vendorContent.includes("if(!baseUrl) baseUrl='http://10.143.6.18:8088';"),
  '菜单请求在局域网真机调试时应默认访问电脑后端地址'
);

assert(
  vendorContent.includes("if(baseUrl==='https://angry-pandas-stick.loca.lt') baseUrl='';"),
  '菜单请求不应继续使用已失效的临时 HTTPS 地址'
);

assert(
  vendorContent.includes("if(baseUrl==='https://chilly-ducks-double.loca.lt') baseUrl='';"),
  '菜单请求不应继续使用已失效的临时 HTTPS 地址'
);

assert(
  vendorContent.includes("if(baseUrl==='http://127.0.0.1:8088') baseUrl='';"),
  '菜单请求不应继续使用手机自身的回环地址'
);

console.log('vendor-server-config tests passed');
