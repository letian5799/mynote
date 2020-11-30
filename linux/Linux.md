# Linux

## 版本

linux分为内核版本和发行版本

- kernel 内核版
- Redhat 功能丰富，安全 ，收费
- CentOS 免费，适合服务端应用
- ubuntu 桌面版
- fedora 功能强大

## Linux 与 windows 的区别

- Linux严格区分大小写
- Linux中所有的内容都是以文件形式保存的，包括硬件、用户和文件
- Linux不靠扩展名区分文件类型，靠权限来区分，但是有一些约定的扩展名，是给管理员看的
  	- 压缩包 `.gz`   `.bz2`	`tar.bz2`	`.tgz`
  	- 二进制文件 `.rpm`
  	- 网页文件 `.html`  `.php` 
  	- 脚本文件 `.sh` 

## VMware安装



### 什么是虚拟机

- 虚拟PC的软件
- 可以在现有的操作系统上虚拟出一个新的硬盘环境
- 可以实现在一台机器上真正同时运行两个独立的操作系统

### 虚拟机的特点

- 不需要分区或重新开机就可以在同一台PC机上使用两种以上的操作系统
- 本机可以与虚拟机进行网络通信
- 可以设定并且随时修改虚拟机的硬件环境
- 系统快照可以方便备份和回滚

### 建议的VMWare配置

- CPU建议主频1GHz以上
- 内存建议2G以上（个人电脑推荐8G以上内存）
- 硬盘 建议分区空闲空间8G以上

### 虚拟机的安装

- VMware15 pro

  [百度网盘链接]: 链接：https://pan.baidu.com/s/18Ot3nsPpn2c_OEVSfLt8ew

  

## linux系统启动

### BIOS

### 硬件自检

### 启动顺序

### 主引导记录

### 分区表

### 硬盘启动

### 操作系统

- 控制权交给操作系统后，操作系统的内核首先被载入内存
- 以Linux为例，先载入/boot目录下的kernel。内核加载成功后，第一个运行的程序是 `/sbin/init` 。它根据配置文件产生init进程。这是Linux启动后的第一个进程，pid进程编号为1，其他进程都是它的后代。
- 然后，init线程加载系统的各个模块，比如窗口程序和网络程序，直至执行 `/bin/login` 程序，跳出登录界面，等待用户输入用户名和密码。

## Linux常用命令

### 目录结构

- / 根目录
- /boot 启动目录，启动相关文件
- /dev 设备文件
- /etc 配置文件
- /home 普通用户的家目录，可以操作
- /lib 系统库保存目录
- /mnt 移动设备挂载目录
- /media 光盘挂载目录
- /misc 磁带机挂载目录
- /root 超级用户的家目录，可以操作
- /tmp 临时目录，可以操作
- /proc 正在运行的内核信息映射，主要输出进程信息、内存资源信息和磁盘分区信息等
- /sys 硬件设备的驱动程序信息
- /var 变量
- /bin 普通你的基本命令，如ls、chmod等一般用户可以使用的
- /sbin 基本的系统命令，如shutdowm、reboot、用于启动系统、修复系统，只有管理员可以使用
- /usr 用户资源
- /usr/bin 后期安装的一些软件的运行脚本
- /usr/sbin 一些用户安装的系统管理的必备程序

### 命令基本格式

#### 命令提示符

```bash
[root@c1 ~]# 
```

- root 当前登录用户
- c1 主机名
- ~ 当前工作目录，默认是当前用户的家目录，普通用户是/home, root用户是/root
- #是提示符，普通用户是$，超级用户是#

#### 命令格式

- 命令 [选项] [参数]
- 当有多个选项时，可以连写
- 一般参数有简写和完整写法：比如 `-a` 与 `--all` 等效

### 文件搜索命令-find

- find [搜索范围] [搜索条件]

#### 按名称搜索

```bash
find / -name aa.log
find / -iname AA.log 
```

-i不区分大小写

#### 通配符

- `*` 匹配任意内容
-  `？`匹配任意一个字符
- `[]` 匹配任意一个中括号内的字符

#### 按时间搜索

find /nginx/access.log -mtine +5

### rpm命令

用法: rpm [选项...]

```
-a：查询所有套件；
-b<完成阶段><套件档>+或-t <完成阶段><套件档>+：设置包装套件的完成阶段，并指定套件档的文件名称；
-c：只列出组态配置文件，本参数需配合"-l"参数使用；
-d：只列出文本文件，本参数需配合"-l"参数使用；
-e<套件档>或--erase<套件档>：删除指定的套件；
-f<文件>+：查询拥有指定文件的套件；
-h或--hash：套件安装时列出标记；
-i：显示套件的相关信息；
-i<套件档>或--install<套件档>：安装指定的套件档；
-l：显示套件的文件列表；
-p<套件档>+：查询指定的RPM套件档；
-q：使用询问模式，当遇到任何问题时，rpm指令会先询问用户；
-R：显示套件的关联性信息；
-s：显示文件状态，本参数需配合"-l"参数使用；
-U<套件档>或--upgrade<套件档>：升级指定的套件档；
-v：显示指令执行过程；
-vv：详细显示指令执行过程，便于排错。
```

**常见的用法：**

- `rpm -ql tree  "软件名称"`	rpm包中的文件安装的位置
- rpm -e tree   卸载软件
- `rpm -qa  “软件名称”  `   列出所有安装过的包



#  CentOS6.x 的使用

## 防火墙操作命令

CentOS6.x

- `chkconfig iptables off` 永久性关闭，需要重启
- `service iptables stop` 即时生效，不需要重启
- `iptables -F` 临时关闭
- `service iptables status` 或者 `iptables -L`  查看防火墙状态
- `chkconfig iptables --list` 检查防火墙是否开机自启

CentOS7.x

- `systemctl start firewalld.service`	#启动firewall
- `systemctl stop firewalld.service`      #停止firewall
- `systemctl disable firewalld.service`    #禁止firewall开机启动

## 关闭selinux

- selinux永久性关闭：`vi /etc/sysconfig/selinux` 将 `SELINUX=enforcing` 改为 `SELINUX=disabled`后退出编辑，reboot重启系统
- selinux永久性关闭：sed –i“7s/enforcing/disabled/g”/etc/selinux/config
- selinux临时关闭：setenforce 0
- 查看状态：getenforce





