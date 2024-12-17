<template>
  <div class="welcome-container">
    <!-- 欢迎卡片 -->
    <div class="welcome-card">
      <div class="welcome-text">
        <i class="el-icon-user"></i>
        <span>您好，{{user?.role}}{{ user?.name }}！</span>
      </div>
      <div class="welcome-subtitle">欢迎使用本系统</div>
    </div>

    <!-- 公告区域 -->
    <div class="content-section">
      <div class="notice-card">
        <div class="notice-header">
          <i class="el-icon-bell"></i>
          <span>公告列表</span>
        </div>

        <div class="notice-content">
          <el-timeline reverse>
            <el-timeline-item
                v-for="item in notices"
                :key="item.id"
                :timestamp="item.time"
                :type="item.type">
              <el-popover
                  placement="top-start"
                  width="300"
                  trigger="hover"
                  popper-class="notice-popover"
                  :content="item.content">
                <div slot="reference" class="notice-item">
                  <span class="notice-title">{{ item.title }}</span>
                  <span class="notice-preview">{{ item.content.substring(0, 20) }}...</span>
                </div>
              </el-popover>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: 'Home',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      notices: []
    }
  },
  created() {
    this.$request.get('/notice/selectAll').then(res => {
      this.notices = res.data || []
    })
  }
}
</script>


<style scoped>
.welcome-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 120px);
}

.welcome-card {
  background: linear-gradient(135deg, #409EFF, #67C23A);
  color: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  margin-bottom: 25px;
}

.welcome-text {
  font-size: 24px;
  font-weight: 500;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.welcome-subtitle {
  font-size: 16px;
  opacity: 0.9;
}

.content-section {
  display: flex;
  margin: 20px 0;
}

.notice-card {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  width: 50%;
}

.notice-header {
  font-size: 20px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 25px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.notice-content {
  padding: 10px 0;
}

.notice-item {
  color: #606266;
  cursor: pointer;
  padding: 4px 0;
  transition: all 0.3s ease;
}

.notice-item:hover {
  color: #409EFF;
}

/* 修改时间线样式 */
/deep/ .el-timeline-item__node {
  background-color: #409EFF;
}

/deep/ .el-timeline-item__timestamp {
  color: #909399;
}

.notice-item {
  color: #606266;
  cursor: pointer;
  padding: 8px 12px;
  transition: all 0.3s ease;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #f5f7fa;
  margin: 4px 0;
}

.notice-item:hover {
  color: #409EFF;
  background-color: #ecf5ff;
  transform: translateX(5px);
}

.notice-title {
  font-weight: 500;
  flex: 1;
}

.notice-preview {
  color: #909399;
  font-size: 12px;
  margin-left: 15px;
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 修改弹出框样式 */
>>> .el-popover {
  margin-left: 0 !important;
  margin-top: 5px !important;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

>>> .el-timeline-item__node {
  background-color: #409EFF;
}

>>> .el-timeline-item__timestamp {
  color: #909399;
  font-size: 13px;
}

>>> .el-timeline-item__content {
  width: 100%;
}

/* 添加响应式设计 */
@media screen and (max-width: 768px) {
  .notice-card {
    width: 100%;
  }

  .notice-preview {
    display: none;
  }
}

/* 添加响应式设计 */
@media screen and (max-width: 768px) {
  .notice-card {
    width: 100%;
  }

  .welcome-text {
    font-size: 20px;
  }
}
</style>
