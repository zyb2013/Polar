package com.game.csys.structs;

import com.game.csys.bean.CsysTopInfo;
import com.game.guild.structs.GuildTmpInfo;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;


public class CsysSMS {
	private long playerid;
	private int playerlevel;	
	private String playername;//玩家名字
	private String guildname;//战盟名
	private int kill;	//杀敌数量
	private int death;	//死亡次数
	//采集数量
	private int collectCount;
	//积分
	private int integral;
	//积分获得的时间
	private Long integralTime;
	//连杀数
	private  int  killplayer=0;
	
	
	public int getKillplayer() {
		return killplayer;
	}
	public void setKillplayer(int killplayer) {
		this.killplayer = killplayer;
	}
	public Long getIntegralTime() {
		return integralTime;
	}
	public void setIntegralTime(Long integralTime) {
		this.integralTime = integralTime;
	}
	public CsysSMS(Player player){
		this.setPlayerid(player.getId());
		this.setPlayerlevel(player.getLevel());
		this.setPlayername(player.getName());
		this.setIntegral(0);
		this.setIntegralTime(System.currentTimeMillis());
		GuildTmpInfo guildTmpInfo = ManagerPool.guildServerManager.getGuildTmpInfo(player.getGuildId());
		if (guildTmpInfo != null) {
			this.setGuildname(guildTmpInfo.getGuildname());
		}else {
			this.setGuildname(ResManager.getInstance().getString("无"));
		}
	}
	public CsysSMS(){
		
	}
	
	
	/**生成排行信息
	 * 
	 * @return
	 */
	public CsysTopInfo getinfo(){
		CsysTopInfo csysTopInfo = new CsysTopInfo();
		csysTopInfo.setCollectCount(this.getCollectCount());
		csysTopInfo.setDeath(this.getDeath());
		csysTopInfo.setIntegral(this.getIntegral());
		csysTopInfo.setKill(this.getKill());
		csysTopInfo.setPlayerid(this.getPlayerid());
		csysTopInfo.setPlayername(this.getPlayername());
		return csysTopInfo;
	}
	
	public int getCollectCount() {
		return collectCount;
	}
	public void setCollectCount(int collectCount) {
		this.collectCount = collectCount;
	}
	public int getIntegral() {
		return integral;
	}
	public void setIntegral(int integral) {
		this.integral = integral;
	}

	public long getPlayerid() {
		return playerid;
	}
	public void setPlayerid(long playerid) {
		this.playerid = playerid;
	}

	public String getGuildname() {
		return guildname;
	}
	public void setGuildname(String guildname) {
		this.guildname = guildname;
	}
	public int getKill() {
		return kill;
	}
	public void setKill(int kill) {
		this.kill = kill;
	}
	public int getDeath() {
		return death;
	}
	public void setDeath(int death) {
		this.death = death;
	}
	

	public String getPlayername() {
		return playername;
	}

	public void setPlayername(String playername) {
		this.playername = playername;
	}

	public int getPlayerlevel() {
		return playerlevel;
	}

	public void setPlayerlevel(int playerlevel) {
		this.playerlevel = playerlevel;
	}
	
	
	
	
	
	
	
}
