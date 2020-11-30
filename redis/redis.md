# Redis

官方网站：http://redis.io

## Redis介绍

redis是key-value型数据库，适合做数据共享和缓存

## redis优势

## Redis安装

### Windows安装

### Linux 安装

本文采用源码安装， linux系统版本为Centos7，redis版本为5.0.8

本人下载的软件包放在/download目录下

```bash
#1.使用wget下载软件包，wget命令默认下载到当前目录
wget http://download.redis.io/releases/redis-5.0.8.tar.gz
#2.解压
tar xzf redis-5.0.8.tar.gz
cd redis-5.0.8
#执行make命令，编译
make
#执行make install 将可执行脚本放到/usr/local/bin目录下
make install
```

执行完make install 之后，我们可以进入/usr/local/bin目录看一下生成的redis脚本文件，并做简单介绍。

![image-20201129230854962](D:\java学习笔记\mynote\redis\image-20201129230854962.png)

- redis-benchmark -----性能测试脚本
- redis-check-aof ----修复aof文件
- redis-check-rdb ---修复rdb文件
- redis-cli ----客户端连接
- redis-sentinel -> redis-server ----哨兵集群
- redis-server ----redis服务

## redis设置外网访问

```
 1.注释bind并且把protected-mode no
 2.使用bind
 3.设置密码
 protected-mode它启用的条件有两个，第一是没有使用bind，第二是没有设置访问密码。
```

第一步：进入redis.conf文件，修改bind 0.0.0.0 或者注释掉bind,如果采用注释bind的方式，需要同时将protected-mode配置为no，默认是yes的，protected-mode是保护模式，意思是只允许本机访问。

那么为什么设置bind 0.0.0.0 而protected-mode yes也可以访问呢？protected-mode yes启用必须满足两个条件第一是没有使用bind，第二是没有设置访问密码。

## redis访问密码设置

在配置文件中配置requirepass

```bash
# 默认是关闭的，可以修改其值，这样连接redis时需要通过auth <password>的方式连接
requirepass foobared
```

redis启动

```bash
#默认在/usr/local/bin目录下执行
./redis-server
# 也可以后面跟指定的配置文件来执行
./redis-server /usr/local/myredis/redis.conf
```

redis停止

```bash
# 查看redis进程id
ps -ef | grep redis
# kill掉redis进程
kill -9 进程ID
```




## redis数据类型

 - 字符串(string)
 - 哈希
 - 集合
 - 列表

### 字符串(string)

string是redis最基本的类型，你可以理解成与Memcached一模一样的类型，一个key对应一个value。一个redis中字符串value最多可以是512M。

string类型是二进制安全的。意思是redis的string可以包含任何数据。比如jpg图片或者序列化的对象 。

- `set  key  value`   设置key  value
- `get  key`    查看当前key的值
- `del  key`   删除key
- `append key  value`   如果key存在，则在指定的key末尾添加，如果key存在则类似set
- `strlen  key`  返回此key的长度

以下几个命令只有在key值为数字的时候才能正常操作：

------

- `incr  key`  为执定key的值加一
- `decr  key`  为指定key的值减一
- `incrby key`  数值     为指定key的值增加数值
- `decrby key`  数值     为指定key的值减数值

------

- `getrange  key  0(开始位置)  -1（结束位置）`    获取指定区间范围内的值，类似between......and的关系 （0 -1）表示全部
- `setrange key 1（开始位置，从哪里开始设置） 具体值`    设置（替换）指定区间范围内的值
- `setex 键 秒值 真实值`    设置带过期时间的key，动态设置。
- `setnx  key   value`         只有在 key 不存在时设置 key 的值。
- `mset   key1   value  key2   value`       同时设置一个或多个 key-value 对。
- `mget   key1   key 2`    获取所有(一个或多个)给定 key 的值。
- `msetnx   key1   value  key2   value`   同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在。
- `getset   key    value`  将给定 key 的值设为 value ，并返回 key 的旧值(old value)。

### 哈希(hash)

Redis hash 是一个键值对集合。
Redis hash是一个string类型的field和value的映射表，hash特别适合用于存储对象。

kv模式不变，但v是一个键值对，类似Java里面的Map<String,Object>

