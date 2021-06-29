<template>
    <div class="left" :style="{height:topheight+'px'}">
            <div class="title" ref="ceshi">
                <p>DiskFailurePredict</p>
            </div>
            <div >
                <el-menu
                default-active="1"
                class="el-menu-vertical-demo"
                @open="handleOpen"
                @close="handleClose"
                background-color="#4860E6"
                text-color="#fff"
                active-text-color="#ffd04b">
                <el-submenu  v-for="(item,i) in navlist" :key="i" :index="i.toString()">
                    <template slot="title">
                        <i :class="item.icon"></i>
                        <span >{{item.meta.title}}</span>
                    </template>
                    <div v-if="item.children">
                        <el-menu-item    v-for="(item1,i1) in item.children" :key="i1"  @click="tiaozhuan(item1)">
                            <template slot="title">{{item1.meta.title}}</template>
                        </el-menu-item>
                    </div>
                </el-submenu>
                </el-menu> 
            </div>
    </div>
</template>

<script>
import navdata from "@/router"
export default {
    data(){
        return{
            navlist:[],
            topheight:0
        }
    },
    created(){
        var  data=navdata.options.routes
        for(var i=0;i<data.length;i++){
            if(data[i].children && data[i].children.length>0){
                 this.navlist.push(data[i])
            }
        }
        console.log(this.navlist)    
        this.topheight=this.$store.state.winHeight    
    },
    mounted(){
        console.log(this.$store.state.navlist)
    },
    watch:{
        '$route'(to,from){
            console.log(to,from)
            return to
        }
    },
    computed:{
        hieghtdata(){
            var height=this.$store.state.winHeight
            console.log(height)
            return height
        }
    },
    methods: {
        tiaozhuan(item){
                console.log(item)
                console.log(this.$store.state.navlist)
                console.log(this.$store.state.navlist.length)
                var flag=true
                if(this.$store.state.navlist.length<0){
                    this.$store.state.navlist.push(item)
                }else{
                    for(var i=0;i<this.$store.state.navlist.length;i++){
                        console.log(this.$store.state.navlist[i].path,item.path)
                        if(this.$store.state.navlist[i].path == item.path){
                            flag=false
                        }
                    }
                    console.log(flag)
                    if(flag){
                        this.$store.state.navlist.push(item)
                    }
                    if(this.$route.path !== item.path){
                        this.$router.push(item)
                    }
                }
            },
        handleOpen(key, keyPath) {
            console.log(key, keyPath);
        },
        handleClose(key, keyPath) {
            console.log(key, keyPath);
        }
    }
}
</script>



<style scoped>
    .title{
        background-color:#4860E6 ;
        display: flex;
        height:100px;
    }
    .title p{
       margin:auto;
       font-size:26px;
       /* width:120px;
       height:40px;
       line-height: 40px;
       text-align: center;
       background-color: #758B9F;
       border-radius: 5px;    */
       color:white;  
    }
    .el-menu-vertical-demo{
        width:300px !important;
        background-color: #4860E6;
        height:100%;
    }
</style>