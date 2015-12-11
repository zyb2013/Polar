package com.game.casting.log;

import org.apache.log4j.Logger;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;

public class CastingUseItemLog extends BaseLogBean {
	
	private static final Logger logger = Logger.getLogger("CastingUseItemLog");

	private int sid;            	//服务器ID
	private long roleid;			//玩家ID
	private int type;		    	//操作铸造奖励仓库物品的类型,1表示取出;2表示出售;3表示分解
	private int value;		        //type=2时表示出售该格子物品获得的金币数量;type=3时表示分解该格子物品获得的工艺数量
	private String castingGridInfo; //使用铸造工厂物品的数据
	
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

	@Log(logField="type",fieldType="int")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Log(logField="value",fieldType="int")
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Log(logField = "castingGridInfo", fieldType = "text")
	public String getCastingGridInfo() {
		return castingGridInfo;
	}

	public void setCastingGridInfo(String castingGridInfo) {
		this.castingGridInfo = castingGridInfo;
	}

}
