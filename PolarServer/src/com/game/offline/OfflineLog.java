/**
 * 
 */
package com.game.offline;

import org.apache.log4j.Logger;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;

/**
 * @author luminghua
 *
 * @date   2014年1月13日 下午8:46:13
 */
public class OfflineLog extends BaseLogBean {
	
	private static final Logger logger = Logger.getLogger("OfflineLog");
	
	private long playerid;
	private int offlinevalue;//追回值
	private int exp;//获取的经验
	private int gold;//扣除的钻石

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

	@Log(logField="offlinevalue",fieldType="int")
	public int getOfflinevalue() {
		return offlinevalue;
	}


	public void setOfflinevalue(int offlinevalue) {
		this.offlinevalue = offlinevalue;
	}

	@Log(logField="exp",fieldType="int")
	public int getExp() {
		return exp;
	}


	public void setExp(int exp) {
		this.exp = exp;
	}

	@Log(logField="gold",fieldType="int")
	public int getGold() {
		return gold;
	}


	public void setGold(int gold) {
		this.gold = gold;
	}


	@Override
	public void logToFile() {
		logger.error(buildSql());
	}

}
