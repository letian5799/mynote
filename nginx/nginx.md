# nginx学习

## nginx 优势

- 高并发高性能
- 可扩展性好
- 高可靠性
- 热部署
- 开源许可证

## nginx 主要应用场景

- 静态资源服务，通过本地文件系统提供服务
- 反向代理服务、负载均衡
- API服务、权限控制，减少应用服务器压力

## nginx安装

### nginx环境准备

nginx是C语言开发，建议在linux上运行，本教程使用Centos6.4作为安装环境。

```bash
yum -y install gcc gcc-c++ autoconf pcre pcre-devel make automake wget openssl openssl-devel
yum -y install wget httpd-tools vim
```

- `gcc/gcc-c++` 安装nginx需要先将官网下载的源码进行编译，编译依赖gcc环境，如果没有gcc环境，需要安装gcc
- `autoconf` 是一个软件包，以适应多种Uninx系统的shell脚本的工具
- `pcre` PCRE(Perl Compatible Regular Expressions)是一个Perl库，包括 perl 兼容的正则表达式库。nginx的http模块使用pcre来解析正则表达式，所以需要在linux上安装pcre库。
- `pcre-devel` 此包主要是用来供开发使用，包含头文件和链接库
- `make/automake` 用来编译文件的
- `wget` 下载文件的工具
- `httpd-tools` 压测工具
- `vim` 编辑器

### nginx下载及安装

- [nginx压缩包下载](nginx.org/en/download.html)
- [通过yum安装教程](nginx.org/en/linux_packages.html)
- [源码安装教程](D:\BaiduNetdiskDownload\nginx安装手册.doc)

### 通过yum安装nginx

```bash
sudo yum install yum-utils
vi /etc/yum.repos.d/nginx.repo
```

在上面创建的文件中写入以下内容：

```bash
[nginx-stable]
name=nginx stable repo
baseurl=http://nginx.org/packages/centos/$releasever/$basearch/
gpgcheck=1
enabled=1
gpgkey=https://nginx.org/keys/nginx_signing.key
module_hotfixes=true

[nginx-mainline]
name=nginx mainline repo
baseurl=http://nginx.org/packages/mainline/centos/$releasever/$basearch/
gpgcheck=1
enabled=0
gpgkey=https://nginx.org/keys/nginx_signing.key
module_hotfixes=true
```

保存退出后  `yum list | grep nginx`  查看是否输出如下：

```bash
[root@c1 ~]# yum list | grep nginx
nginx.x86_64                               1.18.0-1.el6.ngx             nginx-stable
nginx-debug.x86_64                         1.8.0-1.el6.ngx              nginx-stable
nginx-debuginfo.x86_64                     1.18.0-1.el6.ngx             nginx-stable
nginx-module-geoip.x86_64                  1.18.0-1.el6.ngx             nginx-stable
nginx-module-geoip-debuginfo.x86_64        1.18.0-1.el6.ngx             nginx-stable
nginx-module-image-filter.x86_64           1.18.0-1.el6.ngx             nginx-stable
nginx-module-image-filter-debuginfo.x86_64 1.18.0-1.el6.ngx             nginx-stable
nginx-module-njs.x86_64                    1.18.0.0.4.4-1.el6.ngx       nginx-stable
nginx-module-njs-debuginfo.x86_64          1.18.0.0.4.4-1.el6.ngx       nginx-stable
nginx-module-perl.x86_64                   1.18.0-1.el6.ngx             nginx-stable
nginx-module-perl-debuginfo.x86_64         1.18.0-1.el6.ngx             nginx-stable
nginx-module-xslt.x86_64                   1.18.0-1.el6.ngx             nginx-stable
nginx-module-xslt-debuginfo.x86_64         1.18.0-1.el6.ngx             nginx-stable
nginx-nr-agent.noarch                      2.0.0-12.el6.ngx             nginx-stable
pcp-pmda-nginx.x86_64                      3.10.9-9.el6                 base    
[root@c1 ~]# 
```

之后通过 `yum install -y nginx` 安装

安装完成后可通过 `nginx -v` 查看版本，`nginx -V` 查看编译时的参数

### nginx 编译安装

