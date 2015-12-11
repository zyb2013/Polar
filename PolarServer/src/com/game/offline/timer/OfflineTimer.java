/**
 * 
 */
package com.game.offline.timer;

import java.util.Iterator;

import com.game.manager.ManagerPool;
import com.game.map.manager.MapManager;
import com.game.map.structs.Map;
import com.game.offline.manager.OffLineManager;
import com.game.player.structs.Player;
import com.game.timer.TimerEvent;
import com.game.utils.Global;

/**
 * @author luminghua
 * 
 * @date 2013年12月20日 下午3:36:08
 * 
 *       离线经验系统计算
 */
public class OfflineTimer extends TimerEvent {

	private int serverId;

	private int lineId;

	private int mapId;

	public OfflineTimer(int serverId, int lineId, int mapId) {
		super(-1, 1000);
		this.serverId = serverId;
		this.lineId = lineId;
		this.mapId = mapId;
	}

	public static boolean opengm = false;
	/* 
	 * @see com.game.command.ICommand#action()
	 */
	@Override
	public void action() {
		// 获取地图
		Map map = ManagerPool.mapManager.getMap(serverId, lineId, mapId);
		// 遍历玩家列表
		Iterator<Player> iter = map.getPlayers().values().iterator();
		while (iter.hasNext()) {
			Player player = iter.next();
			// 如果玩家当前不在主城安全区中，时间清零
			if ((mapId != Global.MAIN_CITY_MAP_ID || !MapManager.getInstance().isSafe(player.getPosition(), mapId)) && !opengm) {
				if (player.getEnterMainCtiyTime() != 0)
					player.setEnterMainCtiyTime(0);
			} else {
				long currentTimeMillis = System.currentTimeMillis();
				if (player.getEnterMainCtiyTime() == 0) {
					// 开始计时
					player.setEnterMainCtiyTime(currentTimeMillis);
				} else {
					if (currentTimeMillis < player.getEnterMainCtiyTime()) {
						player.setEnterMainCtiyTime(currentTimeMillis);
					} else {
						// 每12秒增加一点追加值
						int time = (int) ((currentTimeMillis - player.getEnterMainCtiyTime()) / 1000);
						if (time >= 12) {
							player.setEnterMainCtiyTime(player.getEnterMainCtiyTime() + 12 * 1000l);
							int alterOfflineCount = OffLineManager.getInstance().alterOfflineCount(player, 1);
							if (alterOfflineCount < OffLineManager.MAX_OFFLINE_COUNT) {
								OffLineManager.getInstance().sendRetreatInfoMessage(player, 1);
							}
						}
					}

				}
			}
		}
	}

}
