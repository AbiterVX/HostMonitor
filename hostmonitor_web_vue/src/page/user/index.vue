<template>
    <div class="body">
        <div class="main">
            <el-button type="warning" @click="addflag=true">新增</el-button>
            <el-table
                class="table"
                :header-cell-style="{textAlign: 'center'}"
                :cell-style="{ textAlign: 'center' }"
                    :data="tableData"
                    height="650"
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
                     <el-table-column
                     width="280"
                    label="操作">
                        <template>
                            <div class="btn">
                                <p class="check" @click="checkflag=true">查看</p>
                                <p class="exit" @click="exitflag=true">编辑</p>
                                <p class="del">删除</p>
                            </div>
                        </template>
                    </el-table-column>
            </el-table>
        </div>
        <el-dialog
            title="新增"
            :visible.sync="addflag"
            width="30%"
            :before-close="handleClose1">
                <el-form ref="form" :model="form" label-width="80px">
                <el-form-item label="登录账号">
                    <el-input v-model="form.account"></el-input>
                </el-form-item>
                <el-form-item label="用户名">
                    <el-input v-model="form.name"></el-input>
                </el-form-item>
                <el-form-item label="用户名">
                    <div class="radio">
                        <el-radio v-model="form.radio" label="1">备选项</el-radio>
                        <el-radio v-model="form.radio" label="2">备选项</el-radio>
                    </div>
                </el-form-item>
                <el-form-item label="手机号">
                    <el-input v-model="form.tel" @input="form.tel=form.tel.replace(/\D/g,'')" ></el-input>
                </el-form-item>
                <el-form-item label="邮箱号" >
                    <el-input v-model="form.mail" @input="form.mail=form.mail.replace(/[\u4e00-\u9fa5]/g,'')"></el-input>
                </el-form-item>
                <el-form-item label="报告发送">
                    <div class="radio">
                        <el-checkbox v-model="form.checked1" >邮箱</el-checkbox>
                        <el-checkbox v-model="form.checked2" >短信</el-checkbox>
                    </div>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="onSubmit">保存</el-button>
                    <el-button @click="addflag=false">取消</el-button>
                </el-form-item>
                </el-form>
            </el-dialog>
            <el-dialog
                title="编辑"
                :visible.sync="exitflag"
                width="30%"
                :before-close="handleClose2">
                <el-form ref="form" :model="form" label-width="80px">
                <el-form-item label="登录账号">
                    <el-input v-model="form.account"></el-input>
                </el-form-item>
                <el-form-item label="用户名">
                    <el-input v-model="form.name"></el-input>
                </el-form-item>
                <el-form-item label="用户名">
                    <div class="radio">
                        <el-radio v-model="form.radio" label="1">备选项</el-radio>
                        <el-radio v-model="form.radio" label="2">备选项</el-radio>
                    </div>
                </el-form-item>
                <el-form-item label="手机号">
                    <el-input v-model="form.tel" @input="form.tel=form.tel.replace(/\D/g,'')" ></el-input>
                </el-form-item>
                <el-form-item label="邮箱号" >
                    <el-input v-model="form.mail" @input="form.mail=form.mail.replace(/[\u4e00-\u9fa5]/g,'')"></el-input>
                </el-form-item>
                <el-form-item label="报告发送">
                    <div class="radio">
                        <el-checkbox v-model="form.checked1" >邮箱</el-checkbox>
                        <el-checkbox v-model="form.checked2" >短信</el-checkbox>
                    </div>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="onSubmit1">保存</el-button>
                    <el-button @click="exitflag=false">取消</el-button>
                </el-form-item>
                </el-form>
            </el-dialog>
            <el-dialog
                title="查看"
                :visible.sync="checkflag"
                width="30%"
                :before-close="handleClose3">
                <el-form ref="form" :model="form" label-width="80px">
                <el-form-item label="登录账号">
                    <el-input v-model="form.account" disabled></el-input>
                </el-form-item>
                <el-form-item label="用户名">
                    <el-input v-model="form.name" disabled></el-input>
                </el-form-item>
                <el-form-item label="用户名">
                    <div class="radio">
                        <el-radio v-model="form.radio" label="1" disabled>备选项</el-radio>
                        <el-radio v-model="form.radio" label="2" disabled>备选项</el-radio>
                    </div>
                </el-form-item>
                  <el-form-item label="手机号">
                    <el-input v-model="form.tel" disabled></el-input>
                </el-form-item>
                <el-form-item label="邮箱号">
                    <el-input v-model="form.mail" disabled></el-input>
                </el-form-item>
                <el-form-item label="报告发送">
                    <div class="radio">
                        <el-checkbox v-model="form.checked1" disabled >邮箱</el-checkbox>
                        <el-checkbox v-model="form.checked2"  disabled>短信</el-checkbox>
                    </div>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="checkflag=false">保存</el-button>
                    <el-button @click="checkflag=false">取消</el-button>
                </el-form-item>
                </el-form>
            </el-dialog>

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
                },{
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
                }
                ],
                addflag:false,
                form: {
                    account:'123456',
                    name: '测试',
                    radio:'1',
                    tel:'15927426260',
                    mail:'123456789@qq.com',
                    checked1: false,
                    checked2: true  
                },
                checkflag:false,
                exitflag:false,

            }
        },
        methods:{
            handleClose1() {
                this.addflag=false
            },
             handleClose2() {
                this.exitflag=false
            },
             handleClose3() {
                this.checkflag=false
            },
            onSubmit(){
                console.log(this.form)
                var reg_tel = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/;    //11位手机号码正则
               if(!reg_tel.test(this.form.tel)){
                   this.$message("请输入正确的手机号码！")
               }
                var reg_mail= /^[a-z0-9A-Z]+[- | a-z0-9A-Z . _]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-z]{2,}$/;
                if(!reg_mail.test(this.form.mail)){
                    this.$message("请输入正确的邮箱地址！")
                }
                // this.addflag=false
            },
            onSubmit1(){
                this.exitflag=false
                console.log(this.form)
            }

        }

    }
</script>

<style scoped>
    tr td{
        padding:0 !important;
    }
    .radio{
        text-align: start;
    }
    .table{
        margin-top:10px;
    }
    .check{
        color:#4860E6
    }
    .exit{
        color:#FCAB1F
    }
    .del{
        color:#F01C1C
    }
    .btn{
        display: flex;
        align-items: center;
        justify-content: space-around;
    }
    .btn p{
        cursor:pointer;
        margin:0 !important;
    }
    .main{
        background-color: white;
        border-radius: 5px;;
        text-align: start;
        padding:10px;
    }
    .body{
        background-color: #F0F1F9;
        padding:10px;
        margin:0 !important;
    }
</style>