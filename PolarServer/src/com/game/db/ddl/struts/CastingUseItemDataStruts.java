package com.game.db.ddl.struts;

import com.game.casting.log.CastingUseItemLog;

/**
 * @author xiaozhuoming
 * 
 */
public class CastingUseItemDataStruts extends DBStruts {

	@Override
	public String primary() {
		return "";
	}

	@Override
	public String tableName() {
		return "castinguseitemlog";
	}

	@Override
	public String className() {
		return CastingUseItemLog.class.getCanonicalName();
	}

	@Override
	public void buildDbStruts() 
	{
		add("sid", INT, 11, false);
		add("roleid", BIGINT, 20, false);
		add("type", INT, 11, false);
		add("value", INT, 11, true);
		add("castingGridInfo", LONGTEXT, 0, false);
	}
	
}
