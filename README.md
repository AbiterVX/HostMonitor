## 1. 简介

SpringCloud项目。

项目简略文档：见 “Documents/项目需求与设计文档.docx”

### 1.1 结构

#### 1.1.1 项目结构

父工程：HostMonitor

​	|__ 子工程：hostmonitor_commons

​	|__ 子工程：hostmonitor_data_collector_9000

​	|__ 子工程：hostmonitor_data_collector_9001

​	|__ 子工程：hostmonitor_web_80

​	|__ 子工程：hostmonitor_client

#### 1.1.2 模块

**hostmonitor_commons：**公共对象模块，若工程公共对象过多会移动到common并集中打包（目前暂时不用）

**hostmonitor_data_collector_9000：**通过socket接受client的采样数据，数据库CRUD。位于9000端口

**hostmonitor_data_collector_9001：集成了通过socket接受client的采样数据的采样方式和通过SSH方式链接client采样的方式， 数据库CRUD。
位于9001端口，其中socket采样方式与9000相同，如果使用SSH采样方式无需使用client端，且无需安装java、python依赖，与9001端相关的
其他配置信息可见该模块中的README文件

**hostmonitor_web_80：**显示web界面，位于80端口（从而可直接输入域名/ip直接访问页面），向9000请求数据，具有数据缓存功能。

**hostmonitor_client：**运行在被监控节点上，用于采样数据并通过socket发送到hostmonitor_data_collector_9000。

#### 1.1.3 外部依赖

nacos：用于注册中心（可选项）

#### 1.1.4 配置文件

##### 1.1.4.1 CentralizedConfigData（暂不用）

中心式配置文件（目前为离散式）

##### 1.1.4.2 ConfigData

配置文件

（1）HostList.csv  ：data_collector_9001通过SSH采样时，需要连接的被监测节点的信息

（2）Config.json ：数据采样格式

（3）Proxy.csv ：如果HostList中的节点链接需要使用代理，则将代理信息填于此表

（4）SystemSetting.json :与系统采样和报告相关的参数信息

​		 windows：安装同级目录下的smartmontools-7.2-1.win32-setup.exe

​		 Linux：apt-get install smartmontools  或  yum install smartmontools

​		 在windows上安装smartmontools之后，包含 smartctl.exe必须添加到系统路径，可能需重启。

##### 1.1.4.3 DiskPredict

磁盘故障预测文件

**（1）client：**

​		 **SampleData：**采样后的数据以及title.csv（采样表头），的存放位置。

​		 **data_collector.py：**故障预测采样程序。

**（2）DiskPredictModule：**

​		故障预测python文件。进行模型训练、预测、数据处理等操作时会通过java 调用python。

​		**/Lib/site-packages：**运行py文件所需的依赖库文件，为方便配置可直接在python/Lib/site-packages下解压

**（3）original_data：**故障预测原数据文件。需要将采样的数据放置此处。可参考original_data_test的格式

**（4）original_data_test：**用于测试的数据

**（5）processed_data：**数据预处理后的文件

**（6）train_data：**预处理后的训练文件

**（7）models：**训练好的模型文件

**（8）predict_data：**预测文件，当用户需要进行磁盘故障预测时，将当前的磁盘采样数据放置到此文件夹下。

**（9）result：**预测文件的结果，会根据当前预测的时间放在对应时间格式（年/月）下的文件夹里，结果文件与预测文件同名。





## 2. 环境

### 2.1 host客户端

**（1） JDK1.8**

**（2）python3.7**

**（3）python库：**pySMART

**（4）smartmontools：** 根据操作系统安装 smartmontools-7.2-1.win32-setup.exe 或 apt-get install smartmontools

### 2.2 服务端

**（1） JDK1.8**

**（2） Nacos（暂不用）**

​		github：https://github.com/alibaba/nacos/tree/develop

​		下载：https://github.com/alibaba/nacos/releases  选择 nacos-servewr-2.0.0.zip

**（3）python3.7**（3.7.6）

**（4）python库：**scipy，numpy，sklearn，pandas，prettytable等。

可以将打包好的库文件解压覆盖到python的Lib/site-packages下

### 2.3 用户端

（1）浏览器



## 3. 打包

父工程里执行maven clean 和install。会在每个子项目的target中得到对应JAR包。





## 4. 部署

需依照顺序

### 4.1 启动nacos（暂不用）

（目前直接忽略此步骤）

进入nacos/bin，windows下以单机版运行

```
startup.cmd -m standalone
```

### 4.2 启动服务端

```
java -jar HostMonitor_DataCollector.jar
java -jar HostMonitor_Web.jar
```

需要确保ConfigData、DiskPredict及内部配置文件在HostMonitor_DataCollector.jar同级目录中。

确保python3.7安装完成，且DiskPredict\DiskPredictModule的依赖库已安装

**注意：**windows 可能会存在 IIS占用80端口，需要关闭功能。  https://www.laoliang.net/jsjh/technology/4016.html

### 4.3 查看注册中心服务项（暂不用）

进入nacos默认网页。默认账户：nacos，密码：nacos。

```url
http://localhost:8848/nacos/#/login
```

### 4.4 启动客户端数据采样程序

```
java -jar HostMonitor_Client.jar
```

在每个被监控Host上，以管理员身份运行 hostmonitor_client。如使用IDEA调试，需要以管理员身份运行IDEA。

需保证ConfigData，DiskPredict 配置文件在.jar同级目录中。

### 4.5 用户访问

**进入网页**

```
//本机上：直接在浏览器内输入
localhost
//部署在服务器上：浏览器输入服务器ip或域名
```



### 4.6故障预测测试数据

https://www.backblaze.com/b2/hard-drive-test-data.html#downloading-the-raw-hard-drive-test-data

最下面，找到 Downloading the Raw Hard Drive Test Data，下载数据。解压到DiskPredict/original_data

格式应形如：DiskPredict/original_data_test，保证具有年份，月份文件夹。



## 5.注册中心

项目默认不使用Nacos，当然由于未移除@EnableDiscoveryClient等配置，会导致运行报错，但不影响项目正常执行。

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

#### v3.1

故障预测全流程接入