```bash
#下载nginx压缩包
wget http://nginx.org/download/nginx-1.14.0.tar.gz

#解压到指定目录
tar -zxvf nginx-1.14.0.tar.gz -C /usr/local/

#在nginx安装目录下，生成Makefile文件
[root@c1 nginx-1.14.0]# ./configure  --prefix=/usr/local/nginx  --sbin-path=/usr/local/nginx/sbin/nginx --conf-path=/usr/local/nginx/conf/nginx.conf --error-log-path=/var/log/nginx/error.log  --http-log-path=/var/log/nginx/access.log  --pid-path=/var/run/nginx/nginx.pid --lock-path=/var/lock/nginx.lock  --user=nginx --group=nginx  --with-pcre

#安装：make install
[root@c1 nginx-1.14.0]# make && make install

```

由于以上生成makefile文件时指定了nginx的用户及用户组为nginx，所以此时如果启动nginx会报错

```bash
[emerg\]: getpwnam(“nginx”) failed
```

所以我们可以通过以下几步来解决该问题

```bash
#添加用户，不需要登陆
[root@localhost nginx-1.11.2]# useradd -s /sbin/nologin -M nginx
#查看nginx用户
[root@localhost nginx-1.11.2]# id nginx
#启动nginx
[root@localhost nginx-1.11.2]# /usr/local/nginx/sbin/nginx
```

当然，我们也可以在生成makefile时，不指定用户权限来避免这个问题，不过不推荐这么做，因为root权限太大，不安全。

## nginx 配置文件和目录

通过 `rpm -ql nginx` 查看nginx安装的配置文件和目录

```bash
/etc/logrotate.d/nginx
/etc/nginx
/etc/nginx/conf.d
/etc/nginx/conf.d/default.conf
/etc/nginx/fastcgi_params
/etc/nginx/koi-utf
/etc/nginx/koi-win
/etc/nginx/mime.types
/etc/nginx/modules
/etc/nginx/nginx.conf
/etc/nginx/scgi_params
/etc/nginx/uwsgi_params
/etc/nginx/win-utf
/etc/rc.d/init.d/nginx
/etc/rc.d/init.d/nginx-debug
/etc/sysconfig/nginx
/etc/sysconfig/nginx-debug
/usr/lib64/nginx
/usr/lib64/nginx/modules
/usr/sbin/nginx
/usr/sbin/nginx-debug
/usr/share/doc/nginx-1.18.0
/usr/share/doc/nginx-1.18.0/COPYRIGHT
/usr/share/man/man8/nginx.8.gz
/usr/share/nginx
/usr/share/nginx/html
/usr/share/nginx/html/50x.html
/usr/share/nginx/html/index.html
/var/cache/nginx
/var/log/nginx

```

- `/etc/nginx/nginx.conf` 核心配置文件
- `/etc/nginx/conf.d/default.conf` 默认http服务器配置文件
- `/etc/nginx/fastcgi_params` fastcgi配置
- `/etc/nginx/scgi_params` scgi配置
- `/etc/nginx/uwsgi_params` uwsgi配置
- `/etc/nginx/koi-utf`
- `/etc/nginx/koi-win`
- `/etc/nginx/win-utf` 这三个文件是编码映射文件，因为作者是俄国人
- `/etc/nginx/mime.types` 设置HTTP协议的Content-Type与扩展名对应关系的文件
- `/usr/lib/systemd/system/nginx-debug.service`
- `/usr/lib/systemd/system/nginx.service`
- `/etc/sysconfig/nginx`
- `/etc/sysconfig/nginx-debug` 这四个文件是用来配置守护进程管理的
- `/etc/nginx/modules` 基本共享库和内核模块
- `/usr/share/doc/nginx-1.18.0` 帮助文档
- `/usr/share/doc/nginx-1.18.0/COPYRIGHT` 版权声明
- `/usr/share/man/man8/nginx.8.gz` 手册
- `/var/cache/nginx` Nginx的缓存目录
- `/var/log/nginx` Nginx的日志目录
- `/usr/sbin/nginx` 可执行命令
- `/usr/sbin/nginx-debug` 调试执行可执行命令

## nginx 配置文件

nginx核心配置文件 `/etc/nginx/nginx.conf`

### 配置文件语法

- 使用#注释，$符号可以使用变量
- 配置文件由指令和指令块组成，指令块以{}将多条指令组织在一起
- include语句可以把多个配置文件组合起来，提升可维护性
- 每条指令以分号(;)结尾，指令与参数之间以空格分隔
- 有些指令可以支持正则表达式，如 `~  \.png$`表示匹配.png结尾的文件

nginx是以模块组合而成的，下面是一个比较全的配置文件写法，仅供参考。

