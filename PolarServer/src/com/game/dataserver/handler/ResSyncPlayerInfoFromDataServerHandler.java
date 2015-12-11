package com.game.dataserver.handler;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.game.command.Handler;
import com.game.data.bean.Q_clone_activityBean;
import com.game.dataserver.message.ResSyncPlayerInfoFromDataServerMessage;
import com.game.json.JSONserializable;
import com.game.login.message.ResLoginCharacterFailedMessage;
import com.game.manager.ManagerPool;
import com.game.pet.struts.Pet;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerState;
import com.game.publogin.message.ResLoginCharacterFailedForPublicMessage;
import com.game.publogin.message.ResLoginSuccessToPublicGateMessage;
import com.game.publogin.message.ResLoginSuccessToPublicWorldMessage;
import com.game.server.config.MapConfig;
import com.game.server.impl.MServer;
import com.game.server.impl.WServer;
import com.game.utils.MessageUtil;
import com.game.utils.VersionUpdateUtil;
import com.game.zones.structs.ZoneContext;

public class ResSyncPlayerInfoFromDataServerHandler extends Handler{

	Logger log = Logger.getLogger(ResSyncPlayerInfoFromDataServerHandler.class);
	
	private static Object obj = new Object();
	
	//比赛副本列表
	private static HashMap<Long, Long> matchMaps = new HashMap<Long, Long>();
	
	//比赛副本ID
	private static int MATCH_ZONE_ID = 4008;

