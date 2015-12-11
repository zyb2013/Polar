package com.game.casting.log;

import org.apache.log4j.Logger;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;

public class CastingExchangeLog extends BaseLogBean {
	
	private static final Logger logger = Logger.getLogger("CastingExchangeLog");

	private int sid;            	//服务器ID
	private long roleid;			//玩家ID
	private int technologyPoint;    //兑换物品花费的工艺度
	private String itemInfo;        //兑换物品的数据
	
	@Override
	public void logToFile() {
		logger.error(buildSql());
	}
	
	//分表时间
	@Override
	public TableCheckStepEnum getRollingStep() {
		return TableCheckStepEnum.DAY;
	}
	
	@Log(logField="sid",fieldType="int")
	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	@Log(logField="roleid",fieldType="bigint")
	public long getRoleid() {
		return roleid;
	}

	public void setRoleid(long roleid) {
		this.roleid = roleid;
	}

	@Log(logField="technologyPoint",fieldType="int")
	public int getTechnologyPoint() {
		return technologyPoint;
	}

	public void setTechnologyPoint(int technologyPoint) {
		this.technologyPoint = technologyPoint;
	}

	@Log(logField = "itemInfo", fieldType = "text")
	public String getItemInfo() {
		return itemInfo;
	}

	public void setItemInfo(String itemInfo) {
		this.itemInfo = itemInfo;
	}

}
