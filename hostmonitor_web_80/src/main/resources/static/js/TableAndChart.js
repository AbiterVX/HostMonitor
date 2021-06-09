
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



//表格标题
const tableColumns = {
    //概要-1
    summaryPart1: [
        {
            field: 'hostCount',
            title: '主机个数',
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
        },
        {
            field: 'linuxHostCount',
            title: 'Linux',
            width: 100,
        },
    ],
    //概要-3
    summaryPart3: [
        {
            field: 'hddCount',
            title: 'HDD',
            width: 100,
        },
        {
            field: 'ssdCount',
            title: 'SSD',
            width: 100,
        },
    ],
    //主机状态-1
    hostInfo1: [
        {
            field: 'osName',
            title: '操作系统',
            width: 100,
        },
        {
            field: 'cpuUsage',
            title: 'CPU使用率',
            width: 100,
            formatter : function (value, row, index) {

                loadPartition[""];
                return FGetPercentageWithUnit(value);
            }
        },
        {
            field: 'memoryUsage',
            title: '内存使用率',
            width: 100,
            formatter : function (value, row, index) {
                return FGetMBWithUnit(value[0]) + " / " +FGetMBWithUnit(value[1]);
            }
        },
        {
            field: 'diskCapacityTotalUsage',
            title: '硬盘容量',
            width: 100,
            formatter : function (value, row, index) {
                return FGetGBWithUnit(value[0]) + " / " +FGetGBWithUnit(value[1]);
            }
        },
        {
            field: 'netReceiveSpeed',
            title: '网络接受',
            width: 100,
            formatter : function (value, row, index) {
                return FGetKbWithUnit(value) +"/s";
            }
        },
        {
            field: 'netSendSpeed',
            title: '网络发送',
            width: 100,
            formatter : function (value, row, index) {
                return FGetKbWithUnit(value)+"/s";
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
                return FGetGBWithUnit(value[0]) + " / " +FGetGBWithUnit(value[1]);
            }
        },
        {
            field: 'diskCapacityUsage',
            title: '存储使用率',
            width: 100,
            sortable: true,
            formatter : function (value, row, index) {
                return FGetPercentageWithUnit(value);
            }
        },
        {
            field: 'diskIOPS',
            title: 'iops',
            width: 100,
            sortable: true,
        },
        {
            field: 'diskReadSpeed',
            title: '硬盘读取',
            width: 100,
            sortable: true,
            formatter : function (value, row, index) {
                return FGetKbWithUnit(value)+"/s";
            }
        },
        {
            field: 'diskWriteSpeed',
            title: '硬盘写入',
            width: 100,
            sortable: true,
            formatter : function (value, row, index) {
                return FGetKbWithUnit(value)+"/s";
            }
        },
    ],
    //主机状态-2
    hostInfo2: [
        {
            field: 'osName',
            title: 'OS',
            width: 100,
        },
        {
            field: 'ip',
            title: 'IP',
            width: 100,
        },
        {
            field: 'memoryUsage',
            title: '内存使用率',
            width: 100,
            formatter : function (value, row, index) {
                return FGetMBWithUnit(value[0]) + " / " +FGetMBWithUnit(value[1]);
            }
        },
        {
            field: 'diskCapacityTotalUsage',
            title: '硬盘容量',
            width: 100,
            formatter : function (value, row, index) {
                return FGetGBWithUnit(value[0]) + " / " +FGetGBWithUnit(value[1]);
            }
        },
        {
            field: 'netReceiveSpeed',
            title: '网络接受',
            width: 100,
            formatter : function (value, row, index) {
                return FGetKbWithUnit(value)+"/s";
            }
        },
        {
            field: 'netSendSpeed',
            title: '网络发送',
            width: 100,
            formatter : function (value, row, index) {
                return FGetKbWithUnit(value)+"/s";
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
                return FGetPercentageWithUnit(value);
            }
        },
        {
            field: 'cpuTemperature',
            title: '温度',
            width: 100,
            sortable: true,
            formatter : function (value, row, index) {
                return FGetTemperatureWithUnit(value);
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
                return FGetMBWithUnit(value);
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
                return FGetDateTime(value);
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
                return FGetKbWithUnit(value) +"/s";
            }
        },
        {
            field: 'diskWriteSpeed',
            title: '磁盘写入速度',
            width: 50,
            sortable: true,
            formatter : function (value, row, index) {
                return FGetKbWithUnit(value) +"/s";
            }
        },
    ],
    //故障预测信息
    diskFailurePredictInfo:[
        {
            field: 'hostName',
            title: '主机',
            width: 100,
            sortable: true,
        },
        {
            field: 'diskName',
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
        },

        {
            field: 'predictTime',
            title: '预测时间',
            width: 100,
            sortable: true,
            formatter : function (value, row, index) {
                return FGetDateTime(value);
            }
        },
        {
            field: 'predictProbability',
            title: '预测概率',
            width: 80,
            sortable: true,
            formatter : function (value, row, index) {
                if(value === -1){
                    return FGetLoadingImg();
                }
                else{
                    var color = "";
                    for(var i=0;i<dfpPartition.length;i++){
                        if(value < dfpPartition[i]){
                            color = dfpPartitionColor[i];
                            break;
                        }
                    }

                    return '<span style="font-weight:bold;color:'+ color +'">'+FGetPercentageWithUnit(value)+'</span>';
                }
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
            field: 'hostName',
            title: '主机',
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
            width:80,
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
                return dfpModelNames[value];
            }
        },
        {
            field: 'diskModel',
            title: '硬盘Model',
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
        },
        {
            field: 'predict',
            title: '预期',
        },
        {
            field: 'reality',
            title: '实际',
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
                osName: "OS",
                cpuUsage: 0,
                memoryUsage: [0,0],
                diskCapacityTotalUsage: [0,0],
                netReceiveSpeed: 0,
                netSendSpeed: 0,
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
            hostName: [],
            summaryPart1: [
                {
                    hostCount: [0,0],
                    sumCapacity: 0,
                },
            ],
            summaryPart2: [
                {
                    windowsHostCount: 0,
                    linuxHostCount: 0,
                },
            ],
            summaryPart3: [
                {
                    hddCount: 0,
                    ssdCount: 0,
                },
            ],
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

var dfpPartition = [30,60,100];
var dfpPartitionColor = ['#92cc76','#fac859','#ee6767'];
//数据间隔时间
var DateInterval = [24,1];
var DateIntervalText = ["最近1天","最近1小时"];
//[配置]SummaryChart
/* CPU负载统计-百分比+饼状图
   磁盘负载统计-百分比+饼状图
   内存负载统计-百分比+饼状图
*/
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
                name: 'Low',
                title: {
                    offsetCenter: ['0%', '-55%']
                },
                detail: {
                    offsetCenter: ['0%', '-40%']
                }
            },
            {
                value: 0,
                name: 'Medium',
                title: {
                    offsetCenter: ['0%', '-15%']
                },
                detail: {
                    offsetCenter: ['0%', '0%']
                }
            },
            {
                value: 0,
                count:0,
                name: 'High',
                title: {
                    offsetCenter: ['0%', '22%']
                },
                detail: {
                    offsetCenter: ['0%', '40%']
                }
            }
        ],
        title: {
            fontWeight: 'bold',
            fontSize: 20,
        },
        detail: {
            width: 60,
            height: 10,
            fontSize: 16,
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
        ['16%', '54%'],
        ['50%', '54%'],
        ['84%', '54%'],
    ],
    option: {
        title: [
            {
                text: "CPU负载统计",
                left: '8%',
                top: '0px',
            },
            {
                text: "内存负载统计",
                left: '43%',
                top: '0px',
            },
            {
                text: "磁盘负载统计",
                left: '75%',
                top: '0px',
            },
        ],
        color: ['#92cc76','#fac859','#ee6767'],
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
        var tempPieOption = JSON.parse(JSON.stringify(summaryChartOption["pieOption"]));
        tempGaugeOption["center"] = summaryChartOption["centerOption"][i];
        tempPieOption["center"] = summaryChartOption["centerOption"][i];
        option["series"].push(tempGaugeOption);
        option["series"].push(tempPieOption);
    }
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
                top: '50px',
            },
            {
                text: titleName[1],
                left: '50%',
                top: '50px',
            },
            {
                text: titleName[2],
                left: '0%',
                top: '420px',
            },
            {
                text: titleName[3],
                left: '50%',
                top: '420px',
            },
        ],
        legend: {},
        grid: [
            {
                left: '1%',
                right: '51%',
                top: '100px',
                height:'300px',
                containLabel: true
            },
            {
                left: '51%',
                right: '1%',
                top: '100px',
                height:'300px',
                containLabel: true
            },
            {
                left: '1%',
                right: '51%',
                top: '470px',
                height:'300px',
                containLabel: true
            },
            {
                left: '51%',
                right: '1%',
                top: '470px',
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
        xAxis: [],
        yAxis: [],
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
        });
        var tempYAxisOption = {
            type: 'value',
            splitLine: {
                show: true
            },
            axisLabel: {
                formatter: '{value}'+ unitLabel[i]
            },
            gridIndex: i,
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
        left:'33%',
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
        title: {
            text: "磁盘故障趋势",
            left: '48%',
        },
        grid: {
            left: '1%',
            right: '99%',
            width: '98%',
            top: '40px',
            height:'230px',
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
                show: true
            },
            axisLabel: {
                formatter: '{value}%'
            },
            max:100,
        },
        series: {
            name: "故障概率",
            smooth:true,
            type: 'line',
            showSymbol: false,
            hoverAnimation: false,
            data: [],
        },
    };
    return DFPTrendChartOption;
}

//故障类型统计chart
function FGetFailureTypeStatisticsChartOption(){
    var diskType = ["类型1","类型2","类型3","类型4","类型5",];
    var hddCount = [0,2,8,6,3];
    var ssdCount = [6,2,8,3,7];

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
        },
        grid: {
            left: '0%',
            right: '99%',
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
            left: '10%'
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
                show: true
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
function FGetFailureCountStatisticsChartOption(){
    var dataLength = 100;
    var timestamp=new Date().getTime();
    var resultData = [];
    for(var i=0;i<dataLength;i++){
        var currentDate = timestamp - (dataLength-i)*120000;
        resultData.push([currentDate,Math.ceil(Math.random()*10)]);
    }


    var FailureCountStatisticsChartOption = {
        title: {
            text: "故障盘数量统计",
            left: '48%',
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
                show: true
            },
            axisLabel: {
                formatter: '{value}'
            },
        },
        series: {
            name: "故障数量",
            smooth:true,
            type: 'line',
            showSymbol: false,
            hoverAnimation: false,
            data: resultData,
        },
    };
    return FailureCountStatisticsChartOption;
}

