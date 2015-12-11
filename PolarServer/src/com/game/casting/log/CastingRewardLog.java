package com.game.casting.log;

import org.apache.log4j.Logger;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;

public class CastingRewardLog extends BaseLogBean {
	
	private static final Logger logger = Logger.getLogger("CastingRewardLog");

	private int sid;            	//服务器ID
	private long roleid;			//玩家ID
	private int level;		    	//玩家等级
	private int job;		    	//玩家职业
	private int cost;		        //玩家铸造花费金币数量
	private String castingGridInfo; //抽奖物品数据
	
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

	@Log(logField="roleid",fieldType="bigint")
	public long getRoleid() {
		return roleid;
	}

	public void setRoleid(long roleid) {
		this.roleid = roleid;
	}

	@Log(logField="level",fieldType="int")
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Log(logField="job",fieldType="int")
	public int getJob() {
		return job;
	}

	public void setJob(int job) {
		this.job = job;
	}

	@Log(logField="cost",fieldType="int")
	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	@Log(logField = "castingGridInfo", fieldType = "text")
	public String getCastingGridInfo() {
		return castingGridInfo;
	}

	public void setCastingGridInfo(String castingGridInfo) {
		this.castingGridInfo = castingGridInfo;
	}

}
