## zookeeper介绍

## zookeeper安装

## zookeeper服务启动

## zookeeper开机自启配置

1. 进入 /etc/init.d目录下，创建zookeeper启动脚本

```bash
cd /etc/init.d
touch zookeeper
```

2. 进入zookeeper脚本，并复制如下内容:

```bash
#!/bin/bash
#chkconfig:2345 20 90
#description:zookeeper
#processname:zookeeper
ZK_PATH=/usr/local/zookeeper
export JAVA_HOME=/usr/local/java
case $1 in
         start) sh  $ZK_PATH/bin/zkServer.sh start;;
         stop)  sh  $ZK_PATH/bin/zkServer.sh stop;;
         status) sh  $ZK_PATH/bin/zkServer.sh status;;
         restart) sh $ZK_PATH/bin/zkServer.sh restart;;
         *)  echo "require start|stop|status|restart"  ;;
esac
```

需要在 `#!/bin/bash` 后加入以下两句才能注册服务名： `#chkconfig:2345 20 90` 、 `#description:zookeeper`

其中，2345是默认启动级别，级别有0-6共7个级别。

- 等级0表示：表示关机
- 等级1表示：单用户模式
- 等级2表示：无网络连接的多用户命令行模式
- 等级3表示：有网络连接的多用户命令行模式
- 等级4表示：不可用
- 等级5表示：带图形界面的多用户模式
- 等级6表示：重新启动

20是启动优先级，90是停止优先级，优先级范围是0－100，数字越大，优先级越低。

3. 注册脚本为服务

在 `/etc/init.d` 文件夹下执行指令：

`chkconfig --add zookeeper` ：注册脚本为服务

`chkconfig --list` ：查看服务列表

4. 给脚本赋予权限

在 `/etc/init.d` 文件夹下执行指令： `chmod 777 zookeeper`

5. 通过service命令执行zookeeper脚本

```bash
service zookeeper start
service zookeeper status
service zookeeper stop
service zookeeper restart
```



