package com.game.db.ddl.struts;

import com.game.casting.log.CastingRewardLog;

/**
 * @author xiaozhuoming
 * 
 */
public class CastingRewardDataStruts extends DBStruts {

	@Override
	public String primary() {
		return "";
	}

	@Override
	public String tableName() {
		return "castingrewardlog";
	}

	@Override
	public String className() {
		return CastingRewardLog.class.getCanonicalName();
	}

	@Override
	public void buildDbStruts() 
	{
		add("sid", INT, 11, false);
		add("roleid", BIGINT, 20, false);
		add("level", INT, 11, false);
		add("job", INT, 11, true);
		add("cost", INT, 11, true);
		add("castingGridInfo", LONGTEXT, 0, false);
	}

}
