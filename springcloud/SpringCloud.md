 

# **微服务架构：Spring-Cloud**

# 1. 微服务、springCloud、分布式之间的关系？

## 1.1 什么是微服务？

 微服务就是把原本臃肿的一个项目的所有模块拆分开来并做到互相没有关联，甚至可以不使用同一个数据库。  比如：项目里面有User模块和Power模块，但是User模块和Power模块并没有直接关系，仅仅只是一些数据需要交互，那么就可以吧这2个模块单独分开来，当user需要调用power的时候，power是一个服务方，但是power需要调用user的时候，user又是服务方了， 所以，他们并不在乎谁是服务方谁是调用方，他们都是2个独立的服务，这时候，微服务的概念就出来了。

## 1.2 经典问题: 微服务和分布式的区别

 谈到区别，我们先简单说一下分布式是什么，所谓分布式，就是将偌大的系统划分为多个模块（这一点和微服务很像）部署到不同机器上（因为一台机器可能承受不了这么大的压力或者说一台非常好的服务器的成本可能够好几台普通的了），各个模块通过接口进行数据交互，其实 分布式也是一种微服务。 因为都是吧模块拆分开来变为独立的单元，提供接口来调用，那么 他们本质的区别在哪呢？ 他们的区别主要体现在“目标”上， 何为目标，就是你这样架构项目要做到的事情。 分布式的目标是什么？ 我们刚刚也看见了， 就是一台机器承受不了的，或者是成本问题 ， 不得不使用多台机器来完成服务的部署， 而微服务的目标 只是让各个模块拆分开来，不会被互相影响，比如模块的升级亦或是出现BUG等等... 

讲了这么多，可以用一句话来理解：分布式也是微服务的一种，而微服务他可以是在一台机器上。

## 1.3 微服务与 Spring-Cloud 的关系（区别）

 微服务只是一种项目的架构方式，或者说是一种概念，就如同我们的MVC架构一样， 那么Spring-Cloud便是对这种技术的实现。

## 1.4 微服务一定要使用 Spring-Cloud 吗？

  我们刚刚说过，微服务只是一种项目的架构方式，如果你足够了解微服务是什么概念你就会知道，其实微服务就算不借助任何技术也能实现，只是有很多问题需要我们解决罢了例如：负载均衡，服务的注册与发现，服务调用，路由。。。。等等等等一系列问题，所以,Spring-Cloud 就出来了，Spring-Cloud将处理这些问题的的技术全部打包好了，就类似那种开袋即食的感觉。。 

# 2. Spring-Cloud 项目的搭建

 因为spring-cloud是基于spring-boot项目来的，所以我们项目得是一个spring-boot项目，至于spring-boot项目，这节我们先不讨论，这里要注意的一个点是spring-cloud的版本与spring-boot的版本要对应下图：![img](file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9892\wps1.jpg)

在我这里我的版本是这样的

spring-boot：

```xml
  <parent>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-parent</artifactId>
   <version>2.0.2.RELEASE</version>
  </parent>
```

spring-cloud:

```xml
 <dependencyManagement>            
     <dependencies>            
     	<dependency>                
         <groupId>org.springframework.cloud</groupId>
         <artifactId>spring-cloud-dependencies</artifactId>
         <version>Finchley.SR2</version>
         <type>pom</type>
         <scope>import</scope>
        </dependency>
     </dependencies>
</dependencyManagement>
```

当你项目里面有这些依赖之后，你的spring cloud项目已经搭建好了(初次下载spring-cloud可能需要一点时间)

# 3. Spring-Cloud-Eureka(组件)

## 3.1 eureka是什么？

eureka是Netflix的子模块之一，也是一个核心的模块，eureka里有2个组件，一个是EurekaServer(一个独立的项目) 这个是用于定位服务以实现中间层服务器的负载平衡和故障转移，另一个便是EurekaClient（我们的微服务） 它是用于与Server交互的，可以使得交互变得非常简单:只需要通过服务标识符即可拿到服务。

## 3.2 与spring-cloud的关系

Spring Cloud 封装了 Netflix 公司开发的 Eureka 模块来实现服务注册和发现(可以对比Zookeeper)。

Eureka 采用了 C-S 的设计架构。Eureka Server 作为服务注册功能的服务器，它是服务注册中心。

