# koa 鉴权

- 掌握三种鉴权方式
	+ session/cookie
	+ token 一般使用jwt（json web token）
	+ OAuth 第三方鉴权

# 1.session-cookie

## cookie原理解析

cookie是解决http无状态性的一种手段，帮助建立客户端和服务端的连接，传输数据，方便服务端跟踪客户会话。

cookie的实现原理是客户第一次请求服务端，服务端为了保存数据，可以在响应信息中包含Set-Cookie的响应头，客户端收到响应后，会根据这个响应头信息保存在浏览器中，客户再次请求服务器时，就会在http响应头中包含一个Cookie的响应头，服务器接收请求后，可取出该响应头信息，做客户身份校验等用途。

## session原理
session是在服务端保存的一个数据结构（键值对），用来跟踪客户状态，这个数据存储在文件、内存、数据库中都是可以的，默认是保存在服务器的文件中。session依赖于sessionid,sessionid的存储有两种方式：
- 一种是通过cookie实现，服务端通过将sessionid保存到cookie中，客户端在请求是带入sessionid,服务端通过传过来的sessionid去session中取数据。
- 一种是通过URL重写技术实现的，即每次http请求时，URL携带sid的请求参数。

一般在大型应用中，往往需要做session共享，此时redis是一种很好的实现方案。下面介绍基于koa的session-cookie鉴权的实现。

# 2 JWT（json web token）

>Json web token (JWT), 是为了在网络应用环境间传递声明而执行的一种基于JSON的开放标准（(RFC 7519).该token被设计为紧凑且安全的，特别适用于分布式站点的单点登录（SSO）场景。JWT的声明一般被用来在身份提供者和服务提供者间传递被认证的用户身份信息，以便于从资源服务器获取资源，也可以增加一些额外的其它业务逻辑所必须的声明信息，该token也可直接被用于认证，也可被加密

## 2.1 基于token的鉴权机制

基于token的鉴权机制类似于http协议也是无状态的，它不需要在服务端去保留用户的认证信息或者会话信息。这就意味着基于token认证机制的应用不需要去考虑用户在哪一台服务器登录了，这就为应用的扩展提供了便利。

- 客户端发送登录请求给服务器
- 服务端验证客户信息成功后，会返回一个token给客户端
- 客户端保存token，并在下次请求时，在http请求头中带入token
- 服务端验证token成功后返回数据

## 2.2 JWT结构

JWT是由三段信息构成的，将这三段信息文本用.链接一起就构成了Jwt字符串。
- Header 头部
- Payload 载荷
- Signature 签名

### Header
在header中包含了两个部分：token和采用的加密算法，加密算法有HMAC SHA256和RSA

`{"alg":"HS256","typ","JWT"}` 
> typ 表示声明的类型为JWT; alg 表示声明的算法为HSA256

然后将头部进行base64加密（该加密是可以对称解密的),构成了第一部分,如下所示
`eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9`

### Payload

载荷就是存放有效信息的地方。这个名字像是特指飞机上承载的货品，这些有效信息包含三个部分
- 标准中注册的声明
- 公共的声明
- 私有的声明

#### 标准中注册的声明 (建议但不强制使用) ：

- iss: jwt签发者
- sub: jwt所面向的用户
- aud: 接收jwt的一方
- exp: jwt的过期时间，这个过期时间必须要大于签发时间，这是一个秒的时间戳（必须要提供的）
- nbf: 定义在什么时间之前，该jwt都是不可用的.
- iat: jwt的签发时间
- jti: jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。

#### 公共的声明
公共的声明可以添加任何的信息，一般添加用户的相关信息或其他业务需要的必要信息.但不建议添加敏感信息，因为该部分在客户端可解密.

#### 私有的声明
私有声明是提供者和消费者所共同定义的声明，一般不建议存放敏感信息，因为base64是对称解密的，意味着该部分信息可以归类为明文信息。

```
// 定义一个payload
{sub:"1234567890","name":"John Doe","admin":true}
```
然后将其进行base64加密，得到Jwt的第二部分。

`eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9`

### signature
- 创建大的签名需要使用编码后的header和payload以及一个秘钥
- 使用header中指定的算法进行签名

`var signature = HMACHSA256(base64UrlEncode(header) + '.' + base64UrlEncode(header), 'secret')`

> **注意：secret是保存在服务器端的，jwt的签发生成也是在服务器端的，secret就是用来进行jwt的签发和jwt的验证，所以，它就是你服务端的私钥，在任何场景都不应该流露出去。一旦客户端得知这个secret, 那就意味着客户端是可以自我签发jwt了。**

### 2.3 如何使用JWT

