package com.game.vip.log;

import org.apache.log4j.Logger;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;

//vip功能日志
public class RoleVipLog extends BaseLogBean {

	private static final Logger logger=Logger.getLogger("RoleVipLog");

	private long playerid; // 用户id
	private int type; // 0-增加经验点，1-成为vip 2-vip续费 3-升级 4-vip消失 5-降级
	private int befvipid; // 操作前vipLevel
	private int aftvipid; // 操作后vipLevel
	private long actionid; // actionid
	private int exp;// 当前的经验点
	private int expiretime;// 过期时间

	public RoleVipLog() {
		super();
	}

	@Override
	public TableCheckStepEnum getRollingStep() {
		return TableCheckStepEnum.MONTH;
	}

	@Override
	public void logToFile() {
		logger.error(buildSql());
	}
	
	@Log(logField="type",fieldType="int")
	public int getType() {
		return type;
	}
	
	@Log(logField="befvipid",fieldType="int")
	public int getBefvipid() {
		return befvipid;
	}
	
	@Log(logField="aftvipid",fieldType="int")
	public int getAftvipid() {
		return aftvipid;
	}
	
	@Log(logField="actionid",fieldType="bigint")
	public long getActionid() {
		return actionid;
	}

	@Log(logField = "playerid", fieldType = "bigint")
	public long getPlayerid() {
		return playerid;
	}

	@Log(logField = "expiretime", fieldType = "int")
	public int getExpiretime() {
		return expiretime;
	}

	@Log(logField = "exp", fieldType = "int")
	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public void setPlayerid(long playerid) {
		this.playerid = playerid;
	}


	public void setExpiretime(int expiretime) {
		this.expiretime = expiretime;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setBefvipid(int befvipid) {
		this.befvipid = befvipid;
	}

	public void setAftvipid(int aftvipid) {
		this.aftvipid = aftvipid;
	}

	public void setActionid(long actionid) {
		this.actionid = actionid;
	}
	
	
}
