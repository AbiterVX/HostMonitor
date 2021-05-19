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

hostmonitor_commons：公共对象模块，若工程公共对象过多会移动到common并集中打包（目前暂时不用）

hostmonitor_data_collector_9000：对host采样，数据库CRUD。位于9000端口

hostmonitor_web_80：显示web界面，位于80端口（从而可直接输入域名/ip直接访问页面）

**（3）外部依赖**

nacos：用于注册中心（可选项）



## 2. 环境

### 2.1 JDK环境

JDK1.8

### 2.2 Nacos

github：https://github.com/alibaba/nacos/tree/develop

下载：https://github.com/alibaba/nacos/releases  选择 nacos-servewr-2.0.0.zip



## 3. 打包

父工程里执行maven clean 和install。会在每个子项目的target中得到对应JAR包。



## 4. 部署

**（1）先运行nacos注册中心**

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

**（3）查看注册中心内的服务项**

进入nacos默认网页。默认账户：nacos，密码：nacos。

```url
http://localhost:8848/nacos/#/login
```

**（4）正常执行流程**

进入网页

```
localhost
```

windows 可能会存在 IIS占用80端口，需要关闭功能

https://www.laoliang.net/jsjh/technology/4016.html



## 5.注册中心

​	项目默认不使用Nacos，当然由于未移除@EnableDiscoveryClient等配置，会导致运行报错，但不影响项目正常执行。

（1）若需开启注册中心，需要修改hostmonitor_web_80中的配置

​	application.yml：data_collector_service字段，改为名字

​	ApplicationContextBean：使用@LoadBalanced

## 6.Todo

#### v1.0

单个SpringBoot项目demo。具有简单框架，支持demo阶段业务功能。

能够使用ssh远程连接并执行shell。实现数据库存取。前端简单界面显示。

#### v2.0

springcloud项目，需求较为明确。（current version）

以具体需求划分相应界面。实现简单的部署与演示。

#### v2.1

前端界面完善，完善数据库存取过程。

**（1）basic**

HomePage：时间段折线图显示

​						后端：返回最近时间段内的数据

​									实时数据存入数据库

HostInfo：时间段折线图显示

​					后端：返回最近时间段内的数据

ProcessIOInfo：实时显示进程情况

​							后端：返回实时进程情况

**（2）low**

HostConfig：修改Host配置文件

​						读取修改excel文件。

​						后端，Host配置文件CRUD，配置文件写入同步修改。

IOTest：点击进行IO测试

​				后端，返回IO测试结果

**（3）故障预测-工作流程整合**

Host采样部分整合。定期采样Host故障预测所需数据。

DiskFailurePredict：获取Host及其磁盘的故障预测结果，查看磁盘故障预测历史折线图

​									后端：磁盘故障预测结果存入数据库。

​												获取最近数据，获取最近时间段数据。

DiskFailurePredictConfig：重新进行故障预测。

​												后端：调用故障预测代码。

#### v2.2

web后端内数据缓存。

#### v3.0

**hostmonitor_client:** 实现数据分离采样，在windows/Linux环境下的Host本地定时采样数据，并使用socket远程发送数据。

使用OSHI进行硬件采样。