```bash
######Nginx配置文件nginx.conf中文详解#####

#定义Nginx运行的用户和用户组
user nginx nginx;

#nginx进程数，建议设置为等于CPU总核心数。
worker_processes 8;
 
#全局错误日志定义类型，[ debug | info | notice | warn | error | crit ]
error_log /usr/local/nginx/logs/error.log info;

#进程pid文件
pid /usr/local/nginx/logs/nginx.pid;

#指定进程可以打开的最大描述符：数目
#工作模式与连接数上限
#这个指令是指当一个nginx进程打开的最多文件描述符数目，理论值应该是最多打开文件数（ulimit -n）与nginx进程数相除，但是nginx分配请求并不是那么均匀，所以最好与ulimit -n 的值保持一致。
#现在在linux 2.6内核下开启文件打开数为65535，worker_rlimit_nofile就相应应该填写65535。
#这是因为nginx调度时分配请求到进程并不是那么的均衡，所以假如填写10240，总并发量达到3-4万时就有进程可能超过10240了，这时会返回502错误。
worker_rlimit_nofile 65535;


events
{
    #参考事件模型，use [ kqueue | rtsig | epoll | /dev/poll | select | poll ]; epoll模型
    #是Linux 2.6以上版本内核中的高性能网络I/O模型，linux建议epoll，如果跑在FreeBSD上面，就用kqueue模型。
    #补充说明：
    #与apache相类，nginx针对不同的操作系统，有不同的事件模型
    #A）标准事件模型
    #Select、poll属于标准事件模型，如果当前系统不存在更有效的方法，nginx会选择select或poll
    #B）高效事件模型
    #Kqueue：使用于FreeBSD 4.1+, OpenBSD 2.9+, NetBSD 2.0 和 MacOS X.使用双处理器的MacOS X系统使用kqueue可能会造成内核崩溃。
    #Epoll：使用于Linux内核2.6版本及以后的系统。
    #/dev/poll：使用于Solaris 7 11/99+，HP/UX 11.22+ (eventport)，IRIX 6.5.15+ 和 Tru64 UNIX 5.1A+。
    #Eventport：使用于Solaris 10。 为了防止出现内核崩溃的问题， 有必要安装安全补丁。
    use epoll;

    #单个进程最大连接数（最大连接数=连接数*进程数）
    #根据硬件调整，和前面工作进程配合起来用，尽量大，但是别把cpu跑到100%就行。每个进程允许的最多连接数，理论上每台nginx服务器的最大连接数为。
    worker_connections 65535;

    #keepalive超时时间。
    keepalive_timeout 60;

    #客户端请求头部的缓冲区大小。这个可以根据你的系统分页大小来设置，一般一个请求头的大小不会超过1k，不过由于一般系统分页都要大于1k，所以这里设置为分页大小。
    #分页大小可以用命令getconf PAGESIZE 取得。
    #[root@web001 ~]# getconf PAGESIZE
    #4096
    #但也有client_header_buffer_size超过4k的情况，但是client_header_buffer_size该值必须设置为“系统分页大小”的整倍数。
    client_header_buffer_size 4k;

    #这个将为打开文件指定缓存，默认是没有启用的，max指定缓存数量，建议和打开文件数一致，inactive是指经过多长时间文件没被请求后删除缓存。
    open_file_cache max=65535 inactive=60s;

    #这个是指多长时间检查一次缓存的有效信息。
    #语法:open_file_cache_valid time 默认值:open_file_cache_valid 60 使用字段:http, server, location 这个指令指定了何时需要检查open_file_cache中缓存项目的有效信息.
    open_file_cache_valid 80s;

    #open_file_cache指令中的inactive参数时间内文件的最少使用次数，如果超过这个数字，文件描述符一直是在缓存中打开的，如上例，如果有一个文件在inactive时间内一次没被使用，它将被移除。
    #语法:open_file_cache_min_uses number 默认值:open_file_cache_min_uses 1 使用字段:http, server, location  这个指令指定了在open_file_cache指令无效的参数中一定的时间范围内可以使用的最小文件数,如果使用更大的值,文件描述符在cache中总是打开状态.
    open_file_cache_min_uses 1;
    
    #语法:open_file_cache_errors on | off 默认值:open_file_cache_errors off 使用字段:http, server, location 这个指令指定是否在搜索一个文件是记录cache错误.
    open_file_cache_errors on;
}
 
 
 
#设定http服务器，利用它的反向代理功能提供负载均衡支持
http
{
    #文件扩展名与文件类型映射表
    include /etc/nginx/mime.types;

    #默认的Content-Type为文件类型
    default_type application/octet-stream;
		#定义一个日志格式main
		log_format main '$remote_addr - $remote_user [$time_local] "$request" '
										'$status $body_bytes_sent "$http_remote" '
										'"$http_user_agent" "$http_x_forwarded_for" '
		#指定访问日志的存放位置以及设置日志格式为main
		access.log /var/log/nginx/access.log main;
    #默认编码
    #charset utf-8;
		
    #服务器名字的hash表大小
    #保存服务器名字的hash表是由指令server_names_hash_max_size 和server_names_hash_bucket_size所控制的。参数hash bucket size总是等于hash表的大小，并且是一路处理器缓存大小的倍数。在减少了在内存中的存取次数后，使在处理器中加速查找hash表键值成为可能。如果hash bucket size等于一路处理器缓存的大小，那么在查找键的时候，最坏的情况下在内存中查找的次数为2。第一次是确定存储单元的地址，第二次是在存储单元中查找键 值。因此，如果Nginx给出需要增大hash max size 或 hash bucket size的提示，那么首要的是增大前一个参数的大小.
    server_names_hash_bucket_size 128;

    #客户端请求头部的缓冲区大小。这个可以根据你的系统分页大小来设置，一般一个请求的头部大小不会超过1k，不过由于一般系统分页都要大于1k，所以这里设置为分页大小。分页大小可以用命令getconf PAGESIZE取得。
    client_header_buffer_size 32k;

    #客户请求头缓冲大小。nginx默认会用client_header_buffer_size这个buffer来读取header值，如果header过大，它会使用large_client_header_buffers来读取。
    large_client_header_buffers 4 64k;

    #设定通过nginx上传文件的大小
    client_max_body_size 8m;

    #开启高效文件传输模式，sendfile指令指定nginx是否调用sendfile函数来输出文件，对于普通应用设为 on，如果用来进行下载等应用磁盘IO重负载应用，可设置为off，以平衡磁盘与网络I/O处理速度，降低系统的负载。注意：如果图片显示不正常把这个改成off。
    #sendfile指令指定 nginx 是否调用sendfile 函数（zero copy 方式）来输出文件，对于普通应用，必须设为on。如果用来进行下载等应用磁盘IO重负载应用，可设置为off，以平衡磁盘与网络IO处理速度，降低系统uptime。
    sendfile on;

    #开启目录列表访问，合适下载服务器，默认关闭。
    autoindex on;

    #此选项允许或禁止使用socke的TCP_CORK的选项，此选项仅在使用sendfile的时候使用
    tcp_nopush on;
     
    tcp_nodelay on;

    #长连接超时时间，单位是秒
    keepalive_timeout 120;

    #FastCGI相关参数是为了改善网站的性能：减少资源占用，提高访问速度。下面参数看字面意思都能理解。
    fastcgi_connect_timeout 300;
    fastcgi_send_timeout 300;
    fastcgi_read_timeout 300;
    fastcgi_buffer_size 64k;
    fastcgi_buffers 4 64k;
    fastcgi_busy_buffers_size 128k;
    fastcgi_temp_file_write_size 128k;

    #gzip模块设置
    gzip on; #开启gzip压缩输出
    gzip_min_length 1k;    #最小压缩文件大小
    gzip_buffers 4 16k;    #压缩缓冲区
    gzip_http_version 1.0;    #压缩版本（默认1.1，前端如果是squid2.5请使用1.0）
    gzip_comp_level 2;    #压缩等级
    gzip_types text/plain application/x-javascript text/css application/xml;    #压缩类型，默认就已经包含textml，所以下面就不用再写了，写上去也不会有问题，但是会有一个warn。
    gzip_vary on;

    #开启限制IP连接数的时候需要使用
    #limit_zone crawler $binary_remote_addr 10m;



    #负载均衡配置
    upstream piao.jd.com {
     
        #upstream的负载均衡，weight是权重，可以根据机器配置定义权重。weigth参数表示权值，权值越高被分配到的几率越大。
        server 192.168.80.121:80 weight=3;
        server 192.168.80.122:80 weight=2;
        server 192.168.80.123:80 weight=3;

        #nginx的upstream目前支持4种方式的分配
        #1、轮询（默认）
        #每个请求按时间顺序逐一分配到不同的后端服务器，如果后端服务器down掉，能自动剔除。
        #2、weight
        #指定轮询几率，weight和访问比率成正比，用于后端服务器性能不均的情况。
        #例如：
        #upstream bakend {
        #    server 192.168.0.14 weight=10;
        #    server 192.168.0.15 weight=10;
        #}
        #2、ip_hash
        #每个请求按访问ip的hash结果分配，这样每个访客固定访问一个后端服务器，可以解决session的问题。
        #例如：
        #upstream bakend {
        #    ip_hash;
        #    server 192.168.0.14:88;
        #    server 192.168.0.15:80;
        #}
        #3、fair（第三方）
        #按后端服务器的响应时间来分配请求，响应时间短的优先分配。
        #upstream backend {
        #    server server1;
        #    server server2;
        #    fair;
        #}
        #4、url_hash（第三方）
        #按访问url的hash结果来分配请求，使每个url定向到同一个后端服务器，后端服务器为缓存时比较有效。
        #例：在upstream中加入hash语句，server语句中不能写入weight等其他的参数，hash_method是使用的hash算法
        #upstream backend {
        #    server squid1:3128;
        #    server squid2:3128;
        #    hash $request_uri;
        #    hash_method crc32;
        #}

        #tips:
        #upstream bakend{#定义负载均衡设备的Ip及设备状态}{
        #    ip_hash;
        #    server 127.0.0.1:9090 down;
        #    server 127.0.0.1:8080 weight=2;
        #    server 127.0.0.1:6060;
        #    server 127.0.0.1:7070 backup;
        #}
        #在需要使用负载均衡的server中增加 proxy_pass http://bakend/;

        #每个设备的状态设置为:
        #1.down表示单前的server暂时不参与负载
        #2.weight为weight越大，负载的权重就越大。
        #3.max_fails：允许请求失败的次数默认为1.当超过最大次数时，返回proxy_next_upstream模块定义的错误
        #4.fail_timeout:max_fails次失败后，暂停的时间。
        #5.backup： 其它所有的非backup机器down或者忙的时候，请求backup机器。所以这台机器压力会最轻。

        #nginx支持同时设置多组的负载均衡，用来给不用的server来使用。
        #client_body_in_file_only设置为On 可以讲client post过来的数据记录到文件中用来做debug
        #client_body_temp_path设置记录文件的目录 可以设置最多3层目录
        #location对URL进行匹配.可以进行重定向或者进行新的代理 负载均衡
    }
     
     
     
    #虚拟主机的配置，每个server对应一个网站或一个虚拟机
    server
    {
        #监听端口
        listen 80;

        #域名可以有多个，用空格隔开
        server_name www.jd.com jd.com;
        index index.html index.htm index.php;
        root /data/www/jd;

        #对******进行负载均衡
        location ~ .*.(php|php5)?$
        {
            fastcgi_pass 127.0.0.1:9000;
            fastcgi_index index.php;
            include fastcgi.conf;
        }
         
        #图片缓存时间设置
        location ~ .*.(gif|jpg|jpeg|png|bmp|swf)$
        {
            expires 10d;
        }
         
        #JS和CSS缓存时间设置
        location ~ .*.(js|css)?$
        {
            expires 1h;
        }
         
        #日志格式设定
        #$remote_addr与$http_x_forwarded_for用以记录客户端的ip地址；
        #$remote_user：用来记录客户端用户名称；
        #$time_local： 用来记录访问时间与时区；
        #$request： 用来记录请求的url与http协议；
        #$status： 用来记录请求状态；成功是200，
        #$body_bytes_sent ：记录发送给客户端文件主体内容大小；
        #$http_referer：用来记录从那个页面链接访问过来的；
        #$http_user_agent：记录客户浏览器的相关信息；
        #通常web服务器放在反向代理的后面，这样就不能获取到客户的IP地址了，通过$remote_add拿到的IP地址是反向代理服务器的iP地址。反向代理服务器在转发请求的http头信息中，可以增加x_forwarded_for信息，用以记录原有客户端的IP地址和原来客户端的请求的服务器地址。
        log_format access '$remote_addr - $remote_user [$time_local] "$request" '
        '$status $body_bytes_sent "$http_referer" '
        '"$http_user_agent" $http_x_forwarded_for';
         
        #定义本虚拟主机的访问日志
        access_log  /usr/local/nginx/logs/host.access.log  main;
        access_log  /usr/local/nginx/logs/host.access.404.log  log404;
         
        #对 "/" 启用反向代理
        location / {
            proxy_pass http://127.0.0.1:88;
            proxy_redirect off;
            proxy_set_header X-Real-IP $remote_addr;
             
            #后端的Web服务器可以通过X-Forwarded-For获取用户真实IP
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
             
            #以下是一些反向代理的配置，可选。
            proxy_set_header Host $host;

            #允许客户端请求的最大单文件字节数
            client_max_body_size 10m;

            #缓冲区代理缓冲用户端请求的最大字节数，
            #如果把它设置为比较大的数值，例如256k，那么，无论使用firefox还是IE浏览器，来提交任意小于256k的图片，都很正常。如果注释该指令，使用默认的client_body_buffer_size设置，也就是操作系统页面大小的两倍，8k或者16k，问题就出现了。
            #无论使用firefox4.0还是IE8.0，提交一个比较大，200k左右的图片，都返回500 Internal Server Error错误
            client_body_buffer_size 128k;

            #表示使nginx阻止HTTP应答代码为400或者更高的应答。
            proxy_intercept_errors on;

            #后端服务器连接的超时时间_发起握手等候响应超时时间
            #nginx跟后端服务器连接超时时间(代理连接超时)
            proxy_connect_timeout 90;

            #后端服务器数据回传时间(代理发送超时)
            #后端服务器数据回传时间_就是在规定时间之内后端服务器必须传完所有的数据
            proxy_send_timeout 90;

            #连接成功后，后端服务器响应时间(代理接收超时)
            #连接成功后_等候后端服务器响应时间_其实已经进入后端的排队之中等候处理（也可以说是后端服务器处理请求的时间）
            proxy_read_timeout 90;

            #设置代理服务器（nginx）保存用户头信息的缓冲区大小
            #设置从被代理服务器读取的第一部分应答的缓冲区大小，通常情况下这部分应答中包含一个小的应答头，默认情况下这个值的大小为指令proxy_buffers中指定的一个缓冲区的大小，不过可以将其设置为更小
            proxy_buffer_size 4k;

            #proxy_buffers缓冲区，网页平均在32k以下的设置
            #设置用于读取应答（来自被代理服务器）的缓冲区数目和大小，默认情况也为分页大小，根据操作系统的不同可能是4k或者8k
            proxy_buffers 4 32k;

            #高负荷下缓冲大小（proxy_buffers*2）
            proxy_busy_buffers_size 64k;

            #设置在写入proxy_temp_path时数据的大小，预防一个工作进程在传递文件时阻塞太长
            #设定缓存文件夹大小，大于这个值，将从upstream服务器传
            proxy_temp_file_write_size 64k;
        }
         
         
        #设定查看Nginx状态的地址
        location /NginxStatus {
            stub_status on;
            access_log on;
            auth_basic "NginxStatus";
            auth_basic_user_file confpasswd;
            #htpasswd文件的内容可以用apache提供的htpasswd工具来产生。
        }
         
        #本地动静分离反向代理配置
        #所有jsp的页面均交由tomcat或resin处理
        location ~ .(jsp|jspx|do)?$ {
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_pass http://127.0.0.1:8080;
        }
         
        #所有静态文件由nginx直接读取不经过tomcat或resin
        location ~ .*.(htm|html|gif|jpg|jpeg|png|bmp|swf|ioc|rar|zip|txt|flv|mid|doc|ppt|
        pdf|xls|mp3|wma)$
        {
            expires 15d; 
        }
         
        location ~ .*.(js|css)?$
        {
            expires 1h;
        }
    }
}
######Nginx配置文件nginx.conf中文详解#####
```



