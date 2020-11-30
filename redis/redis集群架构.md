# Redis集群架构

## 主从复制模式

所谓的主从架构设计的思路大概是：

- 多台服务器，只有一台主服务器，只负责写数据，而不负责读取数据。
- 多台服务器，从服务器不写入数据，只负责同步主服务器的数据，并让外部程序读取数据。
- 主服务器在写入数据后，即刻将写入数据的命令发送给从服务器，从而使得主从数据同步。
- 应用程序可以随机读取一台从服务器，分摊读取数据的压力。
- 当从服务器不能工作的时候，整个系统不受影响；主服务器不能工作时，选取一台从服务器当主机。

### Redis的主从复制模式是什么？

为了解决单机故障，容量瓶颈以及qps瓶颈，主从复制可以实现读写分离，容灾备份。

Redis的主从同步结构如图所示：

![image-20201130233032975](D:\java学习笔记\mynote\redis\image-20201130233032975.png)

### Redis的主从复制模式的配置

配置原则：

- 配从不配主
- 当使用命令 `slaveof server port` 动态指定主从关系  ，如果设置了密码，关联后使用 config set masterauth 密码
- 配置文件和命令混合使用时，如果混合使用，动态指定了主从，请注意一定要修改对应的配置文件

以下是以配置文件的方式配置主从复制模式

1.新建redis8000,redis8001,redis8002文件夹

2.将原生redis.conf文件分别复制在redis8000下

3.分别修改各目录下的redis.conf文件

​	**redis8000/redis.conf**修改以下几项：

```bash
#以8000端口作为主机
bind 192.168.19.105   指定本机ip
port 8000
daemonize yes
pidfile /var/run/redis_8000.pid  
dir /usr/local/myredis/redis8000
requirepass 123456
```

4.把redis8000/redis.conf文件复制到redis8001,redis8002下，这两个作为从机，需要做如下操作：

```bash
1.	分别在redis8001/redis.conf和redis8002/redis.conf中将端口改为对应的端口，如果是在vim编辑器中操作，可使用：%s/8000/8001/g做批量替换
2.	replicaof 192.168.19.105 8000
3.	masterauth 123456
4.	appendonly yes   #从机开启aof
```

5.分别启动8000.8001,8002实例

```bash
[root@localhost myredis]# /usr/local/bin/redis-server /myredis/redis8000/redis.conf 
[root@localhost myredis]# /usr/local/bin/redis-server /myredis/redis8001/redis.conf 
[root@localhost myredis]# /usr/local/bin/redis-server /myredis/redis8002/redis.conf 
```

6.客户端连接

```bash
/usr/local/bin/redis-cli -h 192.168.19.105 -p 8000 -a 123456

/usr/local/bin/redis-cli -h 192.168.19.105 -p 8001 -a 123456

/usr/local/bin/redis-cli -h 192.168.19.105 -p 8002 -a 123456
```

7.常用的操作命令

- 在任意一台客户端上通过 `info replication` 命令查看主从角色
- `config set masterauth password` 给某一台从机动态设置密码。
- `slaveof server port` 动态将本台机器挂到主机上
- `config get save` 查看rdb持久化状态
- `config get appendonly` 查看aof持久化状态，建议将从机设置为开启aof，主机关闭aof

### 主从全量复制的流程

![image-20201201000418493](D:\java学习笔记\mynote\redis\image-20201201000418493.png)

1.bgsave时间
2.rdb文件网络传输
3.从节点请求请求数据时间
4.从节点加载rdb的时间
5.可能的aof重写时间

### Redis的主从复制模式的缺点

1.由于所有的写操作都是先在Master上操作，然后同步更新到Slave上，所以从Master同步到Slave机器有一定的延迟，当系统很繁忙的时候，延迟问题会更加严重，Slave机器数量的增加也会使这个问题更加严重。

2.当主机宕机之后，将不能进行写操作，需要手动将从机升级为主机，从机需要重新制定master

### Redis主从复制总结

一个master可以有多个Slave

一个slave只能有一个master

数据流向是单向的，只能从主到从