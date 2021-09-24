
//时间戳转换
function FGetDateTime(currentDate){

    //时间转换
    var date = new Date(currentDate);
    var Y = date.getFullYear() + '-';
    var M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    var D = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate()) + ' ';
    var h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
    var m = (date.getMinutes() <10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
    var s = (date.getSeconds() <10 ? '0' + date.getSeconds() : date.getSeconds());
    return Y+M+D+h+m+s;
}
//百分制增加单位
function FGetPercentageWithUnit(value){
    return value + "%";
}
//温度增加单位
function FGetTemperatureWithUnit(value){
    return value + "°C";
}
//KB格式加单位
function FGetKbWithUnit(value){
    if(value < 1024){
        return value + " Kb";
    }
    else{
        return (value/1024).toFixed(2) + " MB";
    }
}
//MB格式加单位
function FGetMBWithUnit(value){
    if(value < 1024){
        return value + " MB";
    }
    else{
        return (value/1024).toFixed(2) + " GB";
    }
}
//GB格式加单位
function FGetGBWithUnit(value){
    if(value < 1024){
        return value + " GB";
    }
    else{
        return (value/1024).toFixed(2) + " TB";
    }
}
//加载动图
function FGetLoadingImg(){
    return "<img style='width: 30px;height: 30px;' src='../images/loading-spin.svg' alt=''>";
}

var customPartition = [30,70,100];
var customPartitionColor = ['#92cc76','#fac859','#ee6767'];

var dfpModelNames = ["随机森林"];
var diskType = ["HDD","SSD"];
var userType = ["普通用户","管理员","超级管理员"];
var userValid = ["禁用","启用"];

function FTableColorFormatter(partitionList,colorList,value,displayValue){
    var color = "";
    for(var i=0;i<partitionList.length;i++){
        if(value < partitionList[i]){
            color = colorList[i];
            break;
        }
    }
    return '<span style="font-weight:bold;color:'+ color +'">'+displayValue+'</span>';
}

function FTableColorFormaterCustomColor(value){
    return '<span style="font-weight:bold;color:#5571C6FF">'+value+'</span>';
}

