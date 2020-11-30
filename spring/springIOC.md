# Spring IOC

## SpringIOC概述

springIOC叫做控制反转，是一个比较抽象的概念。控制反转是一种通过描述（注解、xml）并通过第三方区产生或获取特定对象的方式。

## SpringIOC的实现方式

- 依赖注入(构造器注入、setter注入、接口注入)
- 依赖查找（资源定位，例如 JNDI）  

## SpringBean的装配

spring装配Bean有三种方式：

- schema-based（xml）
- anotation-based（注解）
- javaConfig-based（java Configration）

其中xml的方式，使用ClassPathXmlApplicationContext具体类去找到xml配置文件来管理Bean的，而注解方式以及javaConfig的方式是使用AnnotationConfigApplicationContext类去初始化SpringIOC容器的。

### 通过xml配置装配Bean

使用xml的方式装配Bean，需要引入XSD文件，这些文件会定义配置Bean的一些元素，在IDE中会有提示。

配置文件中根元素的定义：

```java
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans 
       http://www.springframework.org/schema/beans/spring-beans.xsd">
</beans>
```

装配简易值代码案例：

```java
<bean id="role" class="com.luban.pojo.Role">
    <property name="id" value="1"></property>
    <property name="roleName" value="高级工程师"></property>
</bean>
```

- id属性是spring找到这个Bean的编号，不写的话，默认为全限定名；class是类的全限定名。
- property元素是定义类的属性，name是属性名称，value是属性的值，如果引入的是其他在xml中定义的bean的话，用ref=“bean的id”来定义。
- 在xml文件中还可以定义List，Map、Set、Array和Properties等复杂的属性。

#### 命名空间装配

spring提供了对应的命名空间的定义，通过引入对应的命名空间以及 XSD 文件的方式来实现属性注入

```java
<!--类似于constructor-arg标签 -->
xmlns:c="http://www.springframework.org/schema/c"
<!--类似于property标签 -->
xmlns:p="http://www.springframework.org/schema/p"
```

### 通过注解装配Bean

spring提供的注解有@Component、@Resource、@Reposity、@AutoWired、@Service

### 通过javaConfig实现Bean装配

### SpringBean装配的混合使用

## SpringBean的作用域

- singleton
- prototype
- request
- session
- application
- websocket

## Spring EL

## Spring生命周期的回调

## SpringBean的循环调用

## Spring Bean 使用Profile