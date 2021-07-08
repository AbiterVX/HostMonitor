<template>
    <div class="body">
        <div class="top">
            <p class="title">系统概况</p>
            <div>
                <ul class="top_ul">
                    <li>Linux:{{summaryData.topdata.first}}</li>
                    <li>Windows:{{summaryData.topdata.second}}</li>
                    <li>硬盘:{{summaryData.topdata.third}}</li>
                    <li>其他:{{summaryData.topdata.four}}</li>
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
                             <p>{{summaryData.leftdata.first}}</p>
                         </li>
                         <li>
                             <p>Free</p>
                             <p>{{summaryData.leftdata.second}}</p>
                         </li>
                         <li>
                             <p>Used</p>
                             <p>{{summaryData.leftdata.third}}</p>
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
            summaryData:{
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

        }
    },
    created(){
        this.getData();
    },
    methods:{
        getData: function (){
            var url = '/vue_web_api/vue_web/homepage/getSummary';
            this.$http.get(url).then((response)=>{
                alert(JSON.stringify(response.data));
                this.summaryData = response.data;
            },(error)=>{
                alert("请求失败处理");
                console.log('请求失败');
                console.log(error);
            });

        }
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