//表格标题
const tableColumns = {
    summaryPart:[
        {
            field: 'hostCount',
            title: '节点个数',
            width: 100,
            formatter : function (value, row, index) {
                return '<span style="font-weight:bold;">节点个数: </span> ' + value;
            }
        },
        {
            field: 'connectedCount',
            title: '已连接个数',
            width: 100,
            formatter : function (value, row, index) {
                return '<span style="font-weight:bold;">已连接个数: </span> ' + value;
            }
        },
        {
            field: 'sumCapacity',
            title: '总容量',
            width: 100,
            formatter : function (value, row, index) {
                return '<span style="font-weight:bold;">总容量: </span> ' + FGetGBWithUnit(value);
            }
        },
        {
            field: 'windowsHostCount',
            title: 'Windows',
            width: 100,
            formatter : function (value, row, index) {
                return "<span style=\"font-weight:bold;\">Windows: </span> "+value;
            }
        },
        {
            field: 'linuxHostCount',
            title: 'Linux',
            width: 100,
            formatter : function (value, row, index) {
                return "<span style=\"font-weight:bold;\">Linux: </span> "+value;
            }
        },
        {
            field: 'hddCount',
            title: 'HDD',
            width: 100,
            formatter : function (value, row, index) {
                return "<span style=\"font-weight:bold;\">HDD: </span> "+value;
            }
        },
        {
            field: 'ssdCount',
            title: 'SSD',
            width: 100,
            formatter : function (value, row, index) {
                return "<span style=\"font-weight:bold;\">SSD: </span> "+value;
            }
        },
    ],

    //概要-1
    summaryPart1: [
        {
            field: 'hostCount',
            title: '节点个数',
            width: 100,
            formatter : function (value, row, index) {
                return value[0] + " / " + value[1];
            }
        },
        {
            field: 'sumCapacity',
            title: '总容量',
            width: 100,
            formatter : function (value, row, index) {
                return FGetGBWithUnit(value);
            }
        },
    ],
    //概要-1
    summaryPart2: [
        {
            field: 'windowsHostCount',
            title: 'Windows',
            width: 100,
            formatter : function (value, row, index) {
                return "Windows: "+value;
            }
        },
        {
            field: 'linuxHostCount',
            title: 'Linux',
            width: 100,
            formatter : function (value, row, index) {
                return "Linux: "+value;
            }
        },
    ],
    //概要-3
    summaryPart3: [
        {
            field: 'hddCount',
            title: 'HDD',
            width: 100,
            formatter : function (value, row, index) {
                return "HDD: "+value;
            }
        },
        {
            field: 'ssdCount',
            title: 'SSD',
            width: 100,
            formatter : function (value, row, index) {
                return "SSD: "+value;
            }
        },
    ],
    //主机状态-1
    hostInfo1: [
        {
            field: 'ip',
            title: 'IP',
            width: 70,
            formatter : function (value, row, index) {
                if(row["connected"] === true){
                    return value;
                }
                else{
                    return '<span style="font-weight:bold;">'+ value+' (Down)</span>';
                }
            }
        },
        {
            field: 'osName',
            title: '操作系统',
            width: 150,
        },
        {
            field: 'cpuUsage',
            title: 'CPU使用率',
            width: 60,
            formatter : function (value, row, index) {
                return FTableColorFormatter(loadPartition["cpu"],customPartitionColor,value,FGetPercentageWithUnit(value));
            }
        },
        {
            field: 'memoryUsage',
            title: '内存使用率',
            width: 90,
            formatter : function (value, row, index) {
                return FTableColorFormatter(loadPartition["memory"],customPartitionColor,value[0]/value[1]*100,FGetMBWithUnit(value[0]) + " / " +FGetMBWithUnit(value[1]));
            }
        },
        {
            field: 'diskCapacityTotalUsage',
            title: '硬盘容量',
            width: 120,
            formatter : function (value, row, index) {
                return FTableColorFormatter(loadPartition["disk"],customPartitionColor,value[0]/value[1]*100,FGetGBWithUnit(value[0]) + " / " +FGetGBWithUnit(value[1]));
            }
        },
        {
            field: 'netReceiveSpeed',
            title: '网络接受',
            width: 60,
            formatter : function (value, row, index) {
                return FTableColorFormaterCustomColor(FGetKbWithUnit(value) +"/s");
            }
        },
        {
            field: 'netSendSpeed',
            title: '网络发送',
            width: 60,
            formatter : function (value, row, index) {
                return FTableColorFormaterCustomColor(FGetKbWithUnit(value)+"/s");
            }
        },
        {
            field: 'diskTotalIOPS',
            title: 'iops',
            width: 50,
            sortable: true,
            formatter : function (value, row, index) {
                return FTableColorFormaterCustomColor(value);
            }
        },
        {
            field: 'diskTotalReadSpeed',
            title: '硬盘读取',
            width: 70,
            sortable: true,
            formatter : function (value, row, index) {
                return FTableColorFormaterCustomColor(FGetKbWithUnit(value)+"/s");
            }
        },
        {
            field: 'diskTotalWriteSpeed',
            title: '硬盘写入',
            width: 70,
            sortable: true,
            formatter : function (value, row, index) {
                return FTableColorFormaterCustomColor(FGetKbWithUnit(value)+"/s");
            }
        },
    ],
    //磁盘状态
    diskInfo: [
        {
            field: 'diskName',
            title: '硬盘名称',
            width: 100,
            sortable: true,
        },
        {
            field: 'diskCapacitySize',
            title: '存储容量',
            width: 100,
            sortable: true,
            sorter: function (a,b){
                return (a[0]/a[1]) - (b[0]/b[1]);
            },
            formatter : function (value, row, index) {
                return FTableColorFormatter(loadPartition["disk"],customPartitionColor,value[0]/value[1]*100,FGetGBWithUnit(value[0]) + " / " +FGetGBWithUnit(value[1]));
            }
        },
        {
            field: 'diskCapacityUsage',
            title: '存储使用率',
            width: 100,
            sortable: true,
            formatter : function (value, row, index) {
                return FTableColorFormatter(loadPartition["disk"],customPartitionColor,value,FGetPercentageWithUnit(value));
            }
        },
        {
            field: 'diskIOPS',
            title: 'iops',
            width: 100,
            sortable: true,
            formatter : function (value, row, index) {
                return FTableColorFormaterCustomColor(value);
            }
        },
        {
            field: 'diskReadSpeed',
            title: '硬盘读取',
            width: 100,
            sortable: true,
            formatter : function (value, row, index) {
                return FTableColorFormaterCustomColor(FGetKbWithUnit(value)+"/s");
            }
        },
        {
            field: 'diskWriteSpeed',
            title: '硬盘写入',
            width: 100,
            sortable: true,
            formatter : function (value, row, index) {
                return FTableColorFormaterCustomColor(FGetKbWithUnit(value)+"/s");
            }
        },
    ],
    //主机状态-2
    hostInfo2: [
        {
            field: 'ip',
            title: 'IP',
            width: 100,
        },
        {
            field: 'osName',
            title: 'OS',
            width: 100,
        },
        {
            field: 'memoryUsage',
            title: '内存使用率',
            width: 100,
            formatter : function (value, row, index) {
                return FTableColorFormatter(loadPartition["memory"],customPartitionColor,value[0]/value[1]*100,FGetMBWithUnit(value[0]) + " / " +FGetMBWithUnit(value[1]));
            }
        },
        {
            field: 'diskCapacityTotalUsage',
            title: '硬盘容量',
            width: 100,
            formatter : function (value, row, index) {
                return FTableColorFormatter(loadPartition["disk"],customPartitionColor,value[0]/value[1]*100,FGetGBWithUnit(value[0]) + " / " +FGetGBWithUnit(value[1]));
            }
        },
        {
            field: 'netReceiveSpeed',
            title: '网络接受',
            width: 100,
            formatter : function (value, row, index) {
                return FTableColorFormaterCustomColor(FGetKbWithUnit(value)+"/s");
            }
        },
        {
            field: 'netSendSpeed',
            title: '网络发送',
            width: 100,
            formatter : function (value, row, index) {
                return FTableColorFormaterCustomColor(FGetKbWithUnit(value)+"/s");
            }
        },
    ],
    //CPU状态
    cpuInfo: [
        {
            field: 'cpuName',
            title: 'CPU型号',
            width: 100,
        },
        {
            field: 'cpuUsage',
            title: '使用率',
            width: 100,
            sortable: true,
            formatter : function (value, row, index) {
                return FTableColorFormatter(loadPartition["cpu"],customPartitionColor,value,FGetPercentageWithUnit(value));
            }
        },
        {
            field: 'cpuTemperature',
            title: '温度',
            width: 100,
            sortable: true,
            formatter : function (value, row, index) {
                return FTableColorFormaterCustomColor(FGetTemperatureWithUnit(value));
            }
        },
    ],
    //GPU状态
    gpuInfo: [
        {
            field: 'gpuName',
            title: 'GPU型号',
            width: 100,
        },
        {
            field: 'gpuAvailableRam',
            title: '可用内存',
            width: 100,
            sortable: true,
            formatter : function (value, row, index) {
                return FTableColorFormaterCustomColor(FGetGBWithUnit(value));
            }
        },
    ],
    //进程状态
    processInfo: [
        {
            field: 'processId',
            title: '进程ID',
            width: 45,
            sortable: true,
        },
        {
            field: 'processName',
            title: '名称',
            width: 100,
        },
        {
            field: 'startTime',
            title: '开始时间',
            width: 100,
            sortable: true,
            formatter : function (value, row, index) {
               if(value=='-'){
                   return '-'
               }else{
                   return FGetDateTime(value);
               }

            }
        },
        {
            field: 'cpuUsage',
            title: 'CPU占用率',
            width: 45,
            sortable: true,
            formatter : function (value, row, index) {
                var color = "";
                for(var i=0;i<customPartition.length;i++){
                    if(value < customPartition[i]){
                        color = customPartitionColor[i];
                        break;
                    }
                }
                return '<span style="font-weight:bold;color:'+ color +'">'+FGetPercentageWithUnit(value)+'</span>';
            }
        },
        {
            field: 'memoryUsage',
            title: '内存占用率',
            width: 45,
            sortable: true,
            formatter : function (value, row, index) {
                var color = "";
                for(var i=0;i<customPartition.length;i++){
                    if(value < customPartition[i]){
                        color = customPartitionColor[i];
                        break;
                    }
                }
                return '<span style="font-weight:bold;color:'+ color +'">'+value + "%"+'</span>';
            }
        },
        {
            field: 'diskReadSpeed',
            title: '磁盘读取速度',
            width: 50,
            sortable: true,
            formatter : function (value, row, index) {
                if(value=="-"){
                    return FTableColorFormaterCustomColor(value +"/s");
                }else{
                    return FTableColorFormaterCustomColor(FGetKbWithUnit(value) +"/s");
                }
            }
        },
        {
            field: 'diskWriteSpeed',
            title: '磁盘写入速度',
            width: 50,
            sortable: true,
            formatter : function (value, row, index) {
                if(value=="-"){
                    return FTableColorFormaterCustomColor(value +"/s");
                }else{
                    return FTableColorFormaterCustomColor(FGetKbWithUnit(value) +"/s");
                }
                // return FTableColorFormaterCustomColor(FGetKbWithUnit(value) +"/s");
            }
        },
    ],
    //故障预测信息
    diskFailurePredictInfo:[
        {
            field: 'ip',
            title: 'IP',
            width: 100,
            sortable: true,
        },
        {
            field: 'diskSerial',
            title: '硬盘名称',
            width: 100,
            sortable: true,
        },
        {
            field: 'diskType',
            title: '类型',
            width: 100,
            sortable: true,
            formatter : function (value, row, index) {
                return diskType[value];
            }
        },
        {
            field: 'manufacturer',
            title: '厂商',
            width: 100,
            sortable: true,
        },
        {
            field: 'diskCapacity',
            title: '容量',
            width: 100,
            sortable: true,
            formatter : function (value, row, index) {
                return FGetGBWithUnit(value);
            }
        },
        {
            field: 'model',
            title: '硬盘Model',
            width: 100,
            sortable: true,
            sortableColor:"#2FAFEB"
        },

        {
            field: 'timestamp',
            title: '预测时间',
            width: 100,
            sortable: true,
            formatter : function (value, row, index) {
                return FGetDateTime(value);
            }
        },
        {
            field: 'predictProbability',
            title: '状态',
            width: 80,
            sortable: true,
            formatter : function (value, row, index) {
                var color = "";
                var displayValue = "";

                for(var i=0;i<dfpPartition.length;i++){
                    if(value < dfpPartition[i]){
                        color = dfpPartitionColor[i];
                        displayValue = displayValueList[i];
                        break;
                    }
                }
                return '<span style="font-weight:bold;color:'+ color +'">'+displayValue+'</span>';
            }
        },
    ],
    //测速
    speedMeasurement: [
        {
            field: 'state',
            checkbox: true,
            align: 'center',
            valign: 'middle',
            width: 15,
            formatter: function(value, row, index) {
                if(row.diskIOSpeed === -1){
                    return { disabled : true,}
                }else{
                    return { disabled : false,}
                }
            },
        },
        {
            field: 'ip',
            title: 'IP',
            width: 100,
        },
        {
            field: 'ioTestLastTime',
            title: '测速时间',
            width: 100,
            sortable: true,
            formatter : function (value, row, index) {
                return FGetDateTime(value);
            }
        },
        {
            field: 'netSendSpeed',
            title: '网络发送速度',
            width: 50,
            sortable: true,
            formatter : function (value, row, index) {
                if(value === -1){
                    return FGetLoadingImg();
                }
                else{
                    return FGetKbWithUnit(value)+"/s";
                }
            }
        },
        {
            field: 'netDownloadSpeed',
            title: '网络下载速度',
            width: 50,
            sortable: true,
            formatter : function (value, row, index) {
                if(value === -1){
                    return FGetLoadingImg();
                }
                else{
                    return FGetKbWithUnit(value)+"/s";
                }
            }
        },
        {
            field: 'diskIOSpeed',
            title: '平均硬盘IO速度',
            width: 50,
            sortable: true,
            formatter : function (value, row, index) {
                if(value === -1){
                    return FGetLoadingImg();
                }
                else{
                    return FGetKbWithUnit(value)+"/s";
                }
            }
        },
    ],
    //用户管理
    userManagement: [
        {
            field: 'ID',
            title: '序号',
            width: 100,
            formatter : function (value, row, index) {
                return index+1;
            }
        },
        {
            field: 'userName',
            title: '用户名',
            width: 100,
        },
        {
            field: 'userID',
            title: '用户ID',
            width: 100,
        },
        {
            field: 'userType',
            title: '用户类型',
            width: 100,
            formatter : function (value, row, index) {
                return userType[value];
            }
        },
        {
            field: 'validState',
            title: '状态',
            width: 100,
            formatter : function (value, row, index) {
                return userValid[value?1:0];
            }

        },
        {
            field: 'lastEditTime',
            title: '最后编辑时间',
            width: 100,
            formatter : function (value, row, index) {
                return FGetDateTime(value);
            }
        },
        {
            field: 'operate',
            title: '操作',
            align: 'center',
            width: 100,
            clickToSelect: false,
            events: {
                'click .display': function (e, value, row, index) {
                    displayUserInfo(row,index);
                },

                'click .delete': function (e, value, row, index) {
                    deleteUser(row,index)
                },
            },
            formatter: function operateFormatter(value, row, index) {
                return [
                    '<a class="display"  style="color:#5470c6;text-decoration:none;" href="javascript:void(0)" title="Like">查看</a>      ',
                    '<a class="delete" style="color:#ee6767;text-decoration:none;" href="javascript:void(0)" title="Remove">删除</a>',
                ].join('')
            }
        }


    ],
    userInfoDisplay:[
        {
            field: 'Field',
            title: '字段',
            align : 'right',
            width: 10,
        },
        {
            field: 'content',
            title: '内容',
            width: 100,
        },
    ],
    //构建记录
    buildRecords:[
        {
            field: 'buildTime',
            title: '构建时间',
            sortable: true,
            width:100,
            formatter : function (value, row, index) {
                return FGetDateTime(value);
            }
        },
        {
            field: 'model',
            title: '预测模型',
            sortable: true,
            width:80,
            formatter : function (value, row, index) {
                return dfpModelNames[value-1];
            }
        },
        {
            field: 'diskModel',
            title: '硬盘Model',
            sortable: true,
            width:80,
        },
        {
            field: 'OperatorID',
            title: '操作用户',
            sortable: true,
            width:80,
        },
        {
            field: 'FDR',
            title: 'FDR',
            sortable: true,
            width:50,
        },
        {
            field: 'FAR',
            title: 'FAR',
            sortable: true,
            width:50,
        },
        {
            field: 'AUC',
            title: 'AUC',
            sortable: true,
            width:50,
        },
        {
            field: 'FNR',
            title: 'FNR',
            sortable: true,
            width:50,
        },
        {
            field: 'Accuracy',
            title: 'Accuracy',
            sortable: true,
            width:50,
        },
        {
            field: 'Precision',
            title: 'Precision',
            sortable: true,
            width:50,
        },
        {
            field: 'Specificity',
            title: 'Specificity',
            sortable: true,
            width:50,
        },
        {
            field: 'ErrorRate',
            title: 'ErrorRate',
            sortable: true,
            width:50,
        },
    ],
    //
    dfpComparison:[
        {
            field: 'field',
            title: '',
            width:50,
            formatter : function (value, row, index) {
                if(value === "predict"){
                    return "预测";
                }
                else if(value === "reality"){
                    return "真实";
                }
                return value;
            }
        },
        {
            field: 'FDR',
            title: 'FDR',
            width:120,
        },
        {
            field: 'FAR',
            title: 'FAR',
            width:150,
        },
        {
            field: 'AUC',
            title: 'AUC',
            width:120,
        },
        {
            field: 'FNR',
            title: 'FNR',
            width:120,
        },
        {
            field: 'Accuracy',
            title: 'Accuracy',
            width:120,
        },
        {
            field: 'Precision',
            title: 'Precision',
            width:120,
        },
        {
            field: 'Specificity',
            title: 'Specificity',
            width:120,
        },
        {
            field: 'ErrorRate',
            title: 'ErrorRate',
            width:120,
        },
    ]
};


