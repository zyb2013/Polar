package scripts.zone.xscb;


import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.game.config.Config;
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
import com.game.structs.Reasons;
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
public class xscbScript  implements ICreateZoneScript, IMonsterDieScript, IEnterMapScript {
	
	
	
	//副本已经杀怪数量
	private static String KILL = "kill";
	private static String KILL2 = "kill2";
	private int ZONEID = 7001;	
	
	
//	//城门 的怪物ID
//	private int CM = 130021008;
//	//棺材 的怪物 ID
//	private int GUANGCAI = 130021007;
//	//天使的怪物 ID
//	private int ANGLE = 330021010;
	//狗男女 的怪物ID
//	private int G1 = 110010003;
//	private int G2 = 110010003;
	
	//城墙
	private int[] CQ = {155, 64};
	
	//棺材
	private int[] center = {195, 39};
	
//	//棺材
//		private int[] center

	private int[] zonemonsters = {130021006,130022006,130023006,130024006,130025006,130026006,130027006,130028006};
	
//  xxxxxxxxx
//	130021006
//	130022006
//	130023006
//	130024006
//	130025006
//	130026006
//	130027006
//	130028006
	
	@Override
	public int getId() {
		return 55002;
	}

	/**
	 * 副本创建
	 */
	@Override
	public ZoneContext onCreate(Player player, int zoneId) {
		if (zoneId >= ZONEID && zoneId<= 7008) {
			if(ZonesManager.getInstance().getContainer(zoneId).getQ_map_boss().split(",").length<3){
				return null;
			}
			
			Q_clone_activityBean zoneBean = ManagerPool.dataManager.q_clone_activityContainer.getMap().get(zoneId);
			
			List<Integer> mapidlist = JSON.parseArray(zoneBean.getQ_mapid(),Integer.class);
			//创建副本
			HashMap<String, Object> others = new HashMap<String, Object>();
//			ArrayList<Integer> maplist = new ArrayList<Integer>();
//			for (int i = 0; i < 1; i++) {
//				maplist.add(mapidlist.get(0));
//			}
			ZoneContext zone = ManagerPool.zonesManager.setZone("血色城堡", others, mapidlist, zoneId);	//创建副本，返回副本消息
			MapConfig config = zone.getConfigs().get(0);
			Map zoneMap = ManagerPool.mapManager.getMap(config.getServerId(), config.getLineId(), config.getMapId());
			zone.getOthers().put("time", (int)(System.currentTimeMillis() / 1000));
			zone.getOthers().put("kill", 0);
			zone.getOthers().put("kill2", 0);
			zone.getOthers().put("zoneprocess", 1);
			int index = zoneBean.getQ_id() % 7000;
			zone.getOthers().put("zoneIndex",index);
			zone.getOthers().put("scriptId", getId());
			zoneMap.setRound(Math.max(zoneMap.getWidth(), zoneMap.getHeight()) * 2 + 1);
			return zone;
		}
		return null;
	}


