package com.game.backpack.log;

import org.apache.log4j.Logger;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;

/**
 * 物品变更日志
 * @author  
 *
 */
public class ItemChangeLog extends BaseLogBean {
	/**
	 * 
	 * @param roleid	角色ID
	 * @param itemid	物品ID
	 * @param modelid	模型 ID
	 * @param num		变更数量
	 * @param itembeforeInfo	变更之前
	 * @param itemafterInfo		变更之后
	 * @param reason	变更原因
	 * @param action	变更事件
	 * @param actionId	事件ID
	 * @param changeAction	变更类型
	 * @return
	 */
	public static ItemChangeLog createLog(String userId, long roleid, long itemid, int modelid, int num, String itembeforeInfo, String itemafterInfo, int reason,String action, long actionId, String changeAction,int sid, String username, String rolename, int rolelevel,int gridId) {
		ItemChangeLog log = new ItemChangeLog();
		log.setAction(action);
		log.setActionId(actionId);
		log.setChangeAction(changeAction);
		log.setItemafterInfo(itemafterInfo);
		log.setItembeforeInfo(itembeforeInfo);
		log.setItemid(itemid);
		log.setModelid(modelid);
		log.setNum(num);
		log.setReason(reason);
		log.setUsername(username);
		log.setUserId(userId);
		log.setRoleid(roleid);
		log.setRolename(rolename);
		log.setRolelevel(rolelevel);
		log.setSid(sid);
		log.setGridid(gridId);
		return log;
	}
	
	private String userId;
	private String username;//用户名
	private String rolename;//角色名
	private int rolelevel;  //角色等级
	private long roleid;//角色ID
	private long itemid;//物品ID
	private int modelid;//物品模型ID
	private int num;//变更的数量
	private String itembeforeInfo;//变更之前
	private String itemafterInfo;//变更之后
	private int reason;//原因
	private String action;//事件
	private long actionId;//关联ID
	private String changeAction;//变更类型
	private int gridid;//格子
	private int sid;

	@Log(logField="username",fieldType="varchar(512)")
	public String getUsername() {
		return username;
	}


	@Log(logField="gridid",fieldType="integer")
	public int getGridid() {
		return gridid;
	}

	public void setGridid(int gridid) {
		this.gridid = gridid;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Log(logField="rolename",fieldType="varchar(512)")
	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	@Log(logField="rolelevel",fieldType="integer")
	public int getRolelevel() {
		return rolelevel;
	}

	public void setRolelevel(int rolelevel) {
		this.rolelevel = rolelevel;
	}

	@Log(logField="sid",fieldType="int")
	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}
	@Log(logField="userId",fieldType="varchar(512)")
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@Override
	public TableCheckStepEnum getRollingStep() {
		
		return TableCheckStepEnum.DAY;
	}

	@Log(logField="roleid",fieldType="bigint")
	public long getRoleid() {
		return roleid;
	}

	public void setRoleid(long roleid) {
		this.roleid = roleid;
	}
	
	@Log(logField="itemid",fieldType="bigint")
	public long getItemid() {
		return itemid;
	}

	public void setItemid(long itemid) {
		this.itemid = itemid;
	}
	
	@Log(logField="modelid",fieldType="int")
	public int getModelid() {
		return modelid;
	}

	public void setModelid(int modelid) {
		this.modelid = modelid;
	}
	
	@Log(logField="num",fieldType="int")
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	@Log(logField="reason",fieldType="int")
	public int getReason() {
		return reason;
	}

	public void setReason(int reason) {
		this.reason = reason;
	}
	@Log(logField="itembeforeInfo",fieldType="text")
	public String getItembeforeInfo() {
		return itembeforeInfo;
	}

	public void setItembeforeInfo(String itembeforeInfo) {
		this.itembeforeInfo = itembeforeInfo;
	}

	@Log(logField="itemafterInfo",fieldType="text")
	public String getItemafterInfo() {
		return itemafterInfo;
	}
	public void setItemafterInfo(String itemafterInfo) {
		this.itemafterInfo = itemafterInfo;
	}
	
	@Log(logField="action",fieldType="varchar(20)")
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}

	@Log(logField="actionid",fieldType="bigint")
	public long getActionId() {
		return actionId;
	}

	public void setActionId(long actionId) {
		this.actionId = actionId;
	}

	@Log(logField="changeaction",fieldType="varchar(20)")
	public String getChangeAction() {
		return changeAction;
	}
	public void setChangeAction(String changeAction) {
		this.changeAction=changeAction;
		
	}
	@Override
	public void logToFile() {
		logger.error(buildSql());
	}
	private static final Logger logger=Logger.getLogger("ItemChangeLog");
}
