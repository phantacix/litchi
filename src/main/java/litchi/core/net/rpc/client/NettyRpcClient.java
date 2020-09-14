//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import litchi.core.Litchi;
import litchi.core.common.NodeInfo;
import litchi.core.exception.RpcException;
import litchi.core.net.rpc.RpcConfig;
import litchi.core.net.rpc.codec.RpcDecoder;
import litchi.core.net.rpc.codec.RpcEncoder;
import litchi.core.net.rpc.handler.GameEventHandler;
import litchi.core.net.rpc.handler.ResponseHandler;
import litchi.core.net.rpc.handler.RpcCallbackHandler;
import litchi.core.net.rpc.packet.RequestPacket;
import litchi.core.net.rpc.packet.RpcCallbackPacket;
import litchi.core.net.rpc.packet.RpcPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;

public class NettyRpcClient {
    protected Logger RPC_LOGGER = LoggerFactory.getLogger(NettyRpcClient.class);

    private String host;
    private int port;
    private String nodeType;
    private String nodeId;

    private static EventLoopGroup bossGroup = new NioEventLoopGroup();

    private ChannelFuture channelFuture;

    /**
     * TODO 因程序异常导致没有触发回调的情况应该定时清除
     */
    private RpcFutureContext futureContext = new RpcFutureContext();

    private static final int DEFAULT_MAX_FRAME_LEN = 1024 * 1024;

    Bootstrap bootstrap = new Bootstrap();

    public static NettyRpcClient valueOfDefault(Litchi litchi, NodeInfo s) {
        return new NettyRpcClient(litchi, s.getNodeType(), s.getNodeId(), s.getRpcHost(), s.getRpcPort());
    }

    public static NettyRpcClient valueOfDefault(Litchi litchi, String nodeType, String nodeId, String host, int port) {
        return new NettyRpcClient(litchi, nodeType, nodeId, host, port);
    }

    private NettyRpcClient(String nodeType, String nodeId, String host, int port) {
        this.nodeType = nodeType;
        this.nodeId = nodeId;
        this.host = host;
        this.port = port;
    }

    private NettyRpcClient(NodeInfo s) {
        this(s.getNodeType(), s.getNodeId(), s.getRpcHost(), s.getRpcPort());
    }

    public NettyRpcClient(Litchi litchi, String nodeType, String nodeId, String host, int port) {
        this(nodeType, nodeId, host, port);

        bootstrap.group(bossGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(DEFAULT_MAX_FRAME_LEN, 0, 4, 0, 0));
                        ch.pipeline().addLast(new RpcDecoder());
                        ch.pipeline().addLast(new RpcEncoder());
                        ch.pipeline().addLast(new GameEventHandler(litchi));
                        ch.pipeline().addLast(new RpcCallbackHandler(litchi, futureContext));
                        ch.pipeline().addLast(new ResponseHandler(litchi));
                    }
                })
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String nodeType() {
        return this.nodeType;
    }

    public String nodeId() {
        return this.nodeId;
    }

    public void writeRpcPacket(Object packet) {
        this.channelFuture.channel().writeAndFlush(new RpcPacket<>(packet));
    }

    public void send(RequestPacket requestPacket, RpcCallbackContext context) {
        if (context != null && context.rpcCallback != null) {
            futureContext.setCallback(requestPacket.sequenceId, context);
        }
        writeRpcPacket(requestPacket);
    }

    public RpcCallbackPacket send(RequestPacket request) {
        try {
            //同步等待结果
            RpcFuture<RpcCallbackPacket> future = new RpcFuture<>();
            futureContext.setRpcFuture(request.sequenceId, future);
            writeRpcPacket(request);
            return future.get(RpcConfig.RPC_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            RPC_LOGGER.warn("client timeout. serverInfo = {} \r\t exception = {} ", printInfo(), e);
            throw new RpcException("", e);
        }
    }

    public String printInfo() {
        return String.format("host=%s port=%s nodeType=%s nodeId=%s", host, port, nodeType, nodeId);
    }

    public boolean isConnect() {
        if (channelFuture == null) {
            return false;
        }
        return channelFuture.channel().isActive();
    }

    public void connect(boolean sync) {
        try {
            if (sync) {
                channelFuture = bootstrap.connect(this.host, this.port).sync();
            } else {
                channelFuture = bootstrap.connect(this.host, this.port);
                channelFuture.addListener((ChannelFutureListener) channelFuture -> RPC_LOGGER.debug("connection complete!"));
            }
        } catch (Exception e) {
            RPC_LOGGER.error("----->connect fail. server info = {}", printInfo());
        }
    }

    public void stop() {
        if (channelFuture != null) {
            channelFuture.channel().close(); //new DefaultChannelPromise(channelFuture.channel(), eventExecutor)
            RPC_LOGGER.info("remote client disconnect. ServerInfo = {}", printInfo());
        }
    }

    public <T> T getProxy(Class<T> clazz, String nodeType, int threadId, long hash, RpcCallback<?> callback) {
        return getProxy(clazz, (proxy, method, args) -> {

            RequestPacket requestPacket = RequestPacket.valueOfRPC(nodeType, clazz.getSimpleName(), method.getName(), args);

            // 如果为void方法，则默认为异步
            if (void.class.equals(method.getReturnType())) {
                send(requestPacket, null);
                return null;
            }

            if (callback != null) {
                send(requestPacket, new RpcCallbackContext(threadId, hash, callback));
                return null;
            }

            //sync
            RpcCallbackPacket response = send(requestPacket);
            if (response == null) {
                return null;
            }
            return response.result;
        });
    }

    private <T> T getProxy(Class<T> clazz, InvocationHandler invocationHandler) {
        return (T) Proxy.newProxyInstance(NettyRpcClient.class.getClassLoader(), new Class[]{clazz}, invocationHandler);
    }

    @Override
    public String toString() {
        return "NettyRpcClient " + printInfo();
    }

}