- `hset  key  (key value)`  向hash表中添加一个元素
- `hget key  key`  向hash表中获取一个元素
- `hmset  key key1 value1 key2 value2 key3 value3` 向集合中添加一个或多个元素
- `hmget key  key1 key2 key3`  向集合中获取一个或多个元素
- `hgetall  key`   获取在hash列表中指定key的所有字段和值
- `hdel  key  key1 key2` 删除一个或多个hash字段
- `hlen key` 获取hash表中字段数量
- `hexits key key`  查看hash表中，指定key（字段）是否存在
- `hkeys  key` 获取指定hash表中所有key（字段）
- `hvals key` 获取指定hash表中所有value(值)
- `hincrdy key  key1  数量（整数）`  执定hash表中某个字段加  数量  ，和incr一个意思
- `hincrdyfloat key key1  数量（浮点数，小数）`  执定hash表中某个字段加  数量  ，和incr一个意思
- `hsetnx key key1 value1`  与hset作用一样，区别是不存在赋值，存在了无效。

### 列表(list)

列表是简单的字符串列表，按照插入顺序排序，可以在头部或尾部插入元素
如果键不存在，创建新的链表；
如果键已存在，新增内容；
如果值全移除，对应的键也就消失了；
链表的操作无论是头和尾效率都极高，但假如是对中间元素进行操作，效率就很惨淡了；
它的底层实际是个链表。

- `lpush  key  value1  value2`  将一个或多个值加入到列表头部
- `rpush  key  value1  value2` 将一个或多个值加入到列表底部
- `lrange key  start  end`  获取列表指定范围的元素   （0 -1）表示全部
- `lpop key` 移出并获取列表第一个元素
- `rpop key`  移出并获取列表最后一个元素
- `lindex key index`   通过索引获取列表中的元素 
- `llen` 获取列表长度
-  `lrem key   0（数量） 值`，表示删除全部给定的值。零个就是全部值   从left往right删除指定数量个值等于指定值的元素，返回的值为实际删除的数量

- `ltrim key  start(从哪里开始截)  end（结束位置）` 截取指定索引区间的元素，格式是ltrim list的key 起始索引 结束索引

### 集合(set)

集合是字符串类型的无序集合，元素是不重复的，如果插入的元素已存在则返回0

- `sadd key value1 value 2` 向集合中添加一个或多个成员
- `smembers  key`  返回集合中所有成员
- `sismembers  key   member`  判断member元素是否是集合key的成员
- `scard key`  获取集合里面的元素个数
- `srem key value`  删除集合中指定元素
- `srandmember key  数值`     从set集合里面随机取出指定数值个元素   如果超过最大数量就全部取出，
- `spop key`  随机移出并返回集合中某个元素
- `smove  key1  key2  value(key1中某个值)`   作用是将key1中执定的值移除  加入到key2集合中
- `sdiff key1 key2`  在第一个set里面而不在后面任何一个set里面的项(差集)
- `sinter key1 key2`  在第一个set和第二个set中都有的 （交集）
- `sunion key1 key2`  两个集合所有元素（并集）

### 有序集合（zset）

Redis 有序集合和集合一样也是string类型元素的集合,且不允许重复的成员。

不同的是每个元素都会关联一个double类型的分数。redis正是通过分数来为集合中的成员进行从小到大的排序。

有序集合的成员是唯一的,但分数(score)却可以重复。

- `zadd  key  score 值   score 值`   向集合中添加一个或多个成员
- `zrange key  0   -1`  表示所有   返回指定集合中所有value
- `zrange key  0   -1  withscores`  返回指定集合中所有value和score
- `zrangebyscore key 开始score 结束score`    返回指定score间的值
- `zrem key score某个对应值（value）`，可以是多个值   删除元素
- `zcard key`  获取集合中元素个数
- `zcount key   开始score 结束score`       获取分数区间内元素个数
- `zrank key vlaue`   获取value在zset中的下标位置(根据score排序)
- `zscore key value`  按照值获得对应的分数


## 发布订阅

- `subscribe channelA` 订阅channelA

- `publish channelA anhuiTV` 在channelA上发布消息

- `ZRANGE key start stop [WITHSCORES]` 通过索引区间返回有序集合指定区间内的成员,withscore选项可以查询出分数

## 事务

Redis 事务可以一次执行多个命令， 并且带有以下三个重要的保证：

