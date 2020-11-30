# SpringBoot 2.X 学习

## 1. SpringBoot 概念

SpringBoot用于简化Spring应用配置，采用“习惯优于配置”的方式开发，它是快速构建Spring应用的工具。

## 2. SpringBoot 开发要求

- 会使用maven构建项目
- jdk1.8及以上版本
- 建议使用IDEA编辑器
- 具备SpringMVC基础
- 会配置pom.xml，引用各种starter启动器简化配置 

## 3. SpringBoot 基本目录结构

![image-20201102213302189](D:\github\images\image-20201102213302189.png)

## 4. SpringBoot 启动器介绍

Spring Boot应用启动器基本的一共有44种，常用的启动器以加粗表示，具体如下：

1）**spring-boot-starter 	这是Spring Boot的核心启动器，包含了自动配置、日志和YAML。**

2）**spring-boot-starter-actuator 	帮助监控和管理应用。**

3）spring-boot-starter-amqp 	通过spring-rabbit来支持AMQP协议（Advanced Message Queuing Protocol）。

4）**spring-boot-starter-aop 	支持面向方面的编程即AOP，包括spring-aop和AspectJ。**

5）spring-boot-starter-artemis 	通过Apache Artemis支持JMS的API（Java Message Service API）。

6）spring-boot-starter-batch 	支持Spring Batch，包括HSQLDB数据库。

7）spring-boot-starter-cache 	支持Spring的Cache抽象。

8）spring-boot-starter-cloud-connectors 	支持Spring Cloud Connectors，简化了在像Cloud Foundry或Heroku这样的云平台上连接服务。

9）spring-boot-starter-data-elasticsearch 	支持ElasticSearch搜索和分析引擎，包括spring-data-elasticsearch。

10）spring-boot-starter-data-gemfire 	支持GemFire分布式数据存储，包括spring-data-gemfire。

11）spring-boot-starter-data-jpa 	支持JPA（Java Persistence API），包括spring-data-jpa、spring-orm、Hibernate。

12）spring-boot-starter-data-mongodb 	支持MongoDB数据，包括spring-data-mongodb。

13）spring-boot-starter-data-rest 	通过spring-data-rest-webmvc，支持通过REST暴露Spring Data数据仓库。

14）spring-boot-starter-data-solr 	支持Apache Solr搜索平台，包括spring-data-solr。

15）spring-boot-starter-freemarker 	支持FreeMarker模板引擎。

16）spring-boot-starter-groovy-templates 	支持Groovy模板引擎。

17）spring-boot-starter-hateoas 	通过spring-hateoas支持基于HATEOAS的RESTful Web服务。

18）spring-boot-starter-hornetq 	通过HornetQ支持JMS。

19）spring-boot-starter-integration 	支持通用的spring-integration模块。

20）spring-boot-starter-jdbc 	支持JDBC数据库。

21）spring-boot-starter-jersey 	支持Jersey RESTful Web服务框架。

22）spring-boot-starter-jta-atomikos 	通过Atomikos支持JTA分布式事务处理。

23）spring-boot-starter-jta-bitronix 	通过Bitronix支持JTA分布式事务处理。

24）spring-boot-starter-mail 	支持javax.mail模块。

25）spring-boot-starter-mobile 	支持spring-mobile。

26）spring-boot-starter-mustache 	支持Mustache模板引擎。

27）spring-boot-starter-redis 	支持Redis键值存储数据库，包括spring-redis。

28）spring-boot-starter-security 	支持spring-security。

29）spring-boot-starter-social-facebook 	支持spring-social-facebook

30）spring-boot-starter-social-linkedin 	支持pring-social-linkedin

31）spring-boot-starter-social-twitter 	支持pring-social-twitter

32）spring-boot-starter-test 	支持常规的测试依赖，包括JUnit、Hamcrest、Mockito以及spring-test模块。

33）spring-boot-starter-thymeleaf 	支持Thymeleaf模板引擎，包括与Spring的集成。

