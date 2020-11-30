## Vue 路由
### 路由的概念
    后端路由：url链接对应服务器资源
    前端路由：单页面资源的跳转，不会刷新页面，主要通过hash（#/）来实现的
### Vue Router介绍
    vueRouter是vue.js官方的路由管理器，主要用于构建单页面应用程序。vueRouter不是组件，需要单独安装库，可以使用npm i vue-router 下载依赖。Vue Router包含的功能有：
- 嵌套的路由/视图表
- 模块化的、基于组件的路由配置
- 路由参数、查询、通配符
- 基于 Vue.js 过渡系统的视图过渡效果
- 细粒度的导航控制
- 带有自动激活的 CSS class 的链接
- HTML5 历史模式或 hash 模式，在 IE9 中自动降级
- 自定义的滚动条行为

 ### Vue Router简单使用
    一个完整的vue项目通常使用webpack来搭建，本文初步学习vue，暂不使用模块化编程来导入各模块
  初步使用vueRouter的步骤：
  - 引入vue-router.js模块
  - 定义路由组件
  - 创建路由实例，并配置路由
  - 将路由挂载到根实例上
  - 在html中使用路由导航
    ```html
    <!-- 使用 router-link 组件来导航. -->
    <!-- 通过传入 `to` 属性指定链接. -->
    <!-- <router-link> 默认会被渲染成一个 `<a>` 标签 -->
    <router-link to="/foo">Go to Foo</router-link>
    <router-link to="/bar">Go to Bar</router-link>
    ```
  - 设置路由出口
    ```html
    <!-- 路由匹配到的组件将渲染在这里 -->
    <router-view></router-view>
    ```
### router-link的class属性
    vueRouter为当前选中的router-link标签添加了两个激活样式，router-link-exact-active和router-link-active，通过为class设置css样式，可以实现路由高亮。
  1. 直接为router-link-active设置css样式
  2. 重命名router-link-active
     > 在路由构造器中使用选项linkActiveClass,来指定自定义激活状态的class名称
### vue路由动画
  1. 路由动画是用<transition>标签将<router-view>包裹
    >在<transition>标签中可以使用mode属性设置动画模式
  2. 使用.v-enter,.v-leave-to和.v-enter-active,.v-leave-active设置动画
### 路由传参
  1. 查询字符串传参
    使用查询字符串传参，不需要修改路由规则，在router-link标签的to属性路径后传查询字符串，如
    <router-link to="/login?id=1&username=zhangs"></router-link>
    在组件中使用this.$route.query可以获取查询字符串的对象
  2. 使用restful形式传参
    需要修改路由规则来匹配参数，如path : '/register/:id',在组件中通过$route.params获取参数对象
  3. 使用props选项使用路由参数
### 路由嵌套
    使用children实现子路由
### 命名视图



