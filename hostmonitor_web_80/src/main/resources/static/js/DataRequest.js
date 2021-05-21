
var requestCoolDownTime = {
    RefreshDataSummary: 30000,
    RefreshDataHostInfoAll: 30000,
    RefreshDataDiskInfoAll: 30000,
    RefreshDataHostInfo: 30000,
    RefreshDataDiskInfo: 30000,
    RefreshDataHostDetailTrend: 30000,
    RefreshDataDFPInfoTrend: 30000,
    RefreshDataDFPInfoAll: 10000,
    RefreshDataSpeedMeasurementInfoAll: 10000,
}


//发送get请求
function FSendGetRequest(async,url,callbackFunc){
    //测试
    /*
    var resultData ="";
    if(url === "/Dispersed/getSummary/Dashboard"){
        resultData = {
            hostName:["hostName1", "hostName2",],
            connectedCount: 2,
            sumCapacity: 8024,
            windowsHostCount: 1,
            linuxHostCount: 1,
            hddCount: 2,
            ssdCount: 1,
            lastUpdateTime: 0,
            load: [
                [2,1,1],
                [1,2,1],
                [1,1,2]
            ],
        }
    }
    else if(url === "/Dispersed/getHostInfo/All/Dashboard"){
        resultData = {
            hostName1: {
                hostName: "hostName1",
                connected: true,
                ip: "0.0.0.1",
                osName: "Windows 10",
                memoryUsage: [4.0, 8.0],
                netReceiveSpeed: 82,
                netSendSpeed: 25,
                cpuUsage: 13,
                diskCapacityTotalUsage: [525.8,1024],
                lastUpdateTime: 0,
                diskInfoList: [
                    {
                        diskName: "ssss",
                        diskIOPS: 5.1,
                        diskReadSpeed: 10,
                        diskWriteSpeed: 15,
                        diskCapacitySize: [ 1.0,512.0],
                        type: 0
                    },
                    {
                        diskName: "vd",
                        diskIOPS: 7,
                        diskReadSpeed: 1,
                        diskWriteSpeed: 1,
                        diskCapacitySize: [5.0, 1024.0],
                        type: 0
                    },
                ],
                cpuInfoList: [
                    {
                        cpuName: "Intel i7",
                        cpuUsage: 1,
                        cpuTemperature: 10,
                    },
                ],
                gpuInfoList: [
                    {
                        gpuName: "GTX xx",
                        gpuAvailableRam: 4.0,
                    },
                ],
                processInfoList: [
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
                ]
            },
            hostName2: {
                hostName: "hostName2",
                connected: false,
                ip: "0.0.0.1",
                osName: "Windows 8",
                memoryUsage: [4.8, 8.4],
                netReceiveSpeed: 71,
                netSendSpeed: 1024,
                cpuUsage: 20,
                diskCapacityTotalUsage: [520.8,1024],
                lastUpdateTime: 0,
                diskInfoList: [
                    {
                        diskName: "sfseffQ",
                        diskIOPS: 1.0,
                        diskReadSpeed: 80,
                        diskWriteSpeed: 9,
                        diskCapacitySize: [400,1024],
                        type: 0
                    },
                ],
                cpuInfoList: [
                    {
                        cpuName: "Intel i5",
                        cpuUsage: 10,
                        cpuTemperature: 20,
                    },
                ],
                gpuInfoList: [
                    {
                        gpuName: "interl xx",
                        gpuAvailableRam: 1.0,
                    },
                ],
                processInfoList: [
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
        }
    }
    else if(url === "/Dispersed/getHostInfo/HostDetail/hostName1"){
        resultData = {
            hostName: "hostName1",
            connected: true,
            ip: "0.0.0.1",
            osName: "Windows 10",
            memoryUsage: [4.0, 8.0],
            netReceiveSpeed: 82,
            netSendSpeed: 25,
            cpuUsage: 13,
            diskCapacityTotalUsage: [525.8,1024],
            lastUpdateTime: 0,
            diskInfoList: [
                {
                    diskName: "ssss",
                    diskIOPS: 5.1,
                    diskReadSpeed: 10,
                    diskWriteSpeed: 15,
                    diskCapacitySize: [ 1.0,512.0],
                    type: 0
                },
                {
                    diskName: "vd",
                    diskIOPS: 7,
                    diskReadSpeed: 1,
                    diskWriteSpeed: 1,
                    diskCapacitySize: [5.0, 1024.0],
                    type: 0
                },
            ],
            cpuInfoList: [
                {
                    cpuName: "Intel i7",
                    cpuUsage: 1,
                    cpuTemperature: 10,
                },
            ],
            gpuInfoList: [
                {
                    gpuName: "GTX xx",
                    gpuAvailableRam: 4.0,
                },
            ],
            processInfoList: [
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
            ]
        };
    }
    else if(url === "/Dispersed/getHostInfo/HostDetail/hostName2"){
        resultData = {
            hostName: "hostName2",
            connected: true,
            ip: "0.0.0.1",
            osName: "Windows 8",
            memoryUsage: [4.8, 8.4],
            netReceiveSpeed: 71,
            netSendSpeed: 1024,
            cpuUsage: 20,
            diskCapacityTotalUsage: [520.8,1024],
            lastUpdateTime: 0,
            diskInfoList: [
                {
                    diskName: "sfseffQ",
                    diskIOPS: 1.0,
                    diskReadSpeed: 80,
                    diskWriteSpeed: 9,
                    diskCapacitySize: [400,1024],
                    type: 0
                },
            ],
            cpuInfoList: [
                {
                    cpuName: "Intel i5",
                    cpuUsage: 10,
                    cpuTemperature: 20,
                },
            ],
            gpuInfoList: [
                {
                    gpuName: "interl xx",
                    gpuAvailableRam: 1.0,
                },
            ],
            processInfoList: [
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
        };
    }
    else if(url === "/Dispersed/getDFPInfo/All"){
        resultData = [
            {
                hostName: "hostName1",
                diskName: "ssss",
                predictTime: 1621060263000,
                predictProbability: 45,
                lastUpdateTime: 0
            },
            {
                hostName: "hostName1",
                diskName: "vd",
                predictTime: 1621065263000,
                predictProbability: 25,
                lastUpdateTime: 0
            },
            {
                hostName: "hostName2",
                diskName: "sfseffQ",
                predictTime: 1621060263000,
                predictProbability: 80,
                lastUpdateTime: 0
            },
        ];
    }
    else if(url === "/Dispersed/getSpeedMeasurementInfo/All"){
        resultData = [
            {
                hostName: "hostName1",
                ioTestLastTime: 1621865263000,
                netSendSpeed: 200,
                netDownloadSpeed: 100,
                diskIOSpeed: 50,
            },
            {
                hostName: "hostName2",
                ioTestLastTime: 1601865263000,
                netSendSpeed: 100,
                netDownloadSpeed: 50,
                diskIOSpeed: 10,
            },
        ];
    }
    else if(url === "/Dispersed/getHostInfo/Trend/HostDetail/hostName1" ||
        url === "/Dispersed/getHostInfo/Trend/HostDetail/hostName2"){
        var dataLength = 100;
        var timestamp=new Date().getTime();
        resultData = [[], [], [], [], [], [] ];
        for(var i=0;i<dataLength;i++){
            var currentDate = timestamp - (dataLength-i)*120000;
            for(var j=0;j<resultData.length;j++){
                resultData[j].push([currentDate,Math.ceil(Math.random()*10)]);
            }
        }
    }
    else{
        var dataLength = 100;
        var timestamp=new Date().getTime();
        resultData = [];
        for(var i=0;i<dataLength;i++){
            var currentDate = timestamp - (dataLength-i)*120000;
            resultData.push([currentDate,Math.ceil(Math.random()*10)]);
        }
    }

    callbackFunc(resultData);
*/


    $.ajax({
        type:"get",
        dataType:"json",                //返回数据类型
        url:url,
        processData :false,             //发送的数据是否转为对象
        contentType:"application/json", //发送数据的格式
        async:async,                     //异步
        success:function (resultData) {
            callbackFunc(resultData);
        },
        error: function (err) {}
    });
}

