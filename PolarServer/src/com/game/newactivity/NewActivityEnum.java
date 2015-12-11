package com.game.newactivity;

import com.game.newactivity.impl.EliteRank;
import com.game.newactivity.impl.Exchange;
import com.game.newactivity.impl.FightRank;
import com.game.newactivity.impl.FirstPay;
import com.game.newactivity.impl.Forum1;
import com.game.newactivity.impl.Forum2;
import com.game.newactivity.impl.Forum3;
import com.game.newactivity.impl.LevelRank;
import com.game.newactivity.impl.MountRank;
import com.game.newactivity.impl.Pay4Gift;
import com.game.newactivity.impl.PayRank;
import com.game.newactivity.impl.Siegecraft;

/**
 * @author luminghua
 *
 * @date   2014年2月24日 下午3:14:06
 */
public enum NewActivityEnum {

	//新服大放送
	Exchange(1,Exchange.class),
	//冲级排行榜
	LevelRank(2,LevelRank.class),
	//攻城争夺战
	Siegecraft(3,Siegecraft.class),
	//精英挑战日
	EliteRank(4,EliteRank.class),
	//充值排行榜
	PayRank(5,PayRank.class),
	//缤纷秀坐骑
	MountRank(6,MountRank.class),
	//战斗终结者
	FightRank(7,FightRank.class),
	//首充大礼包
	FirstPay(8,FirstPay.class),
	//充值有礼
	Pay4Gift(9,Pay4Gift.class),
	//论坛活动一
	Forum1(10,Forum1.class),
	//论坛活动二
	Forum2(11,Forum2.class),
	//论坛活动三
	Forum3(12,Forum3.class);
	
	private int id;
	private Class<? extends AbstractActivity> _class;
	
	NewActivityEnum(int id,Class<? extends AbstractActivity> _class) {
		this.id = id;
		this._class = _class;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Class<? extends AbstractActivity> get_class() {
		return _class;
	}

	public void set_class(Class<? extends AbstractActivity> _class) {
		this._class = _class;
	}
	
	public static NewActivityEnum getByActivityId(int activityId) {
		return values()[activityId-1];
	}
}
