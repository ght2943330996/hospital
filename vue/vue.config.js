const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8080,
    host: '0.0.0.0'  // 添加这一行连接外部网络
  },
  chainWebpack: config =>{
    config.plugin('html')
        .tap(args => {
          args[0].title = "医院预约挂号系统";
          return args;
        })
  }
})
