package com.game.task.log;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;

public class MainTaskLog extends BaseLogBean {
	
	private long   roleId;
	private int type;//1接受任务，2完成任务
	private int taskModelId;
	private int level;
	
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
	@Log(logField="type",fieldType="int")
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	@Log(logField="taskModelId",fieldType="int")
	public int getTaskModelId() {
		return taskModelId;
	}
	public void setTaskModelId(int taskModelId) {
		this.taskModelId = taskModelId;
	}
	@Log(logField="level",fieldType="int")
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
	
	
}
