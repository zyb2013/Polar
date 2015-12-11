package scripts.zone.battlefield4v4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
//import org.apache.mina.util.byteaccess.CompositeByteArrayRelativeWriter.Expander;

import com.alibaba.fastjson.JSON;
import com.game.data.bean.Q_characterBean;
import com.game.fight.structs.Fighter;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.map.script.IEnterMapScript;
import com.game.map.script.IQuitMapScript;
import com.game.map.structs.Map;
import com.game.npc.script.INpcDefaultActionScript;
import com.game.npc.script.INpcGatherActionScript;
import com.game.npc.struts.NPC;
import com.game.pet.struts.Pet;
import com.game.player.message.ResScriptCommonPlayerToClientMessage;
import com.game.player.script.IPlayerDieScript;
import com.game.player.structs.AttributeChangeReason;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.script.structs.ScriptEnum;
import com.game.server.config.MapConfig;
import com.game.structs.Grid;
import com.game.util.TimerUtil;
import com.game.utils.Global;
import com.game.utils.MapUtils;
import com.game.utils.MessageUtil;
import com.game.zones.message.ResBf4v4infoToClientMessage;
import com.game.zones.script.IZoneEventTimerScript;
import com.game.zones.structs.BfStructs;
import com.game.zones.structs.ZoneContext;
import com.game.zones.timer.ZoneLoopEventTimer;
	
	

/**
 * 大天使跨服战场脚本，废弃，仅供参考
 * @author hongxiao.z
 * @date   2014-1-11  下午4:29:20
 */
@Deprecated
//跨服战场4v4
public class Battlefield4V4 implements  IEnterMapScript ,IPlayerDieScript ,IZoneEventTimerScript,IQuitMapScript,INpcDefaultActionScript,INpcGatherActionScript{
	private Logger log = Logger.getLogger(Battlefield4V4.class);
	//战斗时限（秒）
	private  int maxtime = 60*30;
	//副本模版ID
	private  int zonemodelid = 4008;
	//副本模版地图ID
	private  int mapmodelid = 42124;
	//NPCID列表
	private int[] npcids= {50100,50101,50102};
	//旗帜刷新坐标
	private int[][] npcposs = {{76,100},{78,98},{82,102}};
	//保存队伍1和2
	private String groupmark_1 = "groupmark_1";
	private String groupmark_2 = "groupmark_2";
	
	private String playerinfo_list = "playerinfo_list";

	
	//旗帜归属
	private String flagasc= "flagasc";
	//时间记录
	private String timems= "timems";
	//胜负结果
	private String result = "result";
	//过去时间
	private String pasttime="pasttime";
	
	
	@Override
	public int getId() {
		return ScriptEnum.BF4V4;
	}

	


	
	
	//进入地图调用
	@Override
	public void onEnterMap(Player player, Map map) {
		ZoneContext zone = ManagerPool.zonesManager.getContexts().get(map.getZoneId());
		if (zone == null) {
			return;
		}
		if(map.getZoneId() > 0 && map.getZoneModelId() == zonemodelid  ){
			ManagerPool.buffManager.removeByBuffId(player, Global.PROTECT_FOR_KILLED);
			//初始设置
			if( !zone.getOthers().containsKey("initialize")){
				//map.setBanusesp(1);//禁止使用体力类药水
				zone.getOthers().put("initialize", 1);
				//创建3个NPC
				for (int i = 0; i < npcposs.length; i++) {
					Grid grid = MapUtils.getGrid(npcposs[i][0], npcposs[i][1], map.getMapModelid());
					NPC npc = ManagerPool.npcManager.createNpc(npcids[i], map, true);
					npc.getParameters().put("type",i);
					npc.setPosition(grid.getCenter());
					ManagerPool.mapManager.enterMap(npc);
				}
				ZoneLoopEventTimer timer = new ZoneLoopEventTimer(this.getId(), zone.getId(), zonemodelid, new ArrayList<Object>(), 1000);
				TimerUtil.addTimerEvent(timer);
			}
			
			ManagerPool.teamManager.stCreateateam(player, player.getDataServerTeamId());

			//按照队伍设定阵营
			if (getl(zone.getOthers(), groupmark_1)== 0) {
				setl(zone.getOthers(), groupmark_1, player.getDataServerTeamId());
				player.setGroupmark(1);
			}else {
				if (getl(zone.getOthers(), groupmark_1)== player.getDataServerTeamId()) {
					player.setGroupmark(1);
				}else {
					player.setGroupmark(2);
					if (getl(zone.getOthers(), groupmark_2)== 0) {
						setl(zone.getOthers(), groupmark_2, player.getDataServerTeamId());
					}
				}
			}
			
			if (!zone.getOthers().containsKey(playerinfo_list)) {
				zone.getOthers().put(playerinfo_list, new ArrayList<BfStructs>());
			}
			@SuppressWarnings("unchecked")
			ArrayList<BfStructs> list= (ArrayList<BfStructs>)zone.getOthers().get(playerinfo_list);
			BfStructs bfStructs = new BfStructs();
			bfStructs.setCamp(player.getGroupmark());
			bfStructs.setPlayerid(player.getId());
			bfStructs.setPlayerlevel(player.getLevel());
			bfStructs.setPlayername(player.getName());
			list.add(bfStructs);
			//改变攻击模式为全体
			ManagerPool.playerManager.changePkState(player, 2, 0);
			log.error(player.getName() + " groupmark "+ player.getGroupmark() + " player.getDataServerTeamId()" + player.getDataServerTeamId());
			sendBfinfo(map);
		}
	}
	
	
	


