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

## Redis哨兵模式

哨兵模式是在Redis 2.8版本开始引入。哨兵是一个独立的进程，其原理是哨兵通过发送命令，等待redis服务器的响应，从而监控运行的多个redis实例。哨兵的核心功能是主节点的自动故障转移。

### 哨兵干了哪些事？

- 监控（Monitoring）：哨兵会不断地检查主节点和从节点是否运作正常。

- 自动故障转移（Automatic Failover）：当主节点不能正常工作时，哨兵会开始自动故障转移操作，它会将失效主节点的其中一个从节点升级为新的主节点，并让其他从节点改为复制新的主节点。

- 配置提供者（Configuration Provider）：客户端在初始化时，通过连接哨兵来获得当前Redis服务的主节点地址。

- 通知（Notification）：哨兵可以将故障转移的结果发送给客户端。

其中，监控和自动故障转移功能，使得哨兵可以及时发现主节点故障并完成转移；而配置提供者和通知功能，则需要在与客户端的交互中才能体现。

### 哨兵模式架构

![image-20201201151116193](D:\java学习笔记\mynote\redis\哨兵模式架构.png)

### 哨兵模式部署

#### 部署主从节点

哨兵系统中的主从节点，与普通的主从节点配置是一样的，并不需要做任何额外配置。下面分别是主节点（port=8000）和2个从节点（port=8001/8002）的配置文件；

参见主从复制模式的搭建

#### 部署哨兵节点

哨兵节点本质上是特殊的Redis节点。

3个哨兵节点的配置几乎是完全一样的，主要区别在于端口号的不同（26379 / 26380 / 263 81）下面以26379节点为例介绍节点的配置和启动方式；配置部分尽量简化：

```bash
#####sentinel-26379.conf
bind 0.0.0.0
port 26379
daemonize yes
logfile "26379.log"
##### 配置监听的主服务器，这里的sentinel monitor代表监控
### mymaster代表服务名称，可自定义
### 192.168.19.105 8000 主节点的ip和端口
### 2表示至少需要2个哨兵节点同意，才能判定主节点故障并进行故障转移。
sentinel monitor mymaster 192.168.19.105 8000 2
# 服务有密码的话需要配置这个
sentinel auth-pass mymaster 123456
```

哨兵节点的启动有两种方式，二者作用是完全相同的：

`redis-sentinel sentinel-26379.conf`

`redis-server sentinel-26379.conf --sentinel`

### 在java中使用jedis来访问哨兵模式

```java
package com.lesson.sentinel;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 通过kill掉主节点，然后运行main方法，等待一会就会打印出结果
 */
public class SentinelTest {
    public static void main(String[] args) {
        // 连接池配置
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(10);
        config.setMaxIdle(5);
        config.setMinIdle(5);
        // 哨兵信息
        Set<String> sentinels = new HashSet<String>(Arrays.asList(
            "192.168.19.105:26379",
            "192.168.19.105:26380",
            "192.168.19.105:26381"
        ));
        /**
         * 创建哨兵连接池
         * mymaster 服务名称
         * sentinels是哨兵信息
         * config 是redis连接池配置
         * 123456 是连接redis服务器的密码
         */
        JedisSentinelPool jedisSentinelPool=new JedisSentinelPool("mymaster",sentinels,config,"123456");
        // 获取客户端
        Jedis jedis = jedisSentinelPool.getResource();
        jedis.set("k2", "v2");
        String k2 = jedis.get("k2");
        System.out.println(k2);
    }
}
```

打印结果：

```java
[main] INFO redis.clients.jedis.JedisSentinelPool - Trying to find master from available Sentinels...
[main] INFO redis.clients.jedis.JedisSentinelPool - Redis master running at 192.168.19.105:8002, starting Sentinel listeners...
[main] INFO redis.clients.jedis.JedisSentinelPool - Created JedisPool to master at 192.168.19.105:8002
v2
```

可以看出哨兵选出了新的主节点是8002端口，此时如果我们重启8000端口服务，然后通过客户端info 命令访问，会发现它已变成一个挂在8002下的一个从节点。

### 哨兵模式的工作原理

关于哨兵的原理，关键是了解以下几个概念：

