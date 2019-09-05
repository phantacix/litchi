//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.http.server.router;

/**
 * from: https://github.com/sinetja/netty-router
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;

public class Router<T> {
	private final Map<HttpMethod, PatternRouter<T>> routers = new HashMap<>();

	public int size() {
		int ret = 0;
		for (PatternRouter<T> router : routers.values()) {
			ret += router.routes().size();
		}

		return ret;
	}

	public Router<T> addRoute(HttpMethod method, String pathPattern, T target) {
		getMethodlessRouter(method).addRoute(pathPattern, target);
		return this;
	}

	private PatternRouter<T> getMethodlessRouter(HttpMethod method) {
		PatternRouter<T> r = routers.get(method);
		if (r == null) {
			r = new PatternRouter<T>();
			routers.put(method, r);
		}

		return r;
	}

	public RouteResult<T> route(HttpMethod method, String uri) {
		PatternRouter<T> router = routers.get(method);
		if (router == null) {
			return null;
		}

		QueryStringDecoder decoder = new QueryStringDecoder(uri);
		String[] tokens = decodePathTokens(uri);

		RouteResult<T> ret = router.route(uri, decoder.path(), tokens);
		if (ret != null) {
			return new RouteResult<T>(uri, decoder.path(), ret.pathParams(), decoder.parameters(), ret.target());
		}

		return null;
	}

	private String[] decodePathTokens(String uri) {
		// Need to split the original URI (instead of QueryStringDecoder#path)
		// then decode the tokens (components),
		// otherwise /test1/123%2F456 will not match /test1/:p1

		int qPos = uri.indexOf("?");
		String encodedPath = (qPos >= 0) ? uri.substring(0, qPos) : uri;

		String[] encodedTokens = PathPattern.removeSlashesAtBothEnds(encodedPath).split("/");

		String[] decodedTokens = new String[encodedTokens.length];
		for (int i = 0; i < encodedTokens.length; i++) {
			String encodedToken = encodedTokens[i];
			decodedTokens[i] = QueryStringDecoder.decodeComponent(encodedToken);
		}

		return decodedTokens;
	}

	public Set<HttpMethod> allowedMethods(String uri) {
		QueryStringDecoder decoder = new QueryStringDecoder(uri);
		String[] tokens = PathPattern.removeSlashesAtBothEnds(decoder.path()).split("/");

		Set<HttpMethod> ret = new HashSet<HttpMethod>(routers.size());
		for (Map.Entry<HttpMethod, PatternRouter<T>> entry : routers.entrySet()) {
			PatternRouter<T> router = entry.getValue();
			if (router.anyMatched(tokens)) {
				HttpMethod method = entry.getKey();
				ret.add(method);
			}
		}

		return ret;
	}

	@Override
	public String toString() {
		// Step 1/2: Dump routers and anyMethodRouter in order
		int numRoutes = size();
		List<String> methods = new ArrayList<>(numRoutes);
		List<String> patterns = new ArrayList<>(numRoutes);
		List<String> targets = new ArrayList<>(numRoutes);

		// For router
		for (Entry<HttpMethod, PatternRouter<T>> e : routers.entrySet()) {
			HttpMethod method = e.getKey();
			PatternRouter<T> router = e.getValue();
			aggregateRoutes(method.toString(), router.routes(), methods, patterns, targets);
		}

		// Step 2/2: Format the List into aligned columns: <method> <patterns>
		// <target>
		int maxLengthMethod = maxLength(methods);
		int maxLengthPattern = maxLength(patterns);
		String format = "%-" + maxLengthMethod + "s  %-" + maxLengthPattern + "s  %s\n";
		int initialCapacity = (maxLengthMethod + 1 + maxLengthPattern + 1 + 20) * methods.size();
		StringBuilder b = new StringBuilder(initialCapacity);
		for (int i = 0; i < methods.size(); i++) {
			String method = methods.get(i);
			String pattern = patterns.get(i);
			String target = targets.get(i);
			b.append(String.format(format, method, pattern, target));
		}
		return b.toString();
	}

	/**
	 * Helper for toString.
	 */
	private static <T> void aggregateRoutes(String method, Map<PathPattern, T> routes, List<String> accMethods, List<String> accPatterns,
			List<String> accTargets) {
		for (Map.Entry<PathPattern, T> entry : routes.entrySet()) {
			accMethods.add(method);
			accPatterns.add("/" + entry.getKey().pattern());
			accTargets.add(targetToString(entry.getValue()));
		}
	}

	/**
	 * Helper for toString.
	 */
	private static int maxLength(List<String> coll) {
		int max = 0;
		for (String e : coll) {
			int length = e.length();
			if (length > max) {
				max = length;
			}
		}
		return max;
	}

	/**
	 * Helper for toString.
	 *
	 * <p>For example, returns
	 * "io.netty.example.http.router.HttpRouterServerHandler" instead of
	 * "class io.netty.example.http.router.HttpRouterServerHandler"
	 */
	private static String targetToString(Object target) {
		if (target instanceof Class) {
			return ((Class<?>) target).getName();
		} else {
			return target.toString();
		}
	}

	// --------------------------------------------------------------------------
	public Router<T> GET(String path, T target) {
		return addRoute(HttpMethod.GET, path, target);
	}

	public Router<T> POST(String path, T target) {
		return addRoute(HttpMethod.POST, path, target);
	}
	// --------------------------------------------------------------------------
}
