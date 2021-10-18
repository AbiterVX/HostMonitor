
var requestCoolDownTime = {
    RefreshDataSummary: 0,
    RefreshDataHostInfoAll: 0,
    RefreshDataDiskInfoAll: 0,
    RefreshDataHostInfo: 0,
    RefreshDataDiskInfo: 0,
    RefreshDataHostDetailTrend: 0,
    RefreshDataDFPInfoTrend: 0,
    RefreshDataDFPInfoAll: 0,
    RefreshDataSpeedMeasurementInfoAll: 0,
}


//发送get请求
function FSendGetRequest(async,url,callbackFunc){
    $.ajax({
        type:"get",
        dataType:"json",                //返回数据类型
        url:url,
        processData :false,             //发送的数据是否转为对象
        contentType:"application/json", //发送数据的格式
        async:async,                     //异步
        success:function (resultData) {
            //alert(JSON.stringify(resultData));
            callbackFunc(resultData);
        },
        error: function (err) {}
    });
}

//发送get请求
function FSendPostRequest(async,url,parameterData,callbackFunc){
    $.ajax({
        type:"post",
        dataType:"text",                //返回数据类型
        url:url,
        data:parameterData,
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
        FSendGetRequest(false,"/getSummary/Dashboard",function (resultData){
            mainInfo["hostIp"] = resultData["hostIp"];

            mainInfo["summaryPart"] = [
                {
                    hostCount: resultData["hostIp"].length,
                    connectedCount: resultData["connectedCount"],
                    sumCapacity: resultData["sumCapacity"],
                    windowsHostCount: resultData["windowsHostCount"],
                    linuxHostCount: resultData["linuxHostCount"],
                    hddCount: resultData["hddCount"],
                    ssdCount: resultData["ssdCount"],
                }
            ];


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
    FSendGetRequest(false, "/getHostInfo/All/Dashboard", function (resultData) {

        var hostConnectedCount = 0;
        for (var i = 0; i < mainInfo["hostIp"].length; i++) {

            var ip = mainInfo["hostIp"][i];
            var hostInfo = FGetHostInfo(ip);

            //连接个数
            if (resultData[ip]["connected"] === true) {
                hostConnectedCount += 1;
            }
            //hostInfo1
            for (var key in hostInfo["hostInfo1"]) {

                hostInfo["hostInfo1"][key] = resultData[ip][key];
            }

            //hostInfo2
            for (var key in hostInfo["hostInfo2"]) {
                hostInfo["hostInfo2"][key] = resultData[ip][key];
            }

            //diskInfoList
            hostInfo["diskInfoList"] = resultData[ip]["diskInfoList"];
            for (var j = 0; j < hostInfo["diskInfoList"].length; j++) {
                hostInfo["diskInfoList"][j]["diskCapacityUsage"] = (hostInfo["diskInfoList"][j]["diskCapacitySize"][0] / hostInfo["diskInfoList"][j]["diskCapacitySize"][1] * 100).toFixed(2);
            }

            //connected
            hostInfo["connected"] = resultData[ip]["connected"];
            //cpuInfoList
            hostInfo["cpuInfoList"] = resultData[ip]["cpuInfoList"];
            //gpuInfoList
            hostInfo["gpuInfoList"] = resultData[ip]["gpuInfoList"];
            //processInfoList
            hostInfo["processInfoList"] = resultData[ip]["processInfoList"];
            //lastUpdateTime
            //hostInfo["lastUpdateTime"] = 100000;

            //更新hostInfo缓存
            FSetData("hostInfo_" + ip, hostInfo);
        }
        uiRefreshCallbackFunc();
    });
}

function FRefreshDataDiskInfoAll(uiRefreshCallbackFunc){
    FSendGetRequest(false,"/getDiskInfo/All/Dashboard",function (resultData){



        uiRefreshCallbackFunc();
    });
}

function FRefreshDataHostInfo(hostName,uiRefreshCallbackFunc){
    var hostInfo = FGetHostInfo(hostName);
    var timestamp=new Date().getTime();
    if(timestamp-hostInfo["lastUpdateTime"] >= requestCoolDownTime["RefreshDataHostInfo"]){
        FSendGetRequest(false,"/getHostInfo/HostDetail/"+ hostName,function (resultData){
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
            //hostInfo["lastUpdateTime"] = 2000000;

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
        FSendGetRequest(false,"/getHostInfo/Trend/HostDetail/"+ hostName,function (resultData){
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
    FSendGetRequest(false,"/getDiskInfo/"+ hostName,function (resultData){


        uiRefreshCallbackFunc();
    });
}

function FRefreshDataDFPInfoTrend(ip,diskName,uiRefreshCallbackFunc){
    var dfpInfoTrend = FGetDFPInfoTrend(ip,diskName);
    var timestamp=new Date().getTime();
    if(timestamp-dfpInfoTrend["lastUpdateTime"] >= requestCoolDownTime["RefreshDataDFPInfoTrend"]) {
        FSendGetRequest(false,"/getDFPInfo/Trend/"+ip+"/"+diskName,function (resultData){
            dfpInfoTrend["dfpInfoTrend"] = resultData;
            FSetData("dfpInfoTrend_"+ip+"_"+diskName,dfpInfoTrend);
            uiRefreshCallbackFunc(dfpInfoTrend["dfpInfoTrend"]);
        });
    }
    else{
        uiRefreshCallbackFunc(dfpInfoTrend["dfpInfoTrend"]);
    }

}

function FRefreshDataDFPInfoAll(uiRefreshCallbackFunc){
    FSendGetRequest(false,"/getDFPInfo/List",function (resultData){
        if(resultData != null){
            /*for(var i=0;i<resultData.length;i++){
                resultData[i]["predictProbability"] *= 100;
            }*/

            var hostDiskMap = {};
            for(var i=0;i<resultData.length;i++){
                var currentIp = resultData[i]["ip"];
                var currentDiskName = resultData[i]["diskSerial"];
                if(hostDiskMap.hasOwnProperty(currentIp)){
                    hostDiskMap[currentIp].push(currentDiskName);
                }
                else{
                    hostDiskMap[currentIp] = [];
                    hostDiskMap[currentIp].push(currentDiskName);
                }
            }
            FSetData("dfpInfoList",resultData);
            FSetData("hostDiskMap",hostDiskMap);
        }



        uiRefreshCallbackFunc(resultData,hostDiskMap);



    });
}

//-----故障预测

function FDFPTrain(paramData,uiRefreshCallbackFunc){
    FSendPostRequest(false,"/dfpTrain",JSON.stringify(paramData),function (resultData){
        uiRefreshCallbackFunc(resultData);
    });
}

function FRefreshDataDFPSummaryInfo(uiRefreshCallbackFunc){
    FSendGetRequest(false,"/getDFPSummaryInfo",function (resultData){
        uiRefreshCallbackFunc(resultData);
    });
}

function FRefreshDataDFPTrainProgress(uiRefreshCallbackFunc){
    FSendGetRequest(false,"/getDFPTrainProgress",function (resultData){
        var isTraining = true;
        for(var i=0;i<resultData.length;i++){
            if(resultData[i] === -1){
                isTraining = false;
                break;
            }
        }
        if(isTraining){
            uiRefreshCallbackFunc(resultData);
        }
        else{
            uiRefreshCallbackFunc(null);
        }
    });
}





//-----用户

function FSetUser(resultData){
    window.sessionStorage.setItem("User",resultData);
}

function FGetUser(){
    var user = window.sessionStorage.getItem("User");
    if(user == null){
        return null;
    }
    else{
        return JSON.parse(user);
    }
}

function FGetUserList(uiRefreshCallbackFunc){
    FSendGetRequest(false,"/getUsers",function (resultData){
        uiRefreshCallbackFunc(resultData);
    });
}

//用户登录
function userSignIn(userIDValue,passwordValue,callbackFunc){
    var parameterData = {
        userID: userIDValue,
        password: passwordValue,
    };
    FSendPostRequest(false,"/SignIn",JSON.stringify(parameterData),function (resultData){
        if(resultData == null || resultData === ""){
            //wrong!
        }
        else{
            FSetUser(resultData);
        }
        callbackFunc(resultData);
    });
}