**主观下线**：在心跳检测的定时任务中，如果其他节点超过一定时间没有回复，哨兵节点就会将其进行主观下线。顾名思义，主观下线的意思是一个哨兵节点“主观地”判断下线；与主观下线相对应的是客观下线。

**客观下线**：哨兵节点在对主节点进行主观下线后，会通过sentinel is-master-down-by-addr命令询问其他哨兵节点该主节点的状态；如果判断主节点下线的哨兵数量达到一定数值，则对该主节点进行客观下线。

需要特别注意的是，客观下线是主节点才有的概念；如果从节点和哨兵节点发生故障，被哨兵主观下线后，不会再有后续的客观下线和故障转移操作。

**定时任务**：每个哨兵节点维护了3个定时任务。定时任务的功能分别如下：

1.每10秒通过向主从节点发送info命令获取最新的主从结构；

  	发现slave节点

 	 确定主从关系

2.每2秒通过发布订阅功能获取其他哨兵节点的信息；SUBSCRIBE  c2     PUBLISH c2 hello-redis

  	交互对节点的“看法”和自身情况

3.每1秒通过向其他节点发送ping命令进行心跳检测，判断是否下线（monitor）。

  	心跳检测，失败判断依据

**选举领导者哨兵节点**：当主节点被判断客观下线以后，各个哨兵节点会进行协商，选举出一个领导者哨兵节点，并由该领导者节点对其进行故障转移操作。

监视该主节点的所有哨兵都有可能被选为领导者，选举使用的算法是Raft算法；Raft算法的基本思路是先到先得：即在一轮选举中，哨兵A向B发送成为领导者的申请，如果B没有同意过其他哨兵，则会同意A成为领导者。选举的具体过程这里不做详细描述，一般来说，哨兵选择的过程很快，谁先完成客观下线，一般就能成为领导者。

**故障转移**：选举出的领导者哨兵，开始进行故障转移操作，该操作大体可以分为3个步骤：

在从节点中选择新的主节点：选择的原则是，

1.首先过滤掉不健康的从节点；

2.然后选择优先级最高的从节点（由replica-priority指定）；如果优先级无法区分，

3.则选择复制偏移量最大的从节点；如果仍无法区分，

4.则选择runid最小的从节点。

更新主从状态：通过slaveof no one命令，让选出来的从节点成为主节点；并通过slaveof命令让其他节点成为其从节点。

将已经下线的主节点（即6379）保持关注，当6379从新上线后设置为新的主节点的从节点

### 关于哨兵模式的配置文件说明

