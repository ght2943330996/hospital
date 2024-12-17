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
        <el-form-item label="电话" prop="phone">
          <el-input v-model="user.phone" placeholder="电话"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="user.email" placeholder="邮箱"></el-input>
        </el-form-item>
        <el-form-item label="余额" prop="account">
          <el-input v-model="user.account" disabled></el-input>
        </el-form-item>
        <div style="text-align: center; margin-bottom: 20px">
          <el-button type="primary" @click="update">保 存</el-button>
          <el-button type="warning" @click="recharge">充 值</el-button>
        </div>
      </el-form>
    </el-card>
<!--    <el-dialog title="充值金额" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>-->
<!--      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">-->
<!--        <el-form-item prop="title" label="输入金额">-->
<!--          <el-input v-model="account" autocomplete="off"></el-input>-->
<!--        </el-form-item>-->
<!--      </el-form>-->
<!--      <div slot="footer" class="dialog-footer">-->
<!--        <el-button @click="fromVisible = false">取 消</el-button>-->
<!--        <el-button type="primary" @click="save">确 定</el-button>-->
<!--      </div>-->
<!--    </el-dialog>-->


    <!-- 修改充值对话框部分 -->
    <el-dialog title="充值" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">
        <!-- 金额输入 -->
        <el-form-item prop="amount" label="充值金额">
          <el-input v-model="form.amount" placeholder="请输入充值金额">
            <template slot="append">元</template>
          </el-input>
        </el-form-item>

        <!-- 快捷金额选择 -->
        <el-form-item label="快捷金额">
          <el-radio-group v-model="form.amount">
            <el-radio-button label="50">50元</el-radio-button>
            <el-radio-button label="100">100元</el-radio-button>
            <el-radio-button label="200">200元</el-radio-button>
            <el-radio-button label="500">500元</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <!-- 支付方式 -->
        <el-form-item prop="payType" label="支付方式">
          <div class="pay-methods">
            <div
                class="pay-method-item"
                :class="{ active: form.payType === 'wechat' }"
                @click="form.payType = 'wechat'"
            >
              <i class="el-icon-money" style="color: #67C23A; font-size: 20px;"></i>
              <span>微信支付</span>
            </div>
            <div
                class="pay-method-item"
                :class="{ active: form.payType === 'alipay' }"
                @click="form.payType = 'alipay'"
            >
              <i class="el-icon-money" style="color: #409EFF; font-size: 20px;"></i>
              <span>支付宝支付</span>
            </div>
          </div>
        </el-form-item>

        <!-- 二维码区域 -->
        <el-form-item v-if="form.amount && form.payType" label="支付二维码">
          <div class="qr-code-container">
            <div class="qr-code-box">
              <img src="@/assets/imgs/pay.jpg" alt="支付二维码" class="qr-code">
              <div class="qr-tip">
                <p>请使用{{ form.payType === 'wechat' ? '微信' : '支付宝' }}扫码支付</p>
                <p class="amount">￥{{ form.amount }}</p>
              </div>
            </div>
          </div>
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
  name: "UserPerson",
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      fromVisible: false,
      account: null,

      form: {
        amount: '',
        payType: 'wechat'
      },
      rules: {
        amount: [
          { required: true, message: '请输入充值金额', trigger: 'blur' },
          { pattern: /^[1-9]\d*(\.\d{1,2})?$/, message: '请输入正确的金额格式', trigger: 'blur' }
        ],
        payType: [
          { required: true, message: '请选择支付方式', trigger: 'change' }
        ]
      }

    }
  },
  created() {
    this.getPerson()  //实时更新
  },
  methods: {
    getPerson(){
      this.$request.get('user/selectById/' + this.user.id).then(res => {    //退款的实时更新
        if(res.code === '200'){
          this.user = res.data
        }else{
          this.$message.error(res.msg)
        }
      })
    },
    recharge(){
      this.account = 100
      this.fromVisible = true
    },
    // save() {
    //   if(!this.account){
    //     this.$message.warning('请输入充值金额')
    //     return
    //   }
    //   this.user.account = parseFloat(this.user.account) + parseFloat(this.account)
    //   this.update()
    // },

    save() {
      this.$refs['formRef'].validate((valid) => {
        if (valid) {
          // 这里可以调用支付接口
          this.user.account = parseFloat(this.user.account) + parseFloat(this.form.amount)
          this.update()
        }
      })
    },

    update() {
      // 保存当前的用户信息到数据库   实际完整地址前面已配置http://localhost:8080/user/update
      this.$request.put('/user/update', this.user).then(res => {   //发送请求到后端// 处理后端响应res储存
        if (res.code === '200') {             // 判断是否成功
          this.fromVisible = false   //输入框消失
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



.pay-methods {
  display: flex;
  gap: 20px;
}

.pay-method-item {
  flex: 1;
  height: 50px;
  border: 1px solid #DCDFE6;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.pay-method-item:hover {
  border-color: #409EFF;
}

.pay-method-item.active {
  border-color: #409EFF;
  background-color: #F5F7FA;
}

.qr-code-container {
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.qr-code-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.qr-code {
  width: 200px;
  height: 200px;
  object-fit: contain;
}

.qr-tip {
  color: #606266;
  text-align: center;
}

.amount {
  font-size: 20px;
  color: #F56C6C;
  font-weight: bold;
  margin-top: 5px;
}
</style>


