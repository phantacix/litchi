## Hello Litchi
本篇主要介绍引擎相关的组件和功能特性，以及引擎所用到的配置和约定的规则。Litchi通过组件组合方式来创建各种类型服务器。

例如,创建一个`gate-server`网关服务器，那么我们可能会用到 `dispatch`、`route`、`rpc`、`websocket`等组件。

`Litchi.java`类本身是一个对象的容器。通用`Component`接口来统一管理所有组件。

**litchi-sample项目正在开发中，通过示例项目更能了解细节 **

## 术语与概念

**nodeType-结点类型**

指该服务器的应用类型。通常我们会给后台的服务器实例进行归类。例如：网关服务器(gate_server)、逻辑服务器(logic_server)、战斗服务器(battle_server)等。
我们通用`nodeType`属性确定服务器的类型。


**nodeId-结点id**

指服务器的唯一标识。通过`nodeType + nodeId`表示该实例的唯一性。
例如：nodeType=gate,nodeId=gate-1，则表示，网关类型服务器，该结点唯一id为`gate-1`


**envName-环境名称**

在游戏开发过程中,通常我们会区分`开发者环境`、`测试环境`、`正式环境`等多种环境，每个环境涉及到`ip`、`port`、`db信息`、`redis信息`等参数的配置都会不一样。
为了方便管理各种配置参数，litchi约定需要为每一个`环境`创建一个`目录`。


## 启动Litchi

创建一个服务器非常简单。

只需要在main()方法里调用`Litchi.createApp(configPath, envName, nodeId).start();`即可。

`createApp`方法需要提供三个参数，分别是`配置基础路径`、`环境名称`、`当前服务器的结点id`。

下面代码片段展示的是一个gate服务器启动代码：

**该网关监听了两个端口。一个websocket端口用于客户端连接;一个rpc server端口用于内部admin-panel结点管理该gate**
```text
    public static void main(String[] args) throws Exception {
        //config配置路径
        String configPath = System.getProperty(Constants.Property.CONFIG_DIR);
        //配置的环境名称
        String envName = System.getProperty(Constants.Property.ENV_NAME);
        //当前实例的结点id
        String nodeId = System.getProperty(Constants.Property.NODE_ID);

        // create
        Litchi.createApp(configPath, envName, nodeId)
                //配置gate结点的线程池
                .setDispatch(ThreadEnum.getGateThreadInfo())
                //初始化配表读取组件加载
                .setDataConfig()
                //redis组件初始化
                .setRedis()
                //rpc client连接中心服、游戏服
                .setConnectRpc(NodeType.CENTER_SERVER, NodeType.GAME_SERVER)
                //添加组件，网关状态维护组件
                .addComponent(p -> new MaintainStateComponent(p))
                //添加组件，网关启动rpc服务器，用于运维指令调用
                .addComponent(p -> new NettyRpcServer(p)) //listen rpc server
                //添加组件，对外的websocket监听
                .addComponent(p -> {
                    // add route rule to game-server
                    p.rpc().addClientSelect(NodeType.GAME_SERVER, (session, nodeType, request) -> {
                        GateSession gameSession = (GateSession) session;
                        return p.rpc().getClient(nodeType, gameSession.getNodeId());
                    });

                    //init spring
                    SpringContext.init(GateSpringContext.class);

                    // add route 添加本地路由规则
                    Collection<BaseRoute> baseRoutes = SpringContext.getBeanListOfType(BaseRoute.class);
                    p.route().putAll(baseRoutes);

                    //listen webSocket server 对外监听websocket
                    return new WebSocketServer(p);})
                .start(p -> {
                    //自定义的spring事件
                    SpringContext.postEvent(); 
                });
    }
```


## 配置文件
引擎约定，系统必要的配置文件需要放入到`configPath`目录。

