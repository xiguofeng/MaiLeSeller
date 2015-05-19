package com.o2o.maileseller.util.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import android.os.Environment;

public class FileUtil {
	public static final String APP_PATH = "/o2oseller/";

	public static final String DIR_LOG_PATH = "/o2oseller/log/";

	public static final String LOG_PUSH_NAME = "push";

	public static String getSDPath() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return "";

	}

	public static void createLogDir() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + DIR_LOG_PATH;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
	}

	public static void write(String info) {
		try {
			File file = new File(getSDPath() + DIR_LOG_PATH, LOG_PUSH_NAME);
			// 第二个参数意义是说是否以append方式添加内容
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			bw.write(info);
			bw.write("\r\n");
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getFileSuffix(String pathName) {
		String suffix = "";

		if (null != pathName) {
			int lastIndexOf = pathName.lastIndexOf(".");
			if (-1 != lastIndexOf) {
				suffix = pathName.substring(lastIndexOf);
			}
		}

		return suffix;
	}

	public static String getFileName(String path) {
		File file = new File(path);
		return file.getName();
	}

}