- 批量操作在发送 EXEC 命令前被放入队列缓存。
- 收到 EXEC 命令后进入事务执行，事务中任意命令执行失败，其余的命令依然被执行。
- 在事务执行过程，其他客户端提交的命令请求不会插入到事务执行命令序列中。

一个事务从开始到执行会经历以下三个阶段：

- 开始事务。 通过multi开启一个事务
- 命令入队。 此后执行的所有的命令都会进入队列中
- 执行事务。 当遇到exec命令时，批量执行以上操作


```
// 在node.js中的使用案例：
const redis = require('redis')
const client = redis.createClient(6379,'localhost')
client.multi().set('k1','v1').set('k2','v2').get('k2').exec((err,result)=>{
	console.log(result)
})
```



## Redis 持久化

Redis备份有以下两种方式：

- 快照（snapshotting）备份当前瞬间在内存中的数据，Redis持久化数据文件为rdb文件。

- AOP(append-only file )	当redis 执行写命令时，在一定条件下将执行过的写命令保存在Redis的文件中，文件后缀为.aop

redis默认的是第一种方式。而我们在使用redis时可以同时使用两种，也可以两者都不用。以下是Redis默认的持久化配置。

```bash
################################ SNAPSHOTTING  ################################
#

save 900 1
save 300 10
save 60 10000

stop-writes-on-bgsave-error yes

rdbcompression yes

rdbchecksum yes

dbfilename dump.rdb

dir ./

############################## APPEND ONLY MODE ###############################

# By default Redis asynchronously dumps the dataset on disk. This mode is
#AOF模式默认时关闭的，设置为yes，则会开启AOF方式备份数据
appendonly no
#默认的文件名
appendfilename "appendonly.aof"
#AOF文件和Redis命令是同步更新的，值有以下几种：
# always 表示执行命令的时候就更新AOF文件
# everysec 表示每秒同步一次
# no 表示执行命令时，Redis本身不备份文件
appendfsync everysec
# 表示AOF文件重写期间调用fsync,默认为no，表示要调用fsync(无论后台是否有子进程在刷盘)
no-appendfsync-on-rewrite no
# Redis重写AOF文件的条件为文件增长为100%，即大小为原来的2倍
auto-aof-rewrite-percentage 100
# Redis重写AOF文件的条件，文件大小要大于64M，以上两个条件必须同时满足才行
auto-aof-rewrite-min-size 64mb
# 设置Redis在恢复时是否忽略最后一条可能存在问题的指令，yes会继续，no会直接恢复失败
aof-load-truncated yes
# 是否开启RDB和AOF的混合使用
aof-use-rdb-preamble yes
```

### RDB持久化（快照）

#### RDB持久化的原理

原理是redis会单独创建（fork）一个与当前线程一模一样的子进程来进行持久化，这个子线程的所有数据（变量。环境变量，程序程序计数器等）都和原进程一模一样，会先将数据写入到一个临时文件中，待持久化结束了，再用这个临时文件替换上次持久化好的文件，整个过程中，主进程不进行任何的io操作，这就确保了极高的性能。

#### RDB持久化触发机制

- shutdown时，如果没有开启aof，会触发
- 配置文件中默认的快照配置
- 执行命令save或者bgsave  （save是只管保存，其他不管，全部阻塞），而 bgsave： redis会在后台异步进行快照操作，同时可以响应客户端的请求
-  执行flushall命令  但是里面是空的，无意义

#### 关于RDB持久化的测试

前期准备：

（1）一个存储500万数据的dump5000000.rdb文件；

（2）打开两个redis客户端；

（3）在redis.conf文件中修改rdb文件读取路径，然后启动redis服务

### AOF 持久化

#### AOF 持久化的原理

原理是将 Reids 的操作命令以追加的方式写入文件，读操作是不记录的

#### AOF 持久化触发机制

根据配置文件配置项

no：表示等操作系统进行数据缓存同步到磁盘（快，持久化没保证）
always：同步持久化，每次发生数据变更时，立即记录到磁盘（慢，安全）
everysec：表示每秒同步一次（默认值,很快，但可能会丢失一秒以内的数据）

#### AOF 重写机制

