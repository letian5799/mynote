# Servlet 模块学习

## B/S架构原理

B/S架构采用请求/响应模式进行交互的。如图所示：

<img src="F:\学习笔记\images\image-20201031210755079.png" alt="image-20201031210755079" style="zoom: 80%;" />

## Web服务器

web服务器是可以向发出请求的浏览器提供文档的程序，提供网上的信息浏览服务

常用的web服务器

- Microsoft：IIS

- Apache：Tomcat

- Oracle：WebLogic

- IBM：WebSphere

- Nginx
- ......

### Tomcat 服务器

我们学习主要使用 tomcat 服务器，tomcat具有以下几个优点：

- Apache Jakarta的开源项目

- 轻量级应用服务器

- 开源、稳定、资源占用小

tomcat学习推荐用8.5.x版本，8.0版本已经很少有人用了，网上资源少，tomcat8.5版本需要jdk8以上支持

- Tomcat下载   http://tomcat.apache.org/download-70.cgi

- Tomcat解压或安装

- Tomcat目录结构

| 目录     | 说明                                                       |
| -------- | ---------------------------------------------------------- |
| /bin     | 存放各种平台下用于启动和停止Tomcat的脚本文件               |
| /conf    | 存放Tomcat服务器的各种配置文件                             |
| /lib     | 存放Tomcat服务器所需的各种JAR文件                          |
| /logs    | 存放Tomcat的日志文件                                       |
| /temp    | Tomcat运行时用于存放临时文件                               |
| /webapps | 当发布Web应用时，默认情况下会将Web应用的文件存放于此目录中 |
| /work    | Tomcat把由JSP生成的Servlet放于此目录下                     |

#### Tomcat配置

- 默认端口号：8080

- 通过配置文件server.xml修改Tomcat端口号

![image_20201031211746](F:\学习笔记\images\image_20201031211746.png)

**Servlet** **规范中定义了** **web** **应用程序的目录层次：**

![image_20201031212016](F:\学习笔记\images\image_20201031212016.png)



## JSP（java server pages）

运行在服务器端的java页面，使用html嵌套java代码实现

一个简单的JSP文件如下图所示：

![image_20201031234049](F:\学习笔记\images\image_20201031234049.png)

注意字符集保持统一。

- JSP中声明和使用变量。如：`<%String title="谈北京精神";%>`

- <%=%>实现页面输出。如： `<%=title%>`

- 使用<%%>声明局部变量、使用<%!%>声明全局变量

- 使用<%@page%>导包。如：`<%@page language="java" import="java.util.Date" %>`

- 输出转义字符。如：`<%="谈\"北京精神\""%>`

### JSP内置对象（9个）

- 请求对象：request
- 输出对象：out
- 响应对象：response
- 应用程序对象：application
- 会话对象：session
- 页面上下文对象：pageContext
- 页面对象：page
- 配置对象：config
- 异常对象：exception

### Request 请求

get与post的区别

| 比较项          | get  |
| --------------- | ---- |
| 参数出现在URL中 | 是   |
| 长度限制        | 有   |
| 安全性          | 低   |
| URL可传播       | 是   |

### 解决请求中文乱码问题

- 设置请求和响应的编码方式

  request.setCharacterEncoding("utf-8");

  response.setCharacterEncoding("utf-8");

  <%@ page language="java" contentType="text/html; charset=utf-8"%>

- get请求出现乱码
  - new String(s.getBytes("iso-8859-1"),"utf-8");
  - 配置tomcat\conf\server.xml文件，URIEncoding="UTF-8"
  - 配置tomcat\conf\server.xml文件,useBodyEncodingForURI=“true”

### 属性的存取

- setAttribute

- getAttribute

属性的特性

1) 只有作用域才可以存取属性

2) 属性是key value格式存在的

3) 不能用取属性的方式获取参数

4) 属性取得后，不是String类型，是Object类型

5) 属性是可以保存对象的

### 重定向与转发

| 转发                                                         | 重定向                                                     |
| ------------------------------------------------------------ | ---------------------------------------------------------- |
| request.getRequestDispatcher("url").forward(request, response) | response.sendRedirect("url")                               |
| 始终是在服务器内部进行跳转(URL不变)                          | 在反复的重新请求服务器（URL变化）                          |
| 一次请求抵达，服务器内部跳转，所有业务处理完毕以后，一次响应。 | 多次请求，多次响应，响应中会告诉浏览器，你下一次请求的目标 |
| 安全，但是无法访问服务器外部资源                             | 可以访问任意服务器内外资源                                 |
| 可以携带属性和参数                                           | 无法携带属性和参数                                         |



## Servlet