# 9001 配置文件
## 数据库切换
启动前修改 修改application.yml中 spring:profiles:active: prod

##采样方式切换
启动前修改 /Config/Server/Config.json
"sampleSelect" 1时使用SSH方式采样,2时使用客户端采样，3使用混合采样
使用SSH采样前需要配置好被采样节点的IP和路由信息于/ConfigData/Server/HostList.csv中
