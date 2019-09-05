## 新手引导
本篇主要通过示例来介绍litchi引擎相关的组件。简单的`hello world`示例不能充分发掘引擎优势。
所以，我们顺手写一个多进程、分布式的hello world服务器吧。

*我们先捋一捋需求，大概实现以下几点功能，争取把引擎涉及到的功能点全覆盖上!*
* 注册帐号，并登录
* 获取网关地址，并连接网关
* 创建角色
* 分配一个聊天室(嗯~我们做一个聊天室-_-|)
* 系统推送欢迎用户的信息
* 用户输入`hello world`，全聊天室的人都能看得到!


### 顺手写一个网关(用于客户端连接)
先整一个网关服务器，网关包含这些功能：

* 必需是多进程的网关，这样才能显示承载力
* 必需可动态分配(根据系统策略，让用户连接到不同的网关)
* 必需能效验用户登录
* 必需有心跳请求
* 必需采用`websocket`监听(现在h5很流行)

*现在打开IDEA神器，开始撸代码吧！*

* 创建一个gate-server的gradle项目
* 创建一个`GateLauncher.java`类，里面包含一个`main(String[] args)`函数，代码如下:
```
public static void main(String[] args) throws Exception {
  
    //系统文件，配置目录
    String configPath = System.getProperty(Constants.Property.CONFIG_DIR);
    //环境名称
    String envName = System.getProperty(Constants.Property.ENV_NAME);
    //服务器结点id
    String nodeId = System.getProperty(Constants.Property.NODE_ID);
    
    // 创建一个服务
    Litchi.createApp(configPath, envName, nodeId)
                .setDispatch(ThreadEnum.getGateThreadInfo())  //配置线程池
                .addComponent(p -> {new WebSocketServer(p)})  //创建一个websocket
                .start();  //启动
    }
```
一个websocket网关服务器创建成功了！不过还不能运行。缺少litchi.json和servers.json文件。


* 打开IDEA，创建一个gradle项目.

### 顺手写一个RPC服务器(用于多结点聊天服务器)

### 顺手写一个web服务器(用于注册帐号、登陆验证)

### 顺手读一次配置

### 顺手操作一次数据库

### 顺手结束