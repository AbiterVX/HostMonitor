//icon图片
const icon_home = '<svg xmlns="http://www.w3.org/2000/svg"  width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-home"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path><polyline points="9 22 9 12 15 12 15 22"></polyline></svg>\n';
const icon_bar = '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-bar-chart-2"><line x1="18" y1="20" x2="18" y2="10"></line><line x1="12" y1="20" x2="12" y2="4"></line><line x1="6" y1="20" x2="6" y2="14"></line></svg>\n'
const icon_file = '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-file"><path d="M13 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V9z"></path><polyline points="13 2 13 9 20 9"></polyline></svg>\n';
const icon_empty = '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-file"></svg>\n';


function FGetNavItems(){
    return [
        document.getElementById("NavItem1"),
        document.getElementById("NavItem2"),
        document.getElementById("NavItem3"),
        document.getElementById("NavItem4"),
        document.getElementById("NavItem5"),
        document.getElementById("NavItem6"),
        document.getElementById("NavItem7"),
        //document.getElementById("NavItem8"),
    ];
}

var minRequireUserType = [
    0,
    0,
    0,
    0,
    1,
    2,
    2,
    //0,
];

var innerHTMLList = [
    icon_home + '资源监控',
    icon_bar + '节点详情',
    icon_empty + icon_bar + '数据分析',
    icon_empty + icon_file + '故障查询',
    icon_empty + icon_file + '模型训练',
    icon_file + '系统设置',
    icon_file + '用户管理',
    //icon_file + '测速',
];

var signInSrc = "/Signin";
var parentPath = "html/";
var srcHtml = [
    "DashBoard.html",
    "HostDetail.html",
    "DFPAnalysis.html",
    "DiskFailurePrediction.html",
    "DFPModelTraining.html",
    "Settings.html",
    "UserManagement.html",
    //"SpeedMeasurement.html",
];




function FInitNav(){
    //标题
    document.getElementById("PageTitle").innerText = "数据中心资源监控";
    document.getElementById("Title").innerText = "数据中心资源监控";

    //折叠栏
    document.getElementById("NavCollapse1").innerHTML = icon_file + '故障预测';

    //登录按钮
    var UserBtn = document.getElementById("UserBtn");
    var SignInBtn = document.getElementById("SignInBtn");
    var user = FGetUser();
    if(user != null){
        UserBtn.innerText = user["userName"];
        UserBtn.href = "/UserSpace";

        SignInBtn.innerText = "| 退出";
        SignInBtn.href = signInSrc;
    }
    else{
        UserBtn.href =  signInSrc;
        UserBtn.innerText = "登录";
    }


    //导航项
    var NavItems = FGetNavItems();
    var mainInfo = FGetMainInfo();
    var hostIpList = mainInfo["hostIp"];

    for(var i=0;i<NavItems.length;i++){
        NavItems[i].innerHTML = innerHTMLList[i];
        const index = i;
        NavItems[i].onclick = function (){
            if(index === 4 || index === 5 || index === 6){
                var user = FGetUser();
                if(user == null){
                    window.location.href = signInSrc;
                }
                else{
                    if(minRequireUserType[index] <= user["userType"]){
                        FSetCurrentNavItem(index);
                    }
                    else{
                        $('#UserTypeWarningModal').modal('show');
                    }
                }
            }
            else if(index === 1){
                //FSelectHost(hostIpList.length,0);
            }
            else{
                FSetCurrentNavItem(index);
            }
        }
    }

    //Host列表

    var hostList = document.getElementById("hostList");
    hostList.innerHTML = "";
    for(var i=0;i< hostIpList.length;i++){
        hostList.innerHTML +=
            '<li class="nav-item">\n' +
            '    <a id="HostItem'+i+'" class="nav-link" href="#" onclick="FSelectHost('+ hostIpList.length + ","+ i + ')">'+icon_empty +icon_file +hostIpList[i] +'</a>\n' +
            '</li>';
    }


}

function FSelectHost(count, index){
    window.sessionStorage.setItem("currentHostIndex",index);
    for(var i=0;i< count;i++){
        var currentItem = document.getElementById("HostItem"+i);
        if(index !== i){
            currentItem.style.color = '';
            currentItem.style.fontWeight = '';
        }
        else{
            currentItem.style.color ="black";
            currentItem.style.fontWeight = "bold";
        }
    }
    FSetCurrentNavItem(1);
}

//[左侧导航栏]设置当前选中的导航项
function FSetCurrentNavItem(leftNavItemIndex){
    if(leftNavItemIndex !== 1){
        var mainInfo = FGetMainInfo();
        var hostIpList = mainInfo["hostIp"];
        for(var i=0;i< hostIpList.length;i++){
            var currentItem = document.getElementById("HostItem"+i);
            currentItem.style.color = '';
            currentItem.style.fontWeight = '';
        }
    }

    if( (leftNavItemIndex !== 1 && currentIndex !== leftNavItemIndex) || leftNavItemIndex === 1){
        currentIndex = leftNavItemIndex;
        var NavItems = FGetNavItems();
        for(var i=0;i<NavItems.length;i++){
            if(i === leftNavItemIndex){
                NavItems[i].style.color ="black";
                NavItems[i].style.fontWeight = "bold";
            }
            else{
                NavItems[i].style.color = '';
                NavItems[i].style.fontWeight = '';
            }
        }
        //MainPart
        var MainPart = document.getElementById("MainPart");
        MainPart.src = parentPath + srcHtml[leftNavItemIndex];
    }
}

//[下拉菜单]-init
function FInitDropDown(dropDownMenuId,dropDownBtnId,dropDownItems,selectBtnCallback,index){
    var dropDownMenu = document.getElementById(dropDownMenuId);
    var dropDownMenuHtml = "";
    //下拉菜单-设置选项
    for(var i=0;i< dropDownItems.length;i++){
        dropDownMenuHtml += '<a class="dropdown-item" href="javascript:void(0)" ' +
            'onclick="FDropDownOnClick('+ i + ','+  selectBtnCallback +')">' + dropDownItems[i] +'</a>';
    }//
    dropDownMenu.innerHTML = dropDownMenuHtml;
    //下拉按钮-设置当前显示Txt
    document.getElementById(dropDownBtnId).innerText = dropDownItems[index];
}

//[下拉菜单]-onclick选择
function FDropDownOnClick(index,callbackFunc){
    callbackFunc(index);
}




