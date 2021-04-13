
//【左侧导航栏】设置左侧导航栏当前选中的导航项
function setCurrentNavItem(navItemIndex){
    var navItem = document.getElementById("NavItem" + navItemIndex);
    navItem.style.color ="black";
    navItem.style.fontWeight = "bold";

    document.getElementById("Title").innerText = "数据中心资源监控";
    document.getElementById("Navbar1").innerText = "仪表盘";

    document.getElementById("NavItem1").href = "/";
    document.getElementById("NavItem2").href = "HostInfo";
    document.getElementById("NavItem3").href = "ProcessIOInfo";
    document.getElementById("NavItem4").href = "DiskFailurePredict";
    document.getElementById("NavItem5").href = "IOTest";


}