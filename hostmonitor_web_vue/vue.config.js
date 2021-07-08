module.exports = {
    devServer: {
        port: 80, // 端口
        proxy: {
            "/vue_web_api": {
                target: "http://localhost:9000", //设置调用的接口域名和端口
                changeOrigin: true, //是否跨域
                pathRewrite: {
                    "^/vue_web_api": ""
                }
            }
        }
    },
    // lintOnSave: false // 取消 eslint 验证
  }