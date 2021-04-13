//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.http.client;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class Http {
    private static final Logger LOGGER = LoggerFactory.getLogger(Http.class);

    private int connectTimeout = 5000; // 5s
    private int readTimeout = 5000; // 5s
    private int writeTimeout = 5000; // 5s

    private OkHttpClient client;

    private static Http instance;

    private Http() {
        initHttp();
    }

    private void initHttp() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        builder.readTimeout(readTimeout, TimeUnit.MILLISECONDS);
        builder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);

        SSLUtils.SSLParams ssl = SSLUtils.getSslSocketFactory(null, null, null);
        builder.sslSocketFactory(ssl.sSLSocketFactory, ssl.trustManager);
        builder.hostnameVerifier((hostname, session) -> true);

        // Dispatcher dispatcher = new Dispatcher();
        // dispatcher.setMaxRequests(128);
        // dispatcher.setMaxRequestsPerHost(12);
        // builder.dispatcher(dispatcher);

        this.client = builder.build();
    }

    public static Http instance() {
        if (instance == null) {
            instance = new Http();
        }
        return instance;
    }

    public static String get(String url) {
        return get(url, null);
    }

    public static String get(String url, Map<String, String> params) {
        Request.Builder reqBuilder = new Request.Builder();
        reqBuilder.url(concatUrl(url, params));

        return instance().execute(reqBuilder.build());
    }

    public static void get(String url, Map<String, String> params, StringCallback callback) {
        Request.Builder reqBuilder = new Request.Builder();
        reqBuilder.url(concatUrl(url, params));

        instance().execute(reqBuilder.build(), callback);
    }

    public static String post(String url, Map<String, String> params) {
        Request.Builder reqBuilder = new Request.Builder();
        reqBuilder.url(url);

        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Entry<String, String> entry : params.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }
        reqBuilder.post(formBuilder.build());

        return instance().execute(reqBuilder.build());
    }

    public static String postBody(String url, String body) {
        Request.Builder reqBuilder = new Request.Builder();
        reqBuilder.url(url);
        reqBuilder.post(RequestBody.create(null, body));
        return instance().execute(reqBuilder.build());
    }

    public static String postBody(String url, RequestBody requestBody) {
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try {
            String text = instance().execute(request);
            return text;
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public static InputStream postBodyAndGetStream(String url, String body) {
        Request.Builder reqBuilder = new Request.Builder();
        reqBuilder.url(url);
        reqBuilder.post(RequestBody.create(null, body));
        return instance().executeAndGetStream(reqBuilder.build());
    }

    public static void post(String url, Map<String, String> params, StringCallback callback) {
        Request.Builder reqBuilder = new Request.Builder();
        reqBuilder.url(url);

        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Entry<String, String> entry : params.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }
        reqBuilder.post(formBuilder.build());

        instance().execute(reqBuilder.build(), callback);
    }

    public static String concatUrl(String url, Map<String, String> data) {
        if (data == null) {
            return url;
        }

        try {
            StringBuilder sb = new StringBuilder();
            for (Entry<String, String> entry : data.entrySet()) {
                sb.append("&" + entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "utf-8"));
            }

            if (sb.length() < 1) {
                return url;
            }

            String prefix = sb.substring(1).toString();
            if (url.indexOf("?") < 1) {
                url += "?";
            }

            return url + prefix;
        } catch (Exception ex) {
            LOGGER.warn("{}", ex);
        }
        return url;
    }

    public static void getExecute(String url, Map<String, String> params, Callback callback) {
        Request.Builder request = new Request.Builder();
        request.url(concatUrl(url, params));
        final Call call = instance().client.newCall(request.build());
        call.enqueue(callback);
    }

    public static void postExecute(String url, Map<String, String> params, Callback callback) {
        Request.Builder reqBuilder = new Request.Builder();
        reqBuilder.url(url);

        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Entry<String, String> entry : params.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }
        reqBuilder.post(formBuilder.build());

        final Call call = instance().client.newCall(reqBuilder.build());
        call.enqueue(callback);
    }

    public String execute(Request request) {
        final Call call = instance().client.newCall(request);
        Response response = null;
        try {
            response = call.execute();
            ResponseBody body = response.body();
            return body.string();
        } catch (IOException e) {
            LOGGER.error("{}", e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return "";
    }

    public InputStream executeAndGetStream(Request request) {
        final Call call = instance().client.newCall(request);

        Response response = null;
        try {
            response = call.execute();
            ResponseBody body = response.body();
            return body.byteStream();
        } catch (IOException e) {
            LOGGER.error("{}", e);
        }
        return null;
    }

    public void execute(Request request, StringCallback callback) {
        final Call call = instance().client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call paramCall, Response paramResponse) throws IOException {
                callback.completed(paramResponse.body().string());
            }

            @Override
            public void onFailure(Call paramCall, IOException ex) {
                callback.failed(ex);
            }
        });
    }

}
