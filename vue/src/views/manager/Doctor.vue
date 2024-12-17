<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入账号查询" style="width: 200px" v-model="username"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="primary" plain @click="handleAdd">新增</el-button>
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" strip @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="序号" width="70" align="center" sortable></el-table-column>
        <el-table-column label="头像">
          <template v-slot="scope">
            <div style="display: flex; align-items: center">
              <el-image style="width: 40px; height: 40px; border-radius: 50%" v-if="scope.row.avatar"
                        :src="scope.row.avatar" :preview-src-list="[scope.row.avatar]"></el-image>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="账号"></el-table-column>
        <el-table-column prop="name" label="姓名"></el-table-column>
        <el-table-column prop="descr" label="医生介绍" show-overflow-tooltip></el-table-column>
        <el-table-column prop="departmentName" label="科室"></el-table-column>
        <el-table-column prop="price" label="挂号费"></el-table-column>
        <el-table-column prop="phone" label="电话"show-overflow-tooltip></el-table-column>
        <el-table-column prop="email" label="邮箱"show-overflow-tooltip></el-table-column>
        <el-table-column prop="role" label="部门"></el-table-column>
        <el-table-column prop="time" label="入职时间"show-overflow-tooltip></el-table-column>
        <el-table-column prop="position" label="职位"></el-table-column>
        <el-table-column label="操作" align="center" width="180">
          <template v-slot="scope">
            <el-button size="mini" type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" plain @click="del(scope.row.id)">删除</el-button>
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


    <el-dialog title="医生" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="用户名"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="姓名"></el-input>
        </el-form-item>
        <el-form-item label="医生介绍" prop="descr">
          <el-input type="textarea" :rows="4" v-model="form.descr" placeholder="医生介绍"></el-input>
        </el-form-item>
        <el-form-item label="科室" prop="department">
          <el-select v-model="form.departmentId" placeholder="请选择科室"style="width: 100%">
            <el-option
                v-for="item in departmentData"
                :key="item.id"
                :label="item.name"
                :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="挂号费" prop="price">
          <el-input v-model="form.price" placeholder="挂号费"></el-input>
        </el-form-item>
        <el-form-item label="入职时间" prop="time">
          <el-date-picker style="width: 100%"
              v-model="form.time"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="选择日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="职位" prop="position">
          <el-input v-model="form.position" placeholder="职位"></el-input>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="电话"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="邮箱"></el-input>
        </el-form-item>
        <el-form-item label="部门" prop="department">
          <el-select v-model="form.role" placeholder="请选择变更权限角色"style="width: 100%">
            <el-option label="医生" value="DOCTOR"></el-option>
            <el-option label="财务" value="FINANCE"></el-option>
            <el-option label="护士" value="NURSE"></el-option>
            <el-option label="离职" value="NO"></el-option>
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
//解读
//v-for="item in departmentData" 中的 item 是一个临时变量名


// import department from "@/views/manager/Department.vue";    item可以随意改
// 数据结构示例：
// departmentData = [
//   { id: 1, name: '内科' },
//   { id: 2, name: '外科' },
//   { id: 3, name: '儿科' }
// ]
// <el-select v-model="form.departmentId">  <!-- form.departmentId 将保存选中项的 id -->
//   <el-option
//       v-for="item in departmentData"       <!-- 遍历每个部门 -->
//   :key="item.id"                       <!-- 每个选项的唯一标识 -->
//   :label="item.name"                   <!-- 显示的文本（如：'内科'） -->
//   :value="item.id">                    <!-- 选中后的值（如：1） -->
// </el-option>
// </el-select>

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
      username: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        username: [
          {required: true, message: '请输入账号', trigger: 'blur'},
        ]
      },
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
      this.$request.get('/doctor/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          username: this.username,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.username = null
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
//1.Vue 的主要生命周期钩子有：
// export default {
//   // 创建前
//   beforeCreate() {
//     console.log('1. 组件创建前')
//   },
//   // 创建后
//   created() {  //必须要有，这是入口
//     console.log('2. 组件创建后')
//     // 常用于：初始化数据
//   },
//   // 挂载前
//   beforeMount() {
//     console.log('3. 组件挂载前')
//   },
//   // 挂载后
//   mounted() {
//     console.log('4. 组件挂载后')
//     // 常用于：操作DOM、调用需要DOM的插件    //dom是传统获取页面元素然后修改元素对应值，但是vue有自动渲染，基本不用，除特殊情况：集成第三方库（地图、图表等），需要精确控制焦点
//   }
// }
//2.Vue 2 的响应式限制：
//对象
// methods: {
//   updateData() {
//     // ❌ 新添加的属性不是响应式的
//     this.form.newProp = 'value'
//
//     // ✅ 正确添加新属性
//     this.$set(this.form, 'newProp', 'value')
//     // 或者
//     this.form = { ...this.form, newProp: 'value' }
//   }
// }
//数组
// methods: {
//   updateArray() {
//     // ❌ 通过索引修改数组不是响应式的
//     this.departmentData[1] = newDept
//
//     // ✅ 响应式数组修改方法
//     this.departmentData.splice(1, 1, newDept)
//     // 或者
//     this.$set(this.departmentData, 1, newDept)
//   }
// }
</script>
<style scoped>

</style>