//buildRecords表格详情
function buildRecordsDetailFormatter(index, row) {
    var html = [];
    html = "参数：" + row.params;
    return html
}



//----------获取Format
function FGetFormat(key){
    if(key === "cpuInfo"){
        var cpuInfoFormat = {
            cpuName: "cpuName",
            cpuUsage: 0,
            cpuTemperature: 0,
        };
        return cpuInfoFormat;
    }
    else if(key === "gpuInfo"){
        var gpuInfoFormat = {
            gpuName: "gpuName",
            gpuAvailableRam: 0,
        };
        return gpuInfoFormat;
    }
    else if(key === "processInfo"){
        var processInfoFormat = {
            processId: 0,
            processName: "processName",
            startTime: 0,
            cpuUsage: 0,
            memoryUsage: 0,
            diskReadSpeed: 0,
            diskWriteSpeed: 0,
        };
        return processInfoFormat;
    }
    else if(key === "dfpInfo"){
        var dfpInfoFormat = {
            hostName: "hostName",
            diskName: "diskName",
            diskCapacity: 0,
            predictTime: 0,
            predictProbability: 0,
        };
        return dfpInfoFormat;
    }
    else if(key === "speedMeasurementInfo"){
        var speedMeasurementInfoFormat = {
            hostName: "hostName",
            ioTestLastTime: 0,
            netSendSpeed: 0,
            netDownloadSpeed: 0,
            diskIOSpeed: 0,
        };
        return speedMeasurementInfoFormat;
    }
    else if(key === "diskInfo"){
        var diskInfoFormat = {
            diskName: "diskName",
            diskCapacitySize: [0,0],
            diskCapacityUsage: 0,
            diskIOPS: 0,
            diskReadSpeed: 0,
            diskWriteSpeed: 0,
        };
        return diskInfoFormat;
    }
    else if(key === "hostInfo"){
        var hostInfoFormat = {
            connected: false,
            //Dashboard
            hostInfo1:{
                connected: false,
                osName: "OS",
                cpuUsage: 0,
                memoryUsage: [0,0],
                diskCapacityTotalUsage: [0,0],
                netReceiveSpeed: 0,
                netSendSpeed: 0,
                ip:"0.0.0.0",
                diskTotalIOPS:0,
                diskTotalReadSpeed:0,
                diskTotalWriteSpeed:0,
            },
            //Detail
            hostInfo2:{
                osName: "OS",
                ip: "0.0.0.0",
                memoryUsage: [0,0],
                diskCapacityTotalUsage: [0,0],
                netReceiveSpeed: 0,
                netSendSpeed: 0,
            },
            diskInfoList:[],
            cpuInfoList:[],
            gpuInfoList:[],
            processInfoList:[],
            lastUpdateTime:0,
        }
        return hostInfoFormat;
    }
    else if(key === "hostInfoTrend"){
        var hostInfoTrendFormat = {
            hostInfoTrend: [],
            lastUpdateTime:0,
        }
        return hostInfoTrendFormat;
    }
    else if(key === "mainInfo"){
        var mainInfoFormat = {
            hostIp: [],
            summaryPart: [],
            summaryChart:[
                //cpuLoad
                [0,0,0],
                //diskLoad
                [0,0,0],
                //memoryLoad
                [0,0,0],
            ],
            lastUpdateTime:0,
        }
        return mainInfoFormat;
    }
    else if(key === "dfpInfoList"){
        var dfpInfoListFormat = {
            dfpInfo: [],
            dfpSummaryChart:[0,0,0],
            lastUpdateTime:0,
        }
        return dfpInfoListFormat;
    }
    else if(key === "dfpInfoTrend"){
        var dfpInfoTrendFormat = {
            dfpInfoTrend: [],
            lastUpdateTime:0,
        }
        return dfpInfoTrendFormat;
    }
    else if(key === "speedMeasurementInfoList"){
        var speedMeasurementInfoListFormat = {
            speedMeasurementInfo: [],
            lastUpdateTime:0,
        }
        return speedMeasurementInfoListFormat;
    }
}
//----------获取-设置-数据
function FGetMainInfo(){
    var mainInfo = window.sessionStorage.getItem("mainInfo");
    if(mainInfo == null){
        var mainInfoFormat = FGetFormat("mainInfo");
        window.sessionStorage.setItem("mainInfo",JSON.stringify(mainInfoFormat));
        return mainInfoFormat;
    }
    else{
        return JSON.parse(mainInfo);
    }
}
function FGetHostInfo(hostName){
    var hostInfo = window.sessionStorage.getItem("hostInfo_"+hostName);
    if(hostInfo == null){
        var hostInfoFormat = FGetFormat("hostInfo");
        window.sessionStorage.setItem("hostInfo_"+hostName,JSON.stringify(hostInfoFormat));
        return hostInfoFormat;
    }
    else{
        return JSON.parse(hostInfo);
    }
}
function FGetHostInfoTrend(hostName){
    var hostInfoTrend = window.sessionStorage.getItem("hostInfoTrend_"+hostName);
    if(hostInfoTrend == null){
        var hostInfoTrendFormat = FGetFormat("hostInfoTrend");
        window.sessionStorage.setItem("hostInfoTrend_"+hostName,JSON.stringify(hostInfoTrendFormat));
        return hostInfoTrendFormat;
    }
    else{
        return JSON.parse(hostInfoTrend);
    }
}
function FGetDFPInfoList(){
    var dfpInfoList = window.sessionStorage.getItem("dfpInfoList");
    if(dfpInfoList == null){
        var dfpInfoListFormat = FGetFormat("dfpInfoList");
        window.sessionStorage.setItem("dfpInfoList",JSON.stringify(dfpInfoListFormat));
        return dfpInfoListFormat;
    }
    else{
        return JSON.parse(dfpInfoList);
    }
}
function FGetDFPInfoTrend(hostName,diskName){
    var dfpInfoTrend = window.sessionStorage.getItem("dfpInfoTrend_"+hostName+"_"+diskName);
    if(dfpInfoTrend == null){
        var dfpInfoTrendFormat = FGetFormat("dfpInfoTrend");
        window.sessionStorage.setItem("dfpInfoTrend_"+hostName+"_"+diskName,JSON.stringify(dfpInfoTrendFormat));
        return dfpInfoTrendFormat;
    }
    else{
        return JSON.parse(dfpInfoTrend);
    }
}
function FGetSpeedMeasurementInfoList(){
    var speedMeasurementInfoList = window.sessionStorage.getItem("speedMeasurementInfoList");
    if(speedMeasurementInfoList == null){
        var speedMeasurementInfoListFormat = FGetFormat("speedMeasurementInfoList");
        window.sessionStorage.setItem("speedMeasurementInfoList",JSON.stringify(speedMeasurementInfoListFormat));
        return speedMeasurementInfoListFormat;
    }
    else{
        return JSON.parse(speedMeasurementInfoList);
    }
}
//----------Set数据
function FSetData(key,newJsonData){
    //mainInfo
    //"hostInfo_"+hostName
    //"dfpInfo"
    //"speedMeasurementInfo"
    window.sessionStorage.setItem(key,JSON.stringify(newJsonData));
}

