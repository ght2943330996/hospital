<template>
  <div>
    <div class="search">
      <el-date-picker
          v-model="time"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="选择日期">
      </el-date-picker>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe  @selection-change="handleSelectionChange">
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="userName" label="患者姓名" show-overflow-tooltip></el-table-column>
        <el-table-column prop="doctorName" label="医生姓名" show-overflow-tooltip></el-table-column>
        <el-table-column prop="time" label="就诊时间"></el-table-column>
        <el-table-column prop="medicalRecord" label="医嘱病例">
        <template v-slot="scope">
          <el-button type="success" size="mini" @click="viewEditor(scope.row.medicalRecord)">查看病例</el-button>
        </template>
        </el-table-column>
        <el-table-column prop="inhospital" label="是否住院"></el-table-column>
        <el-table-column prop="inhospitalRecord" label="是否住院登记"></el-table-column>
        <el-table-column prop="comment" label="评价">
          <template v-slot="scope">
            <el-button type="success" size="mini" @click="viewEditor2(scope.row.comment)">查看评价</el-button>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" align="center">
          <template v-slot="scope">
            <el-button plain type="primary" v-if="user.role === 'DOCTOR'" @click="handleEdit(scope.row)" size="mini">填写医嘱病例</el-button>
            <el-button plain type="primary" v-if="user.role === 'NURSE' && scope.row.inhospital === '是' && scope.row.inhospitalRecord === '否'" @click="registration(scope.row)" size="mini">住院登记</el-button>
            <el-button plain type="primary" v-if="user.role === 'USER' " @click="handleComment(scope.row)" size="mini">填写评价</el-button>
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


    <el-dialog title="医嘱病例填写" :visible.sync="fromVisible" width="60%" :close-on-click-modal="false" destroy-on-close @close="cancel">
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="medicalRecord" label="医嘱病例">
          <div id="editor"></div>
        </el-form-item>
        <el-form-item prop="inhospital" label="是否住院">
          <el-select v-model="form.inhospital" placeholder="请选择" style="width: 100%">
            <el-option label="是" value="是"></el-option>
            <el-option label="否" value="否"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="medical" label="用药说明">
          <el-input v-model="form.medical" placeholder="请填写药品"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取 消</el-button>
        <el-button type="primary" @click="save()">确 定</el-button>
      </div>
    </el-dialog>


    <el-dialog title="评价填写" :visible.sync="fromComVisible" width="60%" :close-on-click-modal="false" destroy-on-close @close="cancel">
      <el-form label-width="100px" style="padding-right: 50px" :model="form">
        <el-form-item prop="comment">
          <el-input type="textarea" :rows="4" v-model="form.comment" placeholder="请填写评价"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromComVisible = false">取 消</el-button>
        <el-button type="primary" @click="save2">确 定</el-button>
      </div>
    </el-dialog>



    <template>
      <el-dialog
          title="医嘱病例"
          :visible.sync="editorVisible"
          width="60%"
          :close-on-click-modal="false"
          destroy-on-close
          custom-class="medical-record-dialog">
        <div class="medical-record-content" v-html="viewContent"></div>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="editorVisible = false">关 闭</el-button>
        </div>
      </el-dialog>
    </template>


    <template>
      <el-dialog
          title="评价"
          :visible.sync="editorVisible2"
          width="60%"
          :close-on-click-modal="false"
          destroy-on-close
          custom-class="medical-record-dialog">
        <div class="medical-record-content" v-html="viewContent"></div>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="editorVisible2 = false">关 闭</el-button>
        </div>
      </el-dialog>
    </template>


  </div>
</template>

<script>
//引入富文本编辑器
import E from 'wangeditor'
// import scoped from "vue/packages/compiler-sfc/src/stylePlugins/scoped";
let editor
function initWangEditor(content){ setTimeout(() => {
  if(!editor){
    editor = new E('#editor')
    editor.config.placeholder = '请输入内容'
    editor.config.uploadFileName = 'file'
    editor.config.uploadImgServer = 'http://localhost:9090/files/wang/upload'
    editor.create()
  }
  editor.txt.html(content)
}, 0)
}


