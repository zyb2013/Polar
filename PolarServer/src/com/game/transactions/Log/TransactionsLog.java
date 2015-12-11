package com.game.transactions.Log;

import org.apache.log4j.Logger;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;

public class TransactionsLog extends BaseLogBean {
	private long tradingId;		//本次交易ID
	
	private String goodsName;	//交易道具名字
	private long goodsOnlyid;	//道具唯一ID
	private int goodsModid;	//道具模组ID
	private int goodsNum;		//数量
	private String goodsInfo;	//道具详细信息
	
	private String outuserid;		//交易出 用户ID
	private String outusername;		//交易出 用户名
	private long outId;				//交易出 玩家ID
	private String outrolename;		//交易出 角色名
	private int outlevel;			//交易出 角色等级
	private String intouserid;		//交易入 用户ID
	private String intousername;	//交易入 用户名
	private long intoId;			//交易入 玩家ID
	private String intorolename;	//交易入 角色名
	private int intolevel;			//交易入 角色等级

	@Override
	public void logToFile() {
		logger.error(buildSql());
	}
	
	private static final Logger logger=Logger.getLogger("TransactionsLog.class");
	
	//分表时间
	@Override
	public TableCheckStepEnum getRollingStep() {
		return TableCheckStepEnum.MONTH;
	}

	@Log(logField="outuserid",fieldType="varchar(255)")
	public String getOutuserid() {
		return outuserid;
	}

	public void setOutuserid(String outuserid) {
		this.outuserid = outuserid;
	}
	
	@Log(logField="outusername",fieldType="varchar(255)")
	public String getOutusername() {
		return outusername;
	}

	public void setOutusername(String outusername) {
		this.outusername = outusername;
	}

	@Log(logField="outrolename",fieldType="varchar(255)")
	public String getOutrolename() {
		return outrolename;
	}

	public void setOutrolename(String outrolename) {
		this.outrolename = outrolename;
	}

	@Log(logField="outlevel",fieldType="integer")
	public int getOutlevel() {
		return outlevel;
	}

	public void setOutlevel(int outlevel) {
		this.outlevel = outlevel;
	}

	@Log(logField="intouserid",fieldType="varchar(255)")
	public String getIntouserid() {
		return intouserid;
	}

	public void setIntouserid(String intouserid) {
		this.intouserid = intouserid;
	}

	@Log(logField="intousername",fieldType="varchar(255)")
	public String getIntousername() {
		return intousername;
	}

	public void setIntousername(String intousername) {
		this.intousername = intousername;
	}

	@Log(logField="intorolename",fieldType="varchar(255)")
	public String getIntorolename() {
		return intorolename;
	}

	public void setIntorolename(String intorolename) {
		this.intorolename = intorolename;
	}

	@Log(logField="intolevel",fieldType="integer")
	public int getIntolevel() {
		return intolevel;
	}

	public void setIntolevel(int intolevel) {
		this.intolevel = intolevel;
	}

	@Log(logField="tradingId",fieldType="bigint")
	public long getTradingId() {
		return tradingId;
	}

	public void setTradingId(long tradingId) {
		this.tradingId = tradingId;
	}
	
	
	
	@Log(logField="goodsName",fieldType="varchar(80)")
	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	
	@Log(logField="goodsOnlyid",fieldType="bigint")
	public long getGoodsOnlyid() {
		return goodsOnlyid;
	}

	public void setGoodsOnlyid(long goodsOnlyid) {
		this.goodsOnlyid = goodsOnlyid;
	}
	
	
	
	@Log(logField="goodsModid",fieldType="integer")
	public int getGoodsModid() {
		return goodsModid;
	}

	public void setGoodsModid(int goodsModid) {
		this.goodsModid = goodsModid;
	}
	
	

	@Log(logField="goodsNum",fieldType="integer")
	public int getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(int goodsNum) {
		this.goodsNum = goodsNum;
	}
	
	
	@Log(logField="goodsInfo",fieldType="text")
	public String getGoodsInfo() {
		return goodsInfo;
	}

	public void setGoodsInfo(String goodsInfo) {
		this.goodsInfo = goodsInfo;
	}
	
	
	@Log(logField="outId",fieldType="bigint")
	public long getOutId() {
		return outId;
	}

	public void setOutId(long outId) {
		this.outId = outId;
	}
	
	
	
	@Log(logField="intoId",fieldType="bigint")
	public long getIntoId() {
		return intoId;
	}

	public void setIntoId(long intoId) {
		this.intoId = intoId;
	}

}