//----------数据更新-显示更新

function FRefreshDataSummary(uiRefreshCallbackFunc){
    var mainInfo = FGetMainInfo();
    var timestamp=new Date().getTime();
    if(timestamp-mainInfo["lastUpdateTime"] >= requestCoolDownTime["RefreshDataSummary"]){
        FSendGetRequest(false,"/Dispersed/getSummary/Dashboard",function (resultData){
            mainInfo["hostName"] = resultData["hostName"];
            //summaryPart1
            mainInfo["summaryPart1"][0]["hostCount"][0] = resultData["connectedCount"];
            mainInfo["summaryPart1"][0]["hostCount"][1] = resultData["hostName"].length;
            mainInfo["summaryPart1"][0]["sumCapacity"] = resultData["sumCapacity"];
            //summaryPart2
            mainInfo["summaryPart2"][0]["windowsHostCount"] = resultData["windowsHostCount"];
            mainInfo["summaryPart2"][0]["linuxHostCount"] = resultData["linuxHostCount"];
            //summaryPart3
            mainInfo["summaryPart3"][0]["hddCount"] = resultData["hddCount"];
            mainInfo["summaryPart3"][0]["ssdCount"] = resultData["ssdCount"];
            //updateTime
            mainInfo["lastUpdateTime"] = resultData["lastUpdateTime"];
            //load
            mainInfo["summaryChart"] = resultData["load"]

            FSetData("mainInfo",mainInfo);
            uiRefreshCallbackFunc();
        });
    }
    else{
        uiRefreshCallbackFunc();
    }
}

