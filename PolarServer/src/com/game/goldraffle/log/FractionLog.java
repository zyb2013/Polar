package com.game.goldraffle.log;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;
import org.apache.log4j.Logger;

/**
 * 积分兑换日志
 *
 * @author xiaozhuoming 
 */
public class FractionLog extends BaseLogBean {

	private static final Logger logger = Logger.getLogger("FractionLog");

	@Override
	public TableCheckStepEnum getRollingStep() {
		return TableCheckStepEnum.DAY;
	}

	@Override
	public void logToFile() {
		logger.error(buildSql());
	}

	private int sid;        //服务器ID
	private long roleid;	//玩家ID
	private int fraction;   //积分兑换需要的积分
	private String itemInfo;//积分兑换物品的数据

	@Log(logField = "sid", fieldType = "int")
	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	@Log(logField = "roleid", fieldType = "bigint")
	public long getRoleid() {
		return roleid;
	}

	public void setRoleid(long roleid) {
		this.roleid = roleid;
	}

	@Log(logField = "fraction", fieldType = "int")
	public int getFraction() {
		return fraction;
	}

	public void setFraction(int fraction) {
		this.fraction = fraction;
	}

	@Log(logField = "itemInfo", fieldType = "text")
	public String getItemInfo() {
		return itemInfo;
	}

	public void setItemInfo(String itemInfo) {
		this.itemInfo = itemInfo;
	}
	
}
