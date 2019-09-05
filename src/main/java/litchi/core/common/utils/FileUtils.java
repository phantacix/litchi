//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {
    private static Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);
    
    public interface FileLineReader {

    	/**
    	 * 
    	 * @param builder
    	 * @param line
    	 * @return	是否继续读取下一行数据
    	 */
        boolean readLine(StringBuilder builder, String line);
    }

    public static String combine(String first, String... paths) {
        return Paths.get(first, paths).toString();
    }
    
    public static String readFile(String first, String... paths) {
        return readFile(new File(combine(first, paths)));
    }

    public static String readFile(File file) {
        return readFile(file, (builder, line) -> {
        	builder.append(line).append("\n");
        	return true;
        });
    }

    public static String readFile(File file, FileLineReader lineReader) {
        if (!file.exists()) {
            LOGGER.warn("file path:{} not exists.", file.getPath());
            return null;
        }

        StringBuilder builder = new StringBuilder();
        FileInputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader reader = null;
        try {
            inputStream = new FileInputStream(file);
            inputReader = new InputStreamReader(inputStream, "utf-8");
            reader = new BufferedReader(inputReader);
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                boolean next = lineReader.readLine(builder, line);
                if (next == false) {
					break;
				}
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
            if (inputReader != null) {
                try {
                    inputReader.close();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        }
        return builder.toString();
    }

    public static void writeFile(File file, byte[] data) {
        writeFile(file, new String(data));
    }

    public static void writeFile(File file, String str) {
    	writeFile(file, str, false);
    }
    
    public static void writeFile(File file, String str, boolean append) {
        FileOutputStream fileStream = null;
        OutputStreamWriter oswriter = null;
        BufferedWriter writer = null;
        try {
            fileStream = new FileOutputStream(file, append);
            oswriter = new OutputStreamWriter(fileStream, "UTF-8");
            writer = new BufferedWriter(oswriter);
            writer.write(str);
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
            if (oswriter != null) {
                try {
                    oswriter.close();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        }
    }


	public static byte[] readFileToArray(File file) {
		if (!file.exists()) {
            LOGGER.warn("file path:{} not exists.", file.getPath());
            return null;
        }

        FileInputStream inputStream = null;
        ByteArrayOutputStream byteOutputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byteOutputStream = new ByteArrayOutputStream();
            while (true) {
            	byte[] buf = new byte[512];
            	int readLen = inputStream.read(buf);
            	if (readLen < 1) {
					break;
				}
            	byteOutputStream.write(buf, 0, readLen);
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
            if (byteOutputStream != null) {
                try {
                    byteOutputStream.close();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        }
        if (byteOutputStream != null) {
			return byteOutputStream.toByteArray();
		}
        return null;
	}
	
	public static void writeFileFromArray(File file, byte[] bytes) {
        FileOutputStream out = null;
        try {
        	out = new FileOutputStream(file);
            out.write(bytes, 0, bytes.length);
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            if (out != null) {
                try {
                	out.close();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        }
	}

	public static String getAbsolutePath(String fileName) {
		File file = new File(fileName);
		URI uri = file.toURI();
		String path = uri.getPath();
		String[] split = path.split("/");
		StringBuilder builder = new StringBuilder();
		int back = 0;
		for (int i = split.length - 1; i >= 0; i--) {
			String p = split[i];
			if (p == null || p.equals("")) {
				continue;
			}
			if (p.equals("..")) {
				back++;
				continue;
			}
			if (back > 0) {
				back--;
				continue;
			}
			builder.insert(0, p).insert(0, "/");
		}
		if (builder.length() > 0) {
			file = new File(builder.toString());
			return file.getPath();
		}
		return fileName;
	}

	public static void getFiles(File file, List<File> files) {
		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			for (File f : listFiles) {
				getFiles(f, files);
			}
			return;
		}
		files.add(file);
	}
}
