//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipUtils {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ZipUtils.class);
	
	public static boolean compress(String srcFilePath, String destFilePath) {
		File src = new File(srcFilePath);
		if (!src.exists()) {
			throw new RuntimeException(srcFilePath + "not exist");
		}
		File zipFile = new File(destFilePath);
		try {
			FileOutputStream fos = new FileOutputStream(zipFile);
			CheckedOutputStream cos = new CheckedOutputStream(fos, new CRC32());
			ZipOutputStream zos = new ZipOutputStream(cos);
			String baseDir = "";
			compressbyType(src, zos, baseDir);
			zos.close();
			return true;
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return false;
	}

	private static void compressbyType(File src, ZipOutputStream zos, String baseDir) {
		if (!src.exists()) {
			return;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("压缩 {}{}", baseDir, src.getName());
		}
		if (src.isFile()) {
			compressFile(src, zos, baseDir);
		} else if (src.isDirectory()) {
			compressDir(src, zos, baseDir);
		}
	}

	/**
	 * 压缩文件
	 *
	 */
	private static void compressFile(File file, ZipOutputStream zos, String baseDir) {
		if (!file.exists()) {
			return;
		}
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			ZipEntry entry = new ZipEntry(baseDir + file.getName());
			entry.setTime(file.lastModified());
			zos.putNextEntry(entry);
			int count;
			byte[] buf = new byte[8192];
			while ((count = bis.read(buf)) != -1) {
				zos.write(buf, 0, count);
			}
			bis.close();
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	/**
	 * 压缩文件夹
	 *
	 */
	private static void compressDir(File dir, ZipOutputStream zos, String baseDir) {
		if (!dir.exists()) {
			return;
		}
		File[] files = dir.listFiles();
		if (files.length == 0) {
			try {
				ZipEntry zipEntry = new ZipEntry(baseDir + dir.getName() + File.separator);
				zipEntry.setTime(dir.lastModified());
				zos.putNextEntry(zipEntry);
			} catch (IOException e) {
				LOGGER.error("", e);
			}
		}

		for (File file : files) {
			compressbyType(file, zos, baseDir + dir.getName() + File.separator);
		}
	}
}
