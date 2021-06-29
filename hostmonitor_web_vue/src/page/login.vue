<template>
    <div class="login">
        <ul>
            <li>
                <p>账户</p>
                <el-input placeholder="请输入密码" v-model="name" ></el-input>
            </li>
            <li>
                <p>密码</p>
                <el-input placeholder="请输入密码" v-model="password" show-password></el-input>
            </li>
            <li>
                <el-button type="primary" @click="login()">登录</el-button>
            </li>
        </ul>
    </div>
</template>

<script>
export default {
    data(){
        return{
            name:'',
            password:''
        }
    },
    created(){
        this.$store.state.winHeight = document.body.clientHeight || document.documentElement.clientHeight;
    },
    mounted(){

    },
    watch:{

    },
    computed:{

    },
    methods:{
        login(){
            console.log(this.name,this.password)
            var flag=true
            if(!this.name){
                // this.$message('这是一条消息提示');
                this.$message("账号不能为空")
                flag=false
            }
            if(!this.password){
                this.$message("密码不能为空")
                 flag=false
            }
            if(flag){
                this.$router.push('/homepage')
            }
        }
    }
}
</script>
<style scoped>
    .login{
        display:flex;
        align-items: center;
        width:100%;
        height:600px;
    }
    ul{
        margin:auto;
        list-style: none;
    }
    ul li{
        display:flex;
        align-items: center;
        justify-content: center;
    }
    ul li p{
        width:60px;
        padding:0 10px;
    }
</style>