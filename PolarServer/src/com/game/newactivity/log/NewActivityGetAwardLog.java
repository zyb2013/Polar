package com.game.newactivity.log;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;

public class NewActivityGetAwardLog extends BaseLogBean {
	
	private long roleId;
	private int activityId;
	private String awards;
	
	@Override
	public TableCheckStepEnum getRollingStep() {
		return TableCheckStepEnum.MONTH;
	}
	@Override
	public void logToFile() {
		logger.error(buildSql());
	}
	
	@Log(logField="roleId",fieldType="bigint")
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	@Log(logField="activityId",fieldType="int")
	public int getActivityId() {
		return activityId;
	}
	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}
	@Log(logField="awards",fieldType="varchar(255)")
	public String getAwards() {
		return awards;
	}
	public void setAwards(String awards) {
		this.awards = awards;
	}
	
}
