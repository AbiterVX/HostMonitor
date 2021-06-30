import Vue from "vue";
import VueRouter from "vue-router";

Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    name: "login",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "@/page/login.vue"),
  },
  {
    path: "/homepage",
    component: () =>
    import("@/page/main.vue"),
    icon: 'el-icon-setting',
    meta: {
      title: '首页',
      keepAlive:true,
      hideInMenu: false,
  },
    children: [
        {
          path: '/homepage1',
          name: 'homepage1',
          meta: {
              title: '首页',
              keepAlive:true,
              hideInMenu: false,
            },
          component: () =>
          import('@/page/home/index.vue'),            
      },
    ] 
  },
   {
    path: "/faultQuery",
    component: () =>
    import("@/page/main.vue"),
    icon: 'el-icon-setting',
    meta: {
      title: '故障查询',
      keepAlive:true,
      hideInMenu: false,
    },
    children: [{
        path: '/faultDetect',
        name: 'faultDetect',
        icon: 'el-icon-house',
        meta: {
            title: '故障查询',
            keepAlive:true,
            hideInMenu: false,
        },
        component: () =>
        import('@/page/faultDetect/index.vue'),            
        },
    ] 
  },
  {
    path: "/dataAnalyst",
    component: () =>
    import("@/page/main.vue"),
    icon: 'el-icon-setting',
    meta: {
      title: '数据分析',
      keepAlive:true,
      hideInMenu: false,
    },
    children: [
      {
        path: '/deskInformation',
        name: 'deskInformation',
        icon: 'el-icon-house',
        meta: {
            title: '硬盘信息',
            keepAlive:true,
            hideInMenu: false,
        },
        component: () =>
        import('@/page/dataAnalyst/deskInformation/index.vue'),            
        },
      {
        path: '/resourcesMonitoring',
        name: 'resourcesMonitoring',
        icon: 'el-icon-house',
        meta: {
            title: '资源监控',
            keepAlive:true,
            hideInMenu: false,
        },
        component: () =>
        import('@/page/dataAnalyst/resourcesMonitoring/index.vue'),            
        },
    ] 
  },
  {
    path: "/failurePrediction",
    component: () =>
    import("@/page/main.vue"),
    icon: 'el-icon-setting',
    meta: {
      title: '故障预测',
      keepAlive:true,
      hideInMenu: false,
  },
    children: [
        {
          path: '/modelFound',
          name: 'modelFound',
          meta: {
              title: '模型创建',
              keepAlive:true,
              hideInMenu: false,
          },
          component: () =>
          import('@/page/failurePrediction/modelFound/index.vue'),            
      },
      {
        path: '/modelMaintenance',
        name: 'modelMaintenance',
        icon: 'el-icon-house',
        meta: {
            title: '模型维护',
            keepAlive:true,
            hideInMenu: false,
        },
        component: () =>
        import('@/page/failurePrediction/modelMaintenance/index.vue'),            
        },
        {
          path: '/modelForecast',
          name: 'modelForecast',
          icon: 'el-icon-house',
          meta: {
              title: '模型预测',
              keepAlive:true,
              hideInMenu: false,
          },
          component: () =>
          import('@/page/failurePrediction/modelForecast/index.vue'),            
          }
    ] 
  },
  {
    path: "/faultAnalysis",
    component: () =>
    import("@/page/main.vue"),
    icon: 'el-icon-setting',
    meta: {
      title: '故障分析',
      keepAlive:true,
      hideInMenu: false,
  },
    children: [
        {
          path: '/qushi',
          name: 'qushi',
          meta: {
              title: '故障趋势',
              keepAlive:true,
              hideInMenu: false,
          },
          component: () =>
          import('@/page/faultAnalysis/qushi/index.vue'),            
      },
      {
        path: '/jiexi',
        name: 'jiexi',
        icon: 'el-icon-house',
        meta: {
            title: '故障分析',
            keepAlive:true,
            hideInMenu: false,
        },
        component: () =>
        import('@/page/faultAnalysis/jiexi/index.vue'),            
        }
    ] 
  },
  {
    path: "/backup",
    component: () =>
    import("@/page/main.vue"),
    icon: 'el-icon-setting',
    meta: {
      title: '数据备份',
      keepAlive:true,
      hideInMenu: false,
  },
    children: [
        {
          path: '/beofen',
          name: 'beofen',
          meta: {
              title: '数据备份',
              keepAlive:true,
              hideInMenu: false,
          },
          component: () =>
          import('@/page/backup/index.vue'),            
      },
    ] 
  },
  {
    path: "/system",
    component: () =>
    import("@/page/main.vue"),
    icon: 'el-icon-setting',
    meta: {
      title: '系统设置',
      keepAlive:true,
      hideInMenu: false,
  },
    children: [
        {
          path: '/set',
          name: 'set',
          meta: {
              title: '系统设置',
              keepAlive:true,
              hideInMenu: false,
              id:41
          },
          component: () =>
          import('@/page/system/index.vue'),            
      },
    ] 
  },
  {
    path: "/user",
    component: () =>
    import("@/page/main.vue"),
    icon: 'el-icon-setting',
    meta: {
      title: '用户管理',
      keepAlive:true,
      hideInMenu: false,
  },
    children: [
        {
          path: '/yonghu',
          name: 'yonghu',
          meta: {
              title: '用户管理',
              keepAlive:true,
              hideInMenu: false,
          },
          component: () =>
          import('@/page/user/index.vue'),            
      },
    ] 
  },
];

const router = new VueRouter({
  routes,
});

export default router;

