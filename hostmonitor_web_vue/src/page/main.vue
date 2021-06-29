<template>
    <div class="main">
        <div class="left"  :style="{height:Height}">
            <Side></Side>
        </div>
        <div class="right">
            <div class="top">
                <Top></Top>
            </div>
            <div class="center">
                <Center></Center>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    data(){
        return{
            Height:""
        }
    },
    created(){
       this.Height=(document.documentElement.clientHeight-20)+'px';
       console.log(this.Height)
    }
    
}
</script>



<style scoped>
    .main{
        display: flex;

    }
    .left{
        width:300px;
        background-color: #4860E6;        
    }
    .right{
        flex:1;
        display: flex;
        flex-direction: column;
    }
    .top{
        height:100px;
        /* border:1px solid #666666; */
        box-sizing: border-box;
    }
</style>