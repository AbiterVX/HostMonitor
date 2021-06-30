
<template>
    <div class="body">
        <div class="top">
            <div class="search">
                <input type="text">
                <div class="searchicon">
                    <i class="el-icon-search icon"></i>
                </div>
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
                    width="180">
                    </el-table-column>
                    <el-table-column
                    prop="name"
                    label="姓名"
                    width="180">
                    </el-table-column>
                    <el-table-column
                    prop="address"
                    width="280"
                    label="地址">
                    </el-table-column>
                    <el-table-column
                    prop="date"
                    label="日期"
                    width="180">
                    </el-table-column>
                    <el-table-column
                    prop="name"
                    label="姓名"
                    width="180">
                    </el-table-column>
                    <el-table-column
                    prop="address"
                    width="280"
                    label="地址">
                    </el-table-column>
                </el-table>
            </div>
        </div>
        <div class="footer">
            <div class="footer_left">
                <p>性能变化</p>
            </div>
            <div class="footer_right">
                <p>硬盘健康度</p>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    data(){
        return{
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
    .footer{
        display: flex; 
        margin-top:10px;      
        
    }
    .footer_left{
        width:49.5%;
        background-color: white;
        margin-right:1%;
        border-radius:5px;
    }
    .footer_right{
        width:49.5%;
        background-color: white;
        border-radius:5px;
    }
    .body{
        background-color: #F0F1F9;
        padding:10px;
        margin:0 !important;
    }
    .top{
        border-radius: 5px;
        background-color: white;
        display: flex;
        flex-direction: column;
        align-items: center;
    }
    .search{
        height:30px;
        display: flex;
        margin:30px 0;
    }

    .search input{
        width:670px;
        height:30px;
        padding:0 !important;
        box-sizing: border-box;
        border:1px solid #4860E6;
        margin:0 !important;
        
    }
    .searchicon{
        padding:0 !important;
        margin:0 !important;
        width:30px;
        height:30px;
        display: flex;
        background-color: #4860E6;
    }
    .searchicon .icon{
        margin:auto;
        color:white;
    }

</style>