function FRefreshDataHostInfoAll(uiRefreshCallbackFunc){
    var mainInfo = FGetMainInfo();
    var timestamp=new Date().getTime()
    if(timestamp-mainInfo["lastUpdateTime"] >= requestCoolDownTime["RefreshDataSummary"]){
        FSendGetRequest(false,"/Dispersed/getHostInfo/All/Dashboard",function (resultData){
            var hostConnectedCount = 0;
            for(var i=0;i<mainInfo["hostName"].length;i++){
                var hostName = mainInfo["hostName"][i];
                var hostInfo = FGetHostInfo(hostName);
                //连接个数
                if(resultData[hostName]["connected"] === true){
                    hostConnectedCount+=1;
                }
                //hostInfo1
                for(var key in hostInfo["hostInfo1"]){
                    hostInfo["hostInfo1"][key] = resultData[hostName][key];
                }
                //hostInfo2
                for(var key in hostInfo["hostInfo2"]){
                    hostInfo["hostInfo2"][key] = resultData[hostName][key];
                }
                //diskInfoList
                hostInfo["diskInfoList"] = resultData[hostName]["diskInfoList"];
                for(var j=0;j<hostInfo["diskInfoList"].length;j++){
                    hostInfo["diskInfoList"][j]["diskCapacityUsage"] = (hostInfo["diskInfoList"][j]["diskCapacitySize"][0] / hostInfo["diskInfoList"][j]["diskCapacitySize"][1]*100).toFixed(2);
                }

                //connected
                hostInfo["connected"] = resultData[hostName]["connected"];
                //cpuInfoList
                hostInfo["cpuInfoList"] = resultData[hostName]["cpuInfoList"];
                //gpuInfoList
                hostInfo["gpuInfoList"] = resultData[hostName]["gpuInfoList"];
                //processInfoList
                hostInfo["processInfoList"] = resultData[hostName]["processInfoList"];
                //lastUpdateTime
                hostInfo["lastUpdateTime"] = 100000;

                //更新hostInfo缓存
                FSetData("hostInfo_"+hostName,hostInfo);
            }
            uiRefreshCallbackFunc(mainInfo);
        });
    }
    else{
        uiRefreshCallbackFunc();
    }
}

function FRefreshDataDiskInfoAll(uiRefreshCallbackFunc){
    FSendGetRequest(false,"/Dispersed/getDiskInfo/All/Dashboard",function (resultData){



        uiRefreshCallbackFunc();
    });
}

function FRefreshDataHostInfo(hostName,uiRefreshCallbackFunc){
    var hostInfo = FGetHostInfo(hostName);
    var timestamp=new Date().getTime();
    if(timestamp-hostInfo["lastUpdateTime"] >= requestCoolDownTime["RefreshDataHostInfo"]){
        FSendGetRequest(false,"/Dispersed/getHostInfo/HostDetail/"+ hostName,function (resultData){
            //alert(JSON.stringify(resultData));
            //hostInfo1
            for(var key in hostInfo["hostInfo1"]){
                hostInfo["hostInfo1"][key] = resultData[key];
            }

            //hostInfo2
            for(var key in hostInfo["hostInfo2"]){
                hostInfo["hostInfo2"][key] = resultData[key];
            }
            //diskInfoList
            hostInfo["diskInfoList"] = resultData["diskInfoList"];
            for(var j=0;j<hostInfo["diskInfoList"].length;j++){
                hostInfo["diskInfoList"][j]["diskCapacityUsage"] = (hostInfo["diskInfoList"][j]["diskCapacitySize"][0] / hostInfo["diskInfoList"][j]["diskCapacitySize"][1]*100).toFixed(2);
            }

            //connected
            hostInfo["connected"] = resultData["connected"];
            //cpuInfoList
            hostInfo["cpuInfoList"] = resultData["cpuInfoList"];
            //gpuInfoList
            hostInfo["gpuInfoList"] = resultData["gpuInfoList"];
            //processInfoList
            hostInfo["processInfoList"] = resultData["processInfoList"];
            //lastUpdateTime
            hostInfo["lastUpdateTime"] = 2000000;

            //更新hostInfo缓存
            FSetData("hostInfo_"+hostName,hostInfo);

            uiRefreshCallbackFunc(hostInfo);
        });
    }
    else{
        uiRefreshCallbackFunc(hostInfo);
    }


}