而系统中的其他微服务，使用 Eureka 的客户端连接到 Eureka Server并维持心跳连接。这样系统的维护人员就可以通过 Eureka Server 来监控系统中各个微服务是否正常运行。SpringCloud 的一些其他模块（比如Zuul）就可以通过 Eureka Server 来发现系统中的其他微服务，并执行相关的逻辑。

## 3.3 角色关系图

### ![img](file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9892\wps2.jpg)

 

## 3.4 Eureka 如何使用？

 在spring-cloud项目里面加入依赖：

 eureka客户端：

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

 eureka服务端：

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

 eureka服务端项目里面加入以下配置：

```yml
server:
	port: 3000
eureka:
	server:
  		enable-self-preservation: false  #关闭自我保护机制
  		eviction-interval-timer-in-ms: 4000 #设置清理间隔（单位：毫秒 默认是60\*1000）
 	instance:
  		hostname: localhost

	client:
  		registerWithEureka: false #不把自己作为一个客户端注册到自己身上
  		fetchRegistry: false  #不需要从服务端获取注册信息（因为在这里自己就是服务端，而且已经禁用自己注册了）
  		serviceUrl:
   		defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka
```

 当然，不是全部必要的，这里只是把我这里的配置copy过来了

 然后在spring-boot启动项目上 加入注解:@EnableEurekaServer 就可以启动项目了

```java
@EnableEurekaServer
@SpringBootApplication
public class AppEureka {
  public static void main(String[] args) {
    SpringApplication.run(AppEureka.class);
  }
}
```

 如果看见这个图片，那么说明你就搭建好了:

![img](file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9892\wps3.jpg) 

 

这个警告只是说你把他的自我保护机制关闭了

```yml
#eureka客户端配置:

server:
	port: 6000
eureka:
	client:
		serviceUrl:
    	defaultZone: http://localhost:3000/eureka/  #eureka服务端提供的注册地址 参考服务端配置的这个路径
 	instance:
  		instance-id: power-1 #此实例注册到eureka服务端的唯一的实例ID
  		prefer-ip-address: true #是否显示IP地址
  		leaseRenewalIntervalInSeconds: 10 #eureka客户需要多长时间发送心跳给eureka服务器，表明它仍然活着,默认为30 秒 (与下面配置的单位都是秒)
  		leaseExpirationDurationInSeconds: 30 #Eureka服务器在接收到实例的最后一次发出的心跳后，需要等待多久才可以将此实例删除，默认为90秒
spring:
 	application:
  		name: server-power #此实例注册到eureka服务端的name 
```

然后在客户端的spring-boot启动项目上 加入注解:@EnableEurekaClient 就可以启动项目了 这里就不截图了我们直接来看效果图：

![img](file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9892\wps4.jpg) 

 

这里我们能看见 名字叫server-power的（图中将其大写了） id为 power-1的服务 注册到我们的Eureka上面来了 至此，一个简单的eureka已经搭建好了。

 



# 4. Eureka 集群

## 4.1 eureka集群原理

 服务启动后向Eureka注册，Eureka Server会将注册信息向其他Eureka Server进行同步，当服务消费者要调用服务提供者，则向服务注册中心获取服务提供者地址，然后会将服务提供者地址缓存在本地，下次再调用时，则直接从本地缓存中取，完成一次调用。

## 4.2 eureka集群配置

 刚刚我们了解到 Eureka Server会将注册信息向其他Eureka Server进行同步 那么我们得声明有哪些server呢？

这里 假设我们有3个Eureka Server 如图：

![img](file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9892\wps5.jpg) 

 

现在怎么声明集群环境的server呢？ 我们看一张图：

![img](file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9892\wps6.jpg) 

 

 

可能看着有点抽象，我们来看看具体配置

```yml
server:
 port: 3000
eureka:
 server:
  enable-self-preservation: false
  eviction-interval-timer-in-ms: 4000
 instance:
  hostname: eureka3000.com

 client:
  registerWithEureka: false
  fetchRegistry: false
  serviceUrl:
   defaultZone: http://eureka3001.com:3001/eureka,http://eureka3002.com:3002/eureka
```

这里 方便理解集群 我们做了一个域名的映射(条件不是特别支持我使用三台笔记本来测试。。。) 至于域名怎么映射的话 这里简单提一下吧 修改你的hosts文件（win10的目录在C:\Windows\System32\drivers\etc 其他系统的话自行百度一下把）附上我的hosts文件：

```bash
  127.0.0.1  eureka3000.com
  127.0.0.1  eureka3001.com
  127.0.0.1  eureka3002.com
```

