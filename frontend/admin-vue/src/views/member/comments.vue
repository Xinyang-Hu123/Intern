<template>
  <div class="dashboard-container"><div class="container">
    <div class="tableBar">
      <label>会员：</label><el-input v-model="query.memberName" class="query-input" clearable placeholder="会员名称" />
      <label>商品：</label><el-input v-model="query.goodsName" class="query-input" clearable placeholder="商品名称" />
      <label>评分：</label><el-select v-model="query.rating" class="query-input" clearable placeholder="评分"><el-option v-for="item in [5,4,3,2,1]" :key="item" :label="item + '星'" :value="item" /></el-select>
      <label>状态：</label><el-select v-model="query.status" class="query-input" clearable placeholder="状态"><el-option label="展示" :value="1" /><el-option label="隐藏" :value="0" /></el-select>
      <el-button class="normal-btn continue" @click="search">查询</el-button>
    </div>
    <el-table :data="tableData" stripe class="tableBox">
      <el-table-column prop="memberName" label="会员" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="goodsName" label="商品" />
      <el-table-column prop="rating" label="评分" width="80" />
      <el-table-column prop="content" label="评论内容" min-width="220" />
      <el-table-column label="图片" width="120"><template slot-scope="scope"><el-button v-if="scope.row.images" type="text" class="blueBug" @click="openImages(scope.row.images)">查看图片</el-button><span v-else>-</span></template></el-table-column>
      <el-table-column label="状态" width="100"><template slot-scope="scope"><div class="tableColumn-status" :class="{ 'stop-use': scope.row.status === 0 }">{{ scope.row.status === 0 ? '隐藏' : '展示' }}</div></template></el-table-column>
      <el-table-column prop="createTime" label="评论时间" width="180" />
      <el-table-column label="操作" width="170" align="center"><template slot-scope="scope"><el-button type="text" class="blueBug" @click="toggleStatus(scope.row)">{{ scope.row.status === 0 ? '展示' : '隐藏' }}</el-button><el-button type="text" class="delBut" @click="remove(scope.row.id)">删除</el-button></template></el-table-column>
    </el-table>
    <el-pagination v-if="counts > 10" class="pageList" :page-sizes="[10,20,30,40]" :page-size="query.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="counts" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    <el-dialog title="评论图片" :visible.sync="imagesVisible" width="640px">
      <div v-if="imageList.length" class="comment-images">
        <el-image v-for="item in imageList" :key="item" :src="item" fit="cover" :preview-src-list="imageList" />
      </div>
      <div v-else>暂无图片</div>
    </el-dialog>
  </div></div>
</template>
<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import { deleteMemberComment, getMemberCommentPage, memberCommentStatusByStatus } from '@/api/member'
@Component({ name: 'MemberComments' })
export default class extends Vue {
  private counts = 0
  private tableData: any[] = []
  private imageList: string[] = []
  private imagesVisible = false
  private query: any = { page: 1, pageSize: 10, memberName: '', goodsName: '', rating: '', status: '' }
  created() { this.init() }
  private search() { this.query.page = 1; this.init() }
  private init() { getMemberCommentPage({ ...this.query, memberName: this.query.memberName || undefined, goodsName: this.query.goodsName || undefined, rating: this.query.rating === '' ? undefined : this.query.rating, status: this.query.status === '' ? undefined : this.query.status }).then(res => { if (res.data.code === 1) { this.tableData = res.data.data.records; this.counts = Number(res.data.data.total) } }) }
  private toggleStatus(row: any) { memberCommentStatusByStatus({ id: row.id, status: row.status === 0 ? 1 : 0 }).then(() => this.init()) }
  private remove(id: number) { this.$confirm('确认删除该评论?', '提示').then(() => deleteMemberComment(id).then(() => this.init())) }
  private openImages(images: string) { this.imageList = images.split(',').map(item => item.trim()).filter(Boolean); this.imagesVisible = true }
  private handleSizeChange(val: number) { this.query.pageSize = val; this.init() }
  private handleCurrentChange(val: number) { this.query.page = val; this.init() }
}
</script>
<style lang="scss" scoped>.dashboard-container{margin:30px}.container{background:#fff;padding:30px 28px;border-radius:4px}.tableBar{margin-bottom:20px;label{margin:0 10px 0 20px;&:first-child{margin-left:0}}}.query-input{width:180px}.normal-btn{background:#333;color:#fff;margin-left:20px}.tableBox{width:100%;border:1px solid $gray-5;border-bottom:0}.pageList{text-align:center;margin-top:30px}.comment-images{display:flex;flex-wrap:wrap;gap:12px}.comment-images .el-image{width:120px;height:120px;border-radius:4px}</style>
