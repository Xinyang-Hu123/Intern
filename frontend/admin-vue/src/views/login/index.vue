<template>
  <div class="login">
    <div class="login-box">
      <div class="login-visual">
        <div class="visual-brand">
          <span class="brand-mark">LS</span>
          <div>
            <strong>老宋速达</strong>
            <small>智慧配送运营平台</small>
          </div>
        </div>
        <div class="visual-panel">
          <span class="panel-kicker">Real-time Dispatch</span>
          <strong>订单、门店、菜品一屏掌控</strong>
          <p>用绿色科技感统一后台视觉，突出效率、稳定和实时运营感。</p>
        </div>
        <div class="visual-metrics">
          <span>极速履约</span>
          <span>智能调度</span>
          <span>数据看板</span>
        </div>
      </div>
      <div class="login-form">
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules">
          <div class="login-form-title">
            <span class="brand-mark small">LS</span>
            <div>
              <span class="title-label">老宋速达</span>
              <span class="title-sub">后台管理系统</span>
            </div>
          </div>
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="账号"
              prefix-icon="iconfont icon-user"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              prefix-icon="iconfont icon-lock"
              @keyup.enter.native="handleLogin"
            />
          </el-form-item>
          <el-form-item style="width: 100%">
            <el-button
              :loading="loading"
              class="login-btn"
              size="medium"
              type="primary"
              style="width: 100%"
              @click.native.prevent="handleLogin"
            >
              <span v-if="!loading">登录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator'
import { Route } from 'vue-router'
import { Form as ElForm } from 'element-ui'
import { UserModule } from '@/store/modules/user'

@Component({
  name: 'Login',
})
export default class extends Vue {
  private validateUsername = (rule: any, value: string, callback: Function) => {
    if (!value) {
      callback(new Error('请输入用户名'))
    } else {
      callback()
    }
  }
  private validatePassword = (rule: any, value: string, callback: Function) => {
    if (value.length < 6) {
      callback(new Error('密码必须在6位以上'))
    } else {
      callback()
    }
  }
  private loginForm = {
    username: 'admin',
    password: '123456',
  } as {
    username: String
    password: String
  }

  loginRules = {
    username: [{ validator: this.validateUsername, trigger: 'blur' }],
    password: [{ validator: this.validatePassword, trigger: 'blur' }],
  }
  private loading = false
  private redirect?: string

  @Watch('$route', { immediate: true })
  private onRouteChange(route: Route) {}

  // 登录
  private handleLogin() {
    ;(this.$refs.loginForm as ElForm).validate(async (valid: boolean) => {
      if (valid) {
        this.loading = true
        await UserModule.Login(this.loginForm as any)
          .then((res: any) => {
            if (String(res.code) === '1') {
              this.$router.push('/')
            } else {
              // this.$message.error(res.msg)
              this.loading = false
            }
          })
          .catch(() => {
            // this.$message.error('用户名或密码错误！')
            this.loading = false
          })
      } else {
        return false
      }
    })
  }
}
</script>

<style lang="scss">
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background:
    linear-gradient(135deg, rgba(14, 74, 59, 0.94) 0%, rgba(8, 24, 22, 0.96) 52%, rgba(18, 226, 152, 0.24) 100%),
    repeating-linear-gradient(90deg, rgba(255, 255, 255, 0.04) 0 1px, transparent 1px 56px),
    repeating-linear-gradient(0deg, rgba(255, 255, 255, 0.035) 0 1px, transparent 1px 56px);
}

.login-box {
  width: 1040px;
  height: 540px;
  border-radius: 8px;
  display: flex;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 30px 80px rgba(0, 0, 0, 0.34);
}

.login-visual {
  position: relative;
  width: 60%;
  padding: 52px;
  color: #ffffff;
  background:
    linear-gradient(145deg, rgba(6, 26, 22, 0.98) 0%, rgba(11, 78, 61, 0.96) 58%, rgba(26, 204, 137, 0.9) 100%),
    repeating-linear-gradient(135deg, rgba(255, 255, 255, 0.08) 0 1px, transparent 1px 22px);
}

