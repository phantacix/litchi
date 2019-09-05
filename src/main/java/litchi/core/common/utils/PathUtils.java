//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import litchi.core.common.utils.OsUtils.OS;

public class PathUtils {
	
	private static String ROOT_PATH;

	private static String PROJECT_ROOT_DIRECTORY = "";

	private static String CUSTOM_RESOURCE_PATH;

	public static String getResourcePath() {
		return CUSTOM_RESOURCE_PATH;
	}
	
	public static void setResourcePath(String path) {
		String absolutePath = FileUtils.getAbsolutePath(path);
		CUSTOM_RESOURCE_PATH = absolutePath;
	}
	
	public static String combine(String first, String... paths) {
		Path path = Paths.get(first, paths);
		return path.toString();
    }

	/**
	 * 获取项目根目录
	 * @return
	 */
	public static String getProjectDirectory() {
		if (StringUtils.isNotBlank(PROJECT_ROOT_DIRECTORY)) {
			return PROJECT_ROOT_DIRECTORY;
		}

		try {
			File directory = new File("");
			PROJECT_ROOT_DIRECTORY = directory.getAbsolutePath();
		} catch (Exception e) {
		}
		return PROJECT_ROOT_DIRECTORY;
	}

	/**
	 * 获取运行时根目录
	 *
	 * @return
	 */
	public static String getRootPath() {
		if (StringUtils.isBlank(ROOT_PATH)) {
			URL url = ClassLoader.getSystemResource("");
			if (url != null) {
				String path = url.getPath();
				path = path.substring(1, path.length() - 1);
				// path = path.substring(0, path.lastIndexOf("/") + 1);
				File file = new File(path);
				ROOT_PATH = file.getPath();
			}
		}
		return ROOT_PATH;
	}
	
	/**
     * 判断指定路径是否为相对路径
     * @param path
     * @return
     */
    public static boolean isRelativePath(String path) {
    	OS os = OsUtils.getOs();
    	switch (os) {
		case WINDOWS:
			if (path.indexOf(":") > -1) {
				return false;
			}
			return true;
		case LINUX:
			if (path.startsWith("/")) {
				return false;
			}
			return true;
		default:
			break;
		}
    	return false;
    }

	public static String getAbsolutelyPath(String rootPath, String path) {
		if (isRelativePath(path)) {
    		String p = PathUtils.combine(rootPath, path);
    		try {
				return new File(p).getCanonicalPath();
			} catch (IOException e) {
				return p;
			}
		}
		return path;
	}
	
	public static String endWithSeparator(String path) {
		if (path.endsWith("/") == false && path.endsWith("\\") == false) {
			path += File.separator;
		}
		return path;
	}
	
}
