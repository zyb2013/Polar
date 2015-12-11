package com.game.goldraffle.log;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;
import org.apache.log4j.Logger;

/**
 * 钻石抽奖日志
 *
 * @author xiaozhuoming 
 */
public class GoldRaffleLog extends BaseLogBean {

	private static final Logger logger = Logger.getLogger("GoldRaffleLog");

	@Override
	public TableCheckStepEnum getRollingStep() {
		return TableCheckStepEnum.DAY;
	}

	@Override
	public void logToFile() {
		logger.error(buildSql());
	}

	private int sid;              //服务器ID
	private long roleid;		  //玩家ID
	private int type;		      //抽奖类型,1-表示抽1次;2-表示抽10次;3-表示抽50次
	private int coupon;        	  //花费的优惠券
	private int gold;             //花费的钻石
	private int fraction;         //增加的积分
	private int opennum;          //抽奖次数
	private String goldRaffleGridInfo; //抽奖物品数据

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

	@Log(logField = "type", fieldType = "int")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Log(logField = "coupon", fieldType = "int")
	public int getCoupon() {
		return coupon;
	}

	public void setCoupon(int coupon) {
		this.coupon = coupon;
	}

	@Log(logField = "gold", fieldType = "int")
	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	@Log(logField = "fraction", fieldType = "int")
	public int getFraction() {
		return fraction;
	}

	public void setFraction(int fraction) {
		this.fraction = fraction;
	}

	@Log(logField = "opennum", fieldType = "int")
	public int getOpennum() {
		return opennum;
	}

	public void setOpennum(int opennum) {
		this.opennum = opennum;
	}

	@Log(logField = "goldRaffleGridInfo", fieldType = "text")
	public String getGoleRaffleGridInfo() {
		return goldRaffleGridInfo;
	}

	public void setGoldRaffleGridInfo(String goleRaffleGridInfo) {
		this.goldRaffleGridInfo = goleRaffleGridInfo;
	}
	
}
