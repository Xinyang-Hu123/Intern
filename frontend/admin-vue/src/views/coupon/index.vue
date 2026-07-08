<template>
  <div class="dashboard-container">
    <div class="container">
      <div class="tableBar">
        <label style="margin-right: 10px">优惠券名称：</label>
        <el-input
          v-model="query.name"
          placeholder="请输入优惠券名称"
          style="width: 180px"
          clearable
          @clear="init"
          @keyup.enter.native="initFun"
        />
        <label style="margin-right: 10px; margin-left: 20px">状态：</label>
        <el-select
          v-model="query.status"
          placeholder="请选择状态"
          clearable
          style="width: 130px"
          @change="initFun"
        >
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button class="normal-btn continue" @click="initFun">
          查询
        </el-button>
        <el-button type="primary" style="float: right" @click="openDialog()">
          + 新增优惠券
        </el-button>
      </div>

      <el-table :data="tableData" stripe v-if="tableData.length" class="tableBox">
        <el-table-column prop="name" label="优惠券名称" />
        <el-table-column label="折扣">
          <template slot-scope="scope">
            {{ scope.row.discount }} 折
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始日期" />
        <el-table-column prop="endTime" label="截止日期" />
        <el-table-column label="状态">
          <template slot-scope="scope">
            <div
              class="tableColumn-status"
              :class="{ 'stop-use': String(scope.row.status) === '0' }"
            >
              {{ String(scope.row.status) === '0' ? '禁用' : '启用' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="small" class="blueBug" @click="openDialog(scope.row)">
              修改
            </el-button>
            <el-button
              type="text"
              size="small"
              :class="scope.row.status == '1' ? 'delBut' : 'blueBug'"
              @click="statusHandle(scope.row)"
            >
              {{ scope.row.status == '1' ? '禁用' : '启用' }}
            </el-button>
            <el-button type="text" size="small" class="delBut" @click="deleteHandle(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <Empty v-else :is-search="isSearch" />

      <el-pagination
        class="pageList"
        :page-sizes="[10, 20, 30, 40]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="counts"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />

      <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="520px">
        <el-form ref="couponForm" :model="form" :rules="rules" label-width="110px">
          <el-form-item label="优惠券名称：" prop="name">
            <el-input v-model="form.name" maxlength="30" placeholder="请输入优惠券名称" />
          </el-form-item>
          <el-form-item label="折扣：" prop="discount">
            <el-input-number
              v-model="form.discount"
              :min="0.1"
              :max="10"
              :precision="1"
              :step="0.1"
              controls-position="right"
              style="width: 180px"
            />
            <span class="discount-tip">折</span>
          </el-form-item>
          <el-form-item label="有效日期：" prop="dateRange">
            <el-date-picker
              v-model="form.dateRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="截止日期"
              value-format="yyyy-MM-dd HH:mm"
              format="yyyy-MM-dd HH:mm"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="状态：">
            <el-switch
              v-model="form.status"
              :active-value="1"
              :inactive-value="0"
              active-text="启用"
              inactive-text="禁用"
            />
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submit">确定</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import Empty from '@/components/Empty/index.vue'
import {
  addCoupon,
  deleteCoupon,
  editCoupon,
  enableOrDisableCoupon,
  getCouponPage
} from '@/api/coupon'

@Component({
  name: 'Coupon',
  components: {
    Empty
  }
})
export default class extends Vue {
  private query: any = {
    name: '',
    status: undefined
  }
  private counts: number = 0
  private page: number = 1
  private pageSize: number = 10
  private tableData = []
  private isSearch: boolean = false
  private dialogVisible: boolean = false
  private dialogTitle: string = '新增优惠券'
  private form: any = this.defaultForm()
  private rules = {
    name: [
      { required: true, message: '请输入优惠券名称', trigger: 'blur' },
      { min: 2, max: 30, message: '优惠券名称为2-30个字符', trigger: 'blur' }
    ],
    discount: [
      { required: true, message: '请设置优惠券折扣', trigger: 'change' }
    ],
    dateRange: [
      { required: true, message: '请选择优惠券有效日期', trigger: 'change' }
    ]
  }

  created() {
    this.init()
  }

  private defaultForm() {
    return {
      id: undefined,
      name: '',
      discount: 8.5,
      dateRange: [],
      status: 1
    }
  }

  private initFun() {
    this.page = 1
    this.init(true)
  }

  private async init(isSearch?: boolean) {
    this.isSearch = Boolean(isSearch)
    const params = {
      page: this.page,
      pageSize: this.pageSize,
      name: this.query.name || undefined,
      status: this.query.status
    }
    await getCouponPage(params)
      .then((res: any) => {
        if (String(res.data.code) === '1') {
          this.tableData = res.data && res.data.data && res.data.data.records
          this.counts = res.data.data.total
        }
      })
      .catch((err) => {
        this.$message.error('请求出错了：' + err.message)
      })
  }

  private openDialog(row?: any) {
    this.dialogTitle = row ? '修改优惠券' : '新增优惠券'
    this.form = row
      ? {
        id: row.id,
        name: row.name,
        discount: Number(row.discount),
        dateRange: [row.startTime, row.endTime],
        status: row.status
      }
      : this.defaultForm()
    this.dialogVisible = true
    this.$nextTick(() => {
      const form: any = this.$refs.couponForm
      form && form.clearValidate()
    })
  }

  private submit() {
    const formRef: any = this.$refs.couponForm
    formRef.validate((valid: boolean) => {
      if (!valid) return
      const params = {
        id: this.form.id,
        name: this.form.name,
        discount: this.form.discount,
        startTime: this.form.dateRange[0],
        endTime: this.form.dateRange[1],
        status: this.form.status
      }
      const request = this.form.id ? editCoupon(params) : addCoupon(params)
      request
        .then((res: any) => {
          if (String(res.data.code) === '1') {
            this.$message.success(this.form.id ? '优惠券修改成功！' : '优惠券添加成功！')
            this.dialogVisible = false
            this.init()
          }
        })
        .catch((err: any) => {
          this.$message.error('请求出错了：' + err.message)
        })
    })
  }

  private statusHandle(row: any) {
    const nextStatus = String(row.status) === '1' ? 0 : 1
    this.$confirm('确认调整该优惠券状态?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      enableOrDisableCoupon({ id: row.id, status: nextStatus })
        .then((res: any) => {
          if (String(res.data.code) === '1') {
            this.$message.success('优惠券状态更改成功！')
            this.init()
          }
        })
        .catch((err: any) => {
          this.$message.error('请求出错了：' + err.message)
        })
    })
  }

  private deleteHandle(row: any) {
    this.$confirm('此操作将永久删除该优惠券，是否继续？', '确定删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      deleteCoupon(row.id)
        .then((res: any) => {
          if (String(res.data.code) === '1') {
            this.$message.success('优惠券删除成功！')
            this.init()
          }
        })
        .catch((err: any) => {
          this.$message.error('请求出错了：' + err.message)
        })
    })
  }

  private handleSizeChange(val: any) {
    this.pageSize = val
    this.init()
  }

  private handleCurrentChange(val: any) {
    this.page = val
    this.init()
  }
}
</script>

<style lang="scss" scoped>
.discount-tip {
  margin-left: 8px;
  color: #606266;
}
</style>
