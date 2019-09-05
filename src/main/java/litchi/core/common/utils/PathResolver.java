//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.jar.JarEntry;

/**
 * author : gcoder
 */
public final class PathResolver {
	
	private static Logger LOGGER = LoggerFactory.getLogger(PathResolver.class);

    /**
     * 获取指定包内继承自father并且含有注解annotationClass
     *
     * @param pkgPath
     * @param annotationClass
     * @param father
     * @param <F>
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    @SuppressWarnings("unchecked")
	public static final <F> Collection<Class<? extends F>> scanPkg(Class<? extends Annotation> annotationClass, Class<F> father, String... pkgPath) {
        List<Class<? extends F>> result = new ArrayList<>();
        Collection<Class<?>> classes = scanPkg(pkgPath);

        classes.stream().filter(clazz -> father.isAssignableFrom(clazz) && clazz.isAnnotationPresent(annotationClass))
                .forEach(t -> result.add((Class<? extends F>) t));
        return result;
    }

    /**
     * 获取指定包内所有含有指定注解的类
     *
     * @param pkgPath
     * @param annotationClass
     * @return
     */
    public static final Collection<Class<?>> scanPkgWithAnnotation(Class<? extends Annotation> annotationClass, String... pkgPath) {
        List<Class<?>> result = new ArrayList<>();
        Collection<Class<?>> classes = scanPkg(pkgPath);
        classes.stream().filter(clazz -> clazz.isAnnotationPresent(annotationClass))
                .forEach(result::add);
        return result;
    }

    /**
     * 获取指定包内所有father的子类
     *
     * @param pkgPath
     * @param father
     * @param <F>
     * @return
     */
    @SuppressWarnings("unchecked")
	public static final <F> Collection<Class<? extends F>> scanPkgWithFather(Class<F> father, String... pkgPath) {
        List<Class<? extends F>> result = new ArrayList<>();

        for (String p : pkgPath) {
            Collection<Class<?>> classes = scanPkg(p);
            classes.stream().filter(clazz -> father.isAssignableFrom(clazz))
                    .forEach(t -> result.add((Class<? extends F>) t));
        }

        return result;
    }

    private static Map<String, Collection<Class<?>>> CACHE_CLAZZ = new HashMap<>();

    public static Collection<Class<?>> scanPkg(String... packagePath) {
        List<Class<?>> result = new ArrayList<>();

        for (String s : packagePath) {
            Collection<Class<?>> cache = CACHE_CLAZZ.get(s);
            if (cache != null) {
                result.addAll(cache);
                continue;
            }

            Collection<Class<?>> scanClazz = scanClazz(s);
            result.addAll(scanClazz);
            CACHE_CLAZZ.put(s, scanClazz);
        }
        return result;
    }

    private static Collection<Class<?>> scanClazz(String s) {
        List<Class<?>> result = new ArrayList<>();
        String path = s.replace('.', '/');
        try {
            Enumeration<URL> resources = ClassLoader.getSystemResources(path);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                switch (url.getProtocol()) {
                    case "file":
                        result.addAll(fileClassScan(Paths.get(url.toURI()), s));
                        break;
                    case "jar":
                        result.addAll(jarClassScan(url, s));
                        break;
                }
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        } catch (URISyntaxException e) {
            LOGGER.error("", e);
        }
        return result;
    }


    public static Collection<Class<?>> jarClassScan(URL url, String pkgName) throws IOException {
        List<Class<?>> result = new ArrayList<>();
        String path = pkgName.replace('.', '/');
        JarURLConnection conn = (JarURLConnection) url.openConnection();
        Enumeration<JarEntry> entries = conn.getJarFile().entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.startsWith("/")) {
                name = name.substring(1);
            }
            if (name.contains(path) && !entry.isDirectory()) {
                name = name.substring(0, name.lastIndexOf(".class")).replace('/', '.');
                try {
                    Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(name);
                    result.add(clazz);
                } catch (ClassNotFoundException e) {
                    LOGGER.error("", e);
                }
            }
        }
        return result;
    }

    public static Collection<Class<?>> fileClassScan(Path p, String pkgName) throws IOException {
        ConcurrentLinkedQueue<Class<?>> result = new ConcurrentLinkedQueue<>();
        Files.list(p).forEach((Path path) -> {
                    String fileName = path.toFile().getName();
                    if (Files.isDirectory(path)) {
                        try {
                            result.addAll(fileClassScan(path, pkgName + "." + fileName));
                        } catch (IOException e) {
                        	LOGGER.error("", e);
                        }
                    } else {
                        if (fileName.endsWith(".class")) {
                            String className = (pkgName + "." + fileName.substring(0, fileName.lastIndexOf(".class")));
                            try {
                                Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(className);
                                result.offer(clazz);
                            } catch (NoClassDefFoundError ex) {

                            }
                            catch (ClassNotFoundException ex) {

                            } catch (Exception e) {
                            	LOGGER.error("", e);
                            }
                        }
                    }
                }
        );
        return result;
    }

    public static  <T> T scanAndNewInstance(String[] basePackages, String className, Class<T> tClass) {
		Collection<Class<? extends T>> classCollection = scanPkgWithFather(tClass, basePackages);
		if (classCollection.isEmpty()) {
			LOGGER.error("data parse:[{}] class not found.", className);
			return null;
		}

		T instance = null;
		for (Class<? extends T> clazz : classCollection) {
			if (clazz.getName().equals(className)) {
				try {
					instance = clazz.newInstance();
					break;
				} catch (Exception ex) {
					LOGGER.error("", ex);
				}
			}
		}
		return instance;
	}
}
