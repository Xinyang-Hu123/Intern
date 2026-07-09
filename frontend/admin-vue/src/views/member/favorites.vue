<template>
  <div class="dashboard-container"><div class="container">
    <div class="tableBar"><label>会员：</label><el-input v-model="query.memberName" class="query-input" clearable placeholder="会员名称" /><label>商品：</label><el-input v-model="query.goodsName" class="query-input" clearable placeholder="商品名称" /><el-button class="normal-btn continue" @click="search">查询</el-button></div>
    <el-table :data="tableData" stripe class="tableBox">
      <el-table-column prop="memberName" label="会员" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="goodsName" label="收藏商品" />
      <el-table-column prop="createTime" label="收藏时间" width="180" />
      <el-table-column label="操作" width="100" align="center"><template slot-scope="scope"><el-button type="text" class="delBut" @click="remove(scope.row.id)">删除</el-button></template></el-table-column>
    </el-table>
    <el-pagination v-if="counts > 10" class="pageList" :page-sizes="[10,20,30,40]" :page-size="query.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="counts" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
  </div></div>
</template>
<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import { deleteMemberFavorite, getMemberFavoritePage } from '@/api/member'
@Component({ name: 'MemberFavorites' })
export default class extends Vue {
  private counts = 0
  private tableData: any[] = []
  private query: any = { page: 1, pageSize: 10, memberName: '', goodsName: '' }
  created() { this.init() }
  private search() { this.query.page = 1; this.init() }
  private init() { getMemberFavoritePage({ ...this.query, memberName: this.query.memberName || undefined, goodsName: this.query.goodsName || undefined }).then(res => { if (res.data.code === 1) { this.tableData = res.data.data.records; this.counts = Number(res.data.data.total) } }) }
  private remove(id: number) { this.$confirm('确认删除该收藏?', '提示').then(() => deleteMemberFavorite(id).then(() => this.init())) }
  private handleSizeChange(val: number) { this.query.pageSize = val; this.init() }
  private handleCurrentChange(val: number) { this.query.page = val; this.init() }
}
</script>
<style lang="scss" scoped>.dashboard-container{margin:30px}.container{background:#fff;padding:30px 28px;border-radius:4px}.tableBar{margin-bottom:20px;label{margin:0 10px 0 20px;&:first-child{margin-left:0}}}.query-input{width:180px}.normal-btn{background:#333;color:#fff;margin-left:20px}.tableBox{width:100%;border:1px solid $gray-5;border-bottom:0}.pageList{text-align:center;margin-top:30px}</style>
