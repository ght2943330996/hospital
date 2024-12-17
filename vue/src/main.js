import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import '@/assets/css/global.css'
import '@/assets/css/theme/index.css'
import request from "@/utils/request";

Vue.config.productionTip = false

// 将 axios 请求实例挂载到 Vue 原型上，使所有组件都能通过 this.$request 访问
Vue.prototype.$request = request
// 将基础 URL 挂载到 Vue 原型上，使所有组件都能通过 this.$baseUrl 访问
Vue.prototype.$baseUrl = process.env.VUE_APP_BASEURL

// 安装 ElementUI 组件库，并设置组件默认尺寸为 "small"
Vue.use(ElementUI, {size: "small"})

new Vue({
    router,
    render: h => h(App)
}).$mount('#app')
