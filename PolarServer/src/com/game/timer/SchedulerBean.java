package com.game.timer;


/**
 * @author luminghua
 *
 * @date   2014年2月21日 下午2:25:24
 */
public class SchedulerBean {

	private String key;
	
	private String[][] cronExpression;//quartz表达式，包含方法和参数[[cronExp,method,param][cronExp,method,param]...]

	
	String getKey() {
		return key;
	}

	void setKey(String key) {
		this.key = key;
	}

	public String[][] getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String[][] cronExpression) {
		this.cronExpression = cronExpression;
	}
}
