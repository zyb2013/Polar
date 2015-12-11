/**
 * 
 */
package com.game.equipstreng.Log;

import org.apache.log4j.Logger;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;

/**
 * @author luminghua
 *
 * @date   2014年1月13日 下午6:03:06
 */
public class EquipComposeLog extends BaseLogBean {

	
	private static final Logger logger = Logger.getLogger("EquipComposeLog");
	
	private long playerid;		//玩家ID
	private int composeid;//合成表id
	private int modelid;//合成的道具模板id
	private long itemid;//合成的道具id
	private String matrial;//要消耗的材料
	private int result;//成功还是失败
	
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

	@Log(logField="composeid",fieldType="int")
	public int getComposeid() {
		return composeid;
	}


	public void setComposeid(int composeid) {
		this.composeid = composeid;
	}


	@Log(logField="modelid",fieldType="int")
	public int getModelid() {
		return modelid;
	}


	public void setModelid(int modelid) {
		this.modelid = modelid;
	}

	@Log(logField="itemid",fieldType="bigint")
	public long getItemid() {
		return itemid;
	}


	public void setItemid(long itemid) {
		this.itemid = itemid;
	}

	@Log(logField="matrial",fieldType="varchar(500)")
	public String getMatrial() {
		return matrial;
	}



	public void setMatrial(String matrial) {
		this.matrial = matrial;
	}


	@Log(logField="result",fieldType="int")
	public int getResult() {
		return result;
	}



	public void setResult(int result) {
		this.result = result;
	}



	@Override
	public void logToFile() {
		logger.error(buildSql());
	}

}