我们回到主题， 我们发现 集群配置与单体不同的点在于 原来是把服务注册到自己身上，而现在是注册到其它服务身上

至于为什么不注册自己了呢？，回到最上面我们说过，eureka的server会把自己的注册信息与其他的server同步，所以这里我们不需要注册到自己身上，因为另外两台服务器会配置本台服务器。(这里可能有点绕，可以参考一下刚刚那张集群环境的图，或者自己动手配置一下，另外两台eureka的配置与这个是差不多的，就不发出来了，只要注意是注册到其他的服务上面就好了)

当三台eureka配置好之后，全部启动一下就可以看见效果了:![img](file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9892\wps7.jpg)

当然，我们这里仅仅是把服务端配置好了， 那客户端怎么配置呢？ 话不多说，上代码：

```yml
 client:
  serviceUrl:
    defaultZone: http://localhost:3000/eureka/,http://eureka3001.com:3001/eureka,http://eureka3002.com:3002/eureka
```

我们这里只截取了要改动的那一部分。 就是 原来是注册到那一个地址上面，现在是要写三个eureka注册地址，但是不是代表他会注册三次，因为我们eureka server的注册信息是同步的，这里只需要注册一次就可以了，但是为什么要写三个地址呢。因为这样就可以做到高可用的配置：打个比方有3台服务器。但是突然宕机了一台， 但是其他2台还健在，依然可以注册我们的服务，换句话来讲， 只要有一台服务还建在，那么就可以注册服务，这里 需要理解一下。

这里效果图就不发了， 和之前单机的没什么两样，只是你服务随便注册到哪个eureka server上其他的eureka server上都有该服务的注册信息。

 

## 4.3 CAP 定理的含义

 

<img src="file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9892\wps8.png" alt="img" style="zoom:50%;" /> 

 

1998年，加州大学的计算机科学家 Eric Brewer 提出，分布式系统有三个指标。

Consistency  ---一致性
Availability  ---可用性
Partition tolerance  ---分区容错性

他们第一个字母分别是C,A,P

Eric Brewer 说，这三个指标不可能同时做到。这个结论就叫做 CAP 定理。

### **Partition tolerance**

中文叫做"分区容错"。

大多数分布式系统都分布在多个子网络。每个子网络就叫做一个区（partition）。分区容错的意思是，区间通信可能失败。比如，一台服务器放在本地，另一台服务器放在外地（可能是外省，甚至是外国），这就是两个区，它们之间可能无法通信。

<img src="file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9892\wps9.jpg" alt="img" style="zoom:50%;" /> 

上图中，S1 和 S2 是两台跨区的服务器。S1 向 S2 发送一条消息，S2 可能无法收到。系统设计的时候，必须考虑到这种情况。

一般来说，分区容错无法避免，因此可以认为 CAP 的 P 总是成立。CAP 定理告诉我们，剩下的 C 和 A 无法同时做到。

### **Consistency**

Consistency 中文叫做"一致性"。意思是，写操作之后的读操作，必须返回该值。举例来说，某条记录是 v0，用户向 S1 发起一个写操作，将其改为 v1。

<img src="file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9892\wps10.jpg" alt="img" style="zoom:50%;" /> 

接下来用户读操作就会得到v1。这就叫一致性。

<img src="file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9892\wps11.jpg" alt="img" style="zoom:50%;" /> 

问题是，用户有可能会向S2发起读取操作，由于G2的值没有发生变化，因此返回的是v0，所以S1和S2的读操作不一致，这就不满足一致性了

<img src="file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9892\wps12.jpg" alt="img" style="zoom:50%;" /> 

为了让S2的返回值与S1一致，所以我们需要在往S1执行写操作的时候，让S1给S2也发送一条消息，要求G2也变成v1

<img src="file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9892\wps13.jpg" alt="img" style="zoom:50%;" /> 

这样子用户向G2发起读操作，就也能得到v1

<img src="file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9892\wps14.jpg" alt="img" style="zoom:50%;" /> 

### **Availability**

 Availability 中文叫做"可用性"，意思是只要收到用户的请求，服务器就必须给出回应。

用户可以选择向 S1 或 S2 发起读操作。不管是哪台服务器，只要收到请求，就必须告诉用户，到底是 v0 还是 v1，否则就不满足可用性。

### Consistency 和 Availability 的矛盾

