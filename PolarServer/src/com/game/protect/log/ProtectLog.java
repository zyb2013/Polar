package com.game.protect.log;

import org.apache.log4j.Logger;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;

public class ProtectLog  extends BaseLogBean {
	private String userid;		//用户账户ID
	private String password; 	//密码
	private String mail; 		//邮箱
	private int type;		//类型，0注册，1修改密码

	



	@Override
	public void logToFile() {
		logger.error(buildSql());
	}
	private static final Logger logger=Logger.getLogger("ProtectLog");
	
	
	//分表时间
	@Override
	public TableCheckStepEnum getRollingStep() {
		return TableCheckStepEnum.MONTH;
	}




	@Log(logField="userid",fieldType="varchar(100)")
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	
	@Log(logField="password",fieldType="varchar(100)")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
	@Log(logField="mail",fieldType="varchar(100)")
	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}
	
	
	
	@Log(logField="type",fieldType="int")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	

	
	
	

	
	
	
	
	
}
