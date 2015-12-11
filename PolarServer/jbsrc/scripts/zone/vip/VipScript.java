package scripts.zone.vip;


import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.game.data.bean.Q_clone_activityBean;
import com.game.fight.structs.Fighter;
import com.game.json.JSONserializable;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.map.script.IEnterMapScript;
import com.game.map.structs.Map;
import com.game.monster.script.IMonsterDieScript;
import com.game.monster.structs.Monster;
import com.game.player.message.ResScriptCommonPlayerToClientMessage;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.server.config.MapConfig;
import com.game.structs.Grid;
import com.game.task.struts.Task;
import com.game.task.struts.TaskEnum;
import com.game.utils.MapUtils;
import com.game.utils.MessageUtil;
import com.game.zones.manager.ZonesManager;
import com.game.zones.message.ResZoneLifeTimeMessage;
import com.game.zones.script.ICreateZoneScript;
import com.game.zones.structs.ZoneContext;
/**
 * 血色城堡 副本创建
 */
public class VipScript  implements ICreateZoneScript, IMonsterDieScript, IEnterMapScript {
	
	
	
	//副本已经杀怪数量
	private int ZONEID = 3007;	
	private int ZONEIDMAX = 3009;	
	
	
	
	private int[] BOSS = {39,26};
	
	
	
	
	@Override
	public int getId() {
		return 51001;
	}

	/**
	 * 副本创建
	 */
	@Override
	public ZoneContext onCreate(Player player, int zoneId) {
		if (zoneId >= ZONEID && zoneId<= ZONEIDMAX) {
			
			
			Q_clone_activityBean zoneBean = ManagerPool.dataManager.q_clone_activityContainer.getMap().get(zoneId);
			
			List<Integer> mapidlist = JSON.parseArray(zoneBean.getQ_mapid(),Integer.class);
			//创建副本
			HashMap<String, Object> others = new HashMap<String, Object>();
//			ArrayList<Integer> maplist = new ArrayList<Integer>();
//			for (int i = 0; i < 1; i++) {
//				maplist.add(mapidlist.get(0));
//			}
			ZoneContext zone = ManagerPool.zonesManager.setZone("VIP副本"+zoneId%1000, others, mapidlist, zoneId);	//创建副本，返回副本消息
			MapConfig config = zone.getConfigs().get(0);
			Map zoneMap = ManagerPool.mapManager.getMap(config.getServerId(), config.getLineId(), config.getMapId());
			zone.getOthers().put("time", (int)(System.currentTimeMillis() / 1000));
			zone.getOthers().put("kill", 0);
			zone.getOthers().put("zoneprocess", 1);
			zone.getOthers().put("scriptId", getId());
			zoneMap.setRound(Math.max(zoneMap.getWidth(), zoneMap.getHeight()) * 2 + 1);
			
			
			Grid grid = MapUtils.getGrid(BOSS[0], BOSS[1], config.getMapModelId());
			 int monsterId = Integer.parseInt("2"+(zoneId*10000+player.getLevel()));
			Monster gcmonster = ManagerPool.monsterManager.createMonster(monsterId, config.getServerId(), config.getLineId(), config.getMapId(), grid.getCenter());
			gcmonster.setDirection((byte)2);
			ManagerPool.mapManager.enterMap(gcmonster);
			
			return zone;
		}
		return null;
	}


	@Override
	public void onMonsterDie(Monster monster, Fighter killer) {
		
		
		
	}

	@Override
	public void onEnterMap(Player player, Map map) {
		
		
	}
	
}
