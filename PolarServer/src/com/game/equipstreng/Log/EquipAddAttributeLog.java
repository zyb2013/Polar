package com.game.equipstreng.Log;

import org.apache.log4j.Logger;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;

public class EquipAddAttributeLog  extends BaseLogBean {
	// 装备追加日志
	private long playerid;		//玩家ID
	private long equiponlyid;	//道具唯一ID
	private int modelid;// 追加的道具模组ID
	private String consumeitem;		//消耗道具
	private int beforelv; // 追加前的追加等级
	private int money ;		//消耗金币
	private int result; // 追加结果，0失败，1成功
	private int targetlv; // 追加目标等级
	private int backwardslv ;	//失败后倒退等级
	private String failgiveitem; //失败给予道具
	private int startlv;// 开始等级（追加前）

	@Override
	public void logToFile() {
		logger.error(buildSql());
	}

	private static final Logger logger = Logger.getLogger("EquipAddAttributeLog");
//	发生时间，区服ID，玩家ID，装备ID，装备唯一身份识别号，消耗金币数，消耗材料ID，消耗材料数量，
	// 消耗钻石数，进行追加操作的当前星数，追加是否成功，追加失败倒退星数，追加失败返回物品序列ID以及数量，
	//@Log(logField="layer",fieldType="integer")
	//@Log(logField="goodsName",fieldType="varchar(40)")
	//@Log(logField="goodsOnlyid",fieldType="bigint")
	
	
	//分表时间
	@Override
	public TableCheckStepEnum getRollingStep() {
		return TableCheckStepEnum.MONTH;
	}


	@Log(logField="playerid",fieldType="bigint")
	public long getPlayerid() {
		return playerid;
	}



	public void setPlayerid(long playerid) {
		this.playerid = playerid;
	}


	@Log(logField="equiponlyid",fieldType="bigint")
	public long getEquiponlyid() {
		return equiponlyid;
	}



	public void setEquiponlyid(long equiponlyid) {
		this.equiponlyid = equiponlyid;
	}


	@Log(logField="consumeitem",fieldType="varchar(50)")
	public String getConsumeitem() {
		return consumeitem;
	}



	public void setConsumeitem(String consumeitem) {
		this.consumeitem = consumeitem;
	}


	@Log(logField="beforelv",fieldType="integer")
	public int getBeforelv() {
		return beforelv;
	}



	public void setBeforelv(int beforelv) {
		this.beforelv = beforelv;
	}


	@Log(logField="money",fieldType="integer")
	public int getMoney() {
		return money;
	}



	public void setMoney(int money) {
		this.money = money;
	}


	@Log(logField="result",fieldType="integer")
	public int getResult() {
		return result;
	}



	public void setResult(int result) {
		this.result = result;
	}


	@Log(logField="targetlv",fieldType="integer")
	public int getTargetlv() {
		return targetlv;
	}



	public void setTargetlv(int targetlv) {
		this.targetlv = targetlv;
	}


	@Log(logField="backwardslv",fieldType="integer")
	public int getBackwardslv() {
		return backwardslv;
	}



	public void setBackwardslv(int backwardslv) {
		this.backwardslv = backwardslv;
	}


	@Log(logField="failgiveitem",fieldType="longtext")
	public String getFailgiveitem() {
		return failgiveitem;
	}



	public void setFailgiveitem(String failgiveitem) {
		this.failgiveitem = failgiveitem;
	}

	@Log(logField="startlv",fieldType="integer")
	public int getStartlv() {
		return startlv;
	}


	public void setStartlv(int startlv) {
		this.startlv = startlv;
	}

	@Log(logField="modelid",fieldType="integer")
	public int getModelid() {
		return modelid;
	}


	public void setModelid(int modelid) {
		this.modelid = modelid;
	}
	

	
	
	
	
	
}
