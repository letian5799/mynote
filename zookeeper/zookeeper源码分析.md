# zookeeper 源码分析

# zookeeper 源码编译

# zookeeper 客户端流程分析

首先我们在启动客户端时执行的是 zkCli.sh这个脚本，打开这个脚本，我们可以发现它调用的实际就是ZookeeperMain这个类的main方法。

![image-20201115205913287](D:\java学习笔记\zookeeper\image-20201115205913287.png)

![image-20201115210540762](D:\java学习笔记\zookeeper\image-20201115210540762.png)

ZookeeperMain构造函数中干了两件事情

1. 解析命令行参数
2. 连接zk

下面再跟踪一下connectToZK方法：

![image-20201115211450823](D:\java学习笔记\zookeeper\image-20201115211450823.png)

可以看到在这个方法中主要就是去new ZookeeperAdmin()，这个类继承了 Zookeeper 类，跟踪ZookeeperAdmin的构造方法，我们可以看到实际上是调用的 Zookeeper 构造方法，如下所示：

![image-20201115213927630](D:\java学习笔记\zookeeper\image-20201115213927630.png) 

对比一下new ZookeeperAdmin 和 new Zookeeper 的参数，可以发现多了 canBeReadOnly和 aHostProvider 两个参数，其中 canBeReadOnly 默认传的是false，即不开启只读模式，aHostProvider 是对连接地址（connectString）的一个包装。

该方法的核心逻辑是创建一个ClientCnxn并且启动线程，ClientCnxn是sockect通信的管理类，zookeeper 的底层通信用的是java NIO 技术和 netty 实现的。

在createConnection 方法的参数中需要提供一个ClientCnxnSocket，于是先执行的是getClientCnxnSocket() 去获取一个socket 连接。

![image-20201115220022327](D:\java学习笔记\zookeeper\image-20201115220022327.png)

接下来再看一下ClientCnxn这个通信管理类干了哪些事情：

![image-20201115221118958](C:\Users\wangl\AppData\Roaming\Typora\typora-user-images\image-20201115221118958.png)

回头我们再看一下ZookeeperMain构造方法中的main.run()逻辑，该方法主要是执行java命令行的，比如connect、create、get等操作的。