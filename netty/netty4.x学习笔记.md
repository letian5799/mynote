# Netty 4.x 基础学习

# 知识预备

java基础知识：BIO、AIO、NIO三者的实现，以及彼此之间的区别

学习java 网络通讯 ，以Echo程序模型为例，实现一个基础的echo功能。

![image-20201116230211272](C:\Users\wangl\AppData\Roaming\Typora\typora-user-images\image-20201116230211272.png)

## BIO（同步阻塞I/O）

​	在程序的开发之中Java里面最小的处理单元就是线程，也就是说每一个线程可以进行IO的处理，在处理之中，该线程无法进行任何的其他操作。
​	多线程是不可能无限制进行创造的，所以需要去考虑堆线程进行有效的个数控制。如果产生的线程过多，那么直接的问题在于，处理性能降低 ，响应的速度变慢。
​    需要去区分操作系统的内核线程，以及用户线程的区别，所以最好与内核线程有直接联系，需要使用到固定线程池。

## NIO（同步非阻塞I/O）

 在NIO之中采用了一个Reactor事件模型，注册的汇集点Selector

## AIO（异步非阻塞I/O）

AIO是在JDK 1.7的时候才推出的模型。它是利用了本地操作系统的IO操作处理模式，当有IO操作产生之后，会启动有一个单独的线程，它将所有的IO操作全部交由系统完成，只需要知道返回结果即可。
    主要的模式是基于操作回调的方式来完成处理的，如果以烧水为例：在烧水的过程之中你不需要去关注这个水的状态，在烧水完成后才进行通知。

## 传统 IO 与 NIO 的比较

传统 IO 的特点

- 存在两个阻塞点：server.accept() 和 inputstream.read(bytes)
- 单线程的情况下一个服务只能有一个客户端
- 多线程情况下可以有多个客户端连接，但是非常消耗性能

# Netty 概述

## 为什么使用Netty?

我们知道一般在开发之中会使用系统实现的程序类，核心的意义在于：它可以帮助开发者隐藏协议的实现细节，但是在开发中依然会发现有如下几点：
1、程序的实现方式上的差异，因为代码的执行会有底层的实现琐碎问题；
2、在现在给定的通讯里面并没有处理长连接的问题，也就是说按照当前编写的网络通讯，一旦要发送的是稍微大一些的文件，则很大可能是无法传送完成；
3、在数据量较大的时候需要考虑粘包与拆包问题；
4、实现的协议细节操作不好控制；
5、在很多项目开发中（RCP底层实现）需要提供有大量的IO通讯的问题，如果直接使用原始的程序类，开发难度过高；

Netty的产生也是符合时代的要求，可以简化大量的繁琐的程序代码。

知识补充：

>Dubbo使用了Netty作为底层的通讯实现，Netty是基于NIO实现的，也采用了线程池的概念。Netty的最新版本为"4.1.31"，
>一开始为了进一步提高Netty处理性能，所以研发了5.0版本，但是研发之后（修改了一些类名称和方法名称、通讯算法）发现性能没有比4.x提升多少。
>所以Netty 5.x是暂时不会被更新的版本了，而现在主要更新的就是Netty 4.x。

## Netty 可以做什么？

## Netty实现echo程序模型

客户端启动类(EchoClientHandler.java)

```java

```

客户端消息处理类(EchoClientHandler.java)

```java
package com.lesson.netty.demo02.client.handler;

import com.lesson.util.InputUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * 自定义客户端消息处理器
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {
    /**
     * 消息接收
     * @param ctx netty消息处理上下文
     * @param msg  服务端响应的消息
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            // 默认消息传输是Object类型，需要转成netty的缓冲类型
            ByteBuf readBuf = (ByteBuf)msg;
            // 将ByteBuf内容转成字符串类型
            String readData = readBuf.toString(CharsetUtil.UTF_8);
            // 判断服务端响应是否为quit，如果是，则退出客户端，否则打印响应内容，并等待客户发送消息
            if("quit".equalsIgnoreCase(readData)){
                System.out.println("bye bye...");
                ctx.close();
            }else{
                System.out.println(readData);
                // 等待客户从控制台输入待发送的消息
                String s = InputUtil.getStirng("请输入消息：");
                // 转成字节数组
                byte[] sendMsg = s.getBytes();
                // 转成ByteBuf类型，netty默认只接收ByteBuf类型数据
                ByteBuf buf = Unpooled.buffer(sendMsg.length);
                // 将消息写入缓冲区
                buf.writeBytes(sendMsg);
                // 发送消息并刷新
                ctx.writeAndFlush(buf);
            }
        } finally {
            // 释放缓存，固定写法
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 错误捕获或异常消息处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

```

服务端启动类

```java

```

服务端消息处理类

```java

```

测试结果：



# TCP的粘包与拆包解决之道

## 什么是粘包与拆包？

tcp是个“流”协议，通过此协议传输的数据没有界限，是连城一片的，业务层面的数据对于tcp来说，它并不了解数据是怎么的含义，它会根据缓冲区的实际情况进行包的划分，所以一个完整的数据包可能会被拆成多个小的数据包进行发送，也可能会把多个小的数据包封装成大的包进行发送。这就是tcp的粘包与拆包问题。

## tcp产生粘包与拆包的原因

- 一次传输的数据大于缓冲区容量
- 进行MSS大小的tcp分段
- 以太网帧的载荷大于MTU进行IP分片

## 主流的粘包与拆包的解决方案

- 设置消息的边界内容，例如：每一个消息使用"\n"结尾操作；
- 使用特定消息头，在真实信息之前传入一个长度的信息。
- 使用定长信息；固定传输报文的字节，如果不够，用空格补足。

## 粘包现象案例演示

使用Netty演示粘包现象案例，在上述netty的echo模型基础上改造。

改造客户端处理类：

```java
package com.lesson.netty.demo04.client.handler;

import com.lesson.util.InputUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {
    private String req;
    public EchoClientHandler(){
        req = "hello word";
    }

    /**
     * 模拟客户端循环发送100次数据
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf message = null;
        for (int i = 0; i < 100; i++) {
            message = Unpooled.buffer(req.getBytes().length);
            message.writeBytes(req.getBytes());
            ctx.writeAndFlush(message);
        }
    }
	/**
     * 只用来接受数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ByteBuf readBuf = (ByteBuf)msg;
            String readData = readBuf.toString(CharsetUtil.UTF_8);
            if("quit".equalsIgnoreCase(readData)){
                System.out.println("bye bye...");
                ctx.close();
            }else{
                System.out.println(readData);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

```

运行结果分析：

![image-20201119165036294](C:\Users\letian\AppData\Roaming\Typora\typora-user-images\image-20201119165036294.png)

## Netty解决tcp粘包与拆包的方案

为了解决tcp的粘包/拆包导致的半包读写问题，nett默认提供了多种编解码器用于解决该问题。Netty解决拆包与粘包问题的关键在于使用了分割器的模式来进行数据的拆分。



# Netty的序列化管理

# netty开发http服务器

# netty实现websocket