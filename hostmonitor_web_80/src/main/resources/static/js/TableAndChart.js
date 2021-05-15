
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

function FGetPercentageWithUnit(value){
    return value + "%";
}

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

//表格标题
const tableColumns = {
    //概要-1
    summaryPart1: [
        {
            field: 'hostCount',
            title: '主机个数',
            width: 100,
        },
        {
            field: 'sumCapacity',
            title: '总容量',
            width: 100,
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
            field: 'netReceive',
            title: '网络接受',
            width: 100,
            formatter : function (value, row, index) {
                return FGetKbWithUnit(value) +"/s";
            }
        },
        {
            field: 'netSend',
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
            field: 'netReceive',
            title: '网络接受',
            width: 100,
            formatter : function (value, row, index) {
                return FGetKbWithUnit(value)+"/s";
            }
        },
        {
            field: 'netSend',
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
                return FGetPercentageWithUnit(value);
            }
        },
        {
            field: 'memoryUsage',
            title: '内存占用率',
            width: 45,
            sortable: true,
            formatter : function (value, row, index) {
                return value + "%";
            }
        },
        {
            field: 'diskReadSpeed',
            title: '磁盘读取速度',
            width: 50,
            sortable: true,
            formatter : function (value, row, index) {
                return FGetKbWithUnit(value);
            }
        },
        {
            field: 'diskWriteSpeed',
            title: '磁盘写入速度',
            width: 50,
            sortable: true,
            formatter : function (value, row, index) {
                return FGetKbWithUnit(value);
            }
        },
    ],
    //故障预测信息
    diskFailurePredictInfo:[
        {
            field: 'state',
            checkbox: true,
            align: 'center',
            valign: 'middle',
            width: 15,
            formatter: function(value, row, index) {
                if(row.predictProbability === -1){
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
            field: 'diskName',
            title: '硬盘名称',
            width: 100,
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
            width: 50,
            sortable: true,
            formatter : function (value, row, index) {
                if(value === -1){
                    return FGetLoadingImg();
                }
                else{
                    return FGetPercentageWithUnit(value);
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
};

//表格数据
var tableData={
    hostName:[
        "hostName1",
        "hostName2",
    ],
    summaryPart1:[
        {
            hostCount: "70/70",
            sumCapacity: "4.0TB",
        },
    ],
    summaryPart2:[
        {
            windowsHostCount: 60,
            linuxHostCount: 10,
        },
    ],
    summaryPart3:[
        {
            hddCount: 40,
            ssdCount: 30,
        },
    ],
    hostInfo1:{
        hostName1:[
            {
                osName: "Windows 10",
                cpuUsage: 13,
                memoryUsage: [4.0,8.0],
                diskCapacityTotalUsage: [525.8,1024],
                netReceive: 82,
                netSend: 25,
            },
        ],
        hostName2:[
            {
                osName: "Windows 10",
                cpuUsage: 20,
                memoryUsage: [4.8,8.4],
                diskCapacityTotalUsage: "520 GB / 1.0 TB",
                netReceive: 71,
                netSend: 1024
            },
        ]
    },
    diskInfo:{
        hostName1:[
            {
                diskName: "ssss",
                diskCapacitySize: [512.0 ,1.0 ],
                diskCapacityUsage: 50,
                diskIOPS: 5.1,
                diskReadSpeed: 10,
                diskWriteSpeed: 5,
            },
            {
                diskName: "vd",
                diskCapacitySize: [5.0 ,1024.0 ],
                diskCapacityUsage: 1,
                diskIOPS: 7,
                diskReadSpeed: 1,
                diskWriteSpeed: 1,
            },
        ],
        hostName2:[
            {
                diskName: "sfseffQ",
                diskCapacitySize: "525 GB / 1.0 TB",
                diskCapacityUsage: 71,
                diskIOPS: 1.0,
                diskReadSpeed: 80,
                diskWriteSpeed: 8,
            },
        ]
    },
    hostInfo2:{
        hostName1:[
            {
                osName: "Windows 10",
                ip: "0.0.0.0",
                memoryUsage: [4.0,8.0],
                diskCapacityTotalUsage: [50,1024],
                netReceive: 82,
                netSend: 25,
            },
        ],
        hostName2:[
            {
                osName: "Windows 10",
                ip: "0.0.0.1",
                memoryUsage: [3.5,8.0],
                diskCapacityTotalUsage: [750,1024],
                netReceive: 0,
                netSend: 0,
            },
        ]
    },
    cpuInfo:{
        hostName1:[
            {
                cpuName: "Intel i7",
                cpuUsage: 1,
                cpuTemperature: 10,
            },
        ],
        hostName2:[
            {
                cpuName: "Intel i5",
                cpuUsage: 10,
                cpuTemperature: 20,
            },
        ]
    },
    gpuInfo:{
        hostName1:[
            {
                gpuName: "GTX xx",
                gpuAvailableRam: 4.0,
            },
            {
                gpuName: "RTX xx",
                gpuAvailableRam: 6.0,
            },
        ],
        hostName2:[
            {
                gpuName: "interl xx",
                gpuAvailableRam: 1.0,
            },
        ]
    },
    processInfo:{
        hostName1:[
            {
                processId: "101",
                processName: "java",
                startTime: 1621065263000,
                cpuUsage: 13,
                memoryUsage: 10,
                diskReadSpeed: 70,
                diskWriteSpeed: 0,
            },
            {
                processId: 200,
                processName: "mysql",
                startTime: 1620065263000,
                cpuUsage: 5,
                memoryUsage: 5,
                diskReadSpeed: 0,
                diskWriteSpeed: 0,
            },
        ],
        hostName2:[
            {
                processId: 10,
                processName: "java",
                startTime: 1621065263000,
                cpuUsage: 50,
                memoryUsage: 20,
                diskReadSpeed: 40,
                diskWriteSpeed: 50,
            },
        ]
    },
    diskFailurePredictInfo:[
        {
            //state: false,
            hostName: "hostName1",
            diskName: "disk test name",
            diskCapacity: 1024,
            predictTime: 1621060263000,
            predictProbability: 45,
        },
        {
            //state: false,
            hostName: "hostName2",
            diskName: "disk test name2",
            diskCapacity: 1890,
            predictTime: 1621065263000,
            predictProbability: 25,
        },
        {
            //state: false,
            hostName: "hostName3",
            diskName: "disk test name3",
            diskCapacity: 1090,
            predictTime: 1621060263000,
            predictProbability: 80,
        },
    ],
    speedMeasurement:[
        {
            state: false,
            hostName: "hostName1",
            ioTestLastTime: 1621865263000,
            netSendSpeed: 200,
            netDownloadSpeed: 100,
            diskIOSpeed: 50,
        },
        {
            state: false,
            hostName: "hostName2",
            ioTestLastTime: 1601865263000,
            netSendSpeed: 100,
            netDownloadSpeed: 50,
            diskIOSpeed: 10,
        },
    ]
};

var DateInterval = [60,24*60];
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
                text: "磁盘负载统计",
                left: '43%',
                top: '0px',
            },
            {
                text: "内存负载统计",
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
                var returnTxt = "时间: "+params[0].value[0] +"<br/>";
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
                var returnTxt = "时间: "+params[0].value[0] +"<br/>";
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