## nginx启动/停止/重启

cd /usr/sbin/nginx/

- 启动 ./usr/sbin/nginx   # 如果后面 -c  可指定使用哪个配置文件
- nginx -s stop      #停止服务
- 查询nginx进程：ps -ef | grep nginx 或者 ps -aux| grep nginx
- 强制停止 pkill nginx
- 重启 nginx -s reload

> 注意，如果nginx启动成功，但是不能访问主页，一般都是防火墙问题。

## nginx模块配置

### http_stub_status_module模块

用来显示当前服务器的连接请求状态信息

```bash
location /mystatus {                       //添加此location选项，定义状态信息页面；
    stub_status;                         //添加状态信息关键字；
}
```

```bash
Active connections: 2                  
#活动状态的连接数
server accepts handled requests       
#accept:已接收的客户端请求数量;
#handled：已处理的客户端请求数量
#requests：客户端发送的总请求数
 2 2 1 
Reading: 0 Writing: 1 Waiting: 1    
#Reading：正在读取客户端请求报文首部的连接数;
#Writing：正在向客户端发送响应报文的连接数;
#Waiting：正在等待客户端发送请求报文的空闲连接数;

```

### http_random_index_module模块

> 客户端发起请求时，在页面目录中随机选择一个主页返回给用户

```bash
[root@localhost ~]# mkdir /opt/test/web   -pv
mkdir: 已创建目录 "/opt/test"
mkdir: 已创建目录 "/opt/test/web"
[root@localhost ~]# echo "Test Page web1"  > /opt/test/web/index1.html
[root@localhost ~]# echo "Test Page web2"  > /opt/test/web/index2.html
[root@localhost ~]# echo "Test Page web3"  > /opt/test/web/index3.html
[root@localhost ~]# vim /etc/nginx/conf.d/default.conf 
   location / {                     
        root   /opt/test/web;                 //随机主页的目录存放位置;
        random_index on;                     //开启随机功能;
        index  index.html index.htm;
    }


[root@localhost ~]# nginx -c  /etc/nginx/nginx.conf   -s reload           //重载nginx服务;
[root@localhost ~]# ss -tunlp | grep nginx 
tcp    LISTEN     0      128                    *:80                    *:*      users:(("nginx",1756,6),("nginx",4281,6))
[root@localhost ~]# 


```

