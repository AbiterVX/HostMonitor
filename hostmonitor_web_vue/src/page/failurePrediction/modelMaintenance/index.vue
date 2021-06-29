<template>
    <div class="body">
       <div class="top">
           <p class="title">模型概述</p>
           <div>
               <ul class="top_ul">
                   <li>随机森林   v2021.05.17   6</li>
                   <li>XGBcost   v2021.05.17   8</li>
                   <li>GBDT v2021.05.17  5</li>
                   <li>其他</li>
               </ul>
           </div>
       </div>
       <div class="middle">
           <p  class="title">性能概述</p>
           <div>
               <ul class="middle_ul">
                   <li><p><span class="font">{{data.first}}</span>条</p><p>已预存数据</p></li>
                   <li><p><span class="font">{{data.second}}</span>条</p><p>故障盘</p></li>
                   <li><p><span class="font">{{data.third}}</span>条</p><p>误报</p></li>
                   <li><p><span class="font">{{data.four}}</span></p><p>召回率</p></li>
                   <li><p><span class="font">{{data.five}}</span></p><p>误报率</p></li>
               </ul>
           </div>
       </div>
       <div class="footer">
           <div class="select">
              <el-select v-model="value1" placeholder="请选择待测试模型" class="select1">
                <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
                </el-option>
              </el-select>
              <el-select v-model="value2" placeholder="请选择迭代周期" class="select1">
                <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
                </el-option>
              </el-select>
           </div>
           <div class="bar">
               <div class="bar_left">
                   左图
               </div>
               <div class="bar_right">
                   右图
               </div>
           </div>
       </div>
    </div>
</template>

<script>
export default {
    data(){
        return{
            data:{first:21421,second:100,third:6,four:'80%',five:'0.03%'},
            value1:'',
            value2:'',
            options: [{
                value: '选项1',
                label: '黄金糕'
                }, {
                value: '选项2',
                label: '双皮奶'
                }, {
                value: '选项3',
                label: '蚵仔煎'
                }, {
                value: '选项4',
                label: '龙须面'
                }, {
                value: '选项5',
                label: '北京烤鸭'
                }],
        }
    }
}
</script>

<style scoped>
    .font{
        color:#EC6941;
        font-size:30px;
    }
    .body{
        background-color: #F0F1F9;
        padding:10px;
        margin:0 !important;
    }
    ul{
        list-style: none;
        display: flex;
        justify-content: space-around;
    }
    .top_ul li{
        width:24%;
        border-right:1px solid #E9EDF0;
        height:53px;
        line-height:53px;    
    }
    .top{
        height:120px;
        background-color: white;
        border-radius: 5px;;
    }
    .middle{
        height:180px;
        background-color: white;
        border-radius: 5px;;
        margin-top:10px;
    }
     .title{
        text-indent: 15px;;
        text-align: start;
        padding:10px 0;
        border-bottom:1px solid #E9EDF0;
        margin:0 !important;
    }
    .footer{
        background-color: white;
        border-radius: 5px;;
        margin-top:10px;
    }
    .select{
        display:flex;
        align-items: center;
    }
    .select1{
        margin-left:20px;
        margin-top:20px;
    }
    .bar{
        display: flex;
    }
    .bar_left{
        width:48%;
        margin:0 1%;

    }
    .bar_right{
        width:48%;
        margin:0 1%;
    }
</style>