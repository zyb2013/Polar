package com.game.db.ddl.struts;

import com.game.db.bean.ServerParam;


public class ProtectStruts extends DBStruts {

	@Override
	public String primary() {
		return "userid";
	}

	@Override
	public String tableName() {
		return "protect";
	}

	
	
	
	@Override
	public String className() {
		return ServerParam.class.getCanonicalName();
	}

	@Override
	public void buildDbStruts() {
		add("userid",VARCHAR, 100, false);
		add("password",VARCHAR, 100, true);
		add("mail", VARCHAR,100, true);
		
	}
	
}