34）spring-boot-starter-velocity 	支持Velocity模板引擎。

35）**spring-boot-starter-web 	S支持全栈式Web开发，包括Tomcat和spring-webmvc。**

36）spring-boot-starter-websocket 	支持WebSocket开发。

37）spring-boot-starter-ws 	支持Spring Web Services。

38）spring-boot-starter-actuator  	增加了面向产品上线相关的功能，比如测量和监控。

39）spring-boot-starter-remote-shell  	增加了远程ssh shell的支持。

40）spring-boot-starter-jetty  	引入了Jetty HTTP引擎（用于替换Tomcat）。

41）spring-boot-starter-log4j  	支持Log4J日志框架。

42）**spring-boot-starter-logging 	引入了Spring Boot默认的日志框架Logback。**

43）**spring-boot-starter-tomcat 	引入了Spring Boot默认的HTTP引擎Tomcat。**

44）spring-boot-starter-undertow  	引入了Undertow HTTP引擎（用于替换Tomcat）。

## 5. SpringBoot 项目构建的几种方式

- 使用官网提供的Quick start。https://start.spring.io/ （了解）
- 创建 Maven 项目的方式，构建springBoot项目结构，在pom.xml中按官网要求引入依赖即可。（了解）
- 使用 IDEA 中 Spring Initializr 的方式。（推荐）

## 6. SpringBoot 默认的常用配置

![image-20201102214729917](D:\github\images\image-20201102214729917.png)

其中 SpringBoot 支持两种日志接口，如图所示：

![image-20201102214918349](C:\Users\wangl\AppData\Roaming\Typora\typora-user-images\image-20201102214918349.png)

springBoot 的日志常用配置项

| 日志常用配置项     | 默认值             | 备注                 |
| ------------------ | ------------------ | -------------------- |
| logging.file       |                    | 日志输出的文件地址   |
| logging.level.ROOT | info               | 设置日志的输出级别   |
| logging.level.*    | info               | 定义指定包的输出级别 |
| logging.config     | logback-spring.xml | 日志的配置文件       |

注意：SpringBoot默认并没有进行文件输出，只在控制台中进行了打印。

日志级别：debug>info>warn>error，默认情况下springboot日志级别为info

如果设置了debug=true时，日志会降级为debug

