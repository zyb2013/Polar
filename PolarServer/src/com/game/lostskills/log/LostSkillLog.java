package com.game.lostskills.log;

import org.apache.log4j.Logger;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;
import com.game.player.structs.Player;

/**
 * 遗落技能升级日志
 * @author hongxiao.z
 * @date   2014-2-18  下午9:00:59
 */
public class LostSkillLog extends BaseLogBean {

	private static Logger log = Logger.getLogger(LostSkillLog.class);
	
	@Override
	public TableCheckStepEnum getRollingStep() {
		return TableCheckStepEnum.MONTH;
	}

	@Override
	public void logToFile() {
		log.error(buildSql());
	}
	//
	public LostSkillLog() {
		super();
	}
	
	public LostSkillLog(Player player) {
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
	private int skillId;
	private int skillType;
	private int skillLevel;
	private String consumeInfo;
	private String addAttr;

	@Log(logField="skill_id",fieldType="int")
	public int getSkillId() {
		return skillId;
	}

	@Log(logField="skill_type",fieldType="int")
	public int getSkillType() {
		return skillType;
	}

	@Log(logField="skill_level",fieldType="int")
	public int getSkillLevel() {
		return skillLevel;
	}

	@Log(logField="consume_info",fieldType="varchar(1024)")
	public String getConsumeInfo() {
		return consumeInfo;
	}

	@Log(logField="add_attr",fieldType="varchar(1024)")
	public String getAddAttr() {
		return addAttr;
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

	public static Logger getLog() {
		return log;
	}
	
	public void setAddAttr(String addAttr) {
		this.addAttr = addAttr;
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

	public static void setLog(Logger log) {
		LostSkillLog.log = log;
	}
	
	public void setSkillId(int skillId) {
		this.skillId = skillId;
	}
	
	public void setSkillLevel(int skillLevel) {
		this.skillLevel = skillLevel;
	}
	
	public void setSkillType(int skillType) {
		this.skillType = skillType;
	}
	
	public void setConsumeInfo(String consumeInfo) {
		this.consumeInfo = consumeInfo;
	}
}
