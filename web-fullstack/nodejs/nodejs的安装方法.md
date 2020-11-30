### nodejs环境安装
1. 普通安装方式[官方网站](https://nodejs.org/zh-cn/)

2. 多版本安装方式
    - 卸载已有的nodejs
    - 下载nvm[nvm下载地址](https://github.com/coreybutler/nvm-windows)
        > 只需要下载nvm-noinstall.zip即可
    - 在C盘创建dev目录
        > 在dev目录下创建nodejs文件夹
        > 在dev目录下创建nvm文件夹,并把上一步解压的nvm包放进去
    - 在install.cmd文件上右键选择【以管理员省份运行】
    - 打开的cmd上直接回车，会生成一个settings.txt文件，将其另存为settings.txt到nvm目录下
    - 在nvm目录下的sttings.txt中修改配置如下
        > root:  D:\dev\nvm
        > path:  D:\dev\nodejs
        > 其他不用修改
    - 配置nvm和nodejs环境变量，按住win+R打开命令窗口，输入sysdm.cpl回车，在弹出的界面中找到【高级】-【系统环境变量】，进行如下操作
        > NVM_HOME   D:\dev\nvm
        > NVM_SYMLINK   D:\dev\nodejs
        > 将配置好的环境变量添加到Path中
    - 在cmd窗口输入nvm version验证nvm是否安装成功
    - cmd命令窗口 输入nvm install latest安装最新版本node.js
    - cmd命令窗口 输入nvm list查看安装的nodejs版本，然后输入nvm use 【版本号】启用nodejs
    - 再输入nvm list 回车，发现前面带有*号的，即是当前使用的node.js版本
    - 输入node -v 验证nodejs是否安装成功
    - 在settings.txt中配置以下可增加下载nvm的下载速度
        > node_mirror:s http://npm.taobao.org/mirrors/node/
        > npm_mirror: http://npm.taobao.org/mirrors/npm/

### nvm常用命令
- nvm list 查看当前安装的nodejs的所有版本
- nvm install 版本号      安装指定版本的node.js
    > 如不输入版本号，默认安装最新版本
    > 也可输入latest 安装最新版本
- nvm uninstall 版本号      卸载指定版本的node.js