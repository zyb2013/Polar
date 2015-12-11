package com.game.db.bean;

/**
 * @author tangchao
 *
 */
public class GoldRechargeLog {
    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database column goldrechargelog.oid
     *
     * @ibatorgenerated Sun Jul 29 17:16:39 CST 2012
     */
    private String oid;

    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database column goldrechargelog.uid
     *
     * @ibatorgenerated Sun Jul 29 17:16:39 CST 2012
     */
    private String uid;

    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database column goldrechargelog.serverid
     *
     * @ibatorgenerated Sun Jul 29 17:16:39 CST 2012
     */
    private String serverid;

    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database column goldrechargelog.gold
     *
     * @ibatorgenerated Sun Jul 29 17:16:39 CST 2012
     */
    private Integer gold;

    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database column goldrechargelog.time
     *
     * @ibatorgenerated Sun Jul 29 17:16:39 CST 2012
     */
    private Long time;

    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database column goldrechargelog.type
     *
     * @ibatorgenerated Sun Jul 29 17:16:39 CST 2012
     */
    private Integer type;

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method returns the value of the database column goldrechargelog.oid
     *
     * @return the value of goldrechargelog.oid
     *
     * @ibatorgenerated Sun Jul 29 17:16:39 CST 2012
     */
    
    private long userid;
    
    
    private String rmb;
    
    private String rechargeContent;
    
    
	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public String getOid() {
        return oid;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method sets the value of the database column goldrechargelog.oid
     *
     * @param oid the value for goldrechargelog.oid
     *
     * @ibatorgenerated Sun Jul 29 17:16:39 CST 2012
     */
    public void setOid(String oid) {
        this.oid = oid;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method returns the value of the database column goldrechargelog.uid
     *
     * @return the value of goldrechargelog.uid
     *
     * @ibatorgenerated Sun Jul 29 17:16:39 CST 2012
     */
    public String getUid() {
        return uid;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method sets the value of the database column goldrechargelog.uid
     *
     * @param uid the value for goldrechargelog.uid
     *
     * @ibatorgenerated Sun Jul 29 17:16:39 CST 2012
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method returns the value of the database column goldrechargelog.serverid
     *
     * @return the value of goldrechargelog.serverid
     *
     * @ibatorgenerated Sun Jul 29 17:16:39 CST 2012
     */
    public String getServerid() {
        return serverid;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method sets the value of the database column goldrechargelog.serverid
     *
     * @param serverid the value for goldrechargelog.serverid
     *
     * @ibatorgenerated Sun Jul 29 17:16:39 CST 2012
     */
    public void setServerid(String serverid) {
        this.serverid = serverid;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method returns the value of the database column goldrechargelog.gold
     *
     * @return the value of goldrechargelog.gold
     *
     * @ibatorgenerated Sun Jul 29 17:16:39 CST 2012
     */
    public Integer getGold() {
        return gold;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method sets the value of the database column goldrechargelog.gold
     *
     * @param gold the value for goldrechargelog.gold
     *
     * @ibatorgenerated Sun Jul 29 17:16:39 CST 2012
     */
    public void setGold(Integer gold) {
        this.gold = gold;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method returns the value of the database column goldrechargelog.time
     *
     * @return the value of goldrechargelog.time
     *
     * @ibatorgenerated Sun Jul 29 17:16:39 CST 2012
     */
    public Long getTime() {
        return time;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method sets the value of the database column goldrechargelog.time
     *
     * @param time the value for goldrechargelog.time
     *
     * @ibatorgenerated Sun Jul 29 17:16:39 CST 2012
     */
    public void setTime(Long time) {
        this.time = time;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method returns the value of the database column goldrechargelog.type
     *
     * @return the value of goldrechargelog.type
     *
     * @ibatorgenerated Sun Jul 29 17:16:39 CST 2012
     */
    public Integer getType() {
        return type;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method sets the value of the database column goldrechargelog.type
     *
     * @param type the value for goldrechargelog.type
     *
     * @ibatorgenerated Sun Jul 29 17:16:39 CST 2012
     */
    public void setType(Integer type) {
        this.type = type;
    }
    
	public String getRmb() {
		return rmb;
	}

	public void setRmb(String rmb) {
		this.rmb = rmb;
	}

	public String getRechargeContent() {
		return rechargeContent;
	}

	public void setRechargeContent(String rechargeContent) {
		this.rechargeContent = rechargeContent;
	}
    
    
}