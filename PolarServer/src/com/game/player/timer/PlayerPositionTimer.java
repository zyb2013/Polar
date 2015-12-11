package com.game.player.timer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.game.backpack.manager.BackpackManager;
import com.game.manager.ManagerPool;
import com.game.map.message.ResSynPlayerPositionMessage;
import com.game.map.structs.Map;
import com.game.monster.timer.MonsterAiTimer;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.AttributeChangeReason;
import com.game.player.structs.Player;
import com.game.structs.Reasons;
import com.game.timer.TimerEvent;
import com.game.utils.MessageUtil;
import com.game.zones.message.ReqZoneTeamEnterToGameMessage;

//测试专用，前后台位置同步
public class PlayerPositionTimer extends TimerEvent {

	protected Logger log = Logger.getLogger(MonsterAiTimer.class);
	
	private int serverId;
	
	private int lineId;
	
	private int mapId;
	
	public PlayerPositionTimer(int serverId, int lineId, int mapId){
		super(-1, 1000);
		this.serverId = serverId;
		this.lineId = lineId;
		this.mapId = mapId;
	}
	
	@Override
	public void action() {
		//获取地图
		Map map = ManagerPool.mapManager.getMap(serverId, lineId, mapId);
		//遍历玩家列表
		
//		List<Player> list = new ArrayList<Player>();
		
		Iterator<Player> iter = map.getPlayers().values().iterator();
		while (iter.hasNext()) {
			Player player = (Player) iter.next();
			if(player==null) continue;
//			list.add(player);
//			
//		
//			
			//不是本线玩家
			if(player.getServerId()!=this.serverId || player.getLine()!=this.lineId || player.getMap()!=this.mapId) continue;
			if(!PlayerManager.getSyncPosition().contains(player.getId())) continue;
			
			//log.error("玩家坐标：" + player.getPosition());
			ResSynPlayerPositionMessage msg = new ResSynPlayerPositionMessage();
			msg.setPosition(player.getPosition());
			MessageUtil.tell_player_message(player, msg);
		}
		
//		for (int i =0 ; i<list.size();i++) {
//			if(i<10){
//			Player player =list.get(i);
//			if(player.getSex() == 9){
//				if(map.getMapModelid()!=300001){
//					
//					if(player.getLevel()<100){
//					player.getAttibute_one_base()[0] = 9999;
//					player.getAttibute_one_base()[1] = 9999;
//					player.getAttibute_one_base()[2] = 9999;
//					player.getAttibute_one_base()[3] = 9999;
//					player.getAttibute_one_base()[4] = 9999;
//					PlayerManager.getInstance().addExp(player,9999999,AttributeChangeReason.GM);
//					}
//					
//	            	
//	            	ReqZoneTeamEnterToGameMessage cmsg = new ReqZoneTeamEnterToGameMessage();
//					cmsg.setEntertype((byte)0);
//					cmsg.setZoneid(1);
//					ManagerPool.zonesTeamManager.stReqZoneTeamEnterToGameMessage(player,cmsg);
//	            	
//				}
//			}
//			}
//		}
		
	}
}