//获取磁盘名称-DiskName
function FGetDiskNameList(hostName){
    var result = [];
    var hostInfo = FGetHostInfo(hostName);
    for(var i=0;i<hostInfo["diskInfoList"].length;i++){
        result.push(hostInfo["diskInfoList"][i]["diskName"]);
    }
    return result;
}





//Load
var loadPartition ={
    cpu:[30,70,100],
    memory:[30,70,100],
    disk:[30,70,100],
};

var displayValueList = ["故障","报警","正常"];
var dfpPartition = [30,60,100];
var dfpPartitionColor = ['#ee6767','#fac859','#92cc76'];
//数据间隔时间
var DateInterval = [24,1];
var DateIntervalText = ["最近1天","最近1小时"];
//[配置]SummaryChart
/* CPU负载统计-百分比+饼状图
   磁盘负载统计-百分比+饼状图
   内存负载统计-百分比+饼状图
*/
var meterChartOption={
    title : {
        text: '进程资源消耗',
        left:"44%",
        subtext: '',
        textStyle:{
            color:"#2FAFEB"
        }
    },
    tooltip : {
        trigger: 'axis'
    },
    legend: {
        data:["磁盘读取速速"]
    },
    toolbox: {
        show : true,
        feature : {
            mark : {show: true},
            dataView : {show: true, readOnly: false},
            magicType : {show: true, type: ['line', 'bar']},
            restore : {show: true},
            saveAsImage : {show: true}
        }
    },
    calculable : true,
    xAxis : [
        {
            type : 'category',
            data : [],
            name: '进程ID',
            splitNumber: 5,
            nameTextStyle:{ fontSize:20,color:"#2FAFEB"},
            axisLine:{
                lineStyle:{
                    color:'#2FAFEB'
                }
            },
        }
    ],
    axisLine:{
        show:true,
        lineStyle:{
            color:'#6FC6F3',
            width:5,
        }
    },
    yAxis:[{
        type : 'value',
        interval:20,
        axisLabel:{
            show:true,
            textStyle:{
                color:'#2FAFEB',
                fontSize:12
            },
        },
        //用于设置y轴的那一条线
        axisLine: {
            show: true,
            lineStyle: {
                color: '#2FAFEB',
                width: 1,
            }
        },
        splitLine: {
            show: true,
            lineStyle:{
                type:"dashed",
                color:'#032460'
            }
        },
    }],
    series : [
        {
            name:"磁盘读取速度",
            type:'bar',
            data:[2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3],
            itemStyle: {
                normal: {
                    label: {
                        show: true, //开启显示
                        position: 'top', //在上方显示
                        formatter: "{c}kb/s",
                        textStyle: { //数值样式
                            color: '#2FAFEB',
                            fontSize: 12
                        }
                    }
                }
            }
        },
    ]
}
// 获取仪表显示柱图数据
function getMeterChartOption(ip){
    var option = JSON.parse(JSON.stringify(meterChartOption));
    var data=JSON.parse(sessionStorage.getItem('hostInfo_'+ip))
    console.log(data)
    var arr1=[]
    var arr2=[]
    if(data.processInfoList.length>0){
        for(var i=0;i<data.processInfoList.length;i++){
            arr1.push(data.processInfoList[i]["processId"])
            arr2.push(data.processInfoList[i]["diskReadSpeed"])
        }
    }
    option.xAxis[0].data=arr1
    option.series[0].data=arr2
    return option;
}
var summaryChartOption = {
    gaugeOption:{
        type: 'gauge',
        startAngle: 90,
        endAngle: -270,
        radius: '90%',
        center: ['16%', '54%'],
        pointer: {
            show: false
        },
        progress: {
            show: true,
            overlap: true,
            roundCap: false,
            clip: true,
            width: 0,
            itemStyle: {
                borderWidth: 0,
                borderColor: '#464646'
            }
        },
        axisLine: {
            lineStyle: {
                width: 40
            }
        },
        splitLine: {
            show: false,
            distance: 0,
            length: 10
        },
        axisTick: {
            show: false
        },
        axisLabel: {
            show: false,
            distance: 50
        },
        data: [
            {
                value: 0,
                name: '低',
                title: {
                    offsetCenter: ['135%', '-70%']
                },
                detail: {
                    offsetCenter: ['135%', '-50%']
                }
            },
            {
                value: 0,
                name: '中',
                title: {
                    offsetCenter: ['135%', '-20%']
                },
                detail: {
                    offsetCenter: ['135%', '0%']
                }
            },
            {
                value: 0,
                count:0,
                name: '高',
                title: {
                    offsetCenter: ['135%', '30%']
                },
                detail: {
                    offsetCenter: ['135%', '50%']
                }
            }
        ],
        title: {
            fontWeight: 'bold',
            fontSize: 15,
            color:"#2798CE",
        },
        detail: {
            width: 35,
            height: 10,
            fontSize:12,
            color: 'auto',
            borderColor: 'auto',
            borderRadius: 5,
            borderWidth: 1,
            formatter: '{value}%',
        }
    },
    pieOption: {
        name: '数量',
        type: 'pie',
        center: ['16%', '54%'],
        radius: ['64%', '90%'],
        label: {
            show:false,
            position: 'inner',
            fontSize: 10,
            formatter: 'fff',
        },
        data: [
            {
                value: 0,
                name: 'Low'
            },
            {
                value: 0,
                name: 'Medium'
            },
            {
                value: 0,
                name: 'High'
            },
        ]
    },
    centerOption:[
        ['12%', '55%'],
        ['46%', '55%'],
        ['80%', '55%'],
    ],
    option: {
        title: [
            {
                text: "CPU负载统计",
                left: '6.5%',
                top: '0px',
                textStyle:{
                    color:"#2798CE",
                    fontSize:15,
                }
            },
            {
                text: "内存负载统计",
                left: '40.5%',
                top: '0px',
                textStyle:{
                    color:"#2798CE",
                    fontSize:15,
                }
            },
            {
                text: "磁盘负载统计",
                left: '74.5%',
                top: '0px',
                textStyle:{
                    color:"#2798CE",
                    fontSize:15,
                }
            },
        ],
        color: ['#69C8C5','#FF9E3F','#d04040'],
        tooltip: {
            trigger: 'item',
        },
        series: []
    },

    };

