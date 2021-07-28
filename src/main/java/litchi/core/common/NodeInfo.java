//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import litchi.core.common.utils.RuntimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeInfo implements Comparable<NodeInfo> {

    /**
     * 结点id
     */
    private String nodeId;

    /**
     * 结点类型
     */
    private String nodeType;

    /**
     * 外网端口
     */
    private int port;

    /**
     * 开启rpc的主机地址
     */
    private String rpcHost;
    /**
     * 开启rpc的端口
     */
    private int rpcPort;

    /**
     * 组件相关的参数
     */
    private JSONObject _components_;

    /**
     * 启动后服务器的进程id
     */
    private Long pid;

	/** 是否禁用*/
	private boolean isDisable;

    /** 当配置文件缺失时，是否退出服务进程*/
    private boolean lackConfigFileIsExit = true;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRpcHost() {
        return rpcHost;
    }

    public void setRpcHost(String rpcHost) {
        this.rpcHost = rpcHost;
    }

    public int getRpcPort() {
        return rpcPort;
    }

    public void setRpcPort(int rpcPort) {
        this.rpcPort = rpcPort;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public boolean isLackConfigFileIsExit() {
        return lackConfigFileIsExit;
    }

    public void setLackConfigFileIsExit(boolean lackConfigFileIsExit) {
        this.lackConfigFileIsExit = lackConfigFileIsExit;
    }

    public JSONObject get_components_() {
        return _components_;
    }

    public void set_components_(JSONObject _components_) {
        this._components_ = _components_;
    }

    public JSONObject getJsonObjectOpts(String key) {
        return this._components_.getJSONObject(key);
    }

    public String getStringOpts(String key) {
        return this._components_.getString(key);
    }

    public int getIntOpts(String key) {
        Integer value = this._components_.getInteger(key);
        if (value == null) {
            return 0;
        }
        return value;
    }

    public int getIntOpts(String key, int defaultValue) {
        Integer value = this._components_.getInteger(key);
        return value == null ? defaultValue : value;
    }

    public JSONArray getJsonArrayOpts(String key) {
        return this._components_.getJSONArray(key);
    }


    public boolean isDisable() {
		return isDisable;
	}

    public void setDisable(boolean isDisable) {
		this.isDisable = isDisable;
	}

    @Override
    public String toString() {
        return "NodeInfo {" +
                "nodeId='" + nodeId + '\'' +
                ", nodeType='" + nodeType + '\'' +
                ", port=" + port +
                ", rpcHost='" + rpcHost + '\'' +
                ", rpcPort=" + rpcPort +
                ", pid='" + pid + '\'' +
                '}';
    }

    public static Map<String, List<NodeInfo>> getNodeMaps(JSONObject jsonObject, String nodeId) {
        Map<String, List<NodeInfo>> maps = new HashMap<>();

        for (String nodeType : jsonObject.keySet()) {
            JSONArray itemArrays = jsonObject.getJSONArray(nodeType);
            if (itemArrays.isEmpty()) {
                continue;
            }

            List<NodeInfo> list = itemArrays.toJavaList(NodeInfo.class);
            for (NodeInfo si : list) {
                si.setNodeType(nodeType);
                if (si.getNodeId().equals(nodeId)) {
                    si.setPid(RuntimeUtils.pid());
                }
            }

            List<NodeInfo> serverInfoList = maps.getOrDefault(nodeType, new ArrayList<>());
            if (serverInfoList.isEmpty()) {
                maps.put(nodeType, serverInfoList);
            }
            serverInfoList.addAll(list);
        }
        return maps;
    }

    @Override
    public int compareTo(NodeInfo o) {
        return this.nodeId.compareTo(o.nodeId);
    }

}
