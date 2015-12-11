package com.game.db.ddl.struts;

import com.game.pray.log.PrayLog;

/**
 * @author xiaozhuoming
 *   
 * @2014-01-02
 */
public class PrayDataStruts extends DBStruts {

	@Override
	public String primary() {
		return "";
	}

	@Override
	public String tableName() {
		return "praylog";
	}

	@Override
	public String className() {
		return PrayLog.class.getCanonicalName();
	}

	@Override
	public void buildDbStruts() 
	{
		add("sid", INT, 11, false);
		add("playerId", BIGINT, 20, false);
		add("level", INT, 11, false);
		add("vip", INT, 11, false);
		add("type", INT, 11, false);
		add("prayTimes", INT, 11, false);
		add("cost", INT, 11, false);
		add("count", INT, 11, false);
	}

}
