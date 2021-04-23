## 1. 简介

SpringCloud项目。

项目简略文档：见 “Documents/项目需求与设计文档.docx”

### 1.1 结构

**（1）项目结构**

父工程：HostMonitor

​	|__ 子工程：hostmonitor_commons

​	|__ 子工程：hostmonitor_data_collector_9000

​	|__ 子工程：hostmonitor_web_80

**（2）模块**

hostmonitor_data_collector_9000：对host采样，数据库CRUD。位于9000端口

hostmonitor_web_80：显示web界面，位于80端口（从而可直接输入域名/ip直接访问页面）

**（3）外部依赖**

nacos：用于注册中心



## 2. 环境

### 2.1 JDK环境

JDK1.8

### 2.2 Nacos

github：https://github.com/alibaba/nacos/tree/develop

下载：https://github.com/alibaba/nacos/releases  选择 nacos-servewr-2.0.0.zip



## 3. 打包

父工程里执行maven clean 和install。会在每个子项目的target中得到对应JAR包。



## 4. 部署

**（1）先运行nacos配置中心**

进入nacos/bin，windows下以单机版运行

```
startup.cmd -m standalone
```

**（2）运行所有子项目**

```
java -jar HostMonitor_DataCollector.jar
java -jar HostMonitor_Web.jar
```

需要确保ConfigData及内部配置文件在HostMonitor_DataCollector.jar相同目录中。

**（3）查看配置中心内的服务项**

进入nacos默认网页。默认账户：nacos，密码：nacos。

```url
http://localhost:8848/nacos/#/login
```

**（4）正常执行流程**

进入网页

```
localhost
```