在初始`Litchi.createApp(...)`实例时需传入该`(configPath)配置路径`和`(envName)环境名称`和`(nodeId)结点Id`参数。
litchi会根据`configPath`路径加载`litchi.json`、`nodes.json`。根据当前传入的`nodeId`值从`nodes.json`读取相应的参数配置。

同时rpc连接目标结点也使用到了`nodes.json`的`nodeType`、`nodeId`、`ip`、`rpcPort`等信息.

示例目录:
```
config/                             configPath目录
    +dataconfig/                    这里放游戏业务相关的配表，在litchi.json的file方式读取配表会配置该路径
        +signConfig.json            游戏业务的配表...
        +vipConfig.json             同上...
    +env/                           启动环境目录(也可以在该目录创建自己的环境目录)
        +dev/                       dev环境目录
            +litchi.json            引擎的全局参数与组件参数的配置文件
            +nodes.json             服务器结点配置
            +logback.xml            日志配置
        +prod/                      prod正式环境目录
            +litchi.json            同上..
            +nodes.json
            +logback.xml
```

**litchi.json-引擎全局配置**

`Litchi.createApp()`启动时，会根据`configPath + envName`组合的路径扫描 litchi.json文件。

`litchi.json`是引擎必要的参数配置文件。该文件用于配置全局参数，组件全局参数。另外，开发者自定义的组件参数也可以配置在这里。通过`litchi.config()`可获得所需属性。

目前引擎必要的文件有以下：
* `litchi.json` 服务器全局配置、组件参数的配置。
* `nodes.json`  结点配置表，所有服务器结点信息都配置于此。
* `logback.xml` logback配置文件

