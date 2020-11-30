## Redis安装

Linux下安装Redis

```bash
wget https://download.redis.io/releases/redis-6.0.9.tar.gz
tar xzf redis-6.0.9.tar.gz
cd redis-6.0.9
make
```

使用make编译前需要安装编译环境

```bash
yum -y install gcc make gcc-c++
```

redis 默认安装位置为 /usl/local下

## Redis 备份（持久化）

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

shutdown时，如果没有开启aof，会触发
 配置文件中默认的快照配置
 执行命令save或者bgsave  save是只管保存，其他不管，全部阻塞   bgsave： redis会在后台异步进行快照操作，同时可以响应客户端的请求
 执行flushall命令  但是里面是空的，无意义

### AOF 持久化

#### AOF 持久化的原理

原理是将 Reids 的操作命令以追加的方式写入文件，读操作是不记录的

#### AOF 持久化触发机制

根据配置文件配置项

no：表示等操作系统进行数据缓存同步到磁盘（快，持久化没保证）
always：同步持久化，每次发生数据变更时，立即记录到磁盘（慢，安全）
everysec：表示每秒同步一次（默认值,很快，但可能会丢失一秒以内的数据）

#### AOF 重写机制

当AOF文件增长到一定大小的时候Redis能够调用 bgrewriteaof对日志文件进行重写 。当AOF文件大小的增长率大于该配置项时自动开启重写（这里指超过原大小的100%）。
auto-aof-rewrite-percentage 100

当AOF文件增长到一定大小的时候Redis能够调用 bgrewriteaof对日志文件进行重写 。当AOF文件大小大于该配置项时自动开启重写

auto-aof-rewrite-min-size 64mb

### 混合持久化机制（redis4.0版本之后）

在配置文件中通过配置项 `aof-use-rdb-preamble yes` 来开启混合持久化

混合持久化是通过bgrewriteaof完成的，不同的是当开启混合持久化时，fork出的子进程先将共享的内存副本全量的以RDB方式写入aof文件，然后在将重写缓冲区的增量命令以AOF方式写入到文件，写入完成后通知主进程更新统计信息，并将新的含有RDB格式和AOF格式的AOF文件替换旧的的AOF文件。简单的说：新的AOF文件前半段是RDB格式的全量数据后半段是AOF格式的增量数据，

优点：混合持久化结合了RDB持久化 和 AOF 持久化的优点, 由于绝大部分都是RDB格式，加载速度快，同时结合AOF，增量的数据以AOF方式保存了，数据更少的丢失。

缺点：兼容性差，一旦开启了混合持久化，在4.0之前版本都不识别该aof文件，同时由于前部分是RDB格式，阅读性较差。

### 关于 Redis 持久化的总结

#### 1. redis提供了rdb持久化方案，为什么还要aof？

优化数据丢失问题，rdb会丢失最后一次快照后的数据，aof丢失不会超过2秒的数据

#### 2. 如果aof和rdb同时存在，听谁的？

aof

#### 3. rdb和aof优势劣势

rdb 适合大规模的数据恢复，对数据完整性和一致性不高 ，  在一定间隔时间做一次备份，如果redis意外down机的话，就会丢失最后一次快照后的所有操作
aof 根据配置项而定

1.官方建议   两种持久化机制同时开启，如果两个同时开启  优先使用aof持久化机制  

#### 4. 性能建议（这里只针对单机版redis持久化做性能建议）：

因为RDB文件只用作后备用途，只要15分钟备份一次就够了，只保留save 900 1这条规则。

如果Enalbe AOF，好处是在最恶劣情况下也只会丢失不超过两秒数据，启动脚本较简单只load自己的AOF文件就可以了。
代价一是带来了持续的IO，二是AOF rewrite的最后将rewrite过程中产生的新数据写到新文件造成的阻塞几乎是不可避免的。
只要硬盘许可，应该尽量减少AOF rewrite的频率，AOF重写的基础大小默认值64M太小了，可以设到5G以上。
默认超过原大小100%大小时重写可以改到适当的数值。

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