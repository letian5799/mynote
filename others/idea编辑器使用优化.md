## idea注释优化设置

https://blog.csdn.net/weixin_34208283/article/details/89614307

## 自动导包

![image-20201204222958147](D:\java学习笔记\mynote\images\image-20201204222958147.png)

## Mysql数据库配置

![image-20201204223151814](D:\java学习笔记\mynote\images\image-20201204223151814.png)

![image-20201204223510404](D:\java学习笔记\mynote\images\image-20201204223510404.png)

如果第一次连接Mysql失败，报错信息：**Server returns invalid timezone. Go to 'Advanced' tab and set 'serverTimezone' prope**

那么此时你需要配置时区：

1.在本地连接mysql

```bash
mysql -h localhost -u root -p
```

2.执行`show variables like'%time_zone';` 语句，当然也可以直接通过sql工具执行以下命令。

![img](D:\java学习笔记\mynote\images\20191009103216457.png)

如果出现上图，表示没有设置时区，此时执行 `set global time_zone = '+8:00';`即可（注意，不要漏掉命令后面得那个分号，sql以分号结束）

## mysql的sql语句代码提示（表名，字段）

**1.File->Settings->Languages&Frameworks->SQL Dialects**

![img](D:\java学习笔记\mynote\images\image-20201031210755090.png)

**2.File->Settings->Languages&Frameworks->SQL Resolution Scopes**

![img](D:\java学习笔记\mynote\images\image-20201031210755080.png)

**3.File->Settings->Editor->General->Appearance**

![img](D:\java学习笔记\mynote\images\image-20201031210755081.png)