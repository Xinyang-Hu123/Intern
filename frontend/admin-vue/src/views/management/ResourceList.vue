<template>
  <div class="dashboard-container">
    <div class="container">
      <div class="tableBar">
        <label style="margin-right: 10px">名称：</label>
        <el-input
          v-model="keyword"
          placeholder="请输入名称"
          style="width: 220px"
          clearable
          @clear="init"
          @keyup.enter.native="init"
        />
        <label style="margin-left: 20px; margin-right: 10px">状态：</label>
        <el-select v-model="statusFilter" placeholder="全部状态" clearable style="width: 140px" @change="init">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
        <el-button class="normal-btn continue" @click="init">查询</el-button>
        <el-button type="primary" class="add-btn" @click="openDialog()">
          新增{{ title }}
        </el-button>
      </div>

      <el-table v-if="tableData.length" :data="tableData" stripe class="tableBox">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column
          v-for="column in columns"
          :key="column.prop"
          :prop="column.prop"
          :label="column.label"
          :min-width="column.minWidth || 120"
          show-overflow-tooltip
        >
          <template slot-scope="{ row }">
            <el-tag v-if="column.prop === 'status'" :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
            <span v-else-if="Array.isArray(row[column.prop])">
              {{ row[column.prop].join('、') }}
            </span>
            <span v-else>{{ row[column.prop] }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="210" align="center">
          <template slot-scope="{ row }">
            <el-button type="text" class="blueBug" @click="openDialog(row)">
              编辑
            </el-button>
            <el-button type="text" class="blueBug" @click="toggleStatus(row)">
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
            <el-button type="text" class="delBut" @click="remove(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <Empty v-else :is-search="true" />

      <el-pagination
        v-if="counts > 10"
        class="pageList"
        :page-size="pageSize"
        layout="total, prev, pager, next, jumper"
        :total="counts"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog
      :title="form.id ? '编辑' + title : '新增' + title"
      :visible.sync="dialogVisible"
      width="520px"
    >
      <el-form label-width="110px">
        <el-form-item v-for="field in formFields" :key="field.prop" :label="field.label">
          <el-input-number
            v-if="field.type === 'number'"
            v-model="form[field.prop]"
            :min="0"
            :precision="field.precision || 0"
            style="width: 280px"
          />
          <el-switch
            v-else-if="field.type === 'status'"
            v-model="form[field.prop]"
            :active-value="1"
            :inactive-value="0"
          />
          <el-input
            v-else
            v-model="form[field.prop]"
            :placeholder="'请输入' + field.label"
            style="width: 280px"
          />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submit">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Watch, Vue } from 'vue-property-decorator'
import Empty from '@/components/Empty/index.vue'
import {
  deleteManagementResource,
  pageManagementResource,
  saveManagementResource,
  setManagementResourceStatus
} from '@/api/management'

const configs: any = {
  marketing: {
    title: '营销活动',
    columns: [
      { prop: 'name', label: '活动名称', minWidth: 140 },
      { prop: 'rule', label: '活动规则', minWidth: 140 },
      { prop: 'scopeType', label: '适用范围' },
      { prop: 'status', label: '状态', minWidth: 90 },
      { prop: 'startTime', label: '开始时间', minWidth: 150 },
      { prop: 'endTime', label: '结束时间', minWidth: 150 }
    ],
    fields: [
      { prop: 'name', label: '活动名称' },
      { prop: 'rule', label: '活动规则' },
      { prop: 'scopeType', label: '适用范围' },
      { prop: 'status', label: '状态', type: 'status' }
    ]
  },
  coupon: {
    title: '优惠券',
    columns: [
      { prop: 'name', label: '优惠券名称', minWidth: 140 },
      { prop: 'couponType', label: '类型' },
      { prop: 'thresholdAmount', label: '门槛金额' },
      { prop: 'discountAmount', label: '优惠金额' },
      { prop: 'scopeType', label: '适用范围' },
      { prop: 'status', label: '状态', minWidth: 90 }
    ],
    fields: [
      { prop: 'name', label: '优惠券名称' },
      { prop: 'couponType', label: '类型' },
      { prop: 'thresholdAmount', label: '门槛金额', type: 'number', precision: 2 },
      { prop: 'discountAmount', label: '优惠金额', type: 'number', precision: 2 },
      { prop: 'scopeType', label: '适用范围' },
      { prop: 'status', label: '状态', type: 'status' }
    ]
  },
  user: {
    title: '系统用户',
    columns: [
      { prop: 'username', label: '账号' },
      { prop: 'name', label: '姓名' },
      { prop: 'phone', label: '手机号' },
      { prop: 'roles', label: '角色' },
      { prop: 'status', label: '状态', minWidth: 90 }
    ],
    fields: [
      { prop: 'username', label: '账号' },
      { prop: 'name', label: '姓名' },
      { prop: 'phone', label: '手机号' },
      { prop: 'roles', label: '角色编码' },
      { prop: 'status', label: '状态', type: 'status' }
    ]
  },
  role: {
    title: '角色',
    columns: [
      { prop: 'roleCode', label: '角色编码' },
      { prop: 'name', label: '角色名称' },
      { prop: 'menuIds', label: '菜单ID' },
      { prop: 'status', label: '状态', minWidth: 90 }
    ],
    fields: [
      { prop: 'roleCode', label: '角色编码' },
      { prop: 'name', label: '角色名称' },
      { prop: 'status', label: '状态', type: 'status' }
    ]
  },
  menu: {
    title: '菜单权限',
    columns: [
      { prop: 'name', label: '菜单名称' },
      { prop: 'permission', label: '权限标识', minWidth: 140 },
      { prop: 'path', label: '路由地址', minWidth: 140 },
      { prop: 'component', label: '组件路径', minWidth: 160 },
      { prop: 'status', label: '状态', minWidth: 90 }
    ],
    fields: [
      { prop: 'name', label: '菜单名称' },
      { prop: 'permission', label: '权限标识' },
      { prop: 'path', label: '路由地址' },
      { prop: 'component', label: '组件路径' },
      { prop: 'sort', label: '排序', type: 'number' },
      { prop: 'status', label: '状态', type: 'status' }
    ]
  }
}

@Component({
  components: { Empty }
})
export default class extends Vue {
  private keyword = ''
  private statusFilter: any = ''
  private tableData = []
  private counts = 0
  private page = 1
  private pageSize = 10
  private dialogVisible = false
  private form: any = {}

  get resource() {
    return String(this.$route.meta.resource || 'marketing')
  }

  get config() {
    return configs[this.resource]
  }

  get title() {
    return this.config.title
  }

  get columns() {
    return this.config.columns
  }

  get formFields() {
    return this.config.fields
  }

  created() {
    this.init()
  }

  @Watch('$route')
  private onRouteChange() {
    this.keyword = ''
    this.statusFilter = ''
    this.page = 1
    this.init()
  }

  init() {
    pageManagementResource(this.resource, {
      page: this.page,
      pageSize: this.pageSize,
      name: this.keyword || undefined,
      status: this.statusFilter === '' ? undefined : this.statusFilter
    }).then((res) => {
      if (res.data.code === 1) {
        this.tableData = res.data.data.records
        this.counts = Number(res.data.data.total)
      } else {
        this.$message.error(res.data.msg)
      }
    }).catch((err) => {
      this.$message.error('请求出错了：' + err.message)
    })
  }

  openDialog(row?: any) {
    this.form = row ? { ...row } : { status: 1 }
    if (Array.isArray(this.form.roles)) {
      this.form.roles = this.form.roles.join(',')
    }
    this.dialogVisible = true
  }

  submit() {
    const data = { ...this.form }
    if (this.resource === 'user' && typeof data.roles === 'string') {
      data.roles = data.roles.split(',').map((item) => item.trim()).filter(Boolean)
    }
    saveManagementResource(this.resource, data).then((res) => {
      if (res.data.code === 1) {
        this.$message.success('操作成功')
        this.dialogVisible = false
        this.init()
      } else {
        this.$message.error(res.data.msg)
      }
    }).catch((err) => {
      this.$message.error('请求出错了：' + err.message)
    })
  }

  toggleStatus(row: any) {
    setManagementResourceStatus(this.resource, row.id, row.status === 1 ? 0 : 1)
      .then((res) => {
        if (res.data.code === 1) {
          this.$message.success('操作成功')
          this.init()
        } else {
          this.$message.error(res.data.msg)
        }
      })
      .catch((err) => {
        this.$message.error('请求出错了：' + err.message)
      })
  }

  remove(row: any) {
    this.$confirm('确认删除该记录吗？', '提示', { type: 'warning' }).then(() => {
      deleteManagementResource(this.resource, row.id).then((res) => {
        if (res.data.code === 1) {
          this.$message.success('删除成功')
          this.init()
        } else {
          this.$message.error(res.data.msg)
        }
      })
    })
  }

  handleCurrentChange(val: number) {
    this.page = val
    this.init()
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  margin: 30px;
  min-height: 700px;

  .container {
    background: #fff;
    padding: 30px 28px;
    border-radius: 4px;
  }

  .tableBar {
    margin-bottom: 20px;
  }

  .normal-btn {
    background: #333333;
    color: white;
    margin-left: 20px;
  }

  .add-btn {
    margin-left: 12px;
  }

  .tableBox {
    width: 100%;
    border: 1px solid $gray-5;
    border-bottom: 0;
  }

  .pageList {
    text-align: center;
    margin-top: 30px;
  }
}
</style>
