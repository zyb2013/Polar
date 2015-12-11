package com.game.pray.log;

import org.apache.log4j.Logger;
import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;

public class PrayLog extends BaseLogBean {
	
	private static final Logger logger = Logger.getLogger("PrayLog");

	private int sid;            //服务器ID
	private long playerId;		//玩家ID
	private int level;		    //玩家等级
	private int vip;		    //玩家VIP
	private int type;		    //祈愿类型: 1-祈愿金币;2-祈愿经验
	private int prayTimes;		    //玩家当前的祈愿次数
	private int cost;		    //玩家祈愿花费的钻石数量
	private int count;		    //玩家祈愿得到的金币或者经验数量
	
	@Override
	public void logToFile() {
		logger.error(buildSql());
	}
	
	//分表时间
	@Override
	public TableCheckStepEnum getRollingStep() {
		return TableCheckStepEnum.DAY;
	}
	
	@Log(logField="sid",fieldType="int")
	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	@Log(logField="playerId",fieldType="bigint")
	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	@Log(logField="level",fieldType="int")
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Log(logField="vip",fieldType="int")
	public int getVip() {
		return vip;
	}

	public void setVip(int vip) {
		this.vip = vip;
	}

	@Log(logField="type",fieldType="int")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Log(logField="prayTimes",fieldType="int")
	public int getPrayTimes() {
		return prayTimes;
	}

	public void setPrayTimes(int prayTimes) {
		this.prayTimes = prayTimes;
	}

	@Log(logField="cost",fieldType="int")
	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	@Log(logField="count",fieldType="int")
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