```bash
# Example sentinel.conf

# 指定可以连接的ip
# bind 127.0.0.1 192.168.1.1

# 是否开启保护模式
protected-mode no
# 哨兵sentinel实例运行的端口 默认26379  
port 26379
#是否已守护进程模式运行
daemonize no

# When running daemonized, Redis Sentinel writes a pid file in
# /var/run/redis-sentinel.pid by default. You can specify a custom pid file
# location here.
pidfile /var/run/redis-sentinel.pid

#指定日志文件
logfile "sentinel6379.log"

# sentinel announce-ip <ip>
# sentinel announce-port <port>
#
# The above two configuration directives are useful in environments where,
# because of NAT, Sentinel is reachable from outside via a non-local address.
# sentinel announce-ip 1.2.3.4

# 哨兵sentinel的工作目录  
dir /tmp

#sentinel monitor是哨兵最核心的配置，其中：
#masterName指定了主节点名称，
#masterIp和masterPort指定了主节点地址，
#quorum是判断主节点客观下线的哨兵数量阈值：当判定主节点下线的哨兵数量达到quorum时，对主节点进行客观下线。
#建议取值为哨兵数量的一半加1。
sentinel monitor mymaster 127.0.0.1 6379 2

# 当在Redis实例中开启了requirepass foobared 授权密码 这样所有连接Redis实例的客户端都要提供密码  
# 设置哨兵sentinel 连接主从的密码 注意必须为主从设置一样的验证密码  
# sentinel auth-pass <master-name> <password>

#与主观下线的判断有关：哨兵使用ping命令对其他节点进行心跳检测，如果其他节点超过down-after-milliseconds配置的时间没有回复，
#哨兵就会将其进行主观下线。该配置对主节点、从节点和哨兵节点的主观下线判定都有效。
#down-after-milliseconds的默认值是30000，即30s；
#可以根据不同的网络环境和应用要求来调整：
#值越大，对主观下线的判定会越宽松，好处是误判的可能性小，坏处是故障发现和故障转移的时间变长，客户端等待的时间也会变长。
#例如，如果应用对可用性要求较高，则可以将值适当调小，当故障发生时尽快完成转移；如果网络环境相对较差，可以适当提高该阈值，避免频繁误判
sentinel down-after-milliseconds mymaster 30000


#与故障转移之后从节点的复制有关：它规定了每次向新的主节点发起复制操作的从节点个数。
#例如，假设主节点切换完成之后，有3个从节点要向新的主节点发起复制；
#如果parallel-syncs=1，则从节点会一个一个开始复制；
#如果parallel-syncs=3，则3个从节点会一起开始复制。
#parallel-syncs取值越大，从节点完成复制的时间越快，但是对主节点的网络负载、硬盘负载造成的压力也越大；应根据实际情况设置。
#例如，如果主节点的负载较低，而从节点对服务可用的要求较高，可以适量增加parallel-syncs取值。parallel-syncs的默认值是1
#sentinel parallel-syncs <master-name> <numreplicas>  
sentinel parallel-syncs mymaster 1

#与故障转移超时的判断有关，但是该参数不是用来判断整个故障转移阶段的超时，而是其几个子阶段的超时，
#例如如果主节点晋升从节点时间超过timeout，或从节点向新的主节点发起复制操作的时间（不包括复制数据的时间）超过timeout，都会导致故障转移超时失败。
#failover-timeout的默认值是180000，即180s；如果超时，则下一次该值会变为原来的2倍。
sentinel failover-timeout mymaster 180000

# NOTIFICATION SCRIPT
#配置当某一事件发生时所需要执行的脚本，可以通过脚本来通知管理员，例如当系统运行不正常时发邮件通知相关人员。  
#对于脚本的运行结果有以下规则：  
#若脚本执行后返回1，那么该脚本稍后将会被再次执行，重复次数目前默认为10  
#若脚本执行后返回2，或者比2更高的一个返回值，脚本将不会重复执行。  
#如果脚本在执行过程中由于收到系统中断信号被终止了，则同返回值为1时的行为相同。  
#一个脚本的最大执行时间为60s，如果超过这个时间，脚本将会被一个SIGKILL信号终止，之后重新执行。  
#通知型脚本:当sentinel有任何警告级别的事件发生时（比如说redis实例的主观失效和客观失效等等），将会去调用这个脚本，  
#这时这个脚本应该通过邮件，SMS等方式去通知系统管理员关于系统不正常运行的信息。调用该脚本时，将传给脚本两个参数，  
#一个是事件的类型，  
#一个是事件的描述。  
如果sentinel.conf配置文件中配置了这个脚本路径，那么必须保证这个脚本存在于这个路径，并且是可执行的，否则sentinel无法正常启动成功。  
#通知脚本  
# sentinel notification-script <master-name> <script-path>
# Example:
# sentinel notification-script mymaster /var/redis/notify.sh

# CLIENTS RECONFIGURATION SCRIPT
# sentinel client-reconfig-script <master-name> <script-path>
# Example:
# sentinel client-reconfig-script mymaster /var/redis/reconfig.sh

sentinel deny-scripts-reconfig yes

```

### 哨兵模式的实战建议

- 哨兵节点的数量应不止一个。一方面增加哨兵节点的冗余，避免哨兵本身成为高可用的瓶颈；另一方面减少对下线的误判。此外，这些不同的哨兵节点应部署在不同的物理机上。

- 哨兵节点的数量应该是奇数，便于哨兵通过投票做出“决策”：领导者选举的决策、客观下线的决策等。

- 各个哨兵节点的配置应一致，包括硬件、参数等；此外应保证时间准确、一致。

### 哨兵模式的缺点：

