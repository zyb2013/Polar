package com.game.db.ddl.struts;

import com.game.goldraffle.log.FractionLog;

/**
 * @author xiaozhuoming
 *   
 * @since 2014-01-07
 */
public class FractionDataStruts extends DBStruts {

	@Override
	public String primary() {
		return "";
	}

	@Override
	public String tableName() {
		return "fractionlog";
	}

	@Override
	public String className() {
		return FractionLog.class.getCanonicalName();
	}

	@Override
	public void buildDbStruts() 
	{
		add("sid", INT, 11, false);
		add("roleid", BIGINT, 20, false);
		add("fraction", INT, 11, false);
		add("itemInfo", LONGTEXT, 0, false);
	}

}