.login-visual::before {
  content: '';
  position: absolute;
  inset: 28px;
  border: 1px solid rgba(122, 255, 205, 0.22);
  border-radius: 8px;
  pointer-events: none;
}

.visual-brand {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 16px;
}

.brand-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 54px;
  height: 54px;
  border-radius: 8px;
  color: #07231d;
  background: linear-gradient(135deg, #7dffcf 0%, #19d58d 100%);
  font-weight: 800;
  letter-spacing: 0;
  box-shadow: 0 10px 30px rgba(22, 181, 127, 0.34);
}

.brand-mark.small {
  width: 42px;
  height: 42px;
  margin-right: 12px;
  font-size: 15px;
}

.visual-brand strong {
  display: block;
  font-size: 28px;
  line-height: 34px;
  letter-spacing: 0;
}

.visual-brand small {
  display: block;
  margin-top: 4px;
  color: rgba(232, 255, 246, 0.76);
  font-size: 13px;
}

.visual-panel {
  position: relative;
  z-index: 1;
  margin-top: 92px;
  padding: 32px;
  border: 1px solid rgba(133, 255, 210, 0.25);
  border-radius: 8px;
  background: rgba(4, 21, 18, 0.48);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.12);
}

.visual-panel .panel-kicker {
  display: inline-block;
  margin-bottom: 18px;
  color: #78ffd0;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0;
}

.visual-panel strong {
  display: block;
  font-size: 30px;
  line-height: 40px;
  letter-spacing: 0;
}

.visual-panel p {
  width: 78%;
  margin: 16px 0 0;
  color: rgba(232, 255, 246, 0.72);
  line-height: 24px;
}

.visual-metrics {
  position: absolute;
  left: 52px;
  right: 52px;
  bottom: 52px;
  z-index: 1;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.visual-metrics span {
  padding: 14px 12px;
  border: 1px solid rgba(133, 255, 210, 0.22);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.08);
  text-align: center;
  color: rgba(255, 255, 255, 0.86);
  font-size: 13px;
}

.title {
  margin: 0px auto 10px auto;
  text-align: left;
  color: #707070;
}

.login-form {
  background: #ffffff;
  width: 40%;
  border-radius: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: inset 1px 0 0 rgba(221, 235, 230, 0.85);
  .el-form {
    width: 250px;
    height: 307px;
  }
  .el-form-item {
    margin-bottom: 30px;
  }
  .el-form-item.is-error .el-input__inner {
    border: 0 !important;
    border-bottom: 1px solid #fd7065 !important;
    background: #fff !important;
  }
  .input-icon {
    height: 32px;
    width: 18px;
    margin-left: -2px;
  }
  .el-input__inner {
    border: 0;
    border-bottom: 1px solid #dce8e4;
    border-radius: 0;
    font-size: 13px;
    font-weight: 400;
    color: #10231f;
    height: 32px;
    line-height: 32px;
    background: transparent;
  }
  .el-input__prefix {
    left: 0;
  }
  .el-input--prefix .el-input__inner {
    padding-left: 26px;
  }
  .el-input__inner::placeholder {
    color: #aeb5c4;
  }
  .el-form-item--medium .el-form-item__content {
    line-height: 32px;
  }
  .el-input--medium .el-input__icon {
    line-height: 32px;
  }
}

.login-btn {
  border-radius: 20px;
  padding: 11px 20px !important;
  margin-top: 10px;
  font-weight: 500;
  font-size: 13px;
  border: 0;
  color: #08231d;
  background: linear-gradient(135deg, #71f7c5 0%, #16b57f 100%);
  box-shadow: 0 10px 24px rgba(22, 181, 127, 0.28);
  &:hover,
  &:focus {
    background: linear-gradient(135deg, #89ffd5 0%, #1fc98f 100%);
    color: #08231d;
  }
}
.login-form-title {
  min-height: 46px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-bottom: 44px;
  .title-label {
    display: block;
    font-weight: 500;
    font-size: 24px;
    color: #10231f;
    line-height: 28px;
  }
  .title-sub {
    display: block;
    margin-top: 4px;
    font-size: 12px;
    color: #71817d;
  }
}
</style>
