package com.game.shop.log;

import org.apache.log4j.Logger;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;
import com.game.server.log.ServerOnlineCountLog;
/**
 * 商品出售日志
 * @author  
 *
 */
public class ShopSellLog extends BaseLogBean {
	private String username;
	private long userid;
	private String rolename;
	private long roleId;
	private int rolelevel;
	private long itemid;
	private long actionId;
	private int modelid;
	private int price;
	private String items;
	private int sid;
	@Log(logField="sid",fieldType="int")
	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}
	@Override
	public void logToFile() {
		logger.error(buildSql());
	}
	private static final Logger logger=Logger.getLogger("ShopSellLog");
	
	@Override
	public TableCheckStepEnum getRollingStep() {
		return TableCheckStepEnum.MONTH;
	}
	@Log(logField="username",fieldType="varchar(255)")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	@Log(logField="rolename",fieldType="varchar(255)")
	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}
	@Log(logField="rolelevel",fieldType="int")
	public int getRolelevel() {
		return rolelevel;
	}

	public void setRolelevel(int rolelevel) {
		this.rolelevel = rolelevel;
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
	@Log(logField="userid",fieldType="bigint")
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	@Log(logField="price",fieldType="int")
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	@Log(logField="roleid",fieldType="bigint")
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	@Log(logField="iteminfo",fieldType="longtext")
	public String getItems() {
		return items;
	}
	
	public void setItems(String items) {
		this.items = items;
	}
	@Log(logField="actionid",fieldType="bigint")
	public long getActionId() {
		return actionId;
	}
	
	public void setActionId(long actionId) {
		this.actionId = actionId;
	}
}
