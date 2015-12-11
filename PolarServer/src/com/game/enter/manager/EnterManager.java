package com.game.enter.manager;

import java.util.ArrayList;
import java.util.List;

import com.game.dataserver.message.ReqCheckPlayerEnterToGameServerMessage;
import com.game.dataserver.message.ReqCheckTeamEnterToGameServerMessage;
import com.game.dataserver.message.ReqPlayerEnterToDataServerMessage;
import com.game.dataserver.message.ReqStartMatchToGameServerMessage;
import com.game.dataserver.message.ReqTeamEnterToDataServerMessage;
import com.game.dataserver.message.ResCheckPlayerEnterToDataServerMessage;
import com.game.dataserver.message.ResCheckTeamEnterToDataServerMessage;
import com.game.dataserver.message.ResStartMatchMessage;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerState;
import com.game.server.impl.WServer;
import com.game.team.bean.TeamInfo;
import com.game.team.bean.TeamMemberInfo;
import com.game.utils.MessageUtil;

public class EnterManager {

	private static Object obj = new Object();
	
	//实例
	private static EnterManager manager;
	
	private EnterManager() {
	}

	public static EnterManager getInstance() {
		synchronized (obj) {
			if (manager == null) {
				manager = new EnterManager();
			}
		}
		return manager;
	}
	
	/**
	 * 申请队伍跨服
	 * @param player
	 */
	public void reqTeamEnter(Player player){
		TeamInfo team = ManagerPool.teamManager.getTeam(player.getTeamid());
		List<TeamMemberInfo> members = new ArrayList<TeamMemberInfo>();
		//检查队伍
		int result = checkTeam(team, members);
		if(result==0){
			//发送公共服报名信息
			ReqTeamEnterToDataServerMessage msg = new ReqTeamEnterToDataServerMessage();
			msg.setPlayerId(player.getId());
			msg.setServerId(WServer.getInstance().getServerId());
			msg.setWeb(WServer.getInstance().getServerWeb());
			
			int power = 0;
			for (TeamMemberInfo teamMemberInfo : team.getMemberinfo()) {
				msg.getTeamPlayerIds().add(teamMemberInfo.getMemberid());
			}
			WServer.getInstance().getPublicSession().write(msg);
		}
	}
	
	/**
	 * 申请个人跨服
	 * @param player
	 */
	public void reqPlayerEnter(Player player){
		//检查个人
		int result = checkPlayer(player);
		if(result==0){
			//发送公共服报名信息
			ReqPlayerEnterToDataServerMessage msg = new ReqPlayerEnterToDataServerMessage();
			msg.setPlayerId(player.getId());
			msg.setServerId(WServer.getInstance().getServerId());
			msg.setWeb(WServer.getInstance().getServerWeb());
			
			WServer.getInstance().getPublicSession().write(msg);
		}
	}
	
	/**
	 * 队伍跨服检查
	 * @param player
	 */
	public void reqCheckTeamEnterToGameServer(ReqCheckTeamEnterToGameServerMessage message){
		TeamInfo team = ManagerPool.teamManager.getTeam(message.getTeamId());
		List<TeamMemberInfo> members = new ArrayList<TeamMemberInfo>();
		
		//TODO 检查队伍成员是否变化
		
		//检查队伍
		int result = checkTeam(team, members);
		//发送公共服报名信息
		ResCheckTeamEnterToDataServerMessage msg = new ResCheckTeamEnterToDataServerMessage();
		msg.setPlayerId(message.getPlayerId());
		msg.setServerId(WServer.getInstance().getServerId());
		msg.setWeb(WServer.getInstance().getServerWeb());
		msg.setDataServerTeamId(message.getDataServerTeamId());
		msg.setEntryId(message.getEntryId());
		msg.setResult(result);
		//TODO
		for (int i = 0; i < 4; i++) {
			msg.getMembers().add(1);
		}
		WServer.getInstance().getPublicSession().write(msg);
	}
	
	/**
	 * 个人跨服检查
	 * @param player
	 */
	public void reqCheckPlayerEnterToGameServer(ReqCheckPlayerEnterToGameServerMessage message){
		Player player = ManagerPool.playerManager.getPlayer(message.getPlayerId());
		//检查个人
		int result = checkPlayer(player);
		//发送公共服报名信息
		ResCheckPlayerEnterToDataServerMessage msg = new ResCheckPlayerEnterToDataServerMessage();
		msg.setPlayerId(player.getId());
		msg.setServerId(WServer.getInstance().getServerId());
		msg.setWeb(WServer.getInstance().getServerWeb());
		msg.setDataServerTeamId(message.getDataServerTeamId());
		msg.setEntryId(message.getEntryId());
		msg.setResult(result);
		
		WServer.getInstance().getPublicSession().write(msg);
	}
	
	/**
	 * 比赛开始
	 * @param msg
	 */
	public void reqStartMatchToGameServer(ReqStartMatchToGameServerMessage msg){
		Player player = ManagerPool.playerManager.getPlayer(msg.getPlayerId());
		if(player==null){
			return;
		}
		ResStartMatchMessage smsg = new ResStartMatchMessage();
		smsg.setServerId(msg.getServerId());
		smsg.setWeb(msg.getWeb());
		smsg.setUserId(player.getUserId());
		MessageUtil.tell_player_message(player, smsg);
	}
	
	private int checkTeam(TeamInfo team, List<TeamMemberInfo> members){
		if(team==null){
			//不存在队伍
			return 101;
		}
		//TODO 测试暂时关闭
//		if(team.getMemberinfo().size() < 4){
//			//队伍不足4人
//			return 102;
//		}
		
		for (TeamMemberInfo teamMemberInfo : team.getMemberinfo()) {
			Player player = ManagerPool.playerManager.getPlayer(teamMemberInfo.getMemberid());
			int result = checkPlayer(player);
			if(result!=0){
				members.add(teamMemberInfo);
				return result;
			}
		}
		
		return 0;
	}
	
	private int checkPlayer(Player player){
		if(player==null){
			//不在游戏
			return 201;
		}
		if(PlayerState.QUIT.compare(player.getState())){
			//不在游戏
			return 201;
		}
		if(player.getMap()!=player.getMapModelId()){
			//副本中
			return 202;
		}
		if(player.getLevel() < 46){
			//等级不够
			return 203;
		}
		return 0;
	}
}
