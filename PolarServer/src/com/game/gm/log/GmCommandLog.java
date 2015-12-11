package com.game.gm.log;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;
import com.game.player.structs.Player;

public class GmCommandLog extends BaseLogBean {

	@Override
	public TableCheckStepEnum getRollingStep() {
		return TableCheckStepEnum.MONTH;
	}

	@Override
	public void logToFile() {
		logger.error(buildSql());
	}

	private String username;
	private String userid;
	private String loginip;
	private String rolename;
	private String roleid;
	private int rolelevel;
	private String command;
	private int gmlevel;
	private int type; //1-游戏内使用  2-外部调用
	
	public GmCommandLog() {
		super();
	}

	public GmCommandLog(Player player) {
		username = player.getUserName();
		userid = player.getUserId();
		rolename = player.getName();
		roleid = String.valueOf(player.getId());
		rolelevel = player.getLevel();
		gmlevel = player.getGmlevel();
		loginip = player.getLoginIp();
	}

	@Log(logField="type",fieldType="integer")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Log(logField="loginip",fieldType="varchar(50)")
	public String getLoginip() {
		return loginip;
	}

	public void setLoginip(String loginip) {
		this.loginip = loginip;
	}

	@Log(logField="username",fieldType="varchar(255)")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	@Log(logField="userid",fieldType="varchar(255)")
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	@Log(logField="rolename",fieldType="varchar(255)")
	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}
	@Log(logField="roleid",fieldType="varchar(255)")
	public String getRoleid() {
		return roleid;
	}

	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}
	@Log(logField="rolelevel",fieldType="integer")
	public int getRolelevel() {
		return rolelevel;
	}

	public void setRolelevel(int rolelevel) {
		this.rolelevel = rolelevel;
	}
	@Log(logField="command",fieldType="varchar(255)")
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	@Log(logField="gmlevel",fieldType="integer")
	public int getGmlevel() {
		return gmlevel;
	}

	public void setGmlevel(int gmlevel) {
		this.gmlevel = gmlevel;
	}
	
}