当AOF文件增长到一定大小的时候，Redis能够调用 bgrewriteaof对日志文件进行重写 。当AOF文件大小的增长率大于该配置项时自动开启重写（这里指超过原大小的100%）。
auto-aof-rewrite-percentage 100

当AOF文件增长到一定大小的时候Redis能够调用 bgrewriteaof对日志文件进行重写 。当AOF文件大小大于该配置项时自动开启重写

auto-aof-rewrite-min-size 64mb

### 混合持久化机制（redis4.0版本之后）

在配置文件中通过配置项 `aof-use-rdb-preamble yes` 来开启混合持久化

混合持久化是通过bgrewriteaof完成的，不同的是当开启混合持久化时，fork出的子进程先将共享的内存副本全量的以RDB方式写入aof文件，然后在将重写缓冲区的增量命令以AOF方式写入到文件，写入完成后通知主进程更新统计信息，并将新的含有RDB格式和AOF格式的AOF文件替换旧的的AOF文件。简单的说：新的AOF文件前半段是RDB格式的全量数据后半段是AOF格式的增量数据，

优点：混合持久化结合了RDB持久化 和 AOF 持久化的优点, 由于绝大部分都是RDB格式，加载速度快，同时结合AOF，增量的数据以AOF方式保存了，数据更少的丢失。

缺点：兼容性差，一旦开启了混合持久化，在4.0之前版本都不识别该aof文件，同时由于前部分是RDB格式，阅读性较差。

### 关于 Redis 持久化的总结

1. **redis提供了rdb持久化方案，为什么还要aof？**

优化数据丢失问题，rdb会丢失最后一次快照后的数据，aof丢失不会超过2秒的数据

2. **如果aof和rdb同时存在，听谁的？**

aof

3. **rdb和aof优势劣势**

rdb 适合大规模的数据恢复，对数据完整性和一致性不高 ，  在一定间隔时间做一次备份，如果redis意外down机的话，就会丢失最后一次快照后的所有操作
aof 根据配置项而定

**官方建议   两种持久化机制同时开启，如果两个同时开启  优先使用aof持久化机制**  

4. **性能建议（这里只针对单机版redis持久化做性能建议）：**

因为RDB文件只用作后备用途，只要15分钟备份一次就够了，只保留save 900 1这条规则。

如果Enalbe AOF，好处是在最恶劣情况下也只会丢失不超过两秒数据，启动脚本较简单只load自己的AOF文件就可以了。
代价一是带来了持续的IO，二是AOF rewrite的最后将rewrite过程中产生的新数据写到新文件造成的阻塞几乎是不可避免的。
只要硬盘许可，应该尽量减少AOF rewrite的频率，AOF重写的基础大小默认值64M太小了，可以设到5G以上。
默认超过原大小100%大小时重写可以改到适当的数值。

### Redis持久化工作机制

![redis持久化工作机制](D:\java学习笔记\mynote\redis\redis持久化工作机制.jpg)

## Redis 内存回收策略

redis5.0版本提供了8种内存管理，如下所示：

```bash
############################## MEMORY MANAGEMENT #############################

# volatile-lru -> Evict using approximated LRU among the keys with an expire set.
# allkeys-lru -> Evict any key using approximated LRU.
# volatile-lfu -> Evict using approximated LFU among the keys with an expire set.
# allkeys-lfu -> Evict any key using approximated LFU.
# volatile-random -> Remove a random key among the ones with an expire set.
# allkeys-random -> Remove a random key, any key.
# volatile-ttl -> Remove the key with the nearest expire time (minor TTL)
# noeviction -> Don't evict anything, just return an error on write operations.
```

Redis在默认情况下会采用noeviction策略，即内存满时，不再提供写操作，只允许读操作。

说明：LRU算法和TTL算法都是近似的算法，Redis不会通过消耗太多的时间去精确删除哪个键值对，这会导致回收垃圾执行的时间太长，造成服务停顿。

假设我们采取了volatile-ttl算法，在redis的配置文件中提供了 `maxmemory-samples` 默认值为5，表示按顺序探测5个，那么有可能匹配到的最短超时剩余时间不是所有待回收中最小的那个。

**回收超时策略的缺点：**

- 必须指明超时的键值对，增加开发者工作。
- 对所有键值对进行回收，有可能删除正在使用的键值对，增加了存储的不稳定性。