<template>
  <div class="body">
    <div class="main">
      <div class="header">
        <p class="title">模型训练</p>
        <el-button type="primary">模型训练</el-button>
      </div>
      <div class="jindu">
        <p>进度:</p>
        <el-progress
          class="jindutiao"
          :text-inside="true"
          :stroke-width="26"
          :percentage="100"
          type="line"
        ></el-progress>
        <el-progress
          class="jindutiao"
          :text-inside="true"
          :stroke-width="24"
          :percentage="72"
          status="success"
        ></el-progress>
        <el-progress
          class="jindutiao"
          :text-inside="true"
          :stroke-width="22"
          :percentage="12.5"
          status="warning"
        ></el-progress>
      </div>
      <div class="model"> 
        <ul>
          <li>
              <p>模型</p>
              <el-select v-model="value1" placeholder="请选择模型" class="select">
                <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
                </el-option>
                </el-select>
            </li>
          <li>
             <p>正负样本比</p>
             <el-input v-model="input1" placeholder="请输入内容" class="input"></el-input>
             <span class="xie">/</span>
             <el-input v-model="input2" placeholder="请输入内容" class="input"></el-input>
            </li>
          <li>
              <p>验证集占比</p>
               <el-input placeholder="请输入内容" v-model="value2" class="select">
                    <template slot="append">%</template>
                </el-input>
            </li>
        </ul>
      </div>
      <div class="canshu">
          <p class='title'>主要参数(传入数组可使用“,”隔开)</p>
          <ul>
            <li>
              <p>参数</p>
               <el-input placeholder="请输入内容" v-model="value2" class="select">
                </el-input>
            </li>
             <li>
              <p>参数</p>
               <el-input placeholder="请输入内容" v-model="value2" class="select">
                </el-input>
            </li>
             <li>
              <p>参数</p>
               <el-input placeholder="请输入内容" v-model="value2" class="select">
                </el-input>
            </li>
          </ul>
      </div>
    </div>
    <div class="footer">
        <div class="footer_top">
            <p>模型训练记录</p>
            <div class="footer_top1">
                <el-input placeholder="请输入内容" v-model="input3" class="input-with-select"  >
                </el-input>
                <el-button slot="append" icon="el-icon-search"></el-button>
            </div>
        </div>
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
</template>

<script>
export default {
  data() {
    return {
      options: [
        {
          value: "选项1",
          label: "黄金糕",
        },
        {
          value: "选项2",
          label: "双皮奶",
        },
        {
          value: "选项3",
          label: "蚵仔煎",
        },
        {
          value: "选项4",
          label: "龙须面",
        },
        {
          value: "选项5",
          label: "北京烤鸭",
        },
      ],
      value1:'',
      value2:'',
      input1:'',
      input2:'',
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

    };
  },
};
</script>

<style scoped>
.footer_top1{
    display: flex;
    align-items: center;
}
.footer_top{
    display: flex;
    align-items: center;
    justify-content: space-between;
}
.footer{
    background-color: white;
    border-radius: 5px;
    padding:10px;
}
.xie{
    margin:0 10px;
}
.input{
    width:35%;
}
.select{
    width:70%
}
.model{
    margin-top:30px;
}
ul{
    list-style: none;
    display: flex;
    padding:0 !important;
}
ul li{
    display:flex;
    align-items: center;
    width:30%;
    margin:0 1%;
}
ul li p{
    margin-right:30px;
}
.jindutiao {
  height: 30px;
  width: 30%;
}
.jindu {
  display: flex;
  align-items: center;
  margin-top:30px;
}
.jindu p {
  margin: 0 !important;
  height: 30px;
  line-height: 30px;
}
.header {
  display: flex;
  justify-content: space-between;
}
.main {
  background-color: white;
  border-radius: 5px;
  padding: 10px;
}
.title {
  text-indent: 15px;
  text-align: start;
  padding: 10px 0;
  margin: 0 !important;
}
.body {
  background-color: #f0f1f9;
  padding: 10px;
  margin: 0 !important;
}
</style>