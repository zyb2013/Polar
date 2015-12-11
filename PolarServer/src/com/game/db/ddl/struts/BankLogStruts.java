package com.game.db.ddl.struts;

import com.game.bank.log.BankLog;

/**
 * 
 * @author  
 * @2012-11-19 下午3:29:24
 */
public class BankLogStruts extends DBStruts {
//	CREATE TABLE `goldrechargelog` (
//			  `oid` varchar(255) NOT NULL COMMENT '订单号',
//			  `uid` varchar(255) NOT NULL,
//			  `serverid` varchar(255) NOT NULL COMMENT '服务器ID',
//			  `gold` int(11) NOT NULL COMMENT '钻石数',
//			  `time` bigint(20) NOT NULL COMMENT '充值时间',
//			  `type` int(11) NOT NULL COMMENT '充值类型',
//			  `userid` bigint(20) NOT NULL DEFAULT '0',
//			  `rmb` varchar(512) DEFAULT NULL,
//			  `content` text,
//			  PRIMARY KEY (`oid`),
//			  KEY `userid` (`userid`,`type`)
//			) ENGINE=MyISAM DEFAULT CHARSET=utf8;
	@Override
	public void buildDbStruts() {
		add("sid", INT, 11, false);
		add("playerId", BIGINT, 20, false);
		add("level", INT, 11, false);
		add("vip", INT, 11, false);
		add("type", INT, 11, false);
		add("count", INT, 11, false);
	}

	@Override
	public String primary() {
		return "";
	}

	@Override
	public String tableName() {
		return "bankLog";
	}

	@Override
	public String className() {
		return BankLog.class.getCanonicalName();
	}


}
