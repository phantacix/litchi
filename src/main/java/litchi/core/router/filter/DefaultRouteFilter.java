//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
//package litchi.core.router.filter;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import litchi.core.litchi;
//import litchi.core.dispatch.executor.HandlerRouteDispatch;
//import litchi.core.net.remote.client.ClientRoute;
//import litchi.core.net.remote.client.NettyRpcClient;
//import litchi.core.net.remote.client.RpcContext;
//import litchi.core.net.remote.packet.RpcRequest;
//import litchi.core.net.session.NettySession;
//import litchi.core.router.RouteInfo;
//import litchi.core.router.RoutePacket;
//
//import java.util.List;
//
///**
// * 默认的handler路由过滤器
// * 1、如果消息是本机，则行执本机的handler
// * 2、否则转移给对应的ServerType
// * @author 0x737263
// */
//public class DefaultRouteFilter implements RouteFilter<RpcRequest> {
//    private static Logger LOGGER = LoggerFactory.getLogger(DefaultRouteFilter.class);
//
//    @Override
//    public boolean route(litchi litchi, NettySession session, RpcRequest request) {
////        final String nodeType = request.nodeType();
////
////        //如果是当前服务器类型，则进行本地处理
////        if (litchi.currentNode().nodeType() == nodeType) {
////            //是否为handler类型的路由
////            RouteInfo routeInfo = litchi.route().handlerRoutes().get(request.route());
////            if (routeInfo != null) {
////                HandlerRouteDispatch dispatch = new HandlerRouteDispatch(session, request, routeInfo);
////                litchi.dispatch().post(dispatch);
////                return true;
////            }
////
////            //是否为rpc类型的路由
////            routeInfo = litchi.route().rpcRoutes().get(request.route());
////            if (routeInfo != null) {
////
////                return true;
////            }
////
////            return false;
////        }
////
////        // 否则，转发给目标服务器。构造rpc，把路由包对象发到目标服务器进行处理
////        ClientRoute getClientRoute = litchi.remote().getClientRoute(nodeType);
////        NettyRpcClient rpcClient = getClientRoute.select(session, nodeType, request);
////        if (rpcClient == null) {
////            LOGGER.warn("[2] can not find remote client for routePacket = {}", request);
////            return false;
////        }
////
////        RpcRequest request = RpcRequest.valueOfHandler(session.uid(), packet);
////        RpcContext ctx = RpcContext.getContext();
////        ctx.setSync(false);
////        rpcClient.send(request, ctx);
////
////        if (LOGGER.isDebugEnabled()) {
////            LOGGER.debug("route to next server. uid={} routePacket={}", session.uid(), packet);
////        }
//
//        return true;
//    }
//}
