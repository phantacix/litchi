//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core;

/**
 * 系统常量定义
 */
public class Constants {

    /**
     * vm opts
     */
    public class Property {
        /**
         * 配置文件根路径
         */
        public static final String CONFIG_DIR = "litchi.config";
        /**
         * 运行环境名称
         */
        public static final String ENV_NAME = "litchi.env";
        /**
         * 运行服务id
         */
        public static final String NODE_ID = "litchi.nodeid";
        /**
         * 资源文件根路径
         */
        public static final String RESOURCE_DIR = "admin.resources";
    }


    /**
     * component name
     */
    public class Component {
        public static final String BASE_PACKAGES = "basePackages";

        public static final String DATA_CONFIG = "dataConfig";

        public static final String DB_QUEUE = "dbQueue";

        public static final String DISPATCH = "dispatch";

        public static final String JDBC = "jdbc";

        public static final String REDIS = "redis";

        public static final String ROUTE = "route";

        public static final String EVENT = "event";
    }

    public class Net {
        public static final String HTTP_SERVER = "httpServer";

        public static final String RPC_SERVER = "rpcServer";

        public static final String RPC_CLIENT = "rpcClient";

        public static final String WEB_SOCKET_SERVER = "webSocketServer";
    }

    public class File {
        public static final String LITCHI_DOT_JSON = "litchi.json";
        public static final String NODES_DOT_JSON = "nodes.json";
        public static final String LOG_BACK = "logback.xml";
    }
}