以下示例内容是`litchi.json`所包含的参数配置(如需在项目中使用该内容请删除掉//后面的注释)：
```json
{
  "debug": true,              //是否为debug状态，在写业务时可以调用该属性做一些测试逻辑处理。 调用方式：litchi.isDebug();
  "basePackages": [           //需要扫描的包(litchi.core包名为引擎所用到的)
    "litchi.core",
    "com.game",
    "com.mini.shared.rpc.log"
  ],
  "dataConfig": {                 //策划配表读取组件
    "dataSource": "file",         //选择读取源 file 或 redis
    "file": {
      "filePath": "/dataconfig", 
      "reloadPath": "/newconfig",
      "reloadFlushTime": 3000
    },
    "redis": {
      "sourceClassName": "litchi.core.dataconfig.source.RedisDataConfigSource",  //redis读取方式，也可以重写实现类
      "redisKey": "config:server:game-name"
    }
  },
  "dbQueue": {                            //db队列组件
    "dbPoolSize": "4",
    "tableSubmitFrequency": "1000",
    "tableSubmitNum": "200",
    "shutdownTableSubmitFrequency": "1000",
    "shutdownTableSubmitNum": "200"
  },
  "jdbc": [                                 //数据库连接配置
    {
      "id": "admin_panel_db",
      "dbType": "admin_panel_db",
      "dbName": "admin_panel_db",
      "host": "192.168.1.20",
      "userName": "username",
      "password": "password"
    },
    {
      "id": "game_db",
      "dbType": "game_db",
      "dbName": "game_db",
      "host": "192.168.1.20",
      "userName": "username",
      "password": "password"
    }
  ],
  "schedule": {},   //全局调度参数配置(暂时未用)
  "redis": [            //redis源配置
    {
      "id": "redis-1",
      "host": "192.168.1.20",
      "port": 6379,
      "password": "",
      "maxConnect": 10,
      "maxIdleConnect": 10,
      "minIdleConnect": 10
    },
    {
      "id": "shared-redis-1",
      "shared": true,
      "host": "192.168.1.20",
      "port": 6379,
      "password": "",
      "maxConnect": 10,
      "maxIdleConnect": 10,
      "minIdleConnect": 10
    }
  ],
  "platform": {                       //自定义的平台登陆组件
    "sandboxModel": true,
    "hashSalt": "abcd1234",
    "wechat": {
      "appId": "wx1234567",
      "appKey": "appkey_here",
      "envhostSandbox": "https://api.weixin.qq.com"
    }
  }
}
```

**nodes.json-服务器结点配置**

`Litchi.createApp()`启动时，会根据`configPath + evnName`组合的路径扫描nodes.json文件。

nodes.json文件是配置所有服务器结点的文件。

以下示例文件+注释来描述配置的功能:
```json
{
  "admin": [                                  //结点类型为 admin
    {
      "nodeId": "admin-panel",                //结点id
      "port": 10810,                          //对外开放的端口
      "_components_": {                       //该结点，所用到的组件参数
        "httpMaxContentLength":10485760,      // http server相关参数
        "jdbcIds": [                          // 该结点所使用的数据库实例id(litchi.json的"jdbc":"id")
          "admin_panel_db"
        ],
        "redisIds": [                         //该点结所使用的redis实例id(litchi.json的"redis":"id")
          {"id":"redis-1", "dbIndex": 7}      // redis实例名称, dbIndex
        ],
        "blockOnConfigEmpty": false           //db队列相关参数
      }
    }
  ],
  "gate": [                                   //网关结点
    {
      "nodeId": "gate-1",                     //1号网关
      "port": 10850,
      "rpcHost": "127.0.0.1",
	  "rpcPort": 20850,
      "_components_": {
        "maintainState":2,
        "redisIds": [
          {"id":"redis-1", "dbIndex": 7}
        ]
      }
    },
    {
      "nodeId": "gate-2",                     //2号网关
      "port": 10851,
      "rpcHost": "127.0.0.1",
	  "rpcPort": 20850,
      "_components_": {
        "maintainState":2,
        "redisIds": [
          {"id":"redis-1", "dbIndex": 7}
        ]
      }
    }
  ]
}
```


## 消息与路由
消息与路由是litchi的核心。所有消息通过`ringbuffer`消息队列路由到指定的线程执行。

良好的`消息分发机制+业务功能划分`能大大降低多线程开发难度。

**(请查阅引擎的线程模型图)**
[引擎线程模型说明](https://raw.githubusercontent.com/phantacix/litchi/master/docs/images/thread_mode.png)

Litchi创建实例时，通过`litchi.setDispatch(...)`来配置该实例需要用到的线程池。

目前有三种消息类型:
* `@Handler`类型的注解适用于与处理游戏客户端的消息
* `@Rpc`类型的注解用于结点之间rpc方式的显示调用。rpc的调用方式请查阅rpc章节。
* `@EventReceive`类型的注解用于事件接收。

通过在实现类上标注`@Route`注解来描述路由信息与结点类型。

另外，引擎也继承`BaseRoute`实现了`DefaultHandlerRoute`和`DefaultRpcRoute`的默认消息路由。

目前引擎自带以下类型的消息结构:
* `RequestPacket`，请求数据包。handler与rpc底层通信结构。
* `RpcCallbackPacket` RPC回调数据包。调用rpc时会返回该数据结构。一般在调用层仅需设置回调函数即可。
* `GameEvent` 游戏事件。通过 `litchi.event().post()`进行派发.使用 @EventReceive 进行接收.


## 创建handler
TODO ...

## 创建RPC

**创建一个rpc服务器**

* Litchi实例启动时，需要添加rpc服务器组件`.addComponent(p -> new NettyRpcServer(p))`。rpc服务所涉及到ip、端口等都配置在nodes.json文件中。
* 创建一个rpc接口，该接口需要对被调用的者可访问。
```
//Route注解配置了被执行时的线程id，以及当前服务器的nodeType
@Route(defaultThreadId = ThreadId.MAINTAIN, nodeType = NodeType.GAME_SERVER)
public interface GameRpc {

    //标注@rpc注解，设置rpc的传入参数
    @Rpc
    void test(String name);
}
```
* 创建一个rpc实现类
```
//使用spring启动该实现类，继承默认的rpc路由规则，继承GameRpc接口
@Component
public class GameRpcRemote extends DefaultRpcRoute implements GameRpc {
	
	@Override
	public String test(String name) {
	        return "hello world" + name;
	}
}

```

* 调用rpc接口

调用的结点实例在启动Litchi时，需要配置`setConnectRpc(NodeType.GAME_SERVER)`，引擎会在启动时扫描nodes.json文件根据nodeType连接对应的rpc服务器。

使用`litchi.rpc()`来调用rpc方法
```
//随机选择一个nodeId(@nodeType在rpc接口的@Route已经配置好了)调用GameRpc类的test()方法，接收result的线程id为1
litchi.rpc().asyncRandom(GameRpc.class,rpc -> {
    rpc.test("litchi");
}, 1, (result) -> {
    System.out.println(result);
});
```

## 事件的发送与接收

* @EventReceive 注解用于接收事件消息。通常需要设置`name()`和`threadId()`。

* 如果该事件接收函数定义在@Route注解的类中，则默认使用@Route的threadId()。或者你也可以直接指定`theradId()`

```
@Component
@Route(defaultThreadId = ThreadId.ACTOR, nodeType = NodeType.GATE_SERVER)
public class CommonHandler implements BaseRoute<RequestPacket> {

    //接收角色登录事件
    //使用@Route自带的defaultThreadId = ThreadId.ACTOR
    //接收对象为ActorLoginEvent
    @EventReceive(name = EventKey.ACTOR_LOGIN)
    public void userLoginEvent(ActorLoginEvent event) {
        long uid = event.getUid();

        if (!litchi.currentNode().getNodeId().equals(event.getGateServerId())) {
            kick(uid);
        }
    }
}
```

* 发送事件则更为简单。
```
//构建一个ActorLoginEvent对象,post!
litchi.event().post(new ActorLoginEvent(uid, gateServerId, relogin, false));
```

## 策划配表与条件查询

* 系统自带json格式配表解析，通过litchi.json的dataconfig进行配置。

* 输出的json配表，放置在`config/dataconfig`目录(我们以file方式来描述示例。dataconfig组件也支持redis读表)

* `goodsConfig.json`示例配表：
```
[
	{
		"goodsId":1001,
		"name":"金币包",
		"useType":1,
		"effectPropId":306,
		"useValue":"[[6,2,10]]",
		"maxUseCount":15,
		"battleType":"[1,2]"
	},
	{
		"goodsId":1004,
		"name":"时光祝福",
		"useType":1,
		"effectPropId":304,
		"useValue":"[[7,1004,300000,200]]",
		"maxUseCount":5,
		"battleType":"[1]"
	}
]
```

* 编写配表映射类
```
@DataFile(fileName = "goodsConfig")
public class GoodsConfig implements ConfigAdapter {

	/** 物品id,主键*/
	@IndexPK
	private int goodsId;
	/** 名字*/
	@FieldName
	private String name;
	/** 使用类型 0：不可使用*/
	@FieldName
	private int useType;
	/** 使用效果值*/
	@FieldName
	private String useValue;
	/** 最大使用次数*/
	@FieldName
	private int maxUseCount;
	/** 适用的战斗类型(1:默认的战斗,2:副本1)*/
	@FieldName
	private String battleType;
	/** 效果对应的属性id（PropType）*/
	@FieldName
	private int effectPropId;

	
	@Override
	public void initialize() {
	}

	@Override
	public void registerIndex(List<IndexObject> index) {
	    //index.add(...) 可根据多个字段创建索引
	}
	
	//...get/set
}
```

* 查询配表

使用`litchi.data().getModel(GoodsConfig.class, 1001);` 获取条单记录。

使用`litchi.data().getList(GoodsConfig.class);` 获取所有记录条单记录。

同时也可以通过 registerIndex来创建索引，进行多字段查询。


## 数据库组件

* 在mysql创建一张表
TODO

* 配置mysql连接信息
TODO

* 创建表映射
TODO

* 访问表
TODO