- 哨兵无法对从节点进行自动故障转移，在读写分离场景下，从节点故障会导致读服务不可用，需要我们对从节点做额外的监控、切换操作。
- 哨兵仍然没有解决写操作无法负载均衡、及存储能力受到单机限制的问题，每个节点存储的都是全量数据，存储能力无法横向扩展。

## redis-cluster集群

redis cluster集群是一个由多个主从节点群组成的分布式服务器群，它具有复制、高可用和分片特性。Redis cluster集群不需要sentinel哨兵也能完成节点移除和故障转移的功能。需要将每个节点设置成集群模式，这种集群模式没有中心节点，可水平扩展，据官方文档称可以线性扩展到1000节点。redis cluster集群的性能和高可用性均优于之前版本的哨兵模式，且集群配置非常简单。

优点：通过集群，Redis解决了写操作无法负载均衡，以及存储能力受到单机限制的问题，实现了较为完善的高可用方案。

### Redis Cluster集群节点通信

cluster集群通信是通过Gossip协议进行通讯的，通信端口默认是redis服务端口数值+10000。

![image-20201201220248077](D:\java学习笔记\mynote\redis\image-20201201220248077.png)
Gossip协议的主要职责就是信息交换。信息交换的载体就是节点彼此发送的Gossip消息，常用的Gossip消息可分为：ping消息、pong消息、meet消息、fail消息。

- meet消息：用于通知新节点加入。消息发送者通知接收者加入到当前集群，meet消息通信正常完成后，接收节点会加入到集群中并进行周期性的ping、pong消息交换。
- ping消息：集群内交换最频繁的消息，集群内每个节点每秒向多个其他节点发送ping消息，用于检测节点是否在线和交换彼此状态信息。ping消息发送封装了自身节点和部分其他节点的状态数据。
- pong消息：当接收到ping、meet消息时，作为响应消息回复给发送方确认消息正常通信。pong消息内部封装了自身状态数据。节点也可以向集群内广播自身的pong消息来通知整个集群对自身状态进行更新
- fail消息：当节点判定集群内另一个节点下线时，会向集群内广播一个fail消息，其他节点接收到fail消息之后把对应节点更新为下线状态

### Gossip算法

![image-20201201220917229](D:\java学习笔记\mynote\redis\image-20201201220917229.png)

### 集群分片策略（一致性hash算法）

redis采用的是改进版的hash算法，可以叫做hash slot，**slot = CRC16(key) % 16384**，如下图所示：

![image-20201201221251479](D:\java学习笔记\mynote\redis\image-20201201221251479.png)

redis通过对key进行CRC16的一个算法转换，然后对16384取模，得到的结果为槽值，cluster集群将16384各槽位分配到集群的每个主节点上，一般采用均分，根据算出来的槽值落在哪个区间，难么客户端就会连接到那个槽位区间对应的主节点上。

**why 16384 ???**

由于使用CRC16算法，该算法可以产生2^16-1=65535个值，而16384等于65535/4；

- 如果槽位为65536，发送心跳信息的消息头达8k，发送的心跳包过于庞大
- redis的集群主节点数量基本不可能超过1000个， 那么，对于节点数在1000以内的redis cluster集群，16384个槽位够用了。
- 槽位越小，节点少的情况下，压缩率高。

> Redis主节点的配置信息中，它所负责的哈希槽是通过一张bitmap的形式来保存的，在传输过程中，会对bitmap进行压
> 缩，但是如果bitmap的填充率slots / N很高的话(N表示节点数)，bitmap的压缩率就很低。 如果节点数很少，而哈希槽
> 数量很多的话，bitmap的压缩率就很低。

### cluster集群搭建

redis cluster集群需要至少要三个master节点，我们这里搭建三个master节点，并且给每个
master再搭建一个slave节点，总共6个redis节点，由于节点数较多，这里采用在一台机器
上创建6个redis实例，并将这6个redis实例配置成集群模式，所以这里搭建的是伪集群模
式，当然真正的分布式集群的配置方法几乎一样，搭建伪集群的步骤如下：

#### 原生搭建

1.配置开启cluster节点

```
cluster-enabled yes（启动集群模式）

cluster-config-file nodes-8001.conf（这里800x最好和port对应上）
```

