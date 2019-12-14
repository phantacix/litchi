//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.event;

import litchi.core.Constants;
import litchi.core.Litchi;
import litchi.core.components.Component;
import litchi.core.event.annotation.EventReceive;
import litchi.core.exception.CoreException;
import litchi.core.router.annotation.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import litchi.core.common.extend.ASMMethod;
import litchi.core.common.thread.NamedScheduleExecutor;
import litchi.core.common.utils.StringUtils;
import litchi.core.dispatch.executor.GameEventExecutor;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * event component
 *
 * @author 0x737263
 */
public class EventComponent implements Component {
    private static Logger LOGGER = LoggerFactory.getLogger(EventComponent.class);

    Litchi litchi;

    private ConcurrentLinkedQueue<GameEvent> eventQueue = new ConcurrentLinkedQueue<>();

    private NamedScheduleExecutor eventExecutor;

    /**
     * key:eventName, value:{key:threadId, value:List<ASMMethod>}
     */
    private Map<String, Map<Integer, List<ASMMethod>>> eventInfoMaps = new LinkedHashMap<>();

    public EventComponent(Litchi litchi) {
        this.litchi = litchi;
    }

    @Override
    public String name() {
        return Constants.Component.EVENT;
    }

    @Override
    public void start() {
        this.eventExecutor = new NamedScheduleExecutor(1, "event-queue-thread");
        this.eventExecutor.scheduleWithFixedDelay(() -> {
            try {
                for (; ; ) {
                    GameEvent e = eventQueue.poll();
                    if (e == null) {
                        return;
                    }
                    publish(e);
                }
            } catch (Exception e) {
                LOGGER.error("scene queue thread error:{}", e);
            }
        }, 10, 1, TimeUnit.MILLISECONDS);
    }

    @Override
    public void afterStart() {
    }

    @Override
    public void stop() {
    }

    public void post(GameEvent event) {
        if (event != null) {
            this.eventQueue.add(event);
        }
    }

    private void publish(GameEvent event) {
        Map<Integer, List<ASMMethod>> maps = this.eventInfoMaps.get(event.name);
        if (maps == null) {
            LOGGER.warn("event={}, not found target", event.name);
            return;
        }

        try {
            int i = 0;
            for (int threadId : maps.keySet()) {
                if (i == 0) {
                    event.threadId = threadId;
                    litchi.dispatch().publish(new GameEventExecutor(litchi, event));
                } else {
                    GameEvent e = (GameEvent) event.clone();
                    e.threadId = threadId;
                    litchi.dispatch().publish(new GameEventExecutor(litchi, e));
                }
                i++;
            }
        } catch (Exception ex) {
            LOGGER.error("{}", ex);
        }
    }

    /**
     * event execute
     *
     * @param event
     */
    public void execute(GameEvent event) {
        Map<Integer, List<ASMMethod>> maps = this.eventInfoMaps.get(event.name);
        List<ASMMethod> list = maps.get(event.threadId);
        for (ASMMethod method : list) {
            method.invoke(event);
        }
    }

    /**
     * load @EventReceive
     *
     * @param listener
     * @return
     */
    public void register(Object listener) {
        Class<?> clazz = listener.getClass();
        Method[] methods = clazz.getMethods();
        for (Method m : methods) {
            m.setAccessible(true);
            // 普通的事件注解
            EventReceive receive = m.getAnnotation(EventReceive.class);
            if (receive != null) {
                registerEvent(listener, clazz, m, receive);
            }
        }
    }

    private void registerEvent(Object listener, Class<?> clazz, Method method, EventReceive receive) {
        int threadId = receive.threadId();
        if (threadId < 1) {
            Route route = listener.getClass().getAnnotation(Route.class);
            if (route != null) {
                threadId = route.defaultThreadId();
            }
        }

        if (threadId < 1) {
            throw new CoreException(" class = {}, method = {}, no set threadId", listener.getClass(), method.getName());
        }

        for (String eventName : receive.name()) {
            if (StringUtils.isBlank(eventName)) {
                LOGGER.warn("event name is null! class = {}, method = {}", listener.getClass(), method.getName());
                continue;
            }
            addEventInfo(eventName, threadId, ASMMethod.valueOf(method, listener));
        }
    }

    private void addEventInfo(String eventName, int threadId, ASMMethod method) {
        Map<Integer, List<ASMMethod>> valueMaps = eventInfoMaps.getOrDefault(eventName, new HashMap<>());
        if (!eventInfoMaps.containsKey(eventName)) {
            eventInfoMaps.put(eventName, valueMaps);
        }

        List<ASMMethod> eventInfoList = valueMaps.getOrDefault(threadId, new ArrayList<>());
        if (!valueMaps.containsKey(threadId)) {
            valueMaps.put(threadId, eventInfoList);
        }
        eventInfoList.add(method);
    }

}
