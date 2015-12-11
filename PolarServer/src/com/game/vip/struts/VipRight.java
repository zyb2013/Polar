package com.game.vip.struts;

import com.game.player.structs.Player;
import com.game.zones.manager.ZonesManager;




//VIP特权
public class VipRight {
	private int vipStage;// vip等阶： 0无vip，1体验卡，2月卡，3季度卡，4半年年卡
	private int vipLevel = 0;// 当前vip等级
	private int vipExp;// 当前vip等级的经验值
	private long endTime;// vip结束时间，秒
	private long costGold;// 上次统计时玩家的总消耗钻石
	private long preCalculateExpTime;// 上次计算vip经验点的时间
	
	private transient boolean hasTimer;//是否已经加入定时了

	
	public boolean isHasTimer() {
		return hasTimer;
	}

	public void setHasTimer(boolean hasTimer) {
		this.hasTimer = hasTimer;
	}

	public int getVipStage() {
		return vipStage;
	}

	public void setVipStage(int vipStage) {
		this.vipStage = vipStage;
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(int vipLevel) {
		if(vipLevel != this.vipLevel) {
			this.vipLevel = vipLevel;
//			ZonesManager.getInstance().getPtGainRewardInfo(player);
		}
	}

	public int getVipExp() {
		return vipExp;
	}

	public void setVipExp(int vipExp) {
		this.vipExp = vipExp;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getCostGold() {
		return costGold;
	}

	public void setCostGold(long costGold) {
		this.costGold = costGold;
	}

	public long getPreCalculateExpTime() {
		return preCalculateExpTime;
	}

	public void setPreCalculateExpTime(long preCalculateExpTime) {
		this.preCalculateExpTime = preCalculateExpTime;
	}

	/*********************************************************************************************************/

	// private long lastReceiveVipRewardTime = 0; //上次领取礼包时间
	// private long lastFreeFlyTime=0; //上次使用免费传送的时间
	// private int freeflytime = 0; //免费钻石传送次数
	// private int receivedTopReward=0; //是否领取过至尊VIP的奖励
	// private transient int webVipLevel; //vip等级（平台,QQ为蓝钻等级）
	// private transient int webVipLevel2; //vip等级2（平台，3366为包子等级）
	//
	// public void resetVipRight(Player player){
	// int vipid = VipManager.getInstance().getPlayerVipId(player);
	// if(vipid>0){
	// Q_vipBean bean = DataManager.getInstance().q_vipContainer.getMap().get(vipid);
	// freeflytime = bean.getQ_fly();
	// }
	// }
	// public long getLastReceiveVipRewardTime() {
	// return lastReceiveVipRewardTime;
	// }
	//
	//
	// public void setLastReceiveVipRewardTime(long lastReceiveVipRewardTime) {
	// this.lastReceiveVipRewardTime = lastReceiveVipRewardTime;
	// }
	//
	// public int getReceivedTopReward() {
	// return receivedTopReward;
	// }
	// public void setReceivedTopReward(int receivedTopReward) {
	// this.receivedTopReward = receivedTopReward;
	// }
	// public long getLastFreeFlyTime() {
	// return lastFreeFlyTime;
	// }
	// public void setLastFreeFlyTime(long lastFreeFlyTime) {
	// this.lastFreeFlyTime = lastFreeFlyTime;
	// }
	// public int getFreeflytime() {
	// return freeflytime;
	// }
	// public void setFreeflytime(int freeflytime) {
	// this.freeflytime = freeflytime;
	// }
	// public int getWebVipLevel() {
	// return webVipLevel;
	// }
	// public void setWebVipLevel(int webVipLevel) {
	// this.webVipLevel = webVipLevel;
	// }
	// public int getWebVipLevel2() {
	// return webVipLevel2;
	// }
	// public void setWebVipLevel2(int webVipLevel2) {
	// this.webVipLevel2 = webVipLevel2;
	// }
	
}
