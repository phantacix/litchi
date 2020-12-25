//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.http.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http.multipart.*;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;
import litchi.core.common.utils.ObjectUtils;
import litchi.core.common.utils.StringUtils;
import litchi.core.net.http.server.router.RouteAction;
import litchi.core.net.http.server.router.RouteResult;
import litchi.core.net.session.ChannelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

public abstract class HttpController {
    protected Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected static String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";
    protected static String FILE_CONTENT_TYPE = "application/json;charset=UTF-8";

    protected Channel channel;
    protected FullHttpRequest request;
    protected RouteResult<RouteAction> routeResult;
    private boolean enableCookies;

    protected Map<String, String> postMaps = new HashMap<>();

    protected Map<String, Cookie> cookieMaps = new HashMap<>();

    private FileUpload fileUpload;

    public Map<String, String> postMap() {
        return this.postMaps;
    }

    public Map<String, String> getMap() {
        return this.routeResult.pathParams();
    }

    public Map<String, String> getParams() {
        Map<String, List<String>> queryParams = this.routeResult.queryParams();
        Map<String, String> map = new HashMap<>();
        for (Entry<String, List<String>> entry : queryParams.entrySet()) {
            Optional<String> optional = entry.getValue().stream().findFirst();
            map.put(entry.getKey(), optional.orElse(""));
        }
        return map;
    }

    public FileUpload fileUpload() {
        return fileUpload;
    }

    public void init(Channel channel, FullHttpRequest request, RouteResult<RouteAction> routeResult, boolean enableCookies) {
        this.channel = channel;
        this.request = request;
        this.routeResult = routeResult;
        this.enableCookies = enableCookies;

        if (this.method() == HttpMethod.POST) {
            try {
                HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
                decoder.offer(request);
                List<InterfaceHttpData> paramsList = decoder.getBodyHttpDatas();
                for (InterfaceHttpData httpData : paramsList) {
                    if (httpData.getHttpDataType() == HttpDataType.Attribute) {
                        Attribute data = (Attribute) httpData;
                        postMaps.put(data.getName(), data.getValue());
                    } else if (httpData.getHttpDataType() == HttpDataType.FileUpload) {
                        MixedFileUpload fileUpload = (MixedFileUpload) httpData;
                        this.fileUpload = fileUpload;
                    } else {
                        LOGGER.error("not support http data type. type={}", httpData.getHttpDataType());
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("{}", ex);
            }
        }

        if (enableCookies) {
            String cookieValues = request.headers().get(HttpHeaderNames.COOKIE);
            if (StringUtils.isNotBlank(cookieValues)) {
                Set<Cookie> cookieSet = ServerCookieDecoder.STRICT.decode(cookieValues);
                for (Cookie cookie : cookieSet) {
                    if (StringUtils.isBlank(cookie.path())) {
                        cookie.setPath("/");
                    }
                    cookieMaps.put(cookie.name(), cookie);
                }
            }
        }
    }

    public HttpMethod method() {
        return request.method();
    }


    public <T> T get(String name, T defaultValue,boolean checkPost) {
        T result = get(name, defaultValue);
        if (checkPost && result == defaultValue) {
            result = post(name, defaultValue);
        }
        return result;
    }

    public <T> T get(String name, T defaultValue) {
        String result = routeResult.param(name);
        if (StringUtils.isBlank(result)) {
            return defaultValue;
        }

        T value = ObjectUtils.valueOf(result, defaultValue);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public String get(String name) {
        return get(name, "");
    }

    public <T> T post(String name, T defaultValue) {
        String result = postMaps.get(name);
        if (StringUtils.isBlank(result)) {
            return defaultValue;
        }
        T value = ObjectUtils.valueOf(result, defaultValue);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public String post(String name) {
        return post(name, "");
    }

    public String getRemoteIp() {
        return ChannelUtils.getRemoteIp(this.channel);
    }

    public void renderJson(Object object) {
        String json = JSON.toJSONString(object, SerializerFeature.IgnoreNonFieldGetter);
        write(json, JSON_CONTENT_TYPE);
    }

    public void jsonResponse(Object object) {
        String json = JSON.toJSONString(object);
        write(json, JSON_CONTENT_TYPE);
    }

    /**
     * write to response
     *
     * @param text
     */
    public void render(String text) {
        write(text, "text/plain; charset=UTF-8");
    }

    public void render(String text, String contentType) {
        write(text, contentType);
    }

    public void write(String text, String contentType) {
        ByteBuf content = Unpooled.copiedBuffer(text, CharsetUtil.UTF_8);
        HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        response.headers().set(CONTENT_TYPE, contentType);

        if (enableCookies) {
            List<String> values = ServerCookieEncoder.STRICT.encode(cookieMaps.values());
            response.headers().add(HttpHeaderNames.SET_COOKIE, values);
        }

        // 跨域支持
        response.headers().add("Access-Control-Allow-Origin", "*");
        response.headers().add("Access-Control-Allow-Methods", "POST");
        HttpUtil.setContentLength(response, content.readableBytes());
        channel.writeAndFlush(response); //.addListener(ChannelFutureListener.CLOSE);
    }

    public void writeFile(String fileName, String text) {
        ByteBuf content = Unpooled.copiedBuffer(text, CharsetUtil.UTF_8);
        HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        response.headers().set("Pragma", "Pragma");
        response.headers().set("Expires", "0");
        response.headers().set("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.headers().set("Content-Type", "application/download");
        response.headers().set("Content-Disposition", "attachment;filename=" + fileName);
        response.headers().set("Content-Transfer-Encoding", "binary");

        if (enableCookies) {
            for (Map.Entry<String, Cookie> entry : cookieMaps.entrySet()) {
                response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(entry.getValue()));
            }
        }

        // 跨域支持
        response.headers().add("Access-Control-Allow-Origin", "*");
        response.headers().add("Access-Control-Allow-Methods", "POST");
        HttpUtil.setContentLength(response, content.readableBytes());
        channel.writeAndFlush(response); //.addListener(ChannelFutureListener.CLOSE);
    }

    public String getCookie(String name, String defaultValue) {
        if (this.cookieMaps.containsKey(name)) {
            Cookie cookie = this.cookieMaps.get(name);
            return cookie.value();
        }
        return defaultValue;
    }

    public String getCookie(String name) {
        return getCookie(name, "");
    }

    public void setCookie(String name, String value) {
        setCookie(name, value, -1, "/", "");
    }

    public void setCookie(String name, String value, long maxAge, String path, String domain) {
        Cookie cookie = this.cookieMaps.get(name);
        if (cookie == null) {
            cookie = new DefaultCookie(name, value);
            this.cookieMaps.put(name, cookie);
        }
        cookie.setValue(value);
        if (maxAge > 0) {
            cookie.setMaxAge(maxAge);
        }

        if (StringUtils.isNotBlank(path)) {
            cookie.setPath(path);
        }

        if (StringUtils.isNotBlank(domain)) {
            cookie.setDomain(domain);
        }
    }

    public Collection<Cookie> getCookieMaps() {
        return this.cookieMaps.values();
    }

}
