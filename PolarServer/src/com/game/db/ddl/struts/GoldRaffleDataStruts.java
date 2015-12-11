package com.game.db.ddl.struts;

import com.game.goldraffle.log.GoldRaffleLog;

/**
 * @author xiaozhuoming
 *   
 * @since 2014-01-07
 */
public class GoldRaffleDataStruts extends DBStruts {

	@Override
	public String primary() {
		return "";
	}

	@Override
	public String tableName() {
		return "goldrafflelog";
	}

	@Override
	public String className() {
		return GoldRaffleLog.class.getCanonicalName();
	}

	@Override
	public void buildDbStruts() 
	{
		add("sid", INT, 11, false);
		add("roleid", BIGINT, 20, false);
		add("type", INT, 11, false);
		add("coupon", INT, 11, true);
		add("gold", INT, 11, true);
		add("fraction", INT, 11, true);
		add("opennum", INT, 11, false);
		add("goldRaffleGridInfo", LONGTEXT, 0, false);
	}

}
