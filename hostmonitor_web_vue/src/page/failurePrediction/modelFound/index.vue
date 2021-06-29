<template>
    <div class="body">
        <div class="header">
            <div class="select">
                <el-select v-model="value1" placeholder="请选择模型" class="select1">
                <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
                </el-option>
              </el-select>

            </div>
              <p class='title'>参数设置</p>
              <div>
                  <ul class="header_ul">                      
                      <li><p>随机种子</p><el-input v-model="input" placeholder="请输入内容"></el-input></li>
                      <li><p>最大深度</p><el-input v-model="input" placeholder="请输入内容"></el-input></li>
                      <li><p>数的个数</p><el-input v-model="input" placeholder="请输入内容"></el-input></li>
                      <li><p>其他</p><el-input v-model="input" placeholder="请输入内容"></el-input></li>
                      <li><p>最小分支特征</p><el-input v-model="input" placeholder="请输入内容"></el-input></li>
                  </ul>
              </div>
        </div>
        <div class="footer">
            <div class="select">
                <el-select v-model="value2" placeholder="请选择模型" class="select1">
                <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
                </el-option>
                </el-select>
                <div>
                    <el-button type="primary">数据预处理</el-button>
                    <el-button type="primary">生成训练数据</el-button>
                    <el-button type="primary">模型训练</el-button>
                    <el-button type="primary">模型测试</el-button>
                </div>
            </div>
            <p class='title'>模型训练信息展示</p>
            <p class="text">数据信息展示数据信息展示数据信息展示数据信息展示数据信息展示数据信息展示数据信息展示数据信息展示数据信息展示数据信息展示数据信息展示</p>
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
            value2:''

        }
    }
}
</script>

<style scoped>
    .text{
        text-indent: 15px;;
        text-align: start;
        padding-bottom:20px;
    }
    .select{
        display:flex;
        align-items: center;
        justify-content: space-between;
        padding-top:20px;
    }

    .header_ul{
        display: flex;
        flex-wrap: wrap;
        padding:0 !important;
    }
    .header_ul li{
        width:30%;
        margin:0 1%;
        display: flex;
        align-items: center;
    }
    .header_ul li p{
        width:30%;
        text-align: right;
        margin-right:5%;
    }
    .header_ul li input{
        width:65%;
    }
    .select1{
        margin-left:20px;
    }
    .header{
        height:245px;
        background-color: white;
        border-radius: 5px;
    }
    .footer{
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