项目中多采用 logback.xml 配置文件去使用日志，通用常规配置模板如下所示：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false">

    <property name="PROJECT" value="mysb1" />
    <!--定义日志文件的存储地址 勿在 LogBack 的配置中使用相对路径-->
    <property name="ROOT" value="d:/logs/${PROJECT}/" />
    <!--日志文件最大的大小-->
    <property name="FILESIZE" value="50MB" />
    <!--日志文件保留天数-->
    <property name="MAXHISTORY" value="100" />
    <timestamp key="DATETIME" datePattern="yyyy-MM-dd HH:mm:ss" />
    <!-- 控制台打印 -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder charset="utf-8">
            <pattern>[%-5level] %d{${DATETIME}} [%thread] %logger{36} - %m%n
            </pattern>
        </encoder>
    </appender>
    <!-- ERROR 输入到文件，按日期和文件大小 -->
    <!-- RollingFileAppender 按照每天生成日志文件 -->
    <appender name="ERROR" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <encoder charset="utf-8">
            <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符-->
            <pattern>[%-5level] %d{${DATETIME}} [%thread] %logger{36} - %m%n
            </pattern>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--日志文件输出的文件名-->
            <fileNamePattern>${ROOT}%d/error.%i.log</fileNamePattern>
            <!--日志文件保留天数-->
            <maxHistory>${MAXHISTORY}</maxHistory>
            <timeBasedFileNamingAndTriggeringPolicy
                class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <!--日志文件最大的大小-->
                <maxFileSize>${FILESIZE}</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
    </appender>
    
    <!-- WARN 输入到文件，按日期和文件大小 -->
    <appender name="WARN" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <encoder charset="utf-8">
            <pattern>[%-5level] %d{${DATETIME}} [%thread] %logger{36} - %m%n
            </pattern>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>WARN</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${ROOT}%d/warn.%i.log</fileNamePattern>
            <maxHistory>${MAXHISTORY}</maxHistory>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>${FILESIZE}</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
    </appender>
    
    <!-- INFO 输入到文件，按日期和文件大小 -->
    <appender name="INFO" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <encoder charset="utf-8">
            <pattern>[%-5level] %d{${DATETIME}} [%thread] %logger{36} - %m%n
            </pattern>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${ROOT}%d/info.%i.log</fileNamePattern>
            <maxHistory>${MAXHISTORY}</maxHistory>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>${FILESIZE}</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
    </appender>
    <!-- DEBUG 输入到文件，按日期和文件大小 -->
    <appender name="DEBUG" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <encoder charset="utf-8">
            <pattern>[%-5level] %d{${DATETIME}} [%thread] %logger{36} - %m%n
            </pattern>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>DEBUG</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${ROOT}%d/debug.%i.log</fileNamePattern>
            <maxHistory>${MAXHISTORY}</maxHistory>
            <timeBasedFileNamingAndTriggeringPolicy
                class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>${FILESIZE}</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
    </appender>
    <!-- TRACE 输入到文件，按日期和文件大小 -->
    <appender name="TRACE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <encoder charset="utf-8">
            <pattern>[%-5level] %d{${DATETIME}} [%thread] %logger{36} - %m%n
            </pattern>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>TRACE</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <rollingPolicy
            class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${ROOT}%d/trace.%i.log</fileNamePattern>
            <maxHistory>${MAXHISTORY}</maxHistory>
            <timeBasedFileNamingAndTriggeringPolicy
                class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>${FILESIZE}</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
    </appender>
    
    <!-- SQL相关日志输出-->
    <logger name="org.apache.ibatis" level="INFO" additivity="false" />
    <logger name="org.mybatis.spring" level="INFO" additivity="false" />
    <logger name="com.github.miemiedev.mybatis.paginator" level="INFO" additivity="false" />
    
    <!-- Logger 根目录 -->
    <!-- 日志输出级别 -->
    <root level="DEBUG">
        <appender-ref ref="STDOUT" />
        <appender-ref ref="DEBUG" />  
        <appender-ref ref="ERROR" />
        <appender-ref ref="WARN" />
        <appender-ref ref="INFO" />
        <appender-ref ref="TRACE" />
    </root>
</configuration>
```

## 7. SpringBoot 配置文件各式

springBoot 支持两种配置文件格式，默认的是properties文件，另一种是yml格式文件。yml是一种简洁的非标记语言。yml以数据为中心，使用空白，缩进，分行组织数据，从而使得表示更加简洁易读。

> yml语法格式：
>
> 标准格式：key:(空格)value
>
> 使用空格代表层级关系，以“:”结束
>
> yml和properties同时都存在时，以properties为主。

## 8. Banner 的使用

Banner是指 SpringBoot 启动时显示的字符画，默认是“spring”。我们可以新建 resources/banner.txt 进行修改。我们可以通过代码来关闭banner效果。

```java
SpringApplication app = new SpringApplication(Springbootdemo200Application.class);
app.setBannerMode(Banner.Mode.OFF);
app.run(args);
```

## 9. SpringBoot工程热部署

springBoot项目实现热部署有好几种：

- 禁用模板的cache，如在配置文件中设置 `spring.thymeleaf.cache=false`

- 使用debug模式，无需装任何插件即可，但是无发对配置文件，方法名称改变，增加类及方法进行热部署，使用范围有限。

- 使用 spring-boot-devtools     在 Spring Boot 项目中添加 spring-boot-devtools依赖即可实现页面和代码的热部署。

  ```xml
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
  </dependency>
  ```

- Spring Loaded