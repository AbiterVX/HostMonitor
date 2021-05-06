
//【左侧导航栏】设置左侧导航栏当前选中的导航项
function setCurrentNavItem(leftNavItemIndex,TopNavItemIndex){



    document.getElementById("Title").innerText = "数据中心资源监控";
    document.getElementById("Navbar1").innerText = "仪表盘";
    document.getElementById("Navbar2").innerText = "磁盘故障预测";
    document.getElementById("Navbar3").innerText = "主机配置";

    document.getElementById("Navbar1").href = "/";
    document.getElementById("Navbar2").href = "DiskFailurePredict";


    var navbar = document.getElementById("Navbar" + TopNavItemIndex);
    var classVal = navbar.getAttribute("class");
    classVal = classVal.concat(" active");
    navbar.setAttribute("class",classVal);


    var navItem = document.getElementById("NavItem" + leftNavItemIndex);
    navItem.style.color ="black";
    navItem.style.fontWeight = "bold";

    if(TopNavItemIndex === 1){
        document.getElementById("NavItem1").href = "/";
        document.getElementById("NavItem2").href = "HostInfo";
        document.getElementById("NavItem3").href = "ProcessIOInfo";
        //document.getElementById("NavItem4").href = "DiskFailurePredict";
        document.getElementById("NavItem5").href = "IOTest";
    }
    else if(TopNavItemIndex === 2){
        document.getElementById("NavItem11").href = "DiskFailurePredict";
        //document.getElementById("NavItem12").href = "DiskFailurePredict";
    }
    else if(TopNavItemIndex === 3){
        document.getElementById("NavItem21").href = "HostConfig";
    }
}