	//退出地图
	@Override
	public void onQuitMap(Player player, Map map) {
		if (map !=null && map.getMapModelid() == mapmodelid ) {
			ZoneContext zone = ManagerPool.zonesManager.getContexts().get(map.getZoneId());
			if (zone == null) {
				return;
			}	
		
			for (int npcid : npcids) {
				if(zone.getOthers().containsKey(flagasc+npcid)){
					long pid=(Long)zone.getOthers().get(flagasc+npcid);
					if (pid == player.getId()) {
						zone.getOthers().remove(flagasc+npcid);//移除夺旗标志
						List<NPC> npclist = ManagerPool.npcManager.findNpc(map, npcid);
						if (npclist.size() > 0) {
							NPC npc = npclist.get(0);
							npc.setPosition(player.getPosition());//设置
							ManagerPool.npcManager.showNpc(npc);//NPC出现
						}
						
						String e = (player.getPosition().getX()/MapUtils.GRID_BORDER) + "";
						String f = (player.getPosition().getY()/MapUtils.GRID_BORDER) + "";
						break;
					}
				}
			}
			sendBfinfo(map);
		}
		
	}

	//每秒循环调用
	@Override
	public void action(long zoneId, int zoneModelId, List<Object> parameters) {

		ZoneContext zone = ManagerPool.zonesManager.getContexts().get(zoneId);
		if (zone != null) {
			int ms = (int)(System.currentTimeMillis()/1000);
			
			MapConfig mapconfig = zone.getConfigs().get(0);
			Map map = ManagerPool.mapManager.getMap(mapconfig.getServerId(), mapconfig.getLineId(), mapconfig.getMapId());
			if (map.getPlayerNumber() == 0) return;
			
			HashMap<String, Object> zonedata = zone.getOthers();
			if (getn(zonedata, "stop") >= 1) { 
				return;
			}

			int time = (Integer)zone.getOthers().get("time");
			int newtime = ms - time;
			if (ms%60 == 0) {	//每分钟获得经验真气奖励
				if (zone.getOthers().containsKey(playerinfo_list)) {
					@SuppressWarnings("unchecked")
					ArrayList<BfStructs> list= (ArrayList<BfStructs>)zone.getOthers().get(playerinfo_list);
					for (BfStructs bfStructs : list) {
						Player player = ManagerPool.playerManager.getOnLinePlayer(bfStructs.getPlayerid());
						if (player != null && player.getMapModelId() == mapmodelid) {
							int exp = player.getLevel() * player.getLevel() * 10 ;
							bfStructs.setTotalexp(bfStructs.getTotalexp() + exp);
						}
					}
				}
			}
			
			if (newtime < maxtime) {	//30分钟内 每2秒检测一次旗帜归属
				if (ms%2==0) {
					checkoutcome(map,1);
				}
			}else{
				//30分钟到达，强制结束
				checkoutcome(map,2);
			}
				

		}	
	}
	
	
	
	
	
	
	//玩家死亡调用
	@Override
	public void onPlayerDie(Player player, Fighter attacker) {
		Map map = ManagerPool.mapManager.getMap(player);
		if (map !=null && map.getMapModelid() == mapmodelid ) {
			Player attackPlayer = null;
			if (attacker instanceof Pet) {
				 attackPlayer = ManagerPool.petInfoManager.getPetHost((Pet) attacker);
			} else if (attacker instanceof Player) {
				attackPlayer = (Player)attacker;
			}

			ZoneContext zone = ManagerPool.zonesManager.getContexts().get(map.getZoneId());
			if (zone == null) {
				return;
			}

			for (int npcid : npcids) {
				if(zone.getOthers().containsKey(flagasc+npcid)){
					long pid=(Long)zone.getOthers().get(flagasc+npcid);
					if (pid == player.getId()) {
						zone.getOthers().remove(flagasc+npcid);//移除夺旗标志
						List<NPC> npclist = ManagerPool.npcManager.findNpc(map, npcid);
						if (npclist.size() > 0) {
							NPC npc = npclist.get(0);
							npc.setPosition(player.getPosition());//设置
							ManagerPool.npcManager.showNpc(npc);//NPC出现
						}
						/*xiaozhuoming: 暂时没有用到
						String a = getteamname(player.getGroupmark());
						String b = player.getName();
						String c = getteamname(attackPlayer.getGroupmark());
						String d = attackPlayer.getName();
						String x = (player.getPosition().getX()/MapUtils.GRID_BORDER) + "";
						String y = (player.getPosition().getY()/MapUtils.GRID_BORDER) + "";
						MessageUtil.notify_map(map, Notifys.CHAT_BULL, "{1}玩家【{2}】旗帜被{3}玩家【{4}】击落（{5},{6}）", a,b,c,d,x,y);
						*/
						break;
					}
				}
			}
			
			BfStructs bfStructs = getPlayerBfStructs(map ,player.getId());
			if (bfStructs != null) {
				bfStructs.setDeathnum(bfStructs.getDeathnum() + 1);
			}
			BfStructs akbfStructs = getPlayerBfStructs(map ,attacker.getId());
			if (akbfStructs != null) {
				akbfStructs.setKillnum(akbfStructs.getKillnum() + 1);
			}
			sendBfinfo(map);
		}
	}



	
	
	
	/**旗帜抢夺
	 * 开始，检查
	 */
	@Override
	public void defaultAction(Player player, NPC npc) {
		if (player.isDie() == true) {
			MessageUtil.notify_player(player, Notifys.MOUSEPOS, ResManager.getInstance().getString("死亡状态下不能夺旗"));
			return;
		}
		Map map = ManagerPool.mapManager.getMap(player);
		if (map != null ) {
			ZoneContext zone = ManagerPool.zonesManager.getContexts().get(map.getZoneId());
			if (zone == null) {
				return;
			}
			for (int npcid : npcids) {
				if (zone.getOthers().containsKey(flagasc+npcid) ) {
					long pid=(Long)zone.getOthers().get(flagasc+npcid);
					if (pid == player.getId()) {
						/*xiaozhuoming: 暂时没有用到
						MessageUtil.notify_player(player, Notifys.MOUSEPOS, ResManager.getInstance().getString("您的身上已经携带旗帜，不能再拔起另外的旗帜了"));
						*/
						return;
					}
				}
			}
		}
		MessageUtil.notify_map(map, Notifys.CHAT_BULL, "{1}玩家【{2}】开始拔起旗帜", getteamname(player.getGroupmark()),player.getName());
		ManagerPool.npcManager.playerGather(player, npc);	//开始采集
	}
	
	
	
	
	/**旗帜抢夺
	 * 结束
	 */
	@Override
	public void gather(Player player, NPC npc) {
		Map map = ManagerPool.mapManager.getMap(player);
		if (map != null ) {
			ZoneContext zone = ManagerPool.zonesManager.getContexts().get(map.getZoneId());
			if (zone == null) {
				return;
			}
			
			for (int npcid : npcids) {
				if (zone.getOthers().containsKey(flagasc+npcid) ) {
					long pid=(Long)zone.getOthers().get(flagasc+npcid);
					if (pid == player.getId()) {
						/*xiaozhuoming: 暂时没有用到
						MessageUtil.notify_player(player, Notifys.MOUSEPOS, ResManager.getInstance().getString("您的身上已经携带旗帜，不能再拔起另外的旗帜了"));
						*/
						return;
					}
				}
			}
			//记录旗帜
			zone.getOthers().put(flagasc+npc.getModelId(),player.getId());
			//npc消失
			ManagerPool.npcManager.hideNpc(npc);
			//player停止采集
			ManagerPool.npcManager.playerStopGather(player, true);
			MessageUtil.notify_map(map, Notifys.CHAT_BULL, "{1}玩家【{2}】成功拔起旗帜", getteamname(player.getGroupmark()),player.getName());
			checkoutcome(map,1);
			
			BfStructs bfStructs = getPlayerBfStructs(map ,player.getId());
			if (bfStructs != null) {
				bfStructs.setSeizeflag(bfStructs.getSeizeflag() + 1);
			}
			sendBfinfo(map);
		}
	}
	
	
	/**得到队伍名字
	 * 
	 * @param type
	 * @return
	 */
	public String getteamname(int type){
		if (type == 1) {
			return "蓝队";
		}else {
			return "黄队";
		}
	}

	
	/**检测胜负
	 * type=1即时检测是否全部被夺取， type=2达到结束时间后检测胜负
	 */
	public void checkoutcome(Map map , int type){
		ZoneContext zone = ManagerPool.zonesManager.getContexts().get(map.getZoneId());
		if (zone == null) {
			return;
		}

		int campa = 0;
		int campb = 0;
		for (int npcid : npcids) {
			if (zone.getOthers().containsKey(flagasc+npcid) ) {
				long pid=(Long)zone.getOthers().get(flagasc+npcid);
				Player player = ManagerPool.playerManager.getPlayer(pid);
				if (player != null) {
					if (player.getGroupmark() == 1) {
						campa = campa+ 1;
					}else if (player.getGroupmark() == 2) {
						campb = campb+ 1;
					}
				}
			}
		}
		if (type == 1) {	//即时检测
			if (campa >= 1) {	//A获胜
				setn(zone.getOthers(), "stop",1);
				setn(zone.getOthers(), result, 1);
				Reward(map);
			}else if (campb >= 1) { //B获胜
				setn(zone.getOthers(), "stop",1);
				setn(zone.getOthers(), result, 2);
				Reward(map);
			}
		}else if (type == 2) {	//结束检测
			setn(zone.getOthers(), "stop",1);
			if (campa > campb) {	//A获胜
				setn(zone.getOthers(), result, 1);
			}else if (campb > campa) { //B获胜
				setn(zone.getOthers(), result, 2);
			}else {//平局
				setn(zone.getOthers(), result, 100);
			}
			Reward(map);
		}
	}
	
	
	
	
	/**发奖励
	 * 
	 */
	public void Reward(Map map){
		if (map.getZoneId() > 0) {
			ZoneContext zone = ManagerPool.zonesManager.getContexts().get(map.getZoneId());
			if (zone == null) {
				return;
			}
			HashMap<String, Object> zonedata = zone.getOthers();
			int zoneModelId = zone.getZonemodelid();
			if(getn(zonedata,"fajiang") == 1){//检测是否发奖励
				return;
			}	
			
			HashMap<String, Object> paramMap = new HashMap<String, Object>();
			setn(zonedata,"fajiang",1);//设置发奖励标记
			
			for (Player mPlayer : map.getPlayers().values()) {
				if (mPlayer != null) {
					if (mPlayer.isDie()) {	//死亡自动复活
						ManagerPool.playerManager.autoRevive(mPlayer);
					}
					
					if ( !zonedata.containsKey("rew"+mPlayer.getId())) {
						paramMap.put("groupmark", mPlayer.getGroupmark());
						if (getn(zonedata,result) == 100) {
							MessageUtil.notify_player(mPlayer, Notifys.CHAT_BULL, ResManager.getInstance().getString("双方战平！！"));
						}else {
							if(getn(zonedata,result) == mPlayer.getGroupmark()){
								MessageUtil.notify_player(mPlayer, Notifys.CHAT_BULL, ResManager.getInstance().getString("恭喜获胜！"));
								paramMap.put("result", 1);
								
								int level  = mPlayer.getLevel();
								if (level > 100) {
									level = 100;
								}
								int key = ManagerPool.dataManager.q_characterContainer.getKey(mPlayer.getJob(), level);
								Q_characterBean model = ManagerPool.dataManager.q_characterContainer.getMap().get(key);

								int exp=(int) ((double)level*level*model.getQ_basis_exp()*2*1.2);
								paramMap.put("exp", exp);
								ManagerPool.playerManager.addExp(mPlayer, exp,AttributeChangeReason.SHUIYANDALIANG);
								/*xiaozhuoming: 暂时没有用到
								MessageUtil.notify_player(mPlayer, Notifys.CHAT_ROLE, ResManager.getInstance().getString("获得跨服战4V4副本胜利额外经验{1}！"),exp+"");
								*/
							}else {
								MessageUtil.notify_player(mPlayer, Notifys.CHAT_BULL, ResManager.getInstance().getString("很可惜，我方输了"));
								paramMap.put("result", 2);
								
								int level  = mPlayer.getLevel();
								if (level > 100) {
									level = 100;
								}
								Q_characterBean model = ManagerPool.dataManager.q_characterContainer.getMap().get(level);
								int exp= level*level*model.getQ_basis_exp()*2;
								paramMap.put("exp", exp);
								ManagerPool.playerManager.addExp(mPlayer, exp,AttributeChangeReason.SHUIYANDALIANG);
								/*xiaozhuoming: 暂时没有用到
								MessageUtil.notify_player(mPlayer, Notifys.CHAT_ROLE, ResManager.getInstance().getString("获得跨服战4V4副本参与奖励经验{1}！"),exp+"");
								*/
							}
							ResScriptCommonPlayerToClientMessage sendMessage = new ResScriptCommonPlayerToClientMessage();
							sendMessage.setScriptid(getId());
							sendMessage.setType(2);
							sendMessage.setMessageData(JSON.toJSONString(paramMap));
							MessageUtil.tell_player_message(mPlayer, sendMessage);
						}
						
						zone.getOthers().put("rew"+mPlayer.getId(),zoneModelId);
					}
				}
			}
		}

	}
	
	
	
