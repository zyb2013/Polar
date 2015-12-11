package com.game.db.ddl.struts;

import com.game.casting.log.CastingExchangeLog;

/**
 * @author xiaozhuoming
 *   
 */
public class CastingExchangeDataStruts extends DBStruts {

	@Override
	public String primary() {
		return "";
	}

	@Override
	public String tableName() {
		return "castingexchangelog";
	}

	@Override
	public String className() {
		return CastingExchangeLog.class.getCanonicalName();
	}

	@Override
	public void buildDbStruts() 
	{
		add("sid", INT, 11, false);
		add("roleid", BIGINT, 20, false);
		add("technologyPoint", INT, 11, false);
		add("itemInfo", LONGTEXT, 0, false);
	}

}