一致性和可用性，为什么不可能同时成立？答案很简单，因为可能通信失败（即出现分区容错）。

如果保证 S2 的一致性，那么 S1 必须在写操作时，锁定 S2 的读操作和写操作。只有数据同步后，才能重新开放读写。锁定期间，S2 不能读写，没有可用性不。

如果保证 S2 的可用性，那么势必不能锁定 S2，所以一致性不成立。

综上所述，S2 无法同时做到一致性和可用性。系统设计时只能选择一个目标。如果追求一致性，那么无法保证所有节点的可用性；如果追求所有节点的可用性，那就没法做到一致性。

## 4.4 eureka 对比 Zookeeper

 Zookeeper在设计的时候遵循的是CP原则，即一致性,Zookeeper会出现这样一种情况，当master节点因为网络故障与其他节点失去联系时剩余节点会重新进行leader选举，问题在于，选举leader的时间太长：30~120s，且选举期间整个Zookeeper集群是不可用的，这就导致在选举期间注册服务处于瘫痪状态，在云部署的环境下，因网络环境使Zookeeper集群失去master节点是较大概率发生的事情，虽然服务能够最终恢复，但是漫长的选举时间导致长期的服务注册不可用是不能容忍的。

 Eureka在设计的时候遵循的是AP原则，即可用性。Eureka各个节点（服务)是平等的， 没有主从之分，几个节点down掉不会影响正常工作，剩余的节点（服务） 依然可以提供注册与查询服务，而Eureka的客户端在向某个Eureka注册或发现连接失败，则会自动切换到其他节点，也就是说，只要有一台Eureka还在，就能注册可用（保证可用性）， 只不过查询到的信息不是最新的（不保证强一致），除此之外，Eureka还有自我保护机制，如果在15分钟内超过85%节点都没有正常心跳，那么eureka就认为客户端与注册中心出现了网络故障，此时会出现一下情况:

> 1. Eureka 不再从注册列表中移除因为长时间没有收到心跳而过期的服务。
> 2.  Eureka 仍然能够接收新服务的注册和查询请求，但是不会被同步到其它节点上（即保证当前节点可用）
> 3. 当网络稳定后，当前实例新的注册信息会被同步到其它节点中



 

# 5. Spring-Cloud组件之ribbon

## 5.1 ribbon是什么?

Spring Cloud Ribbon是基于Netflix Ribbon实现的一套客户端负载均衡的工具。

简单的说，Ribbon是Netflix发布的开源项目，主要功能是提供客户端的软件负载均衡算法，将Netflix的中间层服务连接在一起。Ribbon客户端组件提供一系列完善的配置项如连接超时，重试等。简单的说，就是在配置文件中列出Load Balancer（简称LB）后面所有的机器，Ribbon会自动的帮助你基于某种规则（如简单轮询，随机连接等）去连接这些机器。我们也很容易使用Ribbon实现自定义的负载均衡算法。

## 5.2 客户端负载均衡与服务端负载均衡

 我们用一张图来描述一下这两者的区别

![img](file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9620\wps1.jpg) 

 

这篇文章里面不会去解释nginx，如果不知道是什么的话，可以先忽略， 先看看这张图

 服务端的负载均衡是一个url先经过一个代理服务器（这里是nginx），然后通过这个代理服务器通过算法（轮询，随机，权重等等..）反向代理你的服务，l来完成负载均衡

 而客户端的负载均衡则是一个请求在客户端的时候已经声明了要调用哪个服务，然后通过具体的负载均衡算法来完成负载均衡

## 5.3 如何在spring cloud项目中使用riboon

 首先，我们还是要引入依赖，但是，eureka已经把ribbon集成到他的依赖里面去了，所以这里不需要再引用ribbon的依赖，如图：

![img](file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9620\wps2.jpg) 

要使用ribbon，只需要一个注解：

```java
@Bean
@LoadBalanced
public RestTemplate restTemplate(){
  RestTemplate restTemplate = new RestTemplate();
  return restTemplate;
}
```

在RestTemplate上面加入@LoadBalanced注解，这样子就已经有了负载均衡， 怎么来证明?

我们这里现在启动了eureka集群（3个eureka） 和Power集群（2个power） 和一个服务调用者（User）

![img](file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9620\wps3.jpg) 

但是我们的User仅仅只需要调用服务，不需要注册服务信息，所以需要改一下配置文件：

配置什么意思就不做过多解释了，上面讲eureka的时候有讲到过

