package com.game.login.handler;


import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.data.bean.Q_mapBean;
import com.game.login.message.ReqLoadFinishMessage;
import com.game.login.message.ResLoginSuccessToWorldMessage;
import com.game.login.message.TestReqLoadFinishMessage;
import com.game.manager.ManagerPool;
import com.game.map.manager.MapManager;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerState;
import com.game.server.impl.MServer;
import com.game.server.impl.WServer;
import com.game.structs.Grid;
import com.game.utils.CommonConfig;
import com.game.utils.MapUtils;
import com.game.utils.MessageUtil;

public class TestLoadFinishHandler extends Handler{
	Logger log = Logger.getLogger(TestLoadFinishHandler.class);
	@Override
	public void action() {
		//机器人开关
		if(ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.ROBOT_OPEN.getValue()).getQ_int_value() != 1){
			return;
		}
		//panic god 暂时自己添加
		TestReqLoadFinishMessage msg = (TestReqLoadFinishMessage)this.getMessage();
		Player player = ManagerPool.playerManager.getPlayer(msg.getRoleId().get(0));
		if (player == null) {
				// 加载人物
				player = ManagerPool.playerManager.loadPlayer(msg
						.getRoleId().get(0));
				if (player == null) {
					log.error("ReqLoadFinishMessage player "
							+ msg.getRoleId().get(0) + " is null!");
					return;
				}					
		}
		this.setParameter(player);
		ReqLoadFinishMessage msgload = new ReqLoadFinishMessage();
		msgload.setType((byte)0);
		msgload.setHeight(0);
		msgload.setWidth(0);
		//robot专门把机器人设置为上线状态
		//玩家为登陆状态
		player.setState(PlayerState.LOGIN);
		//分配玩家所在线
		ManagerPool.mapManager.selectLine(player);
		ManagerPool.playerManager.registerPlayer(player);
		//
		//通知世界服务器用户登录成功
		ResLoginSuccessToWorldMessage world_msg = new ResLoginSuccessToWorldMessage();
		world_msg.setGateId(player.getGateId());
		world_msg.setServerId(player.getServerId());
		world_msg.setUserId(player.getUserId());
		world_msg.setPlayerId(player.getId());
		world_msg.setIsAdult((byte) 1);
		world_msg.setLogintype(player.getLoginType());
		world_msg.setJob(player.getJob());
		MessageUtil.send_to_world(world_msg);

		
		//再转发到
		MServer line = WServer.getInstance().getMServer(player.getLine(), player.getMap());
		if (line == null) {
			log.error("line  is null!"
					+ player.getName() + "TestLoadFinishHandler");
		}	
		ReqLoadFinishHandler handler = new ReqLoadFinishHandler();
		//进入登陆操作
		handler.setMessage(msgload);
		handler.setParameter(player);
		if(handler==null || line==null){
			Q_mapBean mapBean = ManagerPool.dataManager.q_mapContainer.getMap()
					.get(player.getMapModelId());
				Grid[][] grids = ManagerPool.mapManager.getMapBlocks(100001);
				Grid grid = MapUtils.getGrid(391,
						180, grids);
				MapManager.getInstance().changeMap(player, 100001,
						100001, 0, grid.getCenter(), this
								.getClass().getName() + ".enterMap");
				line = WServer.getInstance().getMServer(player.getLine(), player.getMap());	
				
		}
		line.addCommand(handler);
			
		
	}
	
}