2.meet

```
cluster meet ip port
```

3.指派槽

查看crc16 算法算出key的槽位命令

```bash
cluster keyslot key
```

16384/3    0-5461  5462-10922  10923-16383
16384/4    4096

```bash
cluster addslots slot（槽位下标）
```

4.分配主从

```bash
cluster replicate node-id
```

#### 使用redis提供的脚本

第一步：创建集群目录

```bash
cd /usr/local/myredis/
mkdir redis-cluster
cd redis-cluster
#一次创建6各文件夹
mkdir 7000 7001 7002 7003 7004 7005
```

第二步：在第一步创建的每个文件夹下配置redis.conf文件

```bash
#注意：x表示从1到5，执行6次，每个文件除了700x不一样，其他都一样
cp /usr/local/redis-5.8.0/redis.conf /usr/local/myredis/redis-cluster/700x
#以下是需要修改的属性
bind 192.168.19.105
port 700x
daemonize yes
dir /usr/local/redis-cluster/700x/
cluster-enabled yes  #（启动集群模式）
cluster-config-file nodes-700x.conf
cluster-node-timeout 5000
appendonly yes
```

第三步：分别启动所有redis实例

第四步：创建集群

redis5以下的版本使用/usr/local/redis3/src/redis-trib.rb命令创建集群，首先得安装ruby

（1）yum install ruby
（2）yum install rubygems
（3）gem install redis --version 3.0.0（安装redis和 ruby的接囗）

redis5.0使用下面这个命令：

```bash
/usr/local/bin/redis-cli --cluster create 192.168.0.104:7000 192.168.0.104:7001 192.168.0.104:7002 192.168.0.104:7003 192.168.0.104
:7004 192.168.0.104:7005 --cluster-replicas 1
```

命令中得1表示主节点与从节点得比例分配，如果是3主3从，那么就写2，前面3各是主节点，后面按顺序分配从节点。

集群相关命令：

- --cluster help  查看帮助
- cluster nodes  查看节点列表
- cluster info     查看集群信息

第五步：验证集群