	@Override
	public void onMonsterDie(Monster monster, Fighter killer) {
		Map map = ManagerPool.mapManager.getMap(monster);
		Q_clone_activityBean zoneBean = ManagerPool.dataManager.q_clone_activityContainer.getMap().get(map.getZoneModelId());
		
		if(map==null ||map.getMapModelid()< 400001 || map.getMapModelid()>400008){
			return;
		}
		ZoneContext zone = ManagerPool.zonesManager.getContexts().get(map.getZoneId());
			
	
		String monsters[] = zoneBean.getQ_map_boss().split(",");
//		//城门 的怪物ID
		int CM = Integer.parseInt(monsters[0]);
//		//棺材 的怪物 ID
		int GUANGCAI = Integer.parseInt(monsters[1]);
//		//天使的怪物 ID
		int ANGLE = Integer.parseInt(monsters[2]);
		if(zone.getOthers().get(KILL)==null){
			zone.getOthers().put(KILL, 0);
		}

		
		//仅阶段1 处理
		if((Integer)zone.getOthers().get("zoneprocess")==1  ){

			if(((Integer)zone.getOthers().get(KILL)+1)>=(Integer)zone.getOthers().get("playercount")*40 ){

				MapConfig config = zone.getConfigs().get(0);
				Grid grid = MapUtils.getGrid(CQ[0], CQ[1], config.getMapModelId());
				Monster gcmonster = ManagerPool.monsterManager.createMonster(CM, config.getServerId(), config.getLineId(), config.getMapId(), grid.getCenter());
				gcmonster.setDirection((byte)2);
				ManagerPool.mapManager.enterMap(gcmonster);
				
				MessageUtil.notify_map(map, Notifys.CUTOUT, String.format(ResManager.getInstance().getString("大天使:外围的罪恶正在减弱，请尽快攻破城门"), gcmonster.getName()));
				zone.getOthers().put("zoneprocess", 2);
				ResZoneLifeTimeMessage timemsg = new ResZoneLifeTimeMessage();
				timemsg.setSurplustime((zoneBean.getQ_exist_time()/1000)- ((int) System.currentTimeMillis()/1000 - (Integer)zone.getOthers().get("time")));
				timemsg.setZoneid(map.getZoneModelId());
				timemsg.setZoneprocess(2);
				timemsg.setPlayerCount((Integer)zone.getOthers().get("playercount"));
				zone.getOthers().put(KILL, 0);
				MessageUtil.tell_map_message(map, timemsg);
				
				//创建城门   切换至 阶段 2
			}else{
			zone.getOthers().put(KILL, (Integer)zone.getOthers().get(KILL)+1);
			HashMap<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("zonemodelid", ZONEID);
			paramMap.put("kill", (Integer)zone.getOthers().get(KILL));
			paramMap.put("max", (Integer)zone.getOthers().get("playercount")*40);
			
			ResScriptCommonPlayerToClientMessage sendMessage = new ResScriptCommonPlayerToClientMessage();
			sendMessage.setScriptid(getId());
			sendMessage.setType(1);
			sendMessage.setMessageData(JSONserializable.toString(paramMap));
			MessageUtil.tell_map_message(map, sendMessage);
			}
		}
		
	
		//  阶段  2    城门破了 开始杀杂鱼
		if(monster.getModelId()==CM){
			zone.getOthers().put("zoneprocess", 3);
			ResZoneLifeTimeMessage timemsg = new ResZoneLifeTimeMessage();
			timemsg.setSurplustime((zoneBean.getQ_exist_time()/1000)- ((int) System.currentTimeMillis()/1000 - (Integer)zone.getOthers().get("time")));
			timemsg.setZoneid(map.getZoneModelId());
			timemsg.setZoneprocess(3);
			zone.getOthers().put(KILL, 0);
			timemsg.setPlayerCount((Integer)zone.getOthers().get("playercount"));
			MessageUtil.tell_map_message(map, timemsg);
			MessageUtil.notify_map(map, Notifys.CUTOUT, String.format(ResManager.getInstance().getString("大天使:请消灭{1}只骷灵巫师 ")),(Integer)zone.getOthers().get("playercount")*10+"" );
			
		}
		
		//3 阶段开始 计算杂鱼
		if((Integer)zone.getOthers().get("zoneprocess")==3){
			
			if(((Integer)zone.getOthers().get(KILL2)+1)>=(Integer)zone.getOthers().get("playercount")*10 ){
				//刷新棺材
				MapConfig config = zone.getConfigs().get(0);
				Grid grid = MapUtils.getGrid(center[0], center[1], config.getMapModelId());
				Monster gcmonster = ManagerPool.monsterManager.createMonster(GUANGCAI, config.getServerId(), config.getLineId(), config.getMapId(), grid.getCenter());
				gcmonster.setDirection((byte)2);
				ManagerPool.mapManager.enterMap(gcmonster);
				
				MessageUtil.notify_map(map, Notifys.CUTOUT, String.format(ResManager.getInstance().getString("大天使:巫师力量已被消弱,请速去攻击 水晶灵枢")));
				zone.getOthers().put("zoneprocess", 4);
				
				ResZoneLifeTimeMessage timemsg = new ResZoneLifeTimeMessage();
				timemsg.setSurplustime((zoneBean.getQ_exist_time()/1000)- ((int) System.currentTimeMillis()/1000 - (Integer)zone.getOthers().get("time")));
				timemsg.setZoneid(map.getZoneModelId());
				timemsg.setZoneprocess(4);
				timemsg.setPlayerCount((Integer)zone.getOthers().get("playercount"));
				MessageUtil.tell_map_message(map, timemsg);
				
			}else{
			
				
				if(monster.getModelId() == zonemonsters[(Integer)zone.getOthers().get("zoneIndex")-1]){
					//骷灵巫师
					zone.getOthers().put(KILL2, (Integer)zone.getOthers().get(KILL2)+1);
					HashMap<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("zonemodelid", ZONEID);
					paramMap.put("kill", (Integer)zone.getOthers().get(KILL2));
					paramMap.put("max", (Integer)zone.getOthers().get("playercount")*10);
					ResScriptCommonPlayerToClientMessage sendMessage = new ResScriptCommonPlayerToClientMessage();
					sendMessage.setScriptid(getId());
					sendMessage.setType(1);
					sendMessage.setMessageData(JSONserializable.toString(paramMap));
					MessageUtil.tell_map_message(map, sendMessage);
				}
			}
		}
		
		
		
//		if((Integer)zone.getOthers().get("zoneprocess")==1 && (Integer)zone.getOthers().get(KILL)>=(Integer)zone.getOthers().get("playercount")*40 ){
//
//			//刷新棺材
//			MapConfig config = zone.getConfigs().get(0);
//			Grid grid = MapUtils.getGrid(center[0], center[1], config.getMapModelId());
//			Monster gcmonster = ManagerPool.monsterManager.createMonster(GUANGCAI, config.getServerId(), config.getLineId(), config.getMapId(), grid.getCenter());
//			gcmonster.setDirection((byte)2);
//			ManagerPool.mapManager.enterMap(gcmonster);
//			
//			MessageUtil.notify_map(map, Notifys.CUTOUT, String.format(ResManager.getInstance().getString("大天使:城门已被击破,请速去攻击 水晶灵枢"), gcmonster.getName()));
//			zone.getOthers().put("zoneprocess", 3);
//			
//			ResZoneLifeTimeMessage timemsg = new ResZoneLifeTimeMessage();
//			timemsg.setSurplustime((zoneBean.getQ_exist_time()/1000)- ((int) System.currentTimeMillis()/1000 - (Integer)zone.getOthers().get("time")));
//			timemsg.setZoneid(map.getZoneModelId());
//			timemsg.setZoneprocess(3);
//			MessageUtil.tell_map_message(map, timemsg);
//		}
		//棺材破了 刷天使
		if(monster.getModelId()==GUANGCAI){
			//刷新天使
//			MapConfig config = zone.getConfigs().get(0);
//			Grid grid = MapUtils.getGrid(angleCenter[0], angleCenter[1], config.getMapModelId());
//			Monster gcmonster = ManagerPool.monsterManager.createMonster(ANGLE, config.getServerId(), config.getLineId(), config.getMapId(), grid.getCenter());
//			gcmonster.setDirection((byte)0);
//			ManagerPool.mapManager.enterMap(gcmonster);
			
//			MessageUtil.notify_map(map, Notifys.CUTOUT, String.format(ResManager.getInstance().getString("大天使:不好,被血色天使发现了，只好先击败他了"), gcmonster.getName()));
			zone.getOthers().put("zoneprocess", 5);
			ResZoneLifeTimeMessage timemsg = new ResZoneLifeTimeMessage();
			timemsg.setSurplustime((zoneBean.getQ_exist_time()/1000)- ((int) System.currentTimeMillis()/1000 - (Integer)zone.getOthers().get("time")));
			timemsg.setZoneid(map.getZoneModelId());
			timemsg.setZoneprocess(5);
			timemsg.setPlayerCount((Integer)zone.getOthers().get("playercount"));
			MessageUtil.tell_map_message(map, timemsg);
		}
		
		
		//天使死了 进入最后一步
		if(monster.getModelId()==ANGLE){
//			//刷新棺材
//			MapConfig config = zone.getConfigs().get(0);
//			Grid grid = MapUtils.getGrid(angleCenter[0], angleCenter[1], config.getMapModelId());
//			Monster gcmonster = ManagerPool.monsterManager.createMonster(ANGLE, config.getServerId(), config.getLineId(), config.getMapId(), grid.getCenter());
//			gcmonster.setDirection((byte)0);
//			ManagerPool.mapManager.enterMap(gcmonster);
//			
//			
			MessageUtil.notify_map(map, Notifys.CUTOUT, ResManager.getInstance().getString( "大天使:勇敢的少年,请快些将武器交到我手上"));
			zone.getOthers().put("zoneprocess", 7);
			ResZoneLifeTimeMessage timemsg = new ResZoneLifeTimeMessage();
			timemsg.setSurplustime((zoneBean.getQ_exist_time()/1000)- ((int) System.currentTimeMillis()/1000 - (Integer)zone.getOthers().get("time")));
			timemsg.setZoneid(map.getZoneModelId());
			timemsg.setZoneprocess(7);
			timemsg.setPlayerCount((Integer)zone.getOthers().get("playercount"));
			MessageUtil.tell_map_message(map, timemsg);
			map.getParameters().put("isfinsh", true);
			/*xiaozhuoming: 暂时没有用到
			MessageUtil.notify_map(map, Notifys.CUTOUT,"交还大天使武器完成任务！");
			*/
		}
		return;
	}