//获取ChartOption-SummaryChart
function FGetSummaryChartOption(){
    var option = JSON.parse(JSON.stringify(summaryChartOption["option"]));
    for(var i=0;i<3;i++){
        var tempGaugeOption = JSON.parse(JSON.stringify(summaryChartOption["gaugeOption"]));
        console.log(tempGaugeOption)
        var tempPieOption = JSON.parse(JSON.stringify(summaryChartOption["pieOption"]));
        tempGaugeOption["center"] = summaryChartOption["centerOption"][i];
        tempPieOption["center"] = summaryChartOption["centerOption"][i];
        option["series"].push(tempGaugeOption);
        option["series"].push(tempPieOption);
    }
    console.log(option)
    return option;
}
//[更新Chart数据]SummaryChart
function FRefreshSummaryChartData(currentChart,currentData){
    var option = FGetSummaryChartOption();
    for(var i=0;i<3;i++){
        var percentage = [];
        var sum = 0;
        for(var j=0;j<3;j++){
            sum += currentData[i][j];
        }
        for(var j=0;j<3;j++){
            percentage.push( (currentData[i][j]/sum* 100).toFixed(1) ) ;
        }
        //alert(percentage);
        for(var j=0;j<3;j++){
            option["series"][i*2]["data"][j]["value"] = percentage[j];
            option["series"][i*2+1]["data"][j]["value"] = currentData[i][j];
        }
    }
    //alert(JSON.stringify(option));
    currentChart.setOption(option);
}

