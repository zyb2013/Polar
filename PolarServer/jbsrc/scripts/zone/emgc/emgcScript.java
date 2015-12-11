package scripts.zone.emgc;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.game.data.bean.Q_clone_activityBean;
import com.game.fight.structs.Fighter;
import com.game.json.JSONserializable;
import com.game.manager.ManagerPool;
import com.game.map.script.IEnterMapScript;
import com.game.map.structs.Map;
import com.game.monster.script.IMonsterDieScript;
import com.game.monster.structs.Monster;
import com.game.player.message.ResScriptCommonPlayerToClientMessage;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.server.config.MapConfig;
import com.game.task.struts.Task;
import com.game.task.struts.TaskEnum;
import com.game.utils.MessageUtil;
import com.game.zones.manager.ZonesManager;
import com.game.zones.script.ICreateZoneScript;
import com.game.zones.structs.ZoneContext;
/**
 * 恶魔广场 副本创建
 */
public class emgcScript  implements ICreateZoneScript, IMonsterDieScript, IEnterMapScript {
	
	
	
	//副本已经杀怪数量
	private static String KILL = "kill";
	
	@Override
	public int getId() {
		return 55001;
	}

	/**
	 * 副本创建
	 */
	@Override
	public ZoneContext onCreate(Player player, int zoneId) {
		if (zoneId <= 8) {
			Q_clone_activityBean zonedata = ManagerPool.dataManager.q_clone_activityContainer.getMap().get(zoneId);
			//创建副本
			HashMap<String, Object> others = new HashMap<String, Object>();
			List<Integer> maplist = JSON.parseArray(zonedata.getQ_mapid(),Integer.class);
			
			ZoneContext zone = ManagerPool.zonesManager.setZone("恶魔广场", others, maplist, zoneId);	//创建副本，返回副本消息
			MapConfig config = zone.getConfigs().get(0);
			Map zoneMap = ManagerPool.mapManager.getMap(config.getServerId(), config.getLineId(), config.getMapId());
			
			zone.getOthers().put("time", (int)(System.currentTimeMillis() / 1000));
			zone.getOthers().put("kill", 0);
		
			zone.getOthers().put("scriptId", getId());
			zone.getOthers().put("zoneprocess", 1);
			
			zoneMap.setRound(Math.max(zoneMap.getWidth(), zoneMap.getHeight()) * 2 + 1);
			
			return zone;
		}
		return null;
	}


	@Override
	public void onMonsterDie(Monster monster, Fighter killer) {
		Map map = ManagerPool.mapManager.getMap(monster);
		
		if(map==null || (map.getMapModelid()<300001 || map.getMapModelid()>300007)){
			return;
		}
		ZoneContext zone = ManagerPool.zonesManager.getContexts().get(map.getZoneId());
		if(zone==null){
			return ;
		}
		if(zone.getOthers().get(KILL)==null){
				zone.getOthers().put(KILL, 0);
		}
		zone.getOthers().put(KILL, (Integer)zone.getOthers().get(KILL)+1);
		
		 for(Player player: map.getPlayers().values()	) {
				HashMap<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("zonemodelid", map.getZoneModelId());
				paramMap.put("kill", (Integer)zone.getOthers().get(KILL));
				paramMap.put("exp", (Long)zone.getOthers().get("exp"+player.getId()));
				
				ResScriptCommonPlayerToClientMessage sendMessage = new ResScriptCommonPlayerToClientMessage();
				sendMessage.setScriptid(getId());
				sendMessage.setType(1);
				sendMessage.setMessageData(JSONserializable.toString(paramMap));
//				System.out.println(sendMessage.getMessageData());
				
				MessageUtil.tell_player_message(player, sendMessage);
		 }
		
	
	}
	
	@Override
	public void onEnterMap(Player player, Map map) {
		if(map==null || (map.getMapModelid()<300001 || map.getMapModelid()>300007)){
			return;
		}
		ZoneContext zone = ManagerPool.zonesManager.getContexts().get(map.getZoneId());
		if(zone.getOthers().get("exp"+player.getId())==null){
			zone.getOthers().put("exp"+player.getId(), 0l);
		}
		ManagerPool.taskManager.action(player, Task.ACTION_TYPE_RANK, TaskEnum.ENTER_MISSION, 1);
		
		
		
	}
	
}