	/**发送给前端战场消息
	 * 死亡，占旗，下线，获得经验真气，结束，   都调用
	 */
	public void sendBfinfo(Map map){
		ZoneContext zone = ManagerPool.zonesManager.getContexts().get(map.getZoneId());
		if (zone == null) {
			return;
		}
		
		ResBf4v4infoToClientMessage msg = new ResBf4v4infoToClientMessage();
		if (zone.getOthers().containsKey(playerinfo_list)) {
			@SuppressWarnings("unchecked")
			ArrayList<BfStructs> list= (ArrayList<BfStructs>)zone.getOthers().get(playerinfo_list);
			for (BfStructs bfStructs : list) {
				msg.getBfStructsInfolist().add(bfStructs.makeBfStructsInfo());
			}
		}

		int time = (Integer)zone.getOthers().get("time");
		int newtime = (int) (System.currentTimeMillis()/1000 - time);
		int campa = 0;
		int campb = 0;
		for (int npcid : npcids) {
			if (zone.getOthers().containsKey(flagasc+npcid) ) {
				long pid=(Long)zone.getOthers().get(flagasc+npcid);
				Player player = ManagerPool.playerManager.getPlayer(pid);
				if (player != null) {
					if (player.getGroupmark() == 1) {
						campa = campa+ 1;
					}else if (player.getGroupmark() == 2) {
						campb = campb+ 1;
					}
				}
			}
		}
		msg.setSeizeflag_a(campa);
		msg.setSeizeflag_b(campb);
		msg.setTime(maxtime - newtime);
		MessageUtil.tell_map_message(map, msg);	
	}
	
	


