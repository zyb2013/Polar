package com.game.db.bean;

/**
 * @author ExcelUtil Auto Maker
 *
 * @version 1.0.0
 *
 * Guild Bean
 */
public class GuildBean {

	//战盟id
	private long guildid;
	//战盟名
	private String guildName;
	//战盟数据
	private String guilddata;
	//战盟保存消息数据
	private String guildmsgdata;
	//战盟保存活跃值数据
	private String guildactivevalue;
	//战盟保存计算活跃值时间
	private long guildcalactivevaluetime;
	//战盟战斗力
	private int guildfightpower;

	/**
	 * get 战盟id
	 *
	 * @return
	 */
	public long getGuildid() {
		return guildid;
	}

	/**
	 * set 战盟id
	 */
	public void setGuildid(long guildid) {
		this.guildid = guildid;
	}

	/**
	 * get 战盟名
	 *
	 * @return
	 */
	public String getGuildName() {
		return guildName;
	}

	/**
	 * set 战盟名
	 */
	public void setGuildName(String guildName) {
		this.guildName = guildName;
	}

	/**
	 * get 战盟数据
	 *
	 * @return
	 */
	public String getGuilddata() {
		return guilddata;
	}

	/**
	 * set 战盟数据
	 */
	public void setGuilddata(String guilddata) {
		this.guilddata = guilddata;
	}

	/**
	 * get 战盟保存消息数据
	 *
	 * @return
	 */
	public String getGuildmsgdata() {
		return guildmsgdata;
	}

	/**
	 * set 战盟保存消息数据
	 */
	public void setGuildmsgdata(String guildmsgdata) {
		this.guildmsgdata = guildmsgdata;
	}

	/**
	 * get 战盟保存活跃值数据
	 *
	 * @return
	 */
	public String getGuildactivevalue() {
		return guildactivevalue;
	}

	/**
	 * set 战盟保存活跃值数据
	 */
	public void setGuildactivevalue(String guildactivevalue) {
		this.guildactivevalue = guildactivevalue;
	}

	/**
	 * get 战盟保存计算活跃值时间
	 * 
	 * @return
	 */
	public long getGuildcalactivevaluetime() {
		return guildcalactivevaluetime;
	}

	/**
	 * set 战盟保存计算活跃值时间
	 */
	public void setGuildcalactivevaluetime(long guildcalactivevaluetime) {
		this.guildcalactivevaluetime = guildcalactivevaluetime;
	}

	/**
	 * get 战盟战斗力
	 * 
	 * @return
	 */
	public int getGuildfightpower() {
		return guildfightpower;
	}

	/**
	 * set 战盟战斗力
	 */
	public void setGuildfightpower(int guildfightpower) {
		this.guildfightpower = guildfightpower;
	}
}