function FRefreshDataHostDetailTrend(hostName,uiRefreshCallbackFunc){
    var hostInfoTrend = FGetHostInfoTrend(hostName);
    var timestamp=new Date().getTime();

    if(timestamp-hostInfoTrend["lastUpdateTime"] >= requestCoolDownTime["RefreshDataHostDetailTrend"]) {
        FSendGetRequest(false,"/Dispersed/getHostInfo/Trend/HostDetail/"+ hostName,function (resultData){
            hostInfoTrend["hostInfoTrend"] = resultData;
            FSetData("hostInfoTrend_"+hostName,hostInfoTrend);
            uiRefreshCallbackFunc(hostInfoTrend["hostInfoTrend"]);
        });
    }
    else{
        uiRefreshCallbackFunc(hostInfoTrend["hostInfoTrend"]);
    }

}

function FRefreshDataDiskInfo(hostName,uiRefreshCallbackFunc){
    FSendGetRequest(false,"/Dispersed/getDiskInfo/"+ hostName,function (resultData){


        uiRefreshCallbackFunc();
    });
}

function FRefreshDataDFPInfoTrend(hostName,diskName,uiRefreshCallbackFunc){
    var dfpInfoTrend = FGetDFPInfoTrend(hostName,diskName);
    var timestamp=new Date().getTime();
    if(timestamp-dfpInfoTrend["lastUpdateTime"] >= requestCoolDownTime["RefreshDataDFPInfoTrend"]) {
        FSendGetRequest(false,"/Dispersed/getDFPInfo/Trend/"+hostName+"/"+diskName,function (resultData){
            dfpInfoTrend["dfpInfoTrend"] = resultData;
            FSetData("dfpInfoTrend_"+hostName+"_"+diskName,dfpInfoTrend);
            uiRefreshCallbackFunc(dfpInfoTrend["dfpInfoTrend"]);
        });
    }
    else{
        uiRefreshCallbackFunc(dfpInfoTrend["dfpInfoTrend"]);
    }

}

function FRefreshDataDFPInfoAll(uiRefreshCallbackFunc){
    var dfpInfoList = FGetDFPInfoList();
    var timestamp=new Date().getTime();

    if(timestamp-dfpInfoList["lastUpdateTime"] >= requestCoolDownTime["RefreshDataDFPInfoTrend"]){
        FSendGetRequest(false,"/Dispersed/getDFPInfo/All",function (resultData){
            var dfpSummaryChart = [0,0,0];
            dfpInfoList["dfpInfo"] = resultData;
            for(var i=0;i<dfpInfoList["dfpInfo"].length;i++){
                //设置硬盘容量
                var diskInfoList = FGetHostInfo(dfpInfoList["dfpInfo"][i]["hostName"])["diskInfoList"];
                for(var j=0;j<diskInfoList.length;j++){
                    if(diskInfoList[j]["diskName"] === dfpInfoList["dfpInfo"][i]["diskName"]){
                        dfpInfoList["dfpInfo"][i]["diskCapacity"] = diskInfoList[j]["diskCapacitySize"][1];
                        break;
                    }
                }
                //统计

                for(var j=0;j<dfpPartition.length;j++){
                    if(dfpInfoList["dfpInfo"][i]["predictProbability"]  < dfpPartition[j]){
                        dfpSummaryChart[j] += 1;
                        break;
                    }
                }
            }

            dfpInfoList["dfpSummaryChart"] = dfpSummaryChart;
            FSetData("dfpInfoList",dfpInfoList);
            uiRefreshCallbackFunc(dfpInfoList["dfpInfo"],dfpInfoList["dfpSummaryChart"]);
        });
    }
    else{
        uiRefreshCallbackFunc(dfpInfoList["dfpInfo"],dfpInfoList["dfpSummaryChart"]);
    }

}

function FRefreshDataSpeedMeasurementInfoAll(uiRefreshCallbackFunc){
    var speedMeasurementInfoList = FGetSpeedMeasurementInfoList();
    var timestamp=new Date().getTime();
    if(timestamp-speedMeasurementInfoList["lastUpdateTime"] >= requestCoolDownTime["RefreshDataSpeedMeasurementInfoAll"]) {
        FSendGetRequest(false,"/Dispersed/getSpeedMeasurementInfo/All",function (resultData){
            speedMeasurementInfoList["speedMeasurementInfo"] = resultData;
            FSetData("speedMeasurementInfoList",speedMeasurementInfoList);
            uiRefreshCallbackFunc(speedMeasurementInfoList["speedMeasurementInfo"]);
        });
    }
    else{
        uiRefreshCallbackFunc(speedMeasurementInfoList["speedMeasurementInfo"]);
    }
}