	public void action(){
		ResSyncPlayerInfoFromDataServerMessage msg = (ResSyncPlayerInfoFromDataServerMessage)this.getMessage();
		try{
			int serverId = WServer.getInstance().getServerId();

			//序列化player
			Player player = (Player) JSONserializable.toObject(VersionUpdateUtil.dateLoad(msg.getData()), Player.class);
			if(player==null){
				ResLoginCharacterFailedMessage return_msg = new ResLoginCharacterFailedMessage();
				return_msg.setReason((byte)50);
				return_msg.setCreateServerId(msg.getServerId());
				return_msg.setUserId(msg.getUserId());
				MessageUtil.send_to_gate(msg.getGateId(), msg.getDataServerPlayerId(), return_msg);
				return;
			}
			
			player.setCross(true);
			
			player.setId(msg.getDataServerPlayerId());
			//玩家为登陆状态
			player.setState(PlayerState.LOGIN);
			//注册网关
			player.setGateId(msg.getGateId());
			
			player.setWebName(msg.getWeb());
			//设置玩家所在服务器
			player.setLocate(WServer.getGameConfig().getCountryByServer(WServer.getInstance().getServerId()));
			
			//TODO 换地图
			synchronized (obj) {
				ZoneContext context = null;
				if(matchMaps.containsKey(msg.getEnterId())){
					long contextId = matchMaps.get(msg.getEnterId());
					context = ManagerPool.zonesManager.getContexts().get(contextId);
				}else{
					Q_clone_activityBean zonedata = ManagerPool.dataManager.q_clone_activityContainer.getMap().get(MATCH_ZONE_ID);
					HashMap<String, Object> others = new HashMap<String, Object>();
					others.put("time", (int)(System.currentTimeMillis()/1000));	//开始时间
					others.put("zonetype",zonedata.getQ_zone_type());			//副本类型 3
					others.put("teamtype",1);		//队伍类型，0，单人，1组队
					List<Integer> mapidlist = JSON.parseArray(zonedata.getQ_mapid(),Integer.class);
					context = ManagerPool.zonesManager.setZone("4v4比赛", others, mapidlist, zonedata.getQ_id());
					matchMaps.put(msg.getEnterId(), context.getId());
				}
				
				if(context==null){
					return;
				}
				
				MapConfig mapConfig = context.getConfigs().get(0);
				player.setMap(mapConfig.getMapId());
				player.setMapModelId(mapConfig.getMapModelId());
				player.setLine(mapConfig.getLineId());
				
				player.setDataServerTeamId(msg.getTeamId());
			}
			
			for (Pet pet : player.getPetList()) {
				pet.setOwnerId(player.getId());
			}
			
			ManagerPool.mapManager.selectLine(player);
			log.debug("上线玩家" + player.getId() + "选线为" + player.getLine());
			
			log.info("上线玩家" + player.getId() + "{" + player.getName() + "}" + "选线为" + player.getLine());
			
			ManagerPool.playerManager.registerPlayer(player);
			//重加载自身GM等级
			ManagerPool.gmcommandManager.reloadGMLevel(player);
			//载入平台VIP
			try{
				if("qq3366".equals(WServer.getInstance().getServerWeb())){
//					player.setAgentColdatas("{\"agent\":\"qq3366\",\"qq_appid\":\"123123123\",\"qq_pf\":\"qq3366\",\"qq_domain\":\"asdasdasd\",\"qq_is_blue_vip\":\"0\",\"qq_is_blue_year_vip\":\"1\",\"qq_blue_vip_level\":\"6\",\"qq_is_super_blue_vip\":\"0\",\"3366_grow_level\":\"5\",\"3366_level\":\"0\",\"3366_nickname\":\"asdasdasd\",\"3366_figureurl\":\"asdasdasd\"}");
					@SuppressWarnings("unchecked")
					HashMap<String, String> paras = (HashMap<String, String>)JSONserializable.toObject(player.getAgentPlusdata(), HashMap.class);
					int qq_is_blue_vip = Integer.parseInt(paras.get("qq_is_blue_vip"));
					int qq_is_blue_year_vip = Integer.parseInt(paras.get("qq_is_blue_year_vip"));
					int qq_is_super_blue_vip = Integer.parseInt(paras.get("qq_is_super_blue_vip"));
					int qq_blue_vip_level = Integer.parseInt(paras.get("qq_blue_vip_level"));
					int vip = 0;
					if(qq_is_blue_vip >0 || qq_is_blue_year_vip>0 || qq_is_super_blue_vip>0){
						if(qq_is_blue_year_vip > 0){
							vip = 0x0100;
						}else if(qq_is_super_blue_vip > 0){
							vip = 0x0200;
						}
						vip = vip | qq_blue_vip_level;
					}
					int a3366_grow_level = Integer.parseInt(paras.get("3366_grow_level"));
					
					// player.getVipright().setWebVipLevel(vip);
					// player.getVipright().setWebVipLevel2(a3366_grow_level);
				}
			}catch (Exception e) {
				log.error(e, e);
			}
			
			//通知网关服务器用户登录成功
			ResLoginSuccessToPublicGateMessage gate_msg = new ResLoginSuccessToPublicGateMessage();
			gate_msg.setServerId(serverId);
			gate_msg.setWeb(msg.getWeb());
			gate_msg.setUserId(msg.getUserId());
			gate_msg.setPlayerId(player.getId());
			gate_msg.setMapId(player.getMapModelId());
			MessageUtil.send_to_gate(msg.getGateId(), player.getId(), gate_msg);
			
			//通知世界服务器用户登录成功
			ResLoginSuccessToPublicWorldMessage world_msg = new ResLoginSuccessToPublicWorldMessage();
			world_msg.setGateId(msg.getGateId());
			world_msg.setServerId(serverId);
			world_msg.setWeb(msg.getWeb());
			world_msg.setUserId(msg.getUserId());
			world_msg.setPlayerId(player.getId());
			MessageUtil.send_to_world(world_msg);
			
		}catch(Exception e){
			log.error(e, e);
			ResLoginCharacterFailedForPublicMessage return_msg = new ResLoginCharacterFailedForPublicMessage();
			return_msg.setReason((byte)53);
			return_msg.setCreateServerId(msg.getServerId());
			return_msg.setUserId(msg.getUserId());
			return_msg.setWeb(msg.getWeb());
			MessageUtil.send_to_gate(msg.getGateId(), msg.getDataServerPlayerId(), return_msg);
		}
	}
}