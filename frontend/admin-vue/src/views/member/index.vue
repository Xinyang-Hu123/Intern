<template>
  <div class="dashboard-container">
    <div class="container">
      <div class="tableBar">
        <label>会员名称：</label>
        <el-input v-model="query.name" placeholder="请输入会员名称" clearable class="query-input" @keyup.enter.native="search" />
        <label>手机号：</label>
        <el-input v-model="query.phone" placeholder="请输入手机号" clearable class="query-input" @keyup.enter.native="search" />
        <label>状态：</label>
        <el-select v-model="query.status" placeholder="请选择" clearable class="query-input">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button class="normal-btn continue" @click="search">查询</el-button>
      </div>
      <el-table :data="tableData" stripe class="tableBox">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="会员名称">
          <template slot-scope="scope">{{ scope.row.name || '微信用户' }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" />
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <div class="tableColumn-status" :class="{ 'stop-use': scope.row.status === 0 }">
              {{ scope.row.status === 0 ? '禁用' : '启用' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="totalOrderCount" label="订单数" width="100" />
        <el-table-column prop="totalAmount" label="累计消费" width="120" />
        <el-table-column prop="commentCount" label="评论数" width="100" />
        <el-table-column prop="favoriteCount" label="收藏数" width="100" />
        <el-table-column prop="lastLoginTime" label="最后登录" width="180" />
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="120" align="center">
          <template slot-scope="scope">
            <el-button type="text" class="blueBug" @click="openDetail(scope.row.id)">详情</el-button>
            <el-button type="text" :class="scope.row.status === 0 ? 'blueBug' : 'delBut'" @click="toggleStatus(scope.row)">
              {{ scope.row.status === 0 ? '启用' : '禁用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-if="counts > 10" class="pageList" :page-sizes="[10, 20, 30, 40]" :page-size="query.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="counts" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>
    <el-dialog title="会员详情" :visible.sync="detailVisible" width="520px">
      <div v-if="detail" class="member-detail">
        <div><span>会员ID</span><strong>{{ detail.id }}</strong></div>
        <div><span>会员名称</span><strong>{{ detail.name || '微信用户' }}</strong></div>
        <div><span>手机号</span><strong>{{ detail.phone || '-' }}</strong></div>
        <div><span>状态</span><strong>{{ detail.status === 0 ? '禁用' : '启用' }}</strong></div>
        <div><span>订单数</span><strong>{{ detail.totalOrderCount || 0 }}</strong></div>
        <div><span>累计消费</span><strong>{{ detail.totalAmount || 0 }}</strong></div>
        <div><span>评论数</span><strong>{{ detail.commentCount || 0 }}</strong></div>
        <div><span>收藏数</span><strong>{{ detail.favoriteCount || 0 }}</strong></div>
        <div><span>最后登录</span><strong>{{ detail.lastLoginTime || '-' }}</strong></div>
        <div><span>注册时间</span><strong>{{ detail.createTime || '-' }}</strong></div>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import { getMemberById, getMemberPage, memberStatusByStatus } from '@/api/member'

@Component({ name: 'MemberIndex' })
export default class extends Vue {
  private counts = 0
  private tableData: any[] = []
  private detailVisible = false
  private detail: any = null
  private query: any = { page: 1, pageSize: 10, name: '', phone: '', status: '' }

  created() { this.init() }

  private search() { this.query.page = 1; this.init() }

  private init() {
    getMemberPage({ ...this.query, name: this.query.name || undefined, phone: this.query.phone || undefined, status: this.query.status === '' ? undefined : this.query.status }).then(res => {
      if (res.data.code === 1) {
        this.tableData = res.data.data.records
        this.counts = Number(res.data.data.total)
      }
    })
  }

  private toggleStatus(row: any) {
    const status = row.status === 0 ? 1 : 0
    memberStatusByStatus({ id: row.id, status }).then(res => {
      if (res.data.code === 1) {
        this.$message.success('会员状态已更新')
        this.init()
      }
    })
  }

  private openDetail(id: number) {
    getMemberById(id).then(res => {
      if (res.data.code === 1) {
        this.detail = res.data.data
        this.detailVisible = true
      }
    })
  }

  private handleSizeChange(val: number) { this.query.pageSize = val; this.init() }
  private handleCurrentChange(val: number) { this.query.page = val; this.init() }
}
</script>

<style lang="scss" scoped>
.dashboard-container { margin: 30px; }
.container { background: #fff; padding: 30px 28px; border-radius: 4px; }
.tableBar { margin-bottom: 20px; label { margin: 0 10px 0 20px; &:first-child { margin-left: 0; } } }
.query-input { width: 180px; }
.normal-btn { background: #333; color: #fff; margin-left: 20px; }
.tableBox { width: 100%; border: 1px solid $gray-5; border-bottom: 0; }
.pageList { text-align: center; margin-top: 30px; }
.member-detail { border: 1px solid #ebeef5; border-bottom: 0; div { display: flex; min-height: 42px; border-bottom: 1px solid #ebeef5; span { width: 120px; padding: 12px; color: #909399; background: #f5f7fa; box-sizing: border-box; } strong { flex: 1; padding: 12px; font-weight: 400; color: #333; word-break: break-all; } } }
</style>
