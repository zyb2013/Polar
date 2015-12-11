package com.game.utils;

import java.io.File;

/**
 * 文件工具
 * 
 */
public class FileUtil {
	/**
	 * 判断文件是否存在
	 * 
	 * @param name
	 *            文件名
	 * @return
	 */
	public static boolean isExists(String name) {
		File file = new File(name);
		return file.exists();
	}
}
