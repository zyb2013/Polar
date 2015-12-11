package com.game.stalls.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 摊位搜索消息
 */
public class ReqStallsSearchToWorldMessage extends Message{

	//角色ID
	private long playerid;
	
	//道具名称
	private String goodsname;
	
	//玩家名字
	private String playername;
	
	//搜索金币或者钻石，0不搜索，1金币，2钻石，3两个都搜索
	private byte goldyuanbao;
	
		//职业类型
		private int q_job_limit;//使用职业需求
		//强化等级
		private byte intensify;
		
		//佩戴部位
		private int q_kind;
		
		//物品类型  宝石     分类1_分类2_分类3
		private String q_type;
		
		//可用物品  0 不可用  1 可用
		private int strength;
		
		//可用物品  0 不可用  1 可用
		private int agile;
		
		//追加物品
		private byte addAttribut;
	
		//可用物品  0 不可用  1 可用
		private byte can_use;
	
		//道具名称
		private String hidden;
		//卓越物品
		private byte zhuoyue;
	




	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//角色ID
		writeLong(buf, this.playerid);
		//道具名称
		writeString(buf, this.goodsname);
		//玩家名字
		writeString(buf, this.playername);
		//搜索金币或者钻石，0不搜索，1金币，2钻石，3两个都搜索
		writeByte(buf, this.goldyuanbao);
		writeInt(buf, this.q_job_limit);
		writeByte(buf, this.intensify);
		writeInt(buf, this.q_kind);
		writeString(buf, this.q_type);
		writeInt(buf, this.strength);
		writeInt(buf, this.agile);
		writeByte(buf, this.addAttribut);
		writeByte(buf, this.can_use);
		writeString(buf, this.hidden);
		writeByte(buf, this.zhuoyue);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//角色ID
		this.playerid = readLong(buf);
		//道具名称
		this.goodsname = readString(buf);
		//玩家名字
		this.playername = readString(buf);
		//搜索金币或者钻石，0不搜索，1金币，2钻石，3两个都搜索
		this.goldyuanbao = readByte(buf);
		this.q_job_limit = readInt(buf);
		this.intensify = readByte(buf);
		this.q_kind = readInt(buf);
		this.q_type = readString(buf);
		this.strength = readInt(buf);
		this.agile = readInt(buf);
		this.addAttribut = readByte(buf);
		this.can_use = readByte(buf);
		this.hidden = readString(buf);
		this.zhuoyue = readByte(buf);
		return true;
	}
	
	public byte getZhuoyue() {
		return zhuoyue;
	}

	public void setZhuoyue(byte zhuoyue) {
		this.zhuoyue = zhuoyue;
	}
	public String getHidden() {
		return hidden;
	}

	public void setHidden(String hidden) {
		this.hidden = hidden;
	}
	public byte getCan_use() {
		return can_use;
	}

	public void setCan_use(byte can_use) {
		this.can_use = can_use;
	}

	public int getQ_job_limit() {
		return q_job_limit;
	}

	public void setQ_job_limit(int q_job_limit) {
		this.q_job_limit = q_job_limit;
	}

	public byte getIntensify() {
		return intensify;
	}

	public void setIntensify(byte intensify) {
		this.intensify = intensify;
	}

	public int getQ_kind() {
		return q_kind;
	}

	public void setQ_kind(int q_kind) {
		this.q_kind = q_kind;
	}

	public String getQ_type() {
		return q_type;
	}

	public void setQ_type(String q_type) {
		this.q_type = q_type;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public int getAgile() {
		return agile;
	}

	public void setAgile(int agile) {
		this.agile = agile;
	}

	public byte getAddAttribut() {
		return addAttribut;
	}

	public void setAddAttribut(byte addAttribut) {
		this.addAttribut = addAttribut;
	}
	/**
	 * get 角色ID
	 * @return 
	 */
	public long getPlayerid(){
		return playerid;
	}
	
	/**
	 * set 角色ID
	 */
	public void setPlayerid(long playerid){
		this.playerid = playerid;
	}
	
	/**
	 * get 道具名称
	 * @return 
	 */
	public String getGoodsname(){
		return goodsname;
	}
	
	/**
	 * set 道具名称
	 */
	public void setGoodsname(String goodsname){
		this.goodsname = goodsname;
	}
	
	/**
	 * get 玩家名字
	 * @return 
	 */
	public String getPlayername(){
		return playername;
	}
	
	/**
	 * set 玩家名字
	 */
	public void setPlayername(String playername){
		this.playername = playername;
	}
	
	/**
	 * get 搜索金币或者钻石，0不搜索，1金币，2钻石，3两个都搜索
	 * @return 
	 */
	public byte getGoldyuanbao(){
		return goldyuanbao;
	}
	
	/**
	 * set 搜索金币或者钻石，0不搜索，1金币，2钻石，3两个都搜索
	 */
	public void setGoldyuanbao(byte goldyuanbao){
		this.goldyuanbao = goldyuanbao;
	}
	
	
	@Override
	public int getId() {
		return 123316;
	}

	@Override
	public String getQueue() {
		return null;
	}
	
	@Override
	public String getServer() {
		return null;
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//角色ID
		buf.append("playerid:" + playerid +",");
		//道具名称
		if(this.goodsname!=null) buf.append("goodsname:" + goodsname.toString() +",");
		//玩家名字
		if(this.playername!=null) buf.append("playername:" + playername.toString() +",");
		//搜索金币或者钻石，0不搜索，1金币，2钻石，3两个都搜索
		buf.append("goldyuanbao:" + goldyuanbao +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}