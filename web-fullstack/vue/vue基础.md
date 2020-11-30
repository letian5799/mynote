# vue基本使用
## vue介绍
>渐进式js框架，核心是mvvm
>
| m | vm | v |
| :----: | :----: | :----: |
| model|viewmodel| view | 

## vue的引入方式
1. 直接下载vue.js,通过<script src="vue.js"></script>标签引入
2. 使用npm下载vue
  > 在当前工作目录下，通过npm init -y 生成package.json文件和node_modules目录，通过<script src="./node_modules/vue/dist/vue.js"></script>引入

## vue基本语法

### 创建vue实例

```
<div id="app"></div>
let vm = new new Vue({
  el: '#app',
  data() {
    return {
        msg : 'zhangs',
        msg2 : '<h1>my vue study<h1>'
    }
  }
})
```  
### vue基本指令

- {{msg}}  插值表达式 ，用于取data中的数据，通常配合v-cloak指令使用，可以取消闪烁，用法如下：
```css html 
[v-cloak] { display : none }
<div v-cloak>{{msg}}</div>
```
- v-html    取数据，支持标签，不安全，慎用
- v-text    取文本数据
- v-pre     显示原始值
- v-once    给标签只绑定一次数据，数据变化后不会改变
- v-model   双向数据绑定
- v-bind:属性   属性绑定，简写为 :属性
- v-on:click    绑定点击事件， 简写为 @click  

### vue的class和style绑定

- 绑定class(v-bind:class)
  - 对象语法，通过v-bind:classs绑定，值用{}对象表示，对象里的key和value都是vue实例中的数据，key表示class名称，value是布尔值，用于显示切换`<div v-bind:class="{ active: isActive }"></div>`
  - class和v-bind:class可以共存
    `<div class="static" v-bind:class="{ active: isActive, 'text-danger': hasError }"></div>`
  - 数组用法
    `<div v-bind:class="[activeClass, errorClass]"></div>`
  - 数组的三元表达式写法 === 对象写法
    ```
	<div v-bind:class="[isActive ? activeClass : '', errorClass]"></div>
    <div v-bind:class="[{ active: isActive }, errorClass]"></div>
	```
  - 对象/数据可以混合使用
  - 组件中的class绑定用法
    ```
    //当在一个自定义组件上使用 class property 时，这些 class 将被添加到该组件的根元素上面。这个元素上已经存在的 class 不会被覆盖。
    //声明一个组件
    Vue.component('my-component', {
    template: '<p class="foo bar">Hi</p>'
    })
    //给组件添加class样式
    <my-component class="baz boo"></my-component>
    //html渲染结果为
    <p class="foo bar baz boo">Hi</p>
    //该方式同样适用于带数据绑定的class
    ```
    + 绑定内联样式style(v-bind:style)
      - 对象语法, CSS property 名可以用驼峰式 (camelCase) 或短横线分隔 (kebab-case，记得用引号括起来) 来命名
            <div v-bind:style="{ color: activeColor, fontSize: fontSize + 'px' }"></div>
            ```js
            data: {
                activeColor: 'red',
                fontSize: 30
            }
            ```
      - 通常用一个样式变量表示，而将所有样式都写在data数据中，看起来更直观清晰
            ```js
            <div v-bind:style="styleObject"></div>
            data: {
                styleObject: {
                    color: 'red',
                    fontSize: '13px'
                }
            }
            ```
     - 自动添加前缀，2.3+版本的vue,可以为 style 绑定中的 property 提供一个包含多个值的数组，常用于提供多个带前缀的值
            ```html
            <!--如果浏览器支持不带浏览器前缀的 flexbox，那么就只会渲染 display: flex-->
            <div :style="{ display: ['-webkit-box', '-ms-flexbox', 'flex'] }"></div>
            ```

### vue的条件渲染（v-if和v-for）
+ v-if,v-else-if,v-else
    - v-if单独使用
    `<h1 v-if="awesome">Vue is awesome!</h1>
    //当awesome为true时才渲染h1标签`
    - v-if和v-else一起使用
	```
	<h1 v-if="awesome">Vue is awesome!</h1>
	<h1 v-else>Oh no 😢</h1>
	//v-else 元素必须紧跟在带 v-if 或者 v-else-if 的元素的后面，否则它将不会被识别。
	```
    - v-else-if于v-if v-else连用
    ```
	<h1 v-if="level===1">顶级</h1>
    <h1 v-else-if="level===2">中级</h1>
    <h1 v-else>初级</h1>
	```
    - 用key管理可复用的元素
    ```
    // Vue 会尽可能高效地渲染元素，通常会复用已有元素而不是从头开始渲染。
    // 在元素上使用key属性可以防止v-if绑定的元素和v-else绑定的元素共用
    <template v-if="loginType === 'username'">
    <label>Username</label>
    <input placeholder="Enter your username" key="username-input">
    </template>
    <template v-else>
    <label>Email</label>
    <input placeholder="Enter your email address" key="email-input">
    </template>
    //上面两个input用不同的key表示，这样在条件生效时，input的值会重新刷新
	```
+ v-show 控制元素的隐藏与显示，相当于display:none
	`<h1 v-show="ok">Hello!</h1>`
    > **注意，v-show 不支持 <template> 元素，也不支持v-else**
+ v-if和v-show的区别
>v-if条件渲染是通过频繁创建与销毁dom实现的，同时v-if是惰性的，只有满足条件时才会渲染
v-show总是渲染，元素保留在dom中，通过display属性进行css显示与隐藏切换你
频繁切换时使用v-show，运行时条件很少改变用v-if

+ v-for
  - 对象
    - 第一种写法
      `<ul><li v-for="item in list">{{item}}</li></ul>`
    - 第二种写法,带索引
      `<ul><li v-for="(item,i) in list" >{{i}}{{item}}</li></ul>`
    - 第三种写法，带key，一般用这种写法，key的值是唯一的，防止元素被复用
  - 数组
    - 不带索引,k
    `<ul><li v-for="(k,v) in list">{{item}}</li></ul>`
    - 带索引的写法
    
### 自定义指令 directer（扩展功能）
- 全局定义
    >Vue.derecter('focus',function(){});
    第一个参数表示指令名称，不需要加v-,但是在模板中应用时要用v-focus

- 局部定义
在vue实例中新增directers节点，在该节点下挂载自定义的指令
    ```
	directers:{
        focus: function(){

        }
    }
	```
6. 表单
  + 单选框 radio
  + 复选框 checkbox
  + 下拉列表 select
  + 文本框 textarea
7. v-model修饰符
  - trim
  - number
  - lazy
8. 计算属性
9. 侦听器watch
10. 计算属性与侦听器的区别总结
11. 过滤器
    


