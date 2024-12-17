<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入标题查询" style="width: 200px" v-model="title"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>


    <div class="table">
      <el-table :data="tableData" stripe  @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="userName" label="患者姓名" show-overflow-tooltip></el-table-column>
        <el-table-column prop="doctorName" label="开药医生" show-overflow-tooltip></el-table-column>
        <el-table-column prop="medicalName" label="药品名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="price" label="费用"></el-table-column>
        <el-table-column prop="status" label="缴费状态"></el-table-column>
        <el-table-column prop="health" label="是否医保"></el-table-column>

        <el-table-column label="操作" width="180" align="center" v-if="user.role === 'ADMIN' || user.role === 'NURSE'">
          <template v-slot="scope">
            <el-button plain type="primary" @click="handleEdit(scope.row)" size="mini">编辑</el-button>
            <el-button plain type="danger" size="mini" @click=del(scope.row.id)>删除</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" v-if="user.role === 'USER'">
          <template v-slot="scope">
            <el-button plain type="success" size="mini" @click=pay(scope.row) v-if="scope.row.status === '未缴费' && scope.row.price !== 0">去缴费</el-button>
            <el-button plain type="primary" size="mini" @click=refund(scope.row) v-else-if="scope.row.status === '已缴费'">申请退费</el-button>
            <el-button plain type="warning" size="mini" v-else-if="scope.row.status === '待审核'" disabled>退费审核中</el-button>
            <el-button plain type="primary" size="mini" v-else-if="scope.row.status === '已退款'" disabled>已退款</el-button>
            <el-button plain type="danger" size="mini" v-else-if="scope.row.status === '不予退款'" disabled>不予退款</el-button>
            <el-button plain type="primary" size="mini" v-else disabled>已支付</el-button>
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


    <el-dialog title="信息" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="medicalName" label="药名">
          <el-input v-model="form.medicalName" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="price" label="费用">
          <el-input v-model="form.price" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="status" label="缴费状态">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="已缴费" value="已缴费"></el-option>
            <el-option label="未缴费" value="未缴费"></el-option>
            <el-option label="已退款" value="已退款"></el-option>
            <el-option label="不予退款" value="不予退款"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="health" label="是否医保">
          <el-select v-model="form.health" placeholder="请选择" style="width: 100%">
            <el-option label="是" value="是"></el-option>
            <el-option label="否" value="否"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>


  </div>
</template>

<script>
export default {
  name: "Medical",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      title: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        title: [
          {required: true, message: '请输入标题', trigger: 'blur'},
        ],
        content: [
          {required: true, message: '请输入内容', trigger: 'blur'},
        ]
      },
      ids: []
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    refund(row){
      this.$confirm('您确定要申请退款吗？', '确认退款', {type: "warning"}).then(() => {
        this.$request.put('medical/refund',row).then(res => {
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
    pay(row){
      let data = {
        time: row.time,
        userId: this.user.id,
        price: row.price,
      }
      this.$request.put('medical/pay' , data).then(res => {
        if(res.code === '200'){
          this.$message.success('支付成功')
          this.load(1)
        }else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAdd() {   // 新增数据
      this.form = {}  // 新增数据的时候清空数据
      this.fromVisible = true   // 打开弹窗
    },
    handleEdit(row) {   // 编辑数据
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      this.fromVisible = true   // 打开弹窗
    },
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request({
            url: this.form.id ? '/medical/update' : '/medical/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {  // 表示成功保存
              this.$message.success('保存成功')
              this.load(1)
              this.fromVisible = false
            } else {
              this.$message.error(res.msg)  // 弹出错误的信息
            }
          })
        }
      })
    },
    del(id) {   // 单个删除
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/medical/delete/' + id).then(res => {
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
    handleSelectionChange(rows) {   // 当前选中的所有的行数据
      this.ids = rows.map(v => v.id)   //  [1,2]
    },
    delBatch() {   // 批量删除
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/medical/delete/batch', {data: this.ids}).then(res => {
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
      this.$request.get('/medical/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          title: this.title,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.title = null
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
