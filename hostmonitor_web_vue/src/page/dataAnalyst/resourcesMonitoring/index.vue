<template>
    <div class="body">
        <div class="header">
            <div class="top">
                <p>平均负载</p>
                <ul class="ul">
                    <li v-for="(item,index) in datedata" :key='index' :class="{activeclass:index==current}" @click="addclass(index)">{{item.title}}</li>
                </ul>
            </div>
            <div class="top_footer">
                <div>
                    左图
                </div>
                <div>
                    右图
                </div>
            </div>
        </div>

        <div class="footer">
            <div class="footer_left">
                左图
            </div>
            <div class="footer_right">
                右图
            </div>
        </div>
    </div>
</template>

<script>
export default {
    data(){
        return{
            datedata:[{title:'昨天'},{title:'今天'},{title:'最近7天'},{title:'最近30天'},{title:'自定义时间'}],
            activeclass:"",
            current:0           
        }
    },
    methods:{
        addclass(index){
            this.current=index
        }
       
    }
}
</script>


<style  scoped>
    .footer_left{
        background-color: white;
        border-radius: 5px;
        width:49.5%;
        margin-right:1%;
    }
    .footer_right{
        background-color: white;
        border-radius: 5px;
        width:49.5%;
    }
    .footer{
        display: flex;
        margin-top:10px;
    }
    .header{
        background-color: white;
        border-radius: 5px;
    }
    .body{
        background-color: #F0F1F9;
        padding:10px;
        margin:0 !important;
    }
    .top_footer{
        display: flex;
    }
    .top{
        display: flex;
        justify-content: space-between;
    }
    .activeclass{
        background-color: #EC6941;
        color:white;
    }
    .ul{
        display:flex;
        list-style:none;
    }
    .ul li{
        border:1px solid #FFCCBC;
        width:90px;
        height:30px;
        line-height: 30px;
        text-align: center;
        cursor:pointer;
    }
</style>