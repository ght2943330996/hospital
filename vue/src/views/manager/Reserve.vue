<template>
  <div>
    <div class="search">
      <el-select v-model="status" placeholder="请选择挂号状态" style="width: 200px">
        <el-option label="已叫号" value="已叫号"></el-option>
        <el-option label="未叫号" value="未叫号"></el-option>
      </el-select>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>
    

    <div class="table">
      <el-table :data="tableData" stripe >
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="userName" label="患者姓名" show-overflow-tooltip></el-table-column>
        <el-table-column prop="doctorName" label="医生姓名" show-overflow-tooltip></el-table-column>
        <el-table-column prop="time" label="预约挂号时间"></el-table-column>
        <el-table-column prop="status" label="挂号状态"></el-table-column>

        <el-table-column label="操作" width="180" align="center">
          <template v-slot="scope" >
            <el-button plain type="danger" size="mini" v-if="scope.row.status === '未叫号' && user.role === 'USER'" @click=del(scope.row.id)>取消挂号</el-button>
            <el-button plain type="warning" size="mini" v-if="user.role === 'DOCTOR'"  @click=call(scope.row)>叫号</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[5, 10, 20]"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
        </el-pagination>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: "Reserve",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      status: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {},
      ids: []
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    call(row){
      let reserveData = JSON.parse(JSON.stringify(row));
      reserveData.status = '已叫号'
      this.$request.put('reserve/update', reserveData).then(res => {
        if(res.code === '200'){
          this.$message.success('叫号成功')
          this.load(1)
          //同步数据到record
          this.record(row)
        }
      })
    },
    record(row){
      let data = {
        userId:row.userId,
        doctorId:row.doctorId,
        inhospitalRecord: '否',
        comment: null,
      }
      this.$request.post('/record/add', data).then(res => {
        if(res.code === '200'){
          this.$message.success('数据同步成功')
        }else {
          this.$message.error(res.msg)
        }
      })
    },
    del(id) {   // 单个删除
      this.$confirm('您确定取消挂号吗？', '确认取消挂号', {type: "warning"}).then(() => {
        this.$request.delete('/reserve/delete/' + id).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        }).catch(error => {  // 添加请求错误处理
          console.error('退款申请失败:', error)
          this.$message.error('退款申请失败')
      })
      }).catch(() => {  // 处理用户取消操作
        this.$message.info('已取消退款申请')
      })
    },
    handleSelectionChange(rows) {   // 当前选中的所有的行数据
      this.ids = rows.map(v => v.id)   //  [1,2]
    },
    delBatch() {   // 批量删除
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/reserve/delete/batch', {data: this.ids}).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/reserve/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          status: this.status,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.status = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
  }
}
</script>

<style scoped>

</style>
