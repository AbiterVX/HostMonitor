<template>
    <div class="body">
        <div class="top">
            <p class="title">系统概况</p>
            <div>
                <ul class="top_ul">
                    <li>Linux:{{topdata.first}}</li>
                    <li>Windows:{{topdata.second}}</li>
                    <li>硬盘:{{topdata.third}}</li>
                    <li>其他:{{topdata.four}}</li>
                </ul>
            </div>
        </div>
        <div class="footer">
            <div class="left">
                <div class="left_top">
                     <p class="title">系统概况</p>
                     <ul>
                         <li>
                             <p>Total</p>
                             <p>{{leftdata.first}}</p>
                         </li>
                         <li>
                             <p>Free</p>
                             <p>{{leftdata.second}}</p>
                         </li>
                         <li>
                             <p>Used</p>
                             <p>{{leftdata.third}}</p>
                         </li>
                     </ul>
                </div>  
                <div class="left_footer">
                     <p class="title">硬盘故障</p>
                     <div>
                         柱图
                     </div>
                </div>
            </div>
            <div class="right">
                <p class="title">硬盘健康概况</p>
                <div>
                    树图
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    data(){
        return{
            topdata:{
                first:20,
                second:30,
                third:40,
                four:50,
            },
            leftdata:{
                first:20,
                second:30,
                third:40,
            },
        }
    },
    created(){
    },
    methods:{
    }
}
</script>

<style scoped>

    .left{
        display: flex;
        flex-direction: column;
        width:49.5%;
        margin-right:1%;


    }
    .left_top{
        background-color: white;
        border-radius: 5px;
    }
    .left_footer{
         margin-top:10px;
        background-color: white;
        border-radius: 5px;
    }
    .right{
        width:49.5%;
        background-color: white;
        border-radius: 5px;
    }
    .body{
        background-color: #F0F1F9;
        padding:10px;
        margin:0 !important;
    }
    .top{
        width:100%;
        height:153px;
        border-radius: 5px;
        background-color: white;
    }
    .footer{
        width:100%;
        border-radius: 5px;
        display: flex;
        margin-top:10px;
    }
    .title{
        text-indent: 15px;;
        text-align: start;
        padding:10px 0;
        border-bottom:1px solid #E9EDF0;
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
</style>