### http_access_module模块

实现基于客户端ip地址做访问控制的功能

```
[root@localhost ~]# vim /etc/nginx/conf.d/default.conf
   location / {
        root   /opt/test/web;
        deny 192.168.126.1;
        allow all;
        index  index.html index.htm;
    }


[root@localhost ~]# nginx -c  /etc/nginx/nginx.conf   -s reload 
[root@localhost ~]# ss -tunlp | grep nginx 
tcp    LISTEN     0      128                    *:80                    *:*      users:(("nginx",1756,6),("nginx",4368,6))
[root@localhost ~]# 


```

### http_auth_basic_module模块

客户端访问服务器指定资源时需要输入用户名和密码信息进行认证

```bash
[root@localhost ~]# htpasswd -c /etc/nginx/auth_user_conf  jyy      //创建一个存放用户名和密码的文件
New password: 
Re-type new password: 
Adding password for user jyy
[root@localhost ~]# cat /etc/nginx/auth_user_conf 
jyy:BvGdPQb7iKsOk
[root@localhost ~]# 
[root@localhost ~]# vim /etc/nginx/conf.d/default.conf
   location / {
        root   /opt/test/web;
        auth_basic "Auth access test! input your password";    //用户进行认证时候的自定义描述信息；
        auth_basic_user_file  /etc/nginx/auth_user_conf;	   //指明认证用户的配置文件位置;
    }

[root@localhost ~]# nginx -c  /etc/nginx/nginx.conf   -s reload 
[root@localhost ~]# ss -tunlp | grep nginx 
tcp    LISTEN     0      128                    *:80                    *:*      users:(("nginx",1756,6),("nginx",4419,6))
[root@localhost ~]# 


```

