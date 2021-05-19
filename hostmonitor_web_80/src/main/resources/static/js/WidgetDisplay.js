//icon图片
const icon_home = '<svg xmlns="http://www.w3.org/2000/svg"  width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-home"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path><polyline points="9 22 9 12 15 12 15 22"></polyline></svg>\n';
const icon_bar = '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-bar-chart-2"><line x1="18" y1="20" x2="18" y2="10"></line><line x1="12" y1="20" x2="12" y2="4"></line><line x1="6" y1="20" x2="6" y2="14"></line></svg>\n'
const icon_file = '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-file"><path d="M13 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V9z"></path><polyline points="13 2 13 9 20 9"></polyline></svg>\n';


function FInitNavItems(){
    //标题
    document.getElementById("PageTitle").innerText = "数据中心资源监控";
    document.getElementById("Title").innerText = "数据中心资源监控";
    //导航项
    var NavItems = [
        document.getElementById("NavItem1"),
        document.getElementById("NavItem2"),
        document.getElementById("NavItem3"),
        document.getElementById("NavItem4"),
    ];
    NavItems[0].innerHTML = icon_home + '仪表盘';
    NavItems[1].innerHTML = icon_bar + '主机详情';
    NavItems[2].innerHTML = icon_file + '故障预测';
    NavItems[3].innerHTML = icon_file + '测速';
    NavItems[0].onclick = function (){
        FSetCurrentNavItem(0);
    }
    NavItems[1].onclick = function (){
        FSetCurrentNavItem(1);
    }
    NavItems[2].onclick = function (){
        FSetCurrentNavItem(2);
    }
    NavItems[3].onclick = function (){
        FSetCurrentNavItem(3);
    }

}

//[左侧导航栏]设置当前选中的导航项
function FSetCurrentNavItem(leftNavItemIndex){
    if(currentIndex !== leftNavItemIndex){
        currentIndex = leftNavItemIndex;
        var NavItems = [
            document.getElementById("NavItem1"),
            document.getElementById("NavItem2"),
            document.getElementById("NavItem3"),
            document.getElementById("NavItem4"),
        ];
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
        var parentPath = ""; //"html/";
        if(leftNavItemIndex ===0){
            MainPart.src = parentPath + "DashBoard.html";
        }
        else if(leftNavItemIndex ===1){
            MainPart.src = parentPath + "HostDetail.html";
        }
        else if(leftNavItemIndex ===2){
            MainPart.src = parentPath + "DiskFailurePrediction.html";
        }
        else if(leftNavItemIndex ===3){
            MainPart.src = parentPath + "SpeedMeasurement.html";
        }

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




