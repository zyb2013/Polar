package com.game.toplist.structs;

import com.game.toplist.manager.TopListManager;

public class RechargeTop extends TopData{
	private int rechargeNum;
	private long rechargeTime;

	public RechargeTop(long topid, int rechargeNum, long rechargeTime) {
		super(topid);
		this.rechargeNum = rechargeNum;
		this.rechargeTime = rechargeTime;
	}
	
	public int getRechargeNum(){
		return this.rechargeNum;
	}
	
	public void setRechargeNum(int rechargeNum){
		this.rechargeNum = rechargeNum;
	}
	
	public long getRechargeTime(){
		return this.rechargeTime;
	}
	
	public void setRechargeTime(long rechargeTime){
		this.rechargeTime = rechargeTime;
	}
	
	@Override
	public boolean checkAddCondition() {
		return TopListManager.SYNC_RECHARGE <= this.rechargeNum;
	}

	@Override
	public int compare(TopData otherTopData) {
		if (otherTopData instanceof RechargeTop) {
			RechargeTop othTop = (RechargeTop)otherTopData;
			if (othTop != null) {
				if (othTop.getRechargeNum() > this.getRechargeNum()) {
					return 1;
				}else if (othTop.getRechargeNum() == this.getRechargeNum()){
					if (this.getRechargeTime() > othTop.getRechargeTime()) {
						return 1;
					}else if (othTop.getRechargeTime() == this.getRechargeTime()){
						//return 1;
						return super.compare(otherTopData);
					}
				}
			}
		}
		return -1;
	}
}
