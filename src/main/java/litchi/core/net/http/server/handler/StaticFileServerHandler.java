//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.http.server.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import litchi.core.common.utils.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import litchi.core.common.utils.PathUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author 0x737263
 */
@ChannelHandler.Sharable
public class StaticFileServerHandler extends BaseServerHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(StaticFileServerHandler.class);
    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;

    private String rootPath;

    private Map<String, String> mineTypeMaps = new HashMap<>();
    private static String DEFAULT_MINE_TYPE = "application/octet-stream";

    public String getMimeType(String extension) {
        return mineTypeMaps.getOrDefault(extension, DEFAULT_MINE_TYPE);
    }

    public void addMineType(String key, String value) {
        mineTypeMaps.put(key, value);
    }

    public StaticFileServerHandler(String rootPath) {
        this(rootPath, null);
    }

    public StaticFileServerHandler(String rootPath, Map<String, String> mineTypes) {
        this.rootPath = rootPath;

        mineTypeMaps.put(".jpg", "image/jpeg");
        mineTypeMaps.put(".jpeg", "image/jpeg");
        mineTypeMaps.put(".png", "image/png");
        mineTypeMaps.put(".gif", "image/gif");
        mineTypeMaps.put(".txt", "text/plain");
        mineTypeMaps.put(".css", "text/css");
        mineTypeMaps.put(".js", "application/x-javascript");
        if (mineTypes != null) {
            mineTypeMaps.putAll(mineTypes);
        }
    }

    @Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
		int qPos = req.uri().indexOf("?");
		String encodedPath = (qPos >= 0) ? req.uri().substring(0, qPos) : req.uri();

		try {
			encodedPath = URLDecoder.decode(encodedPath, "UTF-8");
			int dotIndex = encodedPath.lastIndexOf(".");
			if (dotIndex >= 0) {
				File f = new File(PathUtils.getResourcePath() + rootPath + File.separator + encodedPath);
				if (f.exists() && f.isFile()) {
					String suffix = encodedPath.substring(dotIndex, encodedPath.length());
					write(ctx, req, f, suffix);
				} else {
					writeHttpStatus(ctx, HttpResponseStatus.NOT_FOUND);
				}
				return;
			}
		} catch (Exception ex) {
			LOGGER.error("{}", ex);
		}
		req.retain();
		ctx.fireChannelRead(req);
	}

    private void write(ChannelHandlerContext ctx, FullHttpRequest request, File file, String suffix) throws Exception {
        String ifModifiedSince = request.headers().get(HttpHeaderNames.IF_MODIFIED_SINCE);
        if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
            Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);

            long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
            long fileLastModifiedSeconds = file.lastModified() / 1000;
            if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
                sendNotModified(ctx);
                return;
            }
        }

        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException ignore) {
            sendError(ctx, NOT_FOUND);
            return;
        }
        long fileLength = raf.length();

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        HttpUtil.setContentLength(response, fileLength);
        setContentTypeHeader(response, suffix);
        setDateAndCacheHeaders(response, file);
        if (HttpUtil.isKeepAlive(request)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        ctx.write(response);

        ChannelFuture lastContentFuture;
        if (ctx.pipeline().get(SslHandler.class) == null) {
            ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
            lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        } else {
            lastContentFuture = ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192)), ctx.newProgressivePromise());
        }

        if (!HttpUtil.isKeepAlive(request)) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void sendNotModified(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_MODIFIED);
        setDateHeader(response);

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void setDateHeader(FullHttpResponse response) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));
    }

    private void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.headers().set(HttpHeaderNames.EXPIRES, dateFormatter.format(time.getTime()));
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.headers().set(
                HttpHeaderNames.LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
    }

    private void setContentTypeHeader(HttpResponse response, String suffix) {
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, getMimeType(suffix));
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
