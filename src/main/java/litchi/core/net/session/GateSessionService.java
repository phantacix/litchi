//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.session;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import litchi.core.common.Schedule;
import litchi.core.common.utils.ServerTime;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GateSessionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GateSessionService.class);

    /**
     * session集合.
     * key:sessionId, value:GateSession
     */
    private ConcurrentHashMap<String, GateSession> sessionMaps = new ConcurrentHashMap<>();

    /**
     * 在线(已登录)角色Session.
     * key:uid value:sessionId
     */
    private ConcurrentHashMap<Long, OnlineSession> onlineMaps = new ConcurrentHashMap<>();

    private Schedule schedule = new Schedule(1, "session-clean");

    public GateSessionService() {

        schedule.addEveryMillisecond(() -> {
            long now = ServerTime.timeMillis();
            for (Entry<Long, OnlineSession> entry : onlineMaps.entrySet()) {
                if (now - entry.getValue().heartTime > 120_000) {
                    onlineMaps.remove(entry.getKey());
                    LOGGER.info("remove timeout online session. uid={} sessionId={}", entry.getKey(), entry.getValue().sessionId);
                    removeOnlineSession(entry.getValue().sessionId);
                }
            }
        }, 1000 * 6);
    }

    /**
     * 加入在线列表
     *
     * @param session
     * @param uid
     */
    public GateSession putOnlineSession(GateSession session, long uid) {
        // 分配游戏服务器id
        session.setUid(uid);

        // 加入在线列表
        onlineMaps.put(uid, new OnlineSession(session.getChannelId(), ServerTime.timeMillis()));

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("session:[{}] bind uid:[{}] ", session.getChannelId(), uid);
        }
        return session;
    }

    public void putSession(GateSession session) {
        if (session != null) {
            sessionMaps.put(session.getChannelId(), session);
        }
    }

    public GateSession getSession(ChannelHandlerContext ctx) {
        return getSession(ctx.channel().id().asShortText());
    }

    public GateSession getSession(String sessionId) {
        return sessionMaps.get(sessionId);
    }

    public GateSession getOnlineSession(long uid) {
        OnlineSession onlineSession = onlineMaps.get(uid);
        if (onlineSession == null) {
            return null;
        }
        return sessionMaps.get(onlineSession.sessionId);
    }

    public Collection<GateSession> getOnlineSessions() {
        Collection<GateSession> onlineSessions = new HashSet<>();
        for (OnlineSession onlineSession : onlineMaps.values()) {
            GateSession session = sessionMaps.get(onlineSession.sessionId);
            if (session != null) {
                onlineSessions.add(session);
            }
        }
        return onlineSessions;
    }

    /**
     * 移除在线玩家
     *
     * @param sessionId
     */
    public GateSession removeOnlineSession(String sessionId) {
        GateSession oldSession = sessionMaps.remove(sessionId);
        if (oldSession == null) {
            return null;
        }

        long uid = oldSession.uid();
        if (uid > 0) {
            onlineMaps.remove(uid);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("============remove online session:{} uid={}", sessionId, uid);
            }
        } else if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("=============remove session:{} uid={}", sessionId, uid);
        }

        oldSession.close();

        return oldSession;
    }

    public boolean isOnline(long uid) {
        return onlineMaps.containsKey(uid);
    }

    /**
     * 获取在线uid列表
     *
     * @return
     */
    public Set<Long> onlineList() {
        return onlineMaps.keySet();
    }

    /**
     * 获取在线玩家总数
     *
     * @return
     */
    public int onlineCount() {
        return onlineMaps.size();
    }

    /**
     * 获取在线玩家列表
     *
     * @param filterUids 过滤的uid
     * @return
     */
    public Set<Long> onlineList(Collection<Long> filterUids) {
        Set<Long> onlineSets = new HashSet<>();

        for (Long uid : onlineMaps.keySet()) {
            if (filterUids == null || !filterUids.contains(uid)) {
                onlineSets.add(uid);
            }
        }
        return onlineSets;
    }

    public Collection<GateSession> getSessions() {
        return sessionMaps.values();
    }

    /**
     * 更新心跳时间
     *
     * @param uid
     * @param time
     */
    public void onlineHeart(long uid, long time) {
        OnlineSession onlineSession = onlineMaps.get(uid);
        if (onlineSession == null) {
            return;
        }
        onlineSession.heartTime = time;
    }

    public class OnlineSession {
        public String sessionId;
        public long joinTime;
        public long heartTime;

        public OnlineSession(String sessionId, long joinTime) {
            this.sessionId = sessionId;
            this.joinTime = joinTime;
            this.heartTime = joinTime;
        }
    }
}