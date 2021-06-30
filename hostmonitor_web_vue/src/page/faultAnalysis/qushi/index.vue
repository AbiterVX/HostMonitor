<template>
    <div class="body">
        <div class="header">
            <p class='title'>预期指标</p>
            <div>
                柱图
            </div>
        </div>
        <div class="footer">
            <div class='select'>
                <el-select v-model="value1" placeholder="请选择硬盘厂商" class="select1">
                <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
                </el-option>
                </el-select>
                <el-select v-model="value2" placeholder="请选择硬盘系列" class="select1">
                <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
                </el-option>
                </el-select>
                <el-select v-model="value3" placeholder="请选择年度、季度、月度" class="select1">
                <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
                </el-option>
                </el-select>

            </div>
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
            value1:'',
            value2:'',
            value3:''
        }
    }
}
</script>

<style scoped>
    .select{
        display:flex;
        align-items: center;
        padding-top:20px;
    }
     .select1{
        margin-left:20px;
    }
    .title{
        text-indent: 15px;;
        text-align: start;
        padding:10px 0;
        border-bottom:1px solid #E9EDF0;
        margin:0 !important;
    }
     .body{
        background-color: #F0F1F9;
        padding:10px;
        margin:0 !important;
    }
    .header{
        margin-top:10px;
        border-radius: 5px;
        background-color: white;
    }
    .footer{       
        margin-top:10px;
        border-radius: 5px;
        background-color: white;
    }
</style>