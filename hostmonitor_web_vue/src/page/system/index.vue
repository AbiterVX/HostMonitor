<template>
    <div class="body">
        <div class="main">
            <el-button class="btn" type="warning">保存并生效</el-button>
            <p class='title'>发送报告</p>
            <div class="footer">
                <el-button type="info">定时备份</el-button>
                <p>
                    设置每<el-input class="input" v-model="input1" placeholder="请输入内容"></el-input>天发送一次报告(设置为0时不发送)
                </p>
            </div>
            <div class="footer">
                <el-button type="info">紧急备份</el-button>
                <p>
                    设置硬盘故障率达到<el-input  class="input" v-model="input2" placeholder="请输入内容"></el-input>%发送报告
                </p>
            </div>

        </div>
    </div>
</template>

<script>
export default {
    data(){
        return{
            input1:'',
            input2:""
        }
    }
}
</script>

<style scoped>
    .btn{
        margin-top:50px;
    }
    .title{
         margin-top:50px;
        text-align: start;
    }
    .main{
        background-color: white;
        border-radius: 5px;
        text-align: start;
        padding-left:80px;
        height:750px;
    }
    .footer{
         margin-top:50px;
        display: flex;
        align-items: center;
    }
    .footer p{
        margin-left:80px;
    }
    .input{
        height:30px;
        width:100px;
    }
    .body{
        background-color: #F0F1F9;
        padding:10px;
        margin:0 !important;
    }
</style>