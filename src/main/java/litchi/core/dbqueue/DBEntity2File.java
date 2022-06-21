//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dbqueue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import litchi.core.common.utils.ServerTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import litchi.core.common.utils.StringUtils;
import litchi.core.jdbc.table.Table;

/**
 *
 * @author
 */
public class DBEntity2File {
	private static final Logger LOGGER = LoggerFactory.getLogger(DBEntity2File.class);

	/** 备份路径 */
	private String backupPath;
	/** 文件后缀名 */
	private String backupExtension;

	public DBEntity2File() {
		this.backupPath = "backup" + File.separator;
		this.backupExtension = ".data";
	}

	public DBEntity2File(String backupPath, String backupExtension) {
		this.backupPath = backupPath;
		this.backupExtension = backupExtension;
	}
	
	/**
	 * 写入备份
	 * @param table
	 * @param fileName
	 */
	public void write(Table<?> table, String fileName) {
		if (table == null || StringUtils.isBlank(fileName)) {
			return;
		}
		URL resource = getClass().getClassLoader().getResource(backupPath);
		if (resource == null) {
			resource = checkFolderExist();
		}
		
		// 每张表，按天写一个文件
		String filePath = resource.getPath();
		StringBuffer sb = new StringBuffer();
		sb.append(filePath);
		sb.append(fileName);
		sb.append("-");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		sb.append(simpleDateFormat.format(new Date(ServerTime.timeMillis())));
		sb.append(backupExtension);

		FileWriter fw = null;
		PrintWriter out = null;
		try {
			fw = new FileWriter(sb.toString(), true);
			out = new PrintWriter(fw);
			//System.out.println(game.toString());
			out.print(table.toString());
		} catch (Exception e) {
			LOGGER.error("{}", e);
		} finally {
			if (out != null) {
				out.close();
			}

			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					LOGGER.error("{}", e);
				}
			}
		}
	}

	private URL checkFolderExist() {
		URL url = ClassLoader.getSystemResource("");
		File dir = new File(url.getPath() + backupPath);
		if (!dir.exists() && !dir.isDirectory()) {// 判断文件目录是否存在
			boolean isSuccess = dir.mkdirs();
			if (isSuccess) {
				LOGGER.info("create backup folder success...");
			} else {
				LOGGER.warn("create backup folder fail");
			}
		}
		return url;
	}
}
