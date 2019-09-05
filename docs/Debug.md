### 启动参数
```
-Dlitchi.config    		配置文件根相对路径
-Dlitchi.env       		运行环境名称
-Dlitchi.nodeid      	服务器id
-Dadmin.resources	    web容器资源文件相对路径
```

Run->Debug Configrations -> Java Application -> Arguments在VM arguments文本框中填入一下内容。
以下默认为IDEA的jvm参数配置，如果是eclipse则可以把$PROJECT_DIR$替换为../

#### admin-panel启动参数
```text
-Dlitchi.config=$PROJECT_DIR$/config
-Dlitchi.env=local
-Dlitchi.nodeid=admin-panel
-Dadmin.resources=$PROJECT_DIR$/admin-panel/resources
```

#### center-server启动参数
```text
-Dlitchi.config=$PROJECT_DIR$/config
-Dlitchi.env=local
-Dlitchi.nodeid=center-1
```

#### game-server启动参数
```text
-Dlitchi.config=$PROJECT_DIR$/config
-Dlitchi.env=local
-Dlitchi.nodeid=game-1
```

#### gate-server启动参数
```text
-Dlitchi.config=$PROJECT_DIR$/config
-Dlitchi.env=local
-Dlitchi.nodeid=gate-1
```

#### web-server启动参数
```text
-Dlitchi.config=$PROJECT_DIR$/config
-Dlitchi.env=local
-Dlitchi.nodeid=web-1
```


### protobuf协议生成java文件
执行build-proto.bat批处理文件，协议代码会生成到protocol\src\main\java目录下


### 生成客户端TypeScript协议调用接口
运行game-shared下的tool.BuildTypeScriptInterface.java类即可

### 编译发布

批处理发布:
 
- 开发版本运行:`publish_dev.bat`

### 工具相关

- redis下载 https://github.com/MicrosoftArchive/redis/releases

