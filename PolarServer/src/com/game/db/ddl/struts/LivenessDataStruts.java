package com.game.db.ddl.struts;

import com.game.db.bean.LivenessBean;

/**
 * 
 * @author  
 * @2012-11-19 下午3:29:56
 */
public class LivenessDataStruts extends DBStruts {

	@Override
	public String primary() {
		return "roleid";
	}

	@Override
	public String tableName() {
		return "liveness_data";
	}

	@Override
	public String className() {
		return LivenessBean.class.getCanonicalName();
	}

	@Override
	public void buildDbStruts() 
	{
		add("roleid", BIGINT, 20,false);
		add("liveness", INT, 50, false);
		add("gain_state", LONGTEXT, 0, false);
		add("events", LONGTEXT, 0, false);
	}

}
