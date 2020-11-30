# dva学习笔记
**dva官网**：https://dvajs.com/
官网提供了详细的介绍和入门案例，也有源码解析，易学易用。
## 介绍
dva是一个轻量级的应用框架，它是基于redux和redux-saga的数据流方案，其本身内置了react-router和fetch，便于简化开发。
## 特点
- dva只有6个API，对redux用户尤其友好
- elm概念，通过reducers,effects和subcriptions组织model
- 插件机制，如dva-loading可以自动处理loading状态
- 支持HMR，基于babel-plugin-dva-html实现components、routers和models的HMR