```yml
server:
 port: 5000
eureka:

 client:
  registerWithEureka: false
  serviceUrl:
    defaultZone: http://localhost:3000/eureka/,http://eureka3001.com:3001/eureka,http://eureka3002.com:3002/eureka
```

然后启动起来的页面是这样子的

<img src="file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9620\wps4.jpg" alt="img"  /> 

 

我们能看见 微服务名:SERVER-POWER 下面有2个微服务（power-1,power-2），现在我们来通过微服务名调用这个服务

这是我们的user项目的调用代码 ：

```java
private static final String URL="http://SERVER-POWER";

@Autowired
private RestTemplate restTemplate;

@RequestMapping("/power.do")
public Object power(){
  return restTemplate.getForObject(URL+"/power.do",Object.class);
}
```

我们来看看效果:

![img](file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9620\wps5.jpg) 

 

![img](file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9620\wps6.jpg) 

这里可能有点抽象，需要你们自己去写才能体会到，但是我们已经完成了负载均衡， 他默认的负载均衡是轮询策略，也就是一人一次，下一节我们来讲一下他还有哪些策略。

## 5.4 ribbon的核心组件—IRule

IRule是什么? 它是Ribbon对于负载均衡策略实现的接口， 怎么理解这句话？ 说白了就是你实现这个接口，就能自定义负载均衡策略， 自定义我们待会儿来讲， 我们先来看看他有哪些默认的实现

![img](file:///C:\Users\wangl\AppData\Local\Temp\ksohtml9620\wps7.jpg) 

 

这里是ribbon负载均衡默认的实现， 由于是笔记的关系，这里不好测试，只能你们自己去测试一下了， 具体怎么使用呢？

看代码:

```java
@Bean
public IRule iRule(){
  return new RoundRobinRule();
}
```

 在Spring 的配置类里面把对应的实现作为一个Bean返回出去就行了。



## 5.5 feign 负载均衡

### 5.5.1 feign 是什么

 Feign是一个声明式WebService客户端。使用Feign能让编写Web Service客户端更加简单, 它的使用方法是定义一个接口，然后在上面添加注解，同时也支持JAX-RS标准的注解。Feign也支持可拔插式的编码器和解码器。Spring Cloud对Feign进行了封装，使其支持了Spring MVC标准注解和HttpMessageConverters。Feign可以与Eureka和Ribbon组合使用以支持负载均衡。

### 5.5.2 feign 能干什么

Feign旨在使编写Java Http客户端变得更容易。 前面在使用Ribbon+RestTemplate时，利用RestTemplate对http请求的封装处理，形成了一套模版化的调用方法。但是在实际开发中，由于对服务依赖的调用可能不止一处，往往一个接口会被多处调用，所以通常都会针对每个微服务自行封装一些客户端类来包装这些依赖服务的调用。所以，Feign在此基础上做了进一步封装，由他来帮助我们定义和实现依赖服务接口的定义。在Feign的实现下，我们只需创建一个接口并使用注解的方式来配置它(以前是Dao接口上面标注Mapper注解,现在是一个微服务接口上面标注一个Feign注解即可)，即可完成对服务提供方的接口绑定，简化了使用Spring cloud Ribbon时，自动封装服务调用客户端的开发量。

### 5.5.3 feign 如何使用？

在客户端(User)引入依赖：

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

在启动类上面加上注解:@EnableFeignClients

然后编写一个service类加上@FeignClient()注解 参数就是你的微服务名字

```java
@FeignClient("SERVER-POWER")
public interface PowerServiceClient {

  @RequestMapping("/power.do")
  public Object power();

}
```

下面是调用代码：

```java

@RestController
public class UserController {

  private static final String URL="http://SERVER-POWER";

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  PowerServiceClient powerServiceClient;

  @RequestMapping("/power.do")
  public Object power(){
    return restTemplate.getForObject(URL+"/power.do",Object.class);
  }

  @RequestMapping("/feignPower.do")
  public Object feignPower(){
    return powerServiceClient.power();
  }
}
```

这里拿了RestTemplate做对比 可以看看2者区别

Feign集成了Ribbon

利用Ribbon维护了服务列表信息，并且融合了Ribbon的负载均衡配置，也就是说之前自定义的负载均衡也有效，这里需要你们自己跑一遍理解一下。而与Ribbon不同的是，通过feign只需要定义服务绑定接口且以声明式的方法，优雅而简单的实现了服务调用

 

 