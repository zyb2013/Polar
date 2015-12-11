package com.game.stalls.Log;

import org.apache.log4j.Logger;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;

public class StallsLog  extends BaseLogBean {
	private String stallsuserid;	//摊主userid
	private String stallsusername;	//摊主用户名
	private long stallsId;			//摊主ID
	private String stallsrolename;	//摊主角色名
	private int stallslevel;		//摊主等级
	
	private String goodsName;		//交易道具名字
	private long goodsOnlyid;		//道具唯一ID
	private int goodsModid;			//道具模组ID
	private int goodsNum;			//数量
	private String goodsInfo;		//道具详细信息
	private int Pricegold ;			//定价金币
	private int Priceyuanbao ;		//定价钻石

	private String buyuserid;		//购买者userid
	private String buyusername;		//购买者用户名
	private long buyId;				//购买者ID	//-1商品上架，-2商品调整，-3商品下架
	private String buyrolename; 	//购买者角色名
	private int buylevel;			//购买者等级
	
	@Override
	public void logToFile() {
		logger.error(buildSql());
	}
	private static final Logger logger=Logger.getLogger("StallsLog");
	
	
	//分表时间
	@Override
	public TableCheckStepEnum getRollingStep() {
		return TableCheckStepEnum.MONTH;
	}
	
	@Log(logField="stallsuserid",fieldType="varchar(255)")
	public String getStallsuserid() {
		return stallsuserid;
	}

	public void setStallsuserid(String stallsuserid) {
		this.stallsuserid = stallsuserid;
	}
	
	@Log(logField="stallsusername",fieldType="varchar(255)")
	public String getStallsusername() {
		return stallsusername;
	}
	
	public void setStallsusername(String stallsusername) {
		this.stallsusername = stallsusername;
	}

	@Log(logField="stallsrolename",fieldType="varchar(255)")
	public String getStallsrolename() {
		return stallsrolename;
	}

	public void setStallsrolename(String stallsrolename) {
		this.stallsrolename = stallsrolename;
	}

	@Log(logField="stallslevel",fieldType="integer")
	public int getStallslevel() {
		return stallslevel;
	}

	public void setStallslevel(int stallslevel) {
		this.stallslevel = stallslevel;
	}

	@Log(logField="buyuserid",fieldType="varchar(255)")
	public String getBuyuserid() {
		return buyuserid;
	}

	public void setBuyuserid(String buyuserid) {
		this.buyuserid = buyuserid;
	}

	@Log(logField="buyusername",fieldType="varchar(255)")
	public String getBuyusername() {
		return buyusername;
	}

	public void setBuyusername(String buyusername) {
		this.buyusername = buyusername;
	}

	@Log(logField="buyrolename",fieldType="varchar(255)")
	public String getBuyrolename() {
		return buyrolename;
	}

	public void setBuyrolename(String buyrolename) {
		this.buyrolename = buyrolename;
	}

	@Log(logField="buylevel",fieldType="integer")
	public int getBuylevel() {
		return buylevel;
	}

	public void setBuylevel(int buylevel) {
		this.buylevel = buylevel;
	}

	@Log(logField="stallsId",fieldType="bigint")
	public long getStallsId() {
		return stallsId;
	}

	public void setStallsId(long stallsId) {
		this.stallsId = stallsId;
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
	
	
	@Log(logField="buyId",fieldType="bigint")
	public long getBuyId() {
		return buyId;
	}

	public void setBuyId(long buyId) {
		this.buyId = buyId;
	}

	
	
	@Log(logField="Pricegold",fieldType="integer")
	public int getPricegold() {
		return Pricegold;
	}


	public void setPricegold(int pricegold) {
		Pricegold = pricegold;
	}

	@Log(logField="Priceyuanbao",fieldType="integer")
	public int getPriceyuanbao() {
		return Priceyuanbao;
	}


	public void setPriceyuanbao(int priceyuanbao) {
		Priceyuanbao = priceyuanbao;
	}
	
	

	
	
	
	
	
}