1. 用户使用它的认证信息登录系统后，会返回给用户一个JWT字符串
2. 用户通过localStorage或者cookie保存该token
3. 当用户访问一个受保护的路由或者资源时，通常在Authorization头部使用Bearer模式添加JWT,如 `Authorization: Bearer <token>`
4. 因为用户的状态在服务端的内存中是不存储的，所以这是一种无状态的认证机制
5. 服务端的保护路由将会检查http请求头Authorization中的JWT信息，如果合法，则允许用户的行为。
6. 由于JWT是自包含的，所以减少了数据库查询的需要
7. JWT的这些特性使得我们可以完全依赖其无状态的特性型提供数据API服务，甚至是创建一个下载流服务。
8. 因为JWT并不使用cookie,所以不需要担心跨域和资源共享问题（CORS）

### 2.4 基于 koa 的 JWT 示例
本示例前端采用axios发送登录认证请求，后端采用koa-jwt及jsonwebtoken实现路由

前端是一个基于vue.js的login.html页面，可以打印响应日志
```
<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Document</title>

</head>

<body>
  <div id="app">
       <div>
          <input v-model="username" />
          <input v-model="password" />
         </div>
       <div>
          <button @click="login">Login</button>
          <button @click="logout">Logout</button>
          <button @click="getUser">GetUser</button>
         </div>
       <div>
          <button @click="logs=[]">Clear Log</button>
         </div>
       
    <!-- 日志 -->
       <ul>
          <li v-for="(log,idx) in logs" :key="idx">
            {{ log }}
            </li>
         </ul>
      </div>
  <script src="vue.min.js"></script>
  <script src="./axios.min.js"></script>  
  <script>
    axios.defaults.withCredentials = true;
    axios.defaults.baseURL = 'http://localhost:3000'
    // axios通过过滤器设置token
    axios.interceptors.request.use(config => {
      const token = window.localStorage.getItem('token')
      if (token) {
        // 判断是否存在token，如果存在的话，则每个http header都加上token
        // Bearer是JWT的认证头部信息
        config.headers.common["Authorization"] = "Bearer " + token;
      }
      return config
    }, err => {
      return Promise.reject(err)
    })
    // 将axios响应内容保存到logs字段中，用于日志打印
    axios.interceptors.response.use(response => {
      app.logs.push(JSON.stringify(response.data));
      return response;
    }, err => {
      app.logs.push(JSON.stringify(response.data));
      return Promise.reject(err);
    });
    var app = new Vue({
      el: "#app",
      data: {
        username: "test",
        password: "test",
        logs: []
      },
      methods: {
        login: async function () {
          const res = await axios.post("/users/login-token", {
            username: this.username,
            password: this.password
          });
          localStorage.setItem("token", res.data.token);
        },
        logout: async function () {
          // 退出时不用请求后台了，直接将前端的token删除即可
          localStorage.removeItem("token");
        },
        getUser: async function () {
          await axios.get("/users/getUser-token");
        }
      }
    });
  </script>
</body>

</html>
```

router.js
```
const Router = require('koa-router')
const router = new Router({ prefix: '/users' })
// 定义一个秘钥
const secret = 'use token to authorize'

const jwt = require('jsonwebtoken')
const jwtAuth = require('koa-jwt')

// token鉴权
router.post('/login-token', async (ctx) => {
  const { body } = ctx.request
  console.log('body', body)
  const userinfo = body.username
  ctx.body = {
    message: '登录成功',
    user: userinfo,
    token: jwt.sign(
      {
        data: userinfo,
        exp: Math.floor(Date.now() / 1000) + 60 * 60, //1小时后过期
      },
      secret
    ),
  }
})

router.get('/getUser-token', jwtAuth({ secret }), async (ctx) => {
  //koa-jwt中间件的信息挂载ctx.state.user上
  ctx.body = {
    message: '获取数据成功',
    userinfo: ctx.state.user.data,
  }
})

module.exports = router
```

app.js

```
const Koa = require('koa')
const bodyParser = require('koa-bodyparser')
const app = new Koa()
app.use(bodyParser())

const static = require('koa-static')
app.use(static('public'))
// 添加路由中间件
const users = require('./routers/users')
app.use(users.routes())

app.listen(3000)

```

如果不使用koa-jwt的话，可以自己写一个jwt的token验证中间件，只需要将router.js中的`const jwtAuth = require('koa-jwt')` 换成 `const jwtAuth = require('./../middleware/jwtAuth')` 即可。 中间件代码如下：
```
const jsonwebtoken = require('jsonwebtoken')

const jwtAuth = ({ secret }) => async (ctx, next) => {
  const token = ctx.request.header.authorization.split(' ')[1]
  try {
    const user = jsonwebtoken.verify(token, secret)
    ctx.state.user = user
  } catch (e) {
    ctx.throw(401, e.message)
  }
  await next()
}

module.exports = jwtAuth
```





