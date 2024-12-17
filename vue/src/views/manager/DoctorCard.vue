<template>
  <div>
    <div class="search">
      科室
      <el-select v-model="departmentId" placeholder="请选择科室">
        <el-option
            v-for="item in departmentData"
            :key="item.id"
            :label="item.name"
            :value="item.id">
        </el-option>
      </el-select>
      时间
      <el-date-picker
          v-model="week"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="选择日期">
      </el-date-picker>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>


    <div class="table" style="padding: 15px 20px">

      <!-- 当没有数据时显示提示信息 -->
      <template>
        <div v-if="!tableData || tableData.length === 0" class="empty-container">
          <el-empty>
            <template slot="description">
              <p class="empty-text">请先选择科室和日期</p>
            </template>
          </el-empty>
        </div>
      </template>


<!--      <el-row :gutter="20">-->
<!--        <el-col :span="6" v-for="item in tableData" style="margin-bottom: 20px">-->
<!--          <div style="text-align: center; background-color: #c5cefa" class="card" >-->
<!--            <img :src="item.avatar" alt="" style="width: 100px; height: 100px;border-radius:50%">-->
<!--            <div style="font-weight: 550; margin-top: 10px">-->
<!--              {{item.name}} <span style="color: #383535; margin-left: 5px; font-weight: 500">{{item.departmentName}}</span>-->
<!--            </div>-->
<!--            <div style="margin-top: 20px; color: #353523; padding: 0 10px; text-align: left;overflow: hidden;text-overflow: ellipsis;display: -webkit-box;-webkit-box-orient: vertical;-webkit-line-clamp:4;">-->
<!--              简介：{{item.descr}}-->
<!--            </div>-->
<!--            <div style="margin-top: 15px">-->
<!--              挂号费：<span style="color: red; font-weight: 550; margin-right: 20px">￥{{item.price}}</span> 剩余：{{item.num}}-->
<!--            </div>-->
<!--            <div style="margin-top: 15px">-->
<!--              <el-button type="primary" size="mini" @click="reserve(item.id)">挂号</el-button>-->
<!--            </div>-->
<!--          </div>-->
<!--        </el-col>-->
<!--      </el-row>-->
      <template>
        <el-row :gutter="20">
          <el-col :span="6" v-for="item in tableData" style="margin-bottom: 20px">
            <div class="card">
              <img :src="item.avatar" alt="" style="width: 100px; height: 100px;border-radius:50%">
              <div style="font-weight: 550; margin-top: 10px">
                {{item.name}} <span style="color: #383535; margin-left: 5px; font-weight: 500">{{item.departmentName}}</span>
              </div>
              <div style="margin-top: 20px; color: #353523; padding: 0 10px; text-align: left;overflow: hidden;text-overflow: ellipsis;display: -webkit-box;-webkit-box-orient: vertical;-webkit-line-clamp:4;">
                简介：{{item.descr}}
              </div>
              <div style="margin-top: 15px">
                挂号费：<span style="color: red; font-weight: 550; margin-right: 20px">￥{{item.price}}</span> 剩余：{{item.num}}
              </div>
              <div style="margin-top: 15px">
                <el-button type="primary" size="mini" @click="reserve(item.id)" :disabled="item.num === 0">挂号</el-button>
              </div>
            </div>
          </el-col>
        </el-row>
      </template>


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

<!--    <div><el-input v-model="role" value="DOCTOR" v-show="false"></el-input></div>-->



  </div>
</template>

<script>

export default {     //这是代码执行的第一步
  name: "Doctor",
  // computed: {
  //   department() {
  //     return department
  //   }
  // },
  // 1. 组件创建，执行 data 函数
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      departmentId:null,
      week: null,
      fromVisible: false,
      role:'DOCTOR',
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {},
      ids: [],     //创建用于存储批量选择的ID的空数组
      departmentData: []     //创建用于存储所有部门数据的空数组，后面方便vue渲染
    }
  },
  // 2. 执行 created 钩子
  // created 是 Vue 的生命周期钩子，在组件创建时自动执行
  created() {
    this.load(1)      // 加载医生数据
    this.loaddepartment()      // 加载部门数据    //// 获取数据
  },
  methods: {
    reserve(doctorId){
      if(this.user.role !== 'USER'){
        this.$message.warning('您的角色不支持挂号操作')
        return
      }
      let data = {
        userId: this.user.id,
        doctorId: doctorId,
        week: this.week,
      }
      this.$request.post('reserve/add', data).then(res => {
        if(res.code === '200'){
          this.$message.success('挂号成功')
          this.load(1)
        }else {
          this.$message.error(res.msg)
        }
      })
    },
    // 加载部门数据的方法
    loaddepartment(){
      // 发送 GET 请求到后端获取所有部门数据
      this.$request.get('/department/selectAll').then(res =>{
        if(res.code === '200'){
          this.departmentData = res.data    // 如果请求成功，将数据保存到 departmentData
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
            url: this.form.id ? '/doctor/update' : '/doctor/add',
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
        this.$request.delete('/doctor/delete/' + id).then(res => {
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
      this.ids = rows.map(v => v.id)
    },
    delBatch() {   // 批量删除
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/doctor/delete/batch', {data: this.ids}).then(res => {
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
      this.$request.get('/doctor/selectPage2', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          role: this.role,
          departmentId: this.departmentId,
          week: this.week,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.departmentId = null
      this.week = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    handleAvatarSuccess(response, file, fileList) {
      // 把头像属性换成上传的图片的链接
      this.form.avatar = response.data
    },
  }
}
</script>
<style scoped>
.empty-container {
  text-align: center;
  padding: 80px 50px;  /* 增加垂直内边距 */
  background-color: #f8f9fa;  /* 添加背景色 */
  border-radius: 8px;  /* 圆角边框 */
}

.empty-text {
  font-size: 50px;  /* 更大的字体 */
  font-weight: 600;
  color: #409EFF;
  margin: 20px 0;
  letter-spacing: 2px;  /* 字间距 */
  /* 添加渐变色文字效果 */
  background: linear-gradient(45deg, #409EFF, #36cfc9);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  /* 添加动画效果 */
  animation: fadeIn 0.5s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}


.card {
  text-align: center;
  background-color: #c5cefa;
  padding: 20px;
  border-radius: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
  height: 380px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.15);
  background-color: #d5defa;
}

.card img {
  transition: all 0.3s ease;
}

.card:hover img {
  transform: scale(1.05);
}

.card .el-button {
  transition: all 0.3s ease;
}

.card:hover .el-button {
  transform: scale(1.05);
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.3);
}

</style>