### http_gzip_module模块

客户端请求的文件较大时，可以对资源压缩后返回给客户端，节省网络带宽

```bash
   location / {
        root   /opt/test/web;       
        gzip on;					   //开启压缩功能；
        gzip_http_version 1.1;	      //压缩的版本;
        gzip_comp_level 2;           //压缩的等级;
        gzip_type text/plain application/javascript text/css application/xml text/javascripts application/x-httpd-php
                  image/jpg image/gif image/png;               //支持压缩的资源类型;
    }


[root@localhost ~]# nginx -c  /etc/nginx/nginx.conf   -s reload 
[root@localhost ~]# ss -tunlp | grep nginx 
tcp    LISTEN     0      128                    *:80                    *:*      users:(("nginx",1756,6),("nginx",4419,6))
[root@localhost ~]# 


```

## 跨域设置

```bash
location ~ .*\.json$ {
	add_header Access-Control-Allow-Origin http:localhost:8080; 
	add_header Access-Control-Allow-Methods GET,PUT,POST,OPTIONS,DELETE;
}
```

## 防盗链

- 防止网站资源被盗用
- 保证信息安全
- 防止流量过量
- 使用http_referers none block [server_names] IP 设置允许访问的链接

```bash
location ~ .\/(jpg|png|gif) {
	#none 没有refer;block表示非正式的http请求
	valid_referers none block localhost;
	# $invalid_referer可以获取验证结果，0表示通过；1表示不通过
	if ($invalid_referer) {
		return 403;
	}
}
```