	/**设置数值
	 * 
	 * @param zonedata
	 * @param key
	 * @param num
	 */
	public void setn(HashMap<String, Object> zonedata,String key,int num ){
		zonedata.put(key, num);
	}

	/**读取数值
	 * 
	 * @param zonedata
	 * @param key
	 * @return
	 */
	public int getn(HashMap<String, Object> zonedata,String key ){
		if (zonedata.containsKey(key)) {
			int n = (Integer)zonedata.get(key);
			return n;
		}else {
			return 0;
		}
	}

	
	/**设置数值long
	 * 
	 * @param zonedata
	 * @param key
	 * @param num
	 */
	public void setl(HashMap<String, Object> zonedata,String key,long num ){
		zonedata.put(key, num);
	}
	
	
	/**读取数值long
	 * 
	 * @param zonedata
	 * @param key
	 * @return
	 */
	public long getl(HashMap<String, Object> zonedata,String key ){
		if (zonedata.containsKey(key)) {
			long n = (Long)zonedata.get(key);
			return n;
		}else {
			return 0;
		}
	}
	

	/**设置字符
	 * 
	 * @param zonedata
	 * @param key
	 * @param num
	 */
	public void sets(HashMap<String, Object> zonedata,String key,String num ){
		zonedata.put(key, num);
	}
	
	
	/**读取字符
	 * 
	 * @param zonedata
	 * @param key
	 * @return
	 */
	public String gets(HashMap<String, Object> zonedata,String key ){
		if (zonedata.containsKey(key)) {
			String s = (String) zonedata.get(key);
			return s;
		}else {
			return "";
		}
	}
	
	
	/**
	 * 获取玩家战场信息
	 */
	public BfStructs getPlayerBfStructs(Map map, long playerid ) {
		ZoneContext zone = ManagerPool.zonesManager.getContexts().get(map.getZoneId());
		if (zone == null) {
			return null;
		}

		if (zone.getOthers().containsKey(playerinfo_list)) {
			@SuppressWarnings("unchecked")
			ArrayList<BfStructs> list= (ArrayList<BfStructs>)zone.getOthers().get(playerinfo_list);
			for (BfStructs bfStructs : list) {
				if (bfStructs.getPlayerid() == playerid) {
					return bfStructs;
				}
			}
		}
		return null;
	}
	
	
	
	
}
