<template>
    <div class="top" >
        <div class="user">
            <img :src="src" alt="">
            <span>欢迎!</span>
            <span>{{name}}</span>
            <span>|</span>
            <span>退出</span>
        </div>
        <!-- <ul>
            <li v-for="(item,i) in navlist" :key='i' @click="cut(item,i)" :class="{active:i===index}">
                {{item.meta.title}}
                <i class="el-icon-error" @click="del(i)"></i>
            </li>
            <li @click="delall">关闭全部</li>
            <li @click="delqita">关闭其他</li>
        </ul> -->
    </div>
</template>

<script>
export default {
    data(){
        return{
            navlist:[],
            index:'',
            flag:false,
            name:'admin',
            src:require('@/assets/logo.png')
        }
    },
    created(){
        this.navlist=this.$store.state.navlist
        // console.log(this.navlist)
        console.log(this.$store.state.navlist)
    },
    mounted(){

    },
    watch:{
    },
    methods:{
        delqita(){
            console.log(this.index)
            console.log(this.navlist)
            if(this.navlist.length>0 && this.flag){
                var i=this.index
                var arr=[]
                console.log(this.navlist[i])
                arr.push(this.navlist[i])
                console.log(arr)
                this.$store.state.navlist=[]
                this.$store.state.navlist=arr
                this.navlist=this.$store.state.navlist
                this.flag=false
                this.index=''
            }
        },
        delall(){
            this.$store.state.navlist=[]
            this.navlist=this.$store.state.navlist
            this.flag=false
            this.index=''
        },
        cut(item,i){
            console.log(item,i)
            // this.$router.push(item.path)
            this.index=i
            this.flag=true
            console.log(this.index)
            if(this.$route.path !== item.path){
                this.$router.push(item)
            }
        },
        del(i){
            console.log(i)
            if(this.navlist.length>1){
                 this.navlist.splice(i,1)
            }else{
                this.$message('已经是最后一个页面了！')
            }
        }
    }
}
</script>



<style scoped>
    .user{
        width:200px;
        height:50px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-right:30px;
    }
    .user span{
        padding:0 3px;
    }
    .user img{
        width:50px;
        height:50px;
        border-radius: 50%;;
    }
    .active{
        background-color: yellowgreen;
    }
    ul{ 
        width:100%;
        list-style:none;
        display: flex;
    }
    .top{
        display: flex;
        align-items: center;
        justify-content: flex-end;
    }
    ul li{
        padding:0 20px;
        cursor: pointer;
    }
</style>