function FRefreshDFPSummaryChart(currentChart,currentData){
    var option = FGetDFPSummaryChartOption();
    //alert(JSON.stringify(option));
    var percentage = [];
    var sum = 0;
    for(var j=0;j<3;j++){
        sum += currentData[j];
    }
    for(var j=0;j<3;j++){
        percentage.push( (currentData[j]/sum* 100).toFixed(1) ) ;
    }

    for(var j=0;j<3;j++){
        option["series"][0]["data"][j]["value"] = percentage[j];
        option["series"][1]["data"][j]["value"] = currentData[j];
    }

    currentChart.setOption(option);
}

//获取ChartOption-TrendChart
function FGetTrendChartOption(){
    var titleName = ["CPU利用率","内存利用率","硬盘IO","网络IO"];
    var seriesName = ["CPU利用率","内存利用率","硬盘读取","硬盘写入","网络接受","网络发送"];
    var unitLabel =["%","%","Kb/s","Kb/s"];
    var trendChartOption = {
        title: [
            {
                text: titleName[0],
                left: '0%',
                top: '0px',
                textStyle:{
                    color:'#2FAFEB'
                }
            },
            {
                text: titleName[1],
                left: '50%',
                top: '0px',
                textStyle:{
                    color:'#2FAFEB'
                }
            },
            {
                text: titleName[2],
                left: '0%',
                top: '360px',
                textStyle:{
                    color:'#2FAFEB'
                }
            },
            {
                text: titleName[3],
                left: '50%',
                top: '360px',
                textStyle:{
                    color:'#2FAFEB'
                }
            },
        ],
        /*legend: {},*/
        grid: [
            {
                left: '1%',
                right: '51%',
                top: '50px',
                height:'300px',
                containLabel: true
            },
            {
                left: '51%',
                right: '1%',
                top: '50px',
                height:'300px',
                containLabel: true
            },
            {
                left: '1%',
                right: '51%',
                top: '430px',
                height:'300px',
                containLabel: true
            },
            {
                left: '51%',
                right: '1%',
                top: '430px',
                height:'300px',
                containLabel: true
            },
        ],
        tooltip: {
            trigger: 'axis',
            formatter: function(params){
                var returnTxt = "时间: "+ FGetDateTime(params[0].value[0]) +"<br/>";
                for(var i =0; i< params.length; i++){
                    returnTxt += params[i].marker+" "+params[i].seriesName+ " "+params[i].value[1] + unitLabel[params[i].axisIndex] + "<br/>";
                }
                return returnTxt;
            }
        },
        xAxis: [{
            axisLine:{
                lineStyle:{
                    color:'#2FAFEB'
                }
            },
        }],
        yAxis: [{
            axisLine:{
                lineStyle:{
                    color:'#2FAFEB'
                }
            },
        }],
        series: [],
    };

    //数据格式
    for(var i=0;i<6;i++){
        trendChartOption["series"].push({
            name:seriesName[i],
            smooth:true,
            type: 'line',
            showSymbol: false,
            hoverAnimation: false,
            data: [],
        });
    }

    //坐标轴格式
    for(var i=0;i<titleName.length;i++){
        trendChartOption["xAxis"].push({
            type: 'time',
            splitLine: {
                show: false
            },
            axisLabel: {
                formatter: {
                    year: '{yyyy}年',
                    month: '{MM}月',
                    day: '{MM}月{dd}日',
                    hour: '{HH}:{mm}',
                    minute: '{HH}:{mm}',
                    second: '{HH}:{mm}:{ss}',
                    millisecond: '{HH}:{mm}:{ss} ',
                    none: '{yyyy}-{MM}-{dd} {HH}:{mm}:{ss}'
                }
            },
            gridIndex: i,
            axisLine:{
                lineStyle:{
                    color:'#2FAFEB'
                }
            },

        });
        var tempYAxisOption = {
            type: 'value',
            axisLabel: {
                formatter: '{value}'+ unitLabel[i]
            },
            gridIndex: i,

            axisLine:{
                lineStyle:{
                    color:'#2FAFEB'
                }
            },
            splitLine: {
                show: true,
                lineStyle:{
                    type:"dashed",
                    color:'#032460'
                }
            },
        };
        if(unitLabel[i] === "%"){
            tempYAxisOption["max"] = 100;
        }
        trendChartOption["yAxis"].push(tempYAxisOption);
    }

    return trendChartOption;
}

