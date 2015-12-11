package com.game.liveness.log;

import org.apache.log4j.Logger;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;
import com.game.player.structs.Player;

/**
 * 活跃度宝箱领取日志实体
 * @author hongxiao.z
 * @date   2014-1-10  上午10:48:09
 */
public class LivenessLog extends BaseLogBean {

	private static Logger log = Logger.getLogger(LivenessLog.class);
	
	@Override
	public TableCheckStepEnum getRollingStep() {
		return TableCheckStepEnum.MONTH;
	}

	@Override
	public void logToFile() {
		log.error(buildSql());
	}
	//
	public LivenessLog() {
		super();
	}
	
	public LivenessLog(Player player) {
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
	private int boxid; 
	private String content; //领取的具体内容

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

	@Log(logField="box_id",fieldType="int")
	public int getBoxid() {
		return boxid;
	}

	public void setBoxid(int boxid) {
		this.boxid = boxid;
	}

	@Log(logField="content",fieldType="varchar(1024)")
	public String getContent() {
		return content;
	}

	public static void setLog(Logger log) {
		LivenessLog.log = log;
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

	public void setContent(String content) {
		this.content = content;
	}

}
