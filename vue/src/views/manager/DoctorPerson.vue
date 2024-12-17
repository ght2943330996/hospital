<template>
  <div>
    <el-card style="width: 50%">
      <el-form :model="user" label-width="100px" style="padding-right: 50px">
        <div style="margin: 15px; text-align: center">
          <el-upload
              class="avatar-uploader"
              :action="$baseUrl + '/files/upload'"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
          >
            <img v-if="user.avatar" :src="user.avatar" class="avatar" />
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </div>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="user.username" placeholder="用户名" disabled></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="user.name" placeholder="姓名"></el-input>
        </el-form-item>
        <el-form-item label="介绍" prop="descr">
          <el-input type="textarea" :rows="3" v-model="user.descr" placeholder="介绍"></el-input>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="user.phone" placeholder="电话"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="user.email" placeholder="邮箱"></el-input>
        </el-form-item>
        <el-form-item label="科室" prop="departmentName">
          <el-input v-model="user.departmentName" disabled></el-input>
        </el-form-item>
        <el-form-item label="挂号费" prop="department">
          <el-input v-model="user.price" disabled></el-input>
        </el-form-item>
        <el-form-item label="入职时间" prop="time">
          <el-input v-model="user.time" disabled></el-input>
        </el-form-item>
        <el-form-item label="职位" prop="position">
          <el-input v-model="user.position" disabled></el-input>
        </el-form-item>
        <div style="text-align: center; margin-bottom: 20px">
          <el-button type="primary" @click="update">保 存</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
/*
- class="avatar-uploader": 自定义样式类名
- :action: 上传到后端的地址
- :show-file-list: 是否显示文件列表
- :on-success: 上传成功后的回调函数

v-if和v-else是条件判断，:src中的:是动态绑定，可自动解析对应的值而不是纯文本
el-icon-plus内置样式（加号字符）+avatar-uploader-icon自定义样式

prop绑定自定义的定义验证规则
v-model双向绑定,当选择某个选项时，被选中选项的 value 值会自动赋值给绑定值

el-form        // 表单容器
el-form-item   // 表单项
el-input       // 输入框
el-button      // 按钮
el-select      // 下拉选择框
*/

export default {  //导出模块语法
  name: "DoctorPerson",
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}')
    }
  },
  created() {

  },
  methods: {
    update() {
      // 保存当前的用户信息到数据库   实际完整地址前面已配置http://localhost:8080/doctor/update
      this.$request.put('/doctor/update', this.user).then(res => {   //发送请求到后端// 处理后端响应res储存
        if (res.code === '200') {             // 判断是否成功
          this.$message.success('保存成功')     // 显示成功提示

          // 更新浏览器缓存里的用户信息
          localStorage.setItem('xm-user', JSON.stringify(this.user))

          // 触发父级的数据更新
          this.$emit('update:user')
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAvatarSuccess(response, file, fileList) {
      // 把user的头像属性换成上传的图片的链接
      this.$set(this.user, 'avatar', response.data)
      // this.user: 要更新的对象
      // 'avatar': 要更新的属性名
      // response.data: 新的头像链接
      //普通赋值：在笔记本上写字
      //$set：在笔记本上写字，并确保所有看这个笔记本的人都能看到更
    },
  }
}
</script>

<style scoped>
/deep/.el-form-item__label {
  font-weight: bold;
}
/deep/.el-upload {
  border-radius: 50%;
}
/deep/.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  border-radius: 50%;
}
/deep/.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
  border-radius: 50%;
}
.avatar {
  width: 120px;
  height: 120px;
  display: block;
  border-radius: 50%;
}
</style>