//获取CHartOption-DFPSummaryChart
function FGetDFPSummaryChartOption(){
    var option = JSON.parse(JSON.stringify(summaryChartOption["option"]));
    option["title"] = {
        text: "故障预测统计",
        left:'30%',
        textStyle:{
            color:'#2FAFEB'
        }
    };
    var tempGaugeOption = JSON.parse(JSON.stringify(summaryChartOption["gaugeOption"]));
    var tempPieOption = JSON.parse(JSON.stringify(summaryChartOption["pieOption"]));

    tempGaugeOption["center"] = summaryChartOption["centerOption"][1];
    tempPieOption["center"] = summaryChartOption["centerOption"][1];

    option["series"].push(tempGaugeOption);
    option["series"].push(tempPieOption);
    return option;
}

//获取CHartOption-DFPTrendChart
function FGetDFPTrendChartOption(){
    var DFPTrendChartOption = {
        grid: {
            left: '1%',
            right: '99%',
            width: '98%',
            top: '10px',
            height:'260px',
            containLabel: true
        },

        tooltip: {
            trigger: 'axis',
            formatter: function(params){
                var returnTxt = "时间: "+ FGetDateTime(params[0].value[0]) +"<br/>";
                for(var i =0; i< params.length; i++){
                    returnTxt += params[i].marker+" "+params[i].seriesName+ " "+params[i].value[1] + "%" + "<br/>";
                }
                return returnTxt;
            }
        },
        xAxis: {
            type: 'time',
            splitLine: {
                show: false
            },
            axisLine:{
                lineStyle:{
                    color:'#2FAFEB'
                }
            },
            axisLabel: {
                formatter: {
                    year: '{yyyy}年',
                    month: '{MM}月',
                    day: '{MM}月{dd}日',
                    hour: '{HH}:{mm}',
                    minute: '{HH}:{mm}',
                    second: '{HH}:{mm}:{ss}',
                    millisecond: '{HH}:{mm}:{ss} ',
                    none: '{yyyy}-{MM}-{dd} {HH}:{mm}:{ss}'
                }
            },
        },
        yAxis: {
            type: 'value',
            splitLine: {
                show: true,
                lineStyle:{
                    type:"dashed",
                    color:'#032460'
                }
            },
            axisLine:{
                lineStyle:{
                    color:'#2FAFEB'
                }
            },
            axisLabel: {
                formatter:function (value, index) {
                    var result = "";
                    if(value === 100){
                        result = displayValueList[2];
                    }
                    else if(value === 60){
                        result = displayValueList[1];
                    }
                    else if(value === 0){
                        result = displayValueList[0];
                    }
                    return result;
                }
            },
            max:100,
        },
        series: {
            name: "可信度",
            smooth:true,
            type: 'line',
            showSymbol: false,
            hoverAnimation: false,
            data: [],
            itemStyle:{
                normal:{
                    lineStyle:{
                        color:"#2DA9E4"
                    }
                }
            }
        },
    };
    return DFPTrendChartOption;
}