export default {
  name: "Record",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      time: null,
      fromVisible: false,
      editorVisible: false,
      editorVisible2: false,
      fromComVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        inhospital: [
          {required: true, message: '请选择是否住院', trigger: 'change'},
        ],
        medical: [
          {required: true, message: '请输入药品', trigger: 'blur'},
        ],
      },
      ids: [],
      viewContent:null,
      medical: null,
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    registration(row){
      let data = JSON.parse(JSON.stringify(row))
      data.inhospitalRecord = '是'
      this.$request.put('/record/update', data).then(res => {
        if(res.code === '200'){
          this.load(1)
          this.toRegistration(row.userId)
        }
      })
    },
    toRegistration(userId){
      let data = {
        userId: userId,
        hosStatus: '住院中'
      }
      this.$request.post('/registration/add', data).then(res => {
        if(res.code === '200'){
          this.$message.success('登记成功')
        }else {
          this.$message.error(res.msg)
        }
      })
    },
    //小bug用回调函数解决
    cancel(){
      this.fromVisible = false
      location.href = '/record'
    },
    viewEditor(content){
      this.viewContent = content
      this.editorVisible = true
    },
    viewEditor2(content){
      this.viewContent = content
      this.editorVisible2 = true
    },
    handleEdit(row) {   // 编辑数据
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      initWangEditor(this.form.medicalRecord || '')  //调用富文本编辑器函数
      this.fromVisible = true   // 打开弹窗
    },
    handleComment(row){
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      this.fromComVisible = true   // 打开弹窗
    },
    // save() {
    //   this.$refs.formRef.validate((valid) => {     //验证规则
    //     if (valid) {
    //       this.form.medicalRecord = editor.txt.html()
    //       this.$request.put('record/update', this.form).then(res => {
    //         if (res.code === '200') {
    //           this.$message.success('保存成功')
    //           this.load(1)
    //           this.toMedical(this.form)
    //           this.fromVisible = false
    //         }
    //       })
    //     }
    //   })
    // },
    // toMedical(form){
    //   let RecordData = JSON.parse(JSON.stringify(form));  //深拷贝
    //   RecordData = {
    //     userId: RecordData.userId,
    //     doctorId: RecordData.doctorId,
    //     medicalName: this.form.medical,
    //     status: "未缴费",
    //     time: RecordData.id,     //id和time作为关联
    //   }
    //   this.$request.post('/medical/add', RecordData).then(res => {
    //     if(res.code === '200'){
    //       this.$message.success('数据同步成功')
    //     }else{
    //       this.$message.error(res.msg)
    //     }
    //   })
    // },

    // 修改后 - 使用async/await确保顺序 防止异步
    async save() {
      try {
        const valid = await this.$refs.formRef.validate()
        if (valid) {
          this.form.medicalRecord = editor.txt.html()
          const res = await this.$request.put('record/update', this.form)
          if (res.code === '200') {
            this.$message.success('保存成功')
            await this.load(1)  // 等待load完成
            await this.toMedical(this.form)  // 等待toMedical完成
            this.fromVisible = false
          } else {
            this.$message.error(res.msg || '保存失败')
          }
        }
      } catch (error) {
        console.error('保存失败:', error)
        this.$message.error('操作失败')
      }
    },
// toMedical方法也加上async   防止异步
    async toMedical(form) {
      try {
        console.log('开始执行toMedical')  // 添加日志
        let RecordData = JSON.parse(JSON.stringify(form))
        RecordData = {
          userId: RecordData.userId,
          doctorId: RecordData.doctorId,
          medicalName: this.form.medical,
          status: "未缴费",
          time: RecordData.id,
        }
        console.log('准备发送数据:', RecordData)  // 添加日志

        const res = await this.$request.post('/medical/add', RecordData)
        console.log('收到响应:', res)  // 添加日志

        if (res.code === '200') {
          this.$message.success('数据同步成功')
        } else {
          throw new Error(res.msg || '同步失败')
        }
      } catch (error) {
        console.error('toMedical执行失败:', error)
        this.$message.error(error.message || '同步失败')
      }
    },
    save2() {
      this.$request.put('record/update', this.form).then(res => {
        if (res.code === '200') {
          this.$message.success('保存成功')
          this.load(1)
          this.fromComVisible = false
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    del(id) {   // 单个删除
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/record/delete/' + id).then(res => {
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
        this.$request.delete('/record/delete/batch', {data: this.ids}).then(res => {
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
      this.$request.get('/record/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          time: this.time,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.time = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
  }
}
</script>

<style scoped>
.medical-record-dialog >>> .el-dialog__header {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
  background: #f8f9fb;
}

.medical-record-dialog >>> .el-dialog__title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.medical-record-dialog >>> .el-dialog__body {
  padding: 30px;
}

.medical-record-content {
  min-height: 200px;
  line-height: 1.8;
  color: #303133;
  font-size: 14px;
  background: #fff;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

/* 设置富文本内容的样式 */
.medical-record-content >>> h1,
.medical-record-content >>> h2,
.medical-record-content >>> h3,
.medical-record-content >>> h4 {
  margin: 16px 0;
  font-weight: 600;
  color: #1f2f3d;
}

.medical-record-content >>> p {
  margin: 12px 0;
  line-height: 1.8;
}

.medical-record-content >>> ul,
.medical-record-content >>> ol {
  padding-left: 20px;
  margin: 12px 0;
}

.medical-record-content >>> li {
  margin: 4px 0;
}

.medical-record-content >>> table {
  border-collapse: collapse;
  width: 100%;
  margin: 16px 0;
}

.medical-record-content >>> th,
.medical-record-content >>> td {
  border: 1px solid #ebeef5;
  padding: 12px;
  text-align: left;
}

.medical-record-content >>> th {
  background-color: #f8f9fb;
  font-weight: 600;
}

.medical-record-content >>> img {
  max-width: 100%;
  height: auto;
  margin: 16px 0;
  border-radius: 4px;
}

.medical-record-content >>> blockquote {
  margin: 16px 0;
  padding: 16px;
  background-color: #f8f9fb;
  border-left: 4px solid #409EFF;
  color: #606266;
}

.medical-record-content >>> pre {
  background-color: #f8f9fb;
  padding: 16px;
  border-radius: 4px;
  overflow-x: auto;
}

/* 添加打印样式 */
@media print {
  .medical-record-content {
    box-shadow: none;
    padding: 0;
  }
}
</style>
