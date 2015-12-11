package com.game.manager;

import com.game.dblog.LogService;
import com.game.login.manager.IpBlacklistManager;
import com.game.player.manager.PlayerManager;

/** 
 * 
 * 类说明 
 */
public class ManagerPool {
	//玩家管理类
	public static PlayerManager playerManager = PlayerManager.getInstance();
	public static LogService logservice=LogService.getInstance();

    // 封禁ip管理
    public static IpBlacklistManager blacklistManager = IpBlacklistManager.getInstance();
}
