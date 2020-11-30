### npm常用命令
- npm -v :版本查询

- ctrl+c :结束所有dom行命令

- npm init -- 初始化后会出现一个package.json配置文件;

- npm install 包名 --save-dev(npm install 包名 -D) ：安装的包只用于开发环境，不用于生产环境
- npm uninstall 包名  卸载
- npm install 包名 --save (npm install 包名 -S)：安装的包需要发布到生产环境的
    > 安装好后在你的文件夹中会出现多出一个node_modules的文件夹和package-lock.js的文件，在node_modules文件夹中会有子文件夹为你刚刚安装的包,在package.js中也会出现刚刚安装的文件以及版本号

- npm install nrm -g    安装选择源工具包
    > (nrm比较稳定，还是有很多其它的选择的) 终端输入nrm -V可以查验当前电脑是否已经安装了nrm
    > nrm ls 查看镜像
    > nrm test  测试镜像运行速度
    >nrm use taobao     使用淘宝镜像，速度最快
