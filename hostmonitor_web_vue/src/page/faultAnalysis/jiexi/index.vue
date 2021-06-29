<template>
    <div class="body">
        <div class="header">
            <div class="header_left">
                <div class="top">
                    <el-button type="warning">筛选</el-button>
                    <el-input class="input" v-model="input" placeholder="请输入硬盘的SN码"></el-input>
                    <el-date-picker
                    class='date'
                    v-model="value1"
                    type="daterange"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期">
                    </el-date-picker>
                </div>
                <div>
                    <ul>
                        <li><span class="font">SN:</span><span>{{data.first}}</span></li>
                        <li><span class="font">类型:</span><span>{{data.second}}</span></li>
                        <li><span class="font">厂商:</span><span>{{data.third}}</span></li>
                        <li><span class="font">系列:</span><span>{{data.four}}</span></li>
                    </ul>
                </div>
                <div>
                    <el-table
                    :data="tableData"
                    height="350"
                    border
                    style="width: 100%">
                    <el-table-column
                    prop="date"
                    label="日期"
                    width="100">
                    </el-table-column>
                    <el-table-column
                    prop="name"
                    label="姓名"
                    width="80">
                    </el-table-column>
                    <el-table-column
                    prop="address"
                    width="180"
                    label="地址">
                    </el-table-column>
                    <el-table-column
                    prop="date"
                    label="日期"
                    width="100">
                    </el-table-column>
                    <el-table-column
                    prop="name"
                    label="姓名"
                    width="80">
                    </el-table-column>
                    <el-table-column
                    prop="address"
                    width="180"
                    label="地址">
                    </el-table-column>
                </el-table>

                </div>
            </div>
            <div class="header_right">
                <div class="yinsu">
                    <p class='title'>故障影响因素</p>
                    <div>
                        柱图
                    </div>
                </div>
                <div class="fenxi">
                    <p class='title'>故障原因分析</p>
                    <div>
                        饼图
                    </div>
                </div>
            </div>
        </div>
        <div class="footer">
            <p class='title'>特征分析</p>
            <div>
                折线图
            </div>
        </div>
    </div>
</template>

<script>
export default {
    data(){
        return{
            input:'',
            value1:'',
            data:{
                first:'AAA',
                second:'BBB',
                third:'CCC',
                four:'DDD',
            },
            tableData: [{
                date: '2016-05-03',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1518 弄'
                }, {
                date: '2016-05-02',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1518 弄'
                }, {
                date: '2016-05-04',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1518 弄'
                }, {
                date: '2016-05-01',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1518 弄'
                }, {
                date: '2016-05-08',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1518 弄'
                }, {
                date: '2016-05-06',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1518 弄'
                }, {
                date: '2016-05-07',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1518 弄'
                }]

        }
    }
}
</script>

<style scoped>
    ul{
        display: flex;
        list-style:none;
        justify-content: space-between;
        padding:0 !important;
    }
    .font{
        font-weight:700;
    }
    .header{
        display: flex;
    }
    .header_right{
        width:49.5%;
    }
    .input{
        width:170px;
    }
    .top{
        margin-top:10px;
        display: flex;
        align-items:center;
        justify-content: space-between;
    }
    .footer{
        background-color: white;
        border-radius: 5px;
        margin-top:10px;
    }
    .header_left{
        background-color: white;
        border-radius: 5px;
        width:49.5%;
        margin-right:1%;
        padding:0 10px;
    }
    .yinsu{
        background-color: white;
        border-radius: 5px;
    }
    .fenxi{
        margin-top:10px;
        background-color: white;
        border-radius: 5px;
    }
     .body{
        background-color: #F0F1F9;
        padding:10px;
        margin:0 !important;
    }
      .title{
        text-indent: 15px;;
        text-align: start;
        padding:10px 0;
        border-bottom:1px solid #E9EDF0;
        margin:0 !important;
    }
</style>