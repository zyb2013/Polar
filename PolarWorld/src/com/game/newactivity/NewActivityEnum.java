package com.game.newactivity;

/**
 * @author luminghua
 *
 * @date   2014年2月24日 下午3:14:06
 */
public enum NewActivityEnum {

	//新服大放送
	Exchange(1),
	//冲级排行榜
	LevelRank(2),
	//攻城争夺战
	Siegecraft(3),
	//精英挑战日
	EliteRank(4),
	//充值排行榜
	PayRank(5),
	//缤纷秀坐骑
	MountRank(6),
	//战斗终结者
	FightRank(7),
	//首充大礼包
	FirstPay(8),
	//充值有礼
	Pay4Gift(9);
	
	private int id;
	
	NewActivityEnum(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static NewActivityEnum getByActivityId(int activityId) {
		return values()[activityId-1];
	}
}