	@Override
	public void onEnterMap(Player player, Map map) {
		
		if(map.getMapModelid()==100002){
			//移除玩家 身上不该有的任务道具
			ManagerPool.backpackManager.removeItem(player, 720101,1, Reasons.GOODUSE, Config.getId());
			ManagerPool.backpackManager.removeItem(player, 720102,1, Reasons.GOODUSE, Config.getId());
			ManagerPool.backpackManager.removeItem(player, 720100,1, Reasons.GOODUSE, Config.getId());
		}
		
	
		
		if(map==null ||map.getMapModelid()< 400001 || map.getMapModelid()>400008){
			return;
		}
		ZoneContext zone = ManagerPool.zonesManager.getContexts().get(map.getZoneId());
		if(zone.getOthers().get("is_say")==null){
			zone.getOthers().put("is_say", 0);
				MessageUtil.notify_map(map, Notifys.CUTOUT, ResManager.getInstance().getString( "大天使:请先击杀{1}只怪物，减弱外围的罪恶力量"),(Integer)zone.getOthers().get("playercount")*40+"");
		}
		ManagerPool.taskManager.action(player, Task.ACTION_TYPE_RANK, TaskEnum.ENTER_MISSION, 7001);
		
		Q_clone_activityBean zoneBean = ManagerPool.dataManager.q_clone_activityContainer.getMap().get(map.getZoneModelId());
		ResZoneLifeTimeMessage timemsg = new ResZoneLifeTimeMessage();
		timemsg.setSurplustime((zoneBean.getQ_exist_time()/1000)- ((int) System.currentTimeMillis()/1000 - (Integer)zone.getOthers().get("time")));
		timemsg.setZoneid(map.getZoneModelId());
		timemsg.setZoneprocess(7);
		timemsg.setPlayerCount((Integer)zone.getOthers().get("playercount"));
	}
	
}
