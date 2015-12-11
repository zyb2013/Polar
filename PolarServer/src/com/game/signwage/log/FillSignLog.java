package com.game.signwage.log;

import org.apache.log4j.Logger;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;
import com.game.player.structs.Player;

/**
 * 角色补签日志
 * @author hongxiao.z
 * @date   2014-1-10  上午10:48:09
 */
public class FillSignLog extends BaseLogBean {

	private static Logger log = Logger.getLogger(FillSignLog.class);
	
	@Override
	public TableCheckStepEnum getRollingStep() {
		return TableCheckStepEnum.MONTH;
	}

	@Override
	public void logToFile() {
		log.error(buildSql());
	}
	//
	public FillSignLog() {
		super();
	}
	
	public FillSignLog(Player player) {
		super();
		this.userid = player.getUserId();
		this.roleid = String.valueOf(player.getId());
		this.username = player.getUserName();
	}
	//
	private String userid;
	private String roleid;
	private String username;
	private int sid;
	private int day; 	//是补签第几号

	public static Logger getLog() {
		return log;
	}

	@Log(logField="userid",fieldType="bigint")
	public String getUserid() {
		return userid;
	}

	@Log(logField="roleid",fieldType="bigint")
	public String getRoleid() {
		return roleid;
	}

	@Log(logField="username",fieldType="varchar(1024)")
	public String getUsername() {
		return username;
	}

	@Log(logField="sid",fieldType="int")
	public int getSid() {
		return sid;
	}
	
	@Log(logField="day",fieldType="int")
	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public static void setLog(Logger log) {
		FillSignLog.log = log;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}
}
