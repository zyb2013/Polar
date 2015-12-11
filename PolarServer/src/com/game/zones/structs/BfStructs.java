package com.game.zones.structs;

import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.zones.bean.BfStructsInfo;


/**跨服4V4个人信息
 * 
 * @author zhangrong
 *
 */
public class BfStructs {
	//玩家ID
	private long playerid;
	//玩家名字
	private String playername;
	//玩家等级
	private int playerlevel;
	//玩家阵营
	private int camp;
	//死亡次数
	private int deathnum;
	//击杀别人次数
	private int killnum;
	//累计经验
	private int  totalexp;
	//累计真气
	private int  totalzhenqi;
	//夺旗次数
	private int seizeflag;

	
	
	public BfStructsInfo makeBfStructsInfo(){
		BfStructsInfo bfStructsInfo = new BfStructsInfo();
		bfStructsInfo.setCamp(camp);
		bfStructsInfo.setDeathnum(deathnum);
		bfStructsInfo.setKillnum(killnum);
		bfStructsInfo.setPlayerid(playerid);
		bfStructsInfo.setPlayerlevel(playerlevel);
		bfStructsInfo.setPlayername(playername);
		bfStructsInfo.setSeizeflag(seizeflag);
		bfStructsInfo.setTotalexp(totalexp);
		bfStructsInfo.setTotalzhenqi(totalzhenqi);
		Player player = ManagerPool.playerManager.getOnLinePlayer(playerid);
		if (player == null) {
			bfStructsInfo.setOnline((byte) 1);
		}
		
		return bfStructsInfo;
	}
	
	
	public long getPlayerid() {
		return playerid;
	}

	public void setPlayerid(long playerid) {
		this.playerid = playerid;
	}

	public String getPlayername() {
		return playername;
	}

	public void setPlayername(String playername) {
		this.playername = playername;
	}

	public int getCamp() {
		return camp;
	}

	public void setCamp(int camp) {
		this.camp = camp;
	}

	public int getDeathnum() {
		return deathnum;
	}

	public void setDeathnum(int deathnum) {
		this.deathnum = deathnum;
	}

	public int getKillnum() {
		return killnum;
	}

	public void setKillnum(int killnum) {
		this.killnum = killnum;
	}

	public int getPlayerlevel() {
		return playerlevel;
	}

	public void setPlayerlevel(int playerlevel) {
		this.playerlevel = playerlevel;
	}

	public int getTotalexp() {
		return totalexp;
	}

	public void setTotalexp(int totalexp) {
		this.totalexp = totalexp;
	}

	public int getTotalzhenqi() {
		return totalzhenqi;
	}

	public void setTotalzhenqi(int totalzhenqi) {
		this.totalzhenqi = totalzhenqi;
	}

	public int getSeizeflag() {
		return seizeflag;
	}

	public void setSeizeflag(int seizeflag) {
		this.seizeflag = seizeflag;
	}
	
}