## 代理服务

### 正向代理

- 正向代理的对象是客户端，服务端看不到真正的客户端

### 反向代理

反向代理的对象是服务端，客户端看不到真正的服务端

<img src="C:\Users\wangl\AppData\Roaming\Typora\typora-user-images\image-20201009231309871.png" alt="image-20201009231309871" style="zoom: 67%;" />



## 负载均衡

### 什么是负载均衡

当一台服务器的单位时间内的访问量越大时，服务器压力就越大，大到超过自身承受能力时，服务器就会崩溃。为了避免服务器崩溃，让用户有更好的体验，我们通过负载均衡的方式来分担服务器压力。

我们可以建立很多很多服务器，组成一个服务器集群，当用户访问网站时，先访问一个中间服务器，在让这个中间服务器在服务器集群中选择一个压力较小的服务器，然后将该访问请求引入该服务器。

如此以来，用户的每次访问，都会保证服务器集群中的每个服务器压力趋于平衡，分担了服务器压力，避免了服务器崩溃的情况。

**负载均衡是用反向代理的原理实现的。**

### upsream实现负载均衡

- nginx把请求转发到后台的一组upstream服务池
- 用法：在上下文http中  定义  upstream name {}

```BASH
upstream localservers {
	#weight表示权重，权重值越大，概率越大
	server 127.0.0.1:3000 weight=10;
	server 127.0.0.1:4000 weight=1;
	server 127.0.0.1:5000 weight=2;
}
server {
	location / {
		proxy_pass http://localservers
	}
}
```