（1）连接任意一个客户端即可：./redis-cli -c -h -p (-c表示集群模式，指定ip地址和端口
号）如：/usr/local/redis/bin/redis-cli -c -h 127.0.0.1 -p 700*
（2）进行验证： cluster info（查看集群信息）、cluster nodes（查看节点列表）
（3）进行数据操作验证
（4）关闭集群则需要逐个进行关闭，使用命令：
/usr/local/redis/bin/redis-cli -c -h 127.0.0.1 -p 700* shutdown

#### 集群动态扩容与缩容

cluster集群可以方便的实现集群扩容与缩容

**集群扩容**

1、准备新的节点，并启动

2、加入集群

原生方法：

```bash
#建立通信节点
cluster meet ip port
# 作为指定节点的从节点，node-id是主节点的id
cluster replicate node-id
```

使用redis-cli  语法（加入时指定）

```bash
#如果只是添加主节点，用这个命令就可以了
/redis-cli --cluster add-node 新节点ip:端口  已存在节点ip:端口
#这个命令可以指定主从关系
add-node 新节点ip:端口  已存在节点ip:端口  --cluster-slave --cluster-master-id masterID
```

3、迁移槽和数据

```bash
#使用命令分配slot
/redis-cli --cluster reshard 已存在节点ip:端口
#执行命令后根据提示依次输入分配槽的数量以及接受slot的节点ID,slot数值来源于所有节点的数据槽，
#slot = 16384/主节点个数 eg：16384/（原来3个主节点+新主节点）= 4096）
```

4、添加从节点

添加从节点的方式和添加主节点的一样，从节点不需要分配slot

```bash
#这个命令可以指定主从关系
/redis-cli --cluster add-node 新节点ip:端口  已存在节点ip:端口  --cluster-slave --cluster-master-id masterID
```

也可以通过下面的方式实现：

```bash
#第一步将实例添加到集群
/redis-cli --cluster add-node 新节点ip:端口  已存在节点ip:端口
#手动指定主从给关系
# 打开要分配主节点的从机客户端，然后执行下面的命令
cluster replicate 主节点ID
```

**集群缩容**

cluster删除节点操作应遵循：**先删除slave从节点，然后在删除master主节点**

删除从节点：

```bash
redis-cli --cluster del-node IP:port 节点ID -a 密码
```

删除主节点：

```bash
#第一步：迁移数据slot
redis-cli --cluster reshard --cluster-from 要迁出节点ID  --cluster-to  接收槽节点ID --cluster-slots 迁出槽数量 已存在节点ip:端口 -a 密码
#第二步：删除节点
/usr/local/bin/redis-cli --cluster del-node 192.168.154.3:7006 513873d67584cbae49632728fbe3e42656abd5eb -a 123456

```

#### cluster客户端

在使用cluster客户端时，要注意启动命令与非集群的区别是多了一个 `-c` 的指令

```bash
/usr/local/bin/redis-cli -h 192.168.19.105 -p 7000 -a 123456 -c
```

moved重定向：

> 指我们发送命令时，会对发送的key进行crc16算法，得到一个数字，然而我们连接的客户端并不是管理这个数字的范围，所以会返回错误并告诉你此key应该对应的槽位，然后客户端需要捕获此异常，重新发起请求到对应的槽位

asx重定向：

> 指在我们送发命令时，对应的客户端正在迁移槽位中，所以此时我们不能确定这个key是还在旧的节点中还是新的节点中

smart客户端

> 1.从集群中选取一个可运行节点，使用cluster slots初始化槽和节点映射。
>
> 2.将cluster slots的结果映射到本地，为每个节点创建jedispool
>
> 3.准备执行命令

cluster客户端运行机制如下图所示：

![image-20201202203759701](D:\java学习笔记\mynote\redis\image-20201202203759701.png)

#### 在 java 中使用 cluster

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TestRedisCluster {
    public static void main(String[] args) {
        Logger logger= LoggerFactory.getLogger(TestRedisCluster.class);
        Set<HostAndPort> nodesList=new HashSet<HostAndPort>();
        nodesList.add(new HostAndPort("192.168.0.104",7000));
        nodesList.add(new HostAndPort("192.168.0.104",7001));
        nodesList.add(new HostAndPort("192.168.0.104",7002));
        nodesList.add(new HostAndPort("192.168.0.104",7003));
        nodesList.add(new HostAndPort("192.168.0.104",7004));
        nodesList.add(new HostAndPort("192.168.0.104",7005));

        // Jedis连接池配置
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 最大空闲连接数, 默认8个
        jedisPoolConfig.setMaxIdle(200);
        // 最大连接数, 默认8个
        jedisPoolConfig.setMaxTotal(1000);
        //最小空闲连接数, 默认0
        jedisPoolConfig.setMinIdle(100);
        // 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        jedisPoolConfig.setMaxWaitMillis(3000); // 设置2秒
        //对拿到的connection进行validateObject校验
        jedisPoolConfig.setTestOnBorrow(false);
        JedisCluster jedisCluster=new JedisCluster(nodesList,jedisPoolConfig);
        // System.out.println(jedisCluster.mset("k1", "v1", "k2", "v2", "k3", "v3"));
        // System.out.println(jedisCluster.mget("k1","k2", "k3" ));
       while (true) {
           try {
               String s = UUID.randomUUID().toString();
               jedisCluster.set("k" + s, "v" + s);
               System.out.println(jedisCluster.get("k" + s));
               Thread.sleep(1000);
           }catch (Exception e){
               logger.error(e.getMessage());
           }finally {
               if(jedisCluster!=null){
                   jedisCluster.close();
               }
           }
       }
    }
}

```

**注意：在cluster模式中，不支持连续操作，如mset**

#### cluster模式故障转移原理

**故障发现** 

通过ping/pong消息实现故障发现（不依赖sentinel）

**故障恢复**

- 检查资格
  - 每个从节点检查与主节点的断开时间，超过cluster-node-timeout * cluster-replica-validity-factor 时间取消资格

  - 选择偏移量最大的

- 替换主节点
  - 1.当前从节点取消复制变为主节点（slaveof no one）
  - 2.撤销以前主节点的槽位，给新的主节点
  - 3.向集群广播消息，表明已经替换了故障节点