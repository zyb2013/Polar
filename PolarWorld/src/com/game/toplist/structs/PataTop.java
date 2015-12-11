package com.game.toplist.structs;

import com.game.toplist.manager.TopListManager;

public class PataTop extends TopData {
	//! 爬塔副本ID
	private int pataId;
	//! 时间
	private long pataTime;
	
	public PataTop(long topid, int pataId, long pataTime) {
		super(topid);
		this.pataId = pataId;
		this.pataTime = pataTime;
	}

	@Override
	public boolean checkAddCondition() {
		return TopListManager.SYNC_PATA <= this.pataId;
	}
	
	public int getPataId(){
		return this.pataId;
	}
	
	public void setPataId(int pataId){
		this.pataId = pataId;
	}
	
	public long getPataTime(){
		return this.pataTime;
	}
	
	public void setPataTime(int pataTime){
		this.pataTime = pataTime;
	}

	@Override
	public int compare(TopData otherTopData) {
		if (otherTopData instanceof PataTop) {
			PataTop othTop = (PataTop)otherTopData;
			if (othTop != null) {
				if (othTop.getPataId() > this.getPataId()) {
					return 1;
				}else if (othTop.getPataId() == this.getPataId()){
					if (this.getPataTime() > othTop.getPataTime()){
						return 1;
					}else if (othTop.getPataTime() == this.getPataTime()){
						return super.compare(otherTopData);
					}
				}
			}
		}
		return -1;
	}
}
