package com.game.db.bean;
/**二级保护密码
 * 
 * @author zhangrong
 *
 */
public class ProtectBean {
	//id
	private String userid;
	//密码
	private String password;
	//邮件地址
	private String mail;
	
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	

	

}