//故障类型统计chart
function FGetFailureTypeStatisticsChartOption(diskType,hddCount,ssdCount){
    var emphasisStyle = {
        itemStyle: {
            shadowBlur: 10,
            shadowColor: 'rgba(0,0,0,0.3)'
        }
    };
    var FailureTypeStatisticsChartOption = {
        title: {
            text: "故障类型统计",
            left: '48%',
            textStyle:{
                color:'#2FAFEB'
            }
        },
        grid: {
            left: '0%',
            right: '98%',
            width: '99%',
            top: '40px',
            height:'260px',
            containLabel: true
        },
        toolbox: {
            feature: {
                magicType: {
                    type: ['stack', 'tiled']
                },
                dataView: {}
            }
        },
        tooltip: {},
        legend: {
            left: '10%',
            textStyle:{
                color:'#2FAFEB'
            }
        },
        xAxis: {
            data: diskType,
            name: '厂商',
            axisLine: {onZero: true},
            splitLine: {show: false},
            splitArea: {show: false}
        },
        yAxis: {
            type: 'value',
            splitLine: {
                show: true,
                lineStyle:{
                    type:"dashed",
                    color:'#032460'
                }
            },
        },
        series: [
            {
                name: 'HDD',
                type: 'bar',
                stack: 'one',
                emphasis: emphasisStyle,
                data: hddCount,
            },
            {
                name: 'SSD',
                type: 'bar',
                stack: 'one',
                emphasis: emphasisStyle,
                data: ssdCount,
            },
        ]
    };
    return FailureTypeStatisticsChartOption;
}

//
function FGetFailureCountStatisticsChartOption(seriesData){
    var FailureCountStatisticsChartOption = {
        title: {
            text: "故障盘数量趋势",
            left: '44%',
            top:"-10%",
            textStyle:{
                color:'#2FAFEB'
            }
        },
        legend:{

        },
        grid: {
            left: '1%',
            right: '99%',
            width: '98%',
            top: '40px',
            height:'300px',
            containLabel: true
        },
        tooltip:{
            trigger: 'axis',
            formatter: function(params){
                var returnTxt = "时间: "+ FGetDateTime(params[0].value[0]) +"<br/>";
                for(var i =0; i< params.length; i++){
                    returnTxt += params[i].marker+" "+params[i].seriesName+ " "+params[i].value[1] + "" + "<br/>";
                }
                return returnTxt;
            }
        },
        xAxis: {
            type: 'time',
            splitLine: {
                show: false
            },
            axisLine:{
                lineStyle:{
                    color:'#2FAFEB'
                }
            },
            axisLabel: {
                formatter: {
                    year: '{yyyy}年',
                    month: '{MM}月',
                    day: '{MM}月{dd}日',
                    hour: '{HH}:{mm}',
                    minute: '{HH}:{mm}',
                    second: '{HH}:{mm}:{ss}',
                    millisecond: '{HH}:{mm}:{ss} ',
                    none: '{yyyy}-{MM}-{dd} {HH}:{mm}:{ss}'
                }
            },
        },
        yAxis: {
            type: 'value',
            splitLine: {
                show: true,
                lineStyle:{
                    type:"dashed",
                    color:'#032460'
                }
            },
            lineStyle:{

            axisLabel: {
                formatter: '{value}'
            },
            axisLine:{
                lineStyle:{
                    color:'#2FAFEB'
                }
            },
        },
        series: {
            name: "故障数量",
            smooth:true,
            type: 'line',
            showSymbol: false,
            hoverAnimation: false,
            data: seriesData,
            itemStyle:{
                normal:{
                    lineStyle:{
                        color:'#2FAFEB'
                    }
                }
            }
        }
        },
    };
    return FailureCountStatisticsChartOption;
}

