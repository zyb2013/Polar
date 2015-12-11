package com.game.server.config;

import com.game.languageres.manager.ResManager;

/**
 * 服务器对应国家名
 * 
 */
public class ServerName {
	/**
	 * 根据ID获得国家名称
	 * 
	 * @param server
	 * @return
	 */
	public static String getName(int server) {
		switch (server) {
		case 0:
			return ResManager.getInstance().getString("中立区");
		case 1:
			return ResManager.getInstance().getString("秦国");
		case 2:
			return ResManager.getInstance().getString("齐国");
		case 3:
			return ResManager.getInstance().getString("楚国");
		case 4:
			return ResManager.getInstance().getString("赵国");
		case 5:
			return ResManager.getInstance().getString("魏国");
		case 6:
			return ResManager.getInstance().getString("韩国");
		case 7:
			return ResManager.getInstance().getString("燕国");
		}
		return "";
	}

}
