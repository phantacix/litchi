//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.session;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务器结点session管理
 *
 * @author 0x737263
 */
public class NodeSessionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeSessionService.class);

    /**
     * key:sessionId, value:NettySession
     */
    protected Map<Long, NettySession> sessionMaps = new ConcurrentHashMap<>();

    /**
     * key:nodeType,value:{key:nodeId,value:sessionId}
     */
    protected Map<String, Map<String, Long>> sessionServerIdMaps = new ConcurrentHashMap<>();

    public NettySession getSession(ChannelHandlerContext ctx) {
        return getSession(ctx.channel().attr(NettySession.SESSION_ID).get());
    }

    public NettySession getSession(Long sessionId) {
        if (sessionId == null) {
            return null;
        }
        return sessionMaps.get(sessionId);
    }

    public NettySession getSession(String nodeType, String nodeId) {
        Map<String, Long> maps = sessionServerIdMaps.get(nodeType);
        if (maps == null) {
            return null;
        }

        Long sessionId = maps.get(nodeId);
        if (sessionId == null) {
            return null;
        }

        return sessionMaps.get(sessionId);
    }

    public void putSession(NettySession session) {
        sessionMaps.put(session.getSessionId(), session);
    }

    public void removeSession(Long sessionId) {
        if (sessionId == null) {
            return;
        }

        NettySession session = sessionMaps.remove(sessionId);
        if (session != null) {
            removeSessionNode(session);
        }
    }

    public void removeSessionNode(NettySession session) {
        if (session == null) {
            return;
        }

        Iterator<Map<String, Long>> iterator = sessionServerIdMaps.values().iterator();
        while (iterator.hasNext()) {
            Map<String, Long> maps = iterator.next();
            Iterator<Long> sessionIterator = maps.values().iterator();
            while (sessionIterator.hasNext()) {
                if (sessionIterator.next() == session.getSessionId()) {
                    sessionIterator.remove();
                    break;
                }
            }
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("rpc server handler session:[{}] in session list.", session);
        }
    }

    public void addSessionNode(String nodeType, String nodeId, NettySession session) {
        Map<String, Long> maps = sessionServerIdMaps.get(nodeType);
        if (maps == null) {
            maps = new ConcurrentHashMap<>();
            sessionServerIdMaps.put(nodeType, maps);
        }
        Attribute<String> typeAttr = session.attr(NettySession.FROM_NODE_TYPE);
        typeAttr.set(nodeType);
        Attribute<String> idAttr = session.attr(NettySession.FROM_NODE_ID);
        idAttr.set(nodeId);
        maps.put(nodeId, session.getSessionId());
    }

    public List<String> getNodeIdList(String nodeType) {
        Map<String, Long> nodeMaps = sessionServerIdMaps.get(nodeType);
        if (nodeMaps == null || nodeMaps.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(nodeMaps.keySet());
    }
}
