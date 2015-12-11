package com.game.db.ddl.struts;

import com.game.db.bean.ServerParam;

/**
 * 钻石消耗
 * @author Administrator
 *
 */
public class GoldExpendStruts extends DBStruts {

	@Override
	public String primary() {
		return "unuse_index";
	}

	@Override
	public String tableName() {
		return "gold_expend";
	}

	@Override
	public String className() {
		return ServerParam.class.getCanonicalName();
	}

	@Override
	public void buildDbStruts() {
		add("unuse_index", BIGINT, 20, false);
		add("time", BIGINT, 20, false);
		add("roleid", BIGINT, 20, false);
		add("goldnum", INT, 11, false);
		add("reason", INT, 11, false);
	}

}
