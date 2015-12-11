package com.game.goldraffle.structs;

import com.game.object.GameObject;

/**
 * 钻石抽奖宝箱数据
 *
 * @author xiaozhuoming 
 */
public class GoldRaffleData extends GameObject{
	
	private static final long serialVersionUID = 7164222563413472118L;
	
	//玩家ID
	private long playerId;
	
	//物品数据
	private String data;
	
	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "playerId = " + this.playerId + ", data = " + this.data;
	}
}