在上面的例子中，我们可以在服务池中的每个服务后面加状态

| 状态         | 描述                                                         |
| ------------ | ------------------------------------------------------------ |
| down         | 当前服务器不参与负载均衡                                     |
| backup       | 当其他节点都无法使用时的备份服务器                           |
| max_fails    | 允许请求失败的次数，达到最大次数后休眠，等待fail_timeout设定的时间之后再次服务 |
| fail_timeout | 经过max_fails失败后，服务暂停的时间，默认10秒                |
| max_conns    | 限制每个server的最大服务连接数，性能高的服务器可以连接数多一些 |

### 负载均衡的策略

| 类型             | 种类                                                         |
| ---------------- | :----------------------------------------------------------- |
| 轮询（可加权）   | 每个请求按时间依次分配不同的服务器，如果服务器down掉，能自动剔除，可通过weight控制访问比率 |
| ip_hash          | 每个请求按访问的ip的hash结果分配，这样每个访客固定访问一个服务，可以解决session问题 |
| least_conn       | 哪个机器的连接数少就分发给谁                                 |
| url_hash(第三方) | 按访问的url地址来分配，每个url都定向到同一个服务上（缓存）   |
| fair(第三方插件) | 按后端服务的响应时间来分配，响应时间短的优先分配             |
| 自定义hash       | 自定义hash的key                                              |

- 轮询

  ```bash
  upstream localservers {
  	#weight表示权重，权重值越大，概率越大
  	server 127.0.0.1:3000 weight=10;
  	server 127.0.0.1:4000 weight=1;
  	server 127.0.0.1:5000 weight=2;
  }
  ```

- ip_hash

  ```bash
  upstream localservers {
  	ip_hash;
  	server 127.0.0.1:3000 weight=10;
  	server 127.0.0.1:4000 weight=1;
  	server 127.0.0.1:5000 weight=2;
  }
  ```

  ***\*特别注意：配置了ip_hash以后，后面的所有配置都会无效。该配置方式一般只使用于公司内网使用，访问量比较小的管理系统，不用于大型网站。\****

- url_hash

  ```bash
  
  upstream backserver {
      server 127.0.0.1:3000 weight=10;
  		server 127.0.0.1:4000 weight=1;
  		server 127.0.0.1:5000 weight=2;
      hash $request_uri;
      hash_method crc32;
  }
  ```

- fair

  ```bash
  upstream localservers {
  	fair;
  	server 127.0.0.1:3000 weight=10;
  	server 127.0.0.1:4000 weight=1;
  	server 127.0.0.1:5000 weight=2;
  }
  ```

  