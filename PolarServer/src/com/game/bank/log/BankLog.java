package com.game.bank.log;

import org.apache.log4j.Logger;
import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;

public class BankLog extends BaseLogBean {
	
	private static final Logger logger = Logger.getLogger("BankLog");

	private int sid;            //服务器ID
	private long playerId;		//玩家ID
	private int level;		    //玩家等级
	private int vip;		    //玩家VIP
	private int type;		    //操作类型  
	private int count;		    //数量
	
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



	@Log(logField="count",fieldType="int")
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
