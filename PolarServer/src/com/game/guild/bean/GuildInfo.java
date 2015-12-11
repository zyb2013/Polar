package com.game.guild.bean;

import java.util.List;
import java.util.ArrayList;

import com.game.message.Bean;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 战盟信息
 */
public class GuildInfo extends Bean {

	//战盟id
	private long guildId;
	
	//战盟名
	private String guildName;
	
	//战盟旗帜
	private String guildBanner;
	
	//盟主id
	private long bangZhuid;
	
	//盟主名字
	private String bangZhuName;
	
	//盟主等级
	private short bangZhuLevel;
	
	//盟主是否在线
	private byte bangZhuOnline;
	
	//副盟主id
	private long viceBangZhuid;
	
	//副盟主名字
	private String viceBangZhuName;
	
	//副盟主等级
	private short viceBangZhuLevel;
	
	//副盟主是否在线
	private byte viceBangZhuOnline;
	
	//旗帜造型
	private int bannerIcon;
	
	//旗帜等级
	private byte bannerLevel;
	
	//战盟公告
	private String guildBulletin;
	
	//忠诚徽章牌数
	private int dragon;
	
	//荣誉徽章牌数
	private int whiteTiger;
	
	//守护徽章牌数
	private int suzaku;
	
	//勇敢徽章牌数
	private int basaltic;
	
	//库存钻石
	private long stockGold;
	
	//友好战盟列表
	private List<DiplomaticInfo> friendGuildList = new ArrayList<DiplomaticInfo>();
	//敌对战盟列表
	private List<DiplomaticInfo> enemyGuildList = new ArrayList<DiplomaticInfo>();
	//今日活跃值
	private byte activeValue;
	
	//解散警告值
	private byte warningValue;
	
	//自动同意加入战盟的申请
	private byte autoGuildAgreeAdd;
	
	//成员数量
	private byte memberNum;
	
	//成员战斗力之和
	private int memberFightPower;
	
	//占领地图列表
	private List<Integer> ownMapList = new ArrayList<Integer>();
	//拥有王城
	private byte ownKingCity;
	
	//拥有皇城
	private byte ownEmperorCity;
	
	//战盟创建时间
	private int createTime;
	
	//战盟创建IP
	private String createIp;
	
	//战盟公告最后更新时间
	private int lastGuildBulletinTime;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//战盟id
		writeLong(buf, this.guildId);
		//战盟名
		writeString(buf, this.guildName);
		//战盟旗帜
		writeString(buf, this.guildBanner);
		//盟主id
		writeLong(buf, this.bangZhuid);
		//盟主名字
		writeString(buf, this.bangZhuName);
		//盟主等级
		writeShort(buf, this.bangZhuLevel);
		//盟主是否在线
		writeByte(buf, this.bangZhuOnline);
		//副盟主id
		writeLong(buf, this.viceBangZhuid);
		//副盟主名字
		writeString(buf, this.viceBangZhuName);
		//副盟主等级
		writeShort(buf, this.viceBangZhuLevel);
		//副盟主是否在线
		writeByte(buf, this.viceBangZhuOnline);
		//旗帜造型
		writeInt(buf, this.bannerIcon);
		//旗帜等级
		writeByte(buf, this.bannerLevel);
		//战盟公告
		writeString(buf, this.guildBulletin);
		//忠诚徽章牌数
		writeInt(buf, this.dragon);
		//荣誉徽章牌数
		writeInt(buf, this.whiteTiger);
		//守护徽章牌数
		writeInt(buf, this.suzaku);
		//勇敢徽章牌数
		writeInt(buf, this.basaltic);
		//库存钻石
		writeLong(buf, this.stockGold);
		//友好战盟列表
		writeShort(buf, friendGuildList.size());
		for (int i = 0; i < friendGuildList.size(); i++) {
			writeBean(buf, friendGuildList.get(i));
		}
		//敌对战盟列表
		writeShort(buf, enemyGuildList.size());
		for (int i = 0; i < enemyGuildList.size(); i++) {
			writeBean(buf, enemyGuildList.get(i));
		}
		//今日活跃值
		writeByte(buf, this.activeValue);
		//解散警告值
		writeByte(buf, this.warningValue);
		//自动同意加入战盟的申请
		writeByte(buf, this.autoGuildAgreeAdd);
		//成员数量
		writeByte(buf, this.memberNum);
		//成员战斗力之和
		writeInt(buf, this.memberFightPower);
		//占领地图列表
		writeShort(buf, ownMapList.size());
		for (int i = 0; i < ownMapList.size(); i++) {
			writeInt(buf, ownMapList.get(i));
		}
		//拥有王城
		writeByte(buf, this.ownKingCity);
		//拥有皇城
		writeByte(buf, this.ownEmperorCity);
		//战盟创建时间
		writeInt(buf, this.createTime);
		//战盟创建IP
		writeString(buf, this.createIp);
		//战盟公告最后更新时间
		writeInt(buf, this.lastGuildBulletinTime);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//战盟id
		this.guildId = readLong(buf);
		//战盟名
		this.guildName = readString(buf);
		//战盟旗帜
		this.guildBanner = readString(buf);
		//盟主id
		this.bangZhuid = readLong(buf);
		//盟主名字
		this.bangZhuName = readString(buf);
		//盟主等级
		this.bangZhuLevel = readShort(buf);
		//盟主是否在线
		this.bangZhuOnline = readByte(buf);
		//副盟主id
		this.viceBangZhuid = readLong(buf);
		//副盟主名字
		this.viceBangZhuName = readString(buf);
		//副盟主等级
		this.viceBangZhuLevel = readShort(buf);
		//副盟主是否在线
		this.viceBangZhuOnline = readByte(buf);
		//旗帜造型
		this.bannerIcon = readInt(buf);
		//旗帜等级
		this.bannerLevel = readByte(buf);
		//战盟公告
		this.guildBulletin = readString(buf);
		//忠诚徽章牌数
		this.dragon = readInt(buf);
		//荣誉徽章牌数
		this.whiteTiger = readInt(buf);
		//守护徽章牌数
		this.suzaku = readInt(buf);
		//勇敢徽章牌数
		this.basaltic = readInt(buf);
		//库存钻石
		this.stockGold = readLong(buf);
		//友好战盟列表
		int friendGuildList_length = readShort(buf);
		for (int i = 0; i < friendGuildList_length; i++) {
			friendGuildList.add((DiplomaticInfo)readBean(buf, DiplomaticInfo.class));
		}
		//敌对战盟列表
		int enemyGuildList_length = readShort(buf);
		for (int i = 0; i < enemyGuildList_length; i++) {
			enemyGuildList.add((DiplomaticInfo)readBean(buf, DiplomaticInfo.class));
		}
		//今日活跃值
		this.activeValue = readByte(buf);
		//解散警告值
		this.warningValue = readByte(buf);
		//自动同意加入战盟的申请
		this.autoGuildAgreeAdd = readByte(buf);
		//成员数量
		this.memberNum = readByte(buf);
		//成员战斗力之和
		this.memberFightPower = readInt(buf);
		//占领地图列表
		int ownMapList_length = readShort(buf);
		for (int i = 0; i < ownMapList_length; i++) {
			ownMapList.add(readInt(buf));
		}
		//拥有王城
		this.ownKingCity = readByte(buf);
		//拥有皇城
		this.ownEmperorCity = readByte(buf);
		//战盟创建时间
		this.createTime = readInt(buf);
		//战盟创建IP
		this.createIp = readString(buf);
		//战盟公告最后更新时间
		this.lastGuildBulletinTime = readInt(buf);
		return true;
	}
	
	/**
	 * get 战盟id
	 * @return 
	 */
	public long getGuildId(){
		return guildId;
	}
	
	/**
	 * set 战盟id
	 */
	public void setGuildId(long guildId){
		this.guildId = guildId;
	}
	
	/**
	 * get 战盟名
	 * @return 
	 */
	public String getGuildName(){
		return guildName;
	}
	
	/**
	 * set 战盟名
	 */
	public void setGuildName(String guildName){
		this.guildName = guildName;
	}
	
	/**
	 * get 战盟旗帜
	 * @return 
	 */
	public String getGuildBanner(){
		return guildBanner;
	}
	
	/**
	 * set 战盟旗帜
	 */
	public void setGuildBanner(String guildBanner){
		this.guildBanner = guildBanner;
	}
	
	/**
	 * get 盟主id
	 * @return 
	 */
	public long getBangZhuid(){
		return bangZhuid;
	}
	
	/**
	 * set 盟主id
	 */
	public void setBangZhuid(long bangZhuid){
		this.bangZhuid = bangZhuid;
	}
	
	/**
	 * get 盟主名字
	 * @return 
	 */
	public String getBangZhuName(){
		return bangZhuName;
	}
	
	/**
	 * set 盟主名字
	 */
	public void setBangZhuName(String bangZhuName){
		this.bangZhuName = bangZhuName;
	}
	
	/**
	 * get 盟主等级
	 * @return 
	 */
	public short getBangZhuLevel(){
		return bangZhuLevel;
	}
	
	/**
	 * set 盟主等级
	 */
	public void setBangZhuLevel(short bangZhuLevel){
		this.bangZhuLevel = bangZhuLevel;
	}
	
	/**
	 * get 盟主是否在线
	 * @return 
	 */
	public byte getBangZhuOnline(){
		return bangZhuOnline;
	}
	
	/**
	 * set 盟主是否在线
	 */
	public void setBangZhuOnline(byte bangZhuOnline){
		this.bangZhuOnline = bangZhuOnline;
	}
	
	/**
	 * get 副盟主id
	 * @return 
	 */
	public long getViceBangZhuid(){
		return viceBangZhuid;
	}
	
	/**
	 * set 副盟主id
	 */
	public void setViceBangZhuid(long viceBangZhuid){
		this.viceBangZhuid = viceBangZhuid;
	}
	
	/**
	 * get 副盟主名字
	 * @return 
	 */
	public String getViceBangZhuName(){
		return viceBangZhuName;
	}
	
	/**
	 * set 副盟主名字
	 */
	public void setViceBangZhuName(String viceBangZhuName){
		this.viceBangZhuName = viceBangZhuName;
	}
	
	/**
	 * get 副盟主等级
	 * @return 
	 */
	public short getViceBangZhuLevel(){
		return viceBangZhuLevel;
	}
	
	/**
	 * set 副盟主等级
	 */
	public void setViceBangZhuLevel(short viceBangZhuLevel){
		this.viceBangZhuLevel = viceBangZhuLevel;
	}
	
	/**
	 * get 副盟主是否在线
	 * @return 
	 */
	public byte getViceBangZhuOnline(){
		return viceBangZhuOnline;
	}
	
	/**
	 * set 副盟主是否在线
	 */
	public void setViceBangZhuOnline(byte viceBangZhuOnline){
		this.viceBangZhuOnline = viceBangZhuOnline;
	}
	
	/**
	 * get 旗帜造型
	 * @return 
	 */
	public int getBannerIcon(){
		return bannerIcon;
	}
	
	/**
	 * set 旗帜造型
	 */
	public void setBannerIcon(int bannerIcon){
		this.bannerIcon = bannerIcon;
	}
	
	/**
	 * get 旗帜等级
	 * @return 
	 */
	public byte getBannerLevel(){
		return bannerLevel;
	}
	
	/**
	 * set 旗帜等级
	 */
	public void setBannerLevel(byte bannerLevel){
		this.bannerLevel = bannerLevel;
	}
	
	/**
	 * get 战盟公告
	 * @return 
	 */
	public String getGuildBulletin(){
		return guildBulletin;
	}
	
	/**
	 * set 战盟公告
	 */
	public void setGuildBulletin(String guildBulletin){
		this.guildBulletin = guildBulletin;
	}
	
	/**
	 * get 忠诚徽章牌数
	 * @return 
	 */
	public int getDragon(){
		return dragon;
	}
	
	/**
	 * set 忠诚徽章牌数
	 */
	public void setDragon(int dragon){
		this.dragon = dragon;
	}
	
	/**
	 * get 荣誉徽章牌数
	 * @return 
	 */
	public int getWhiteTiger(){
		return whiteTiger;
	}
	
	/**
	 * set 荣誉徽章牌数
	 */
	public void setWhiteTiger(int whiteTiger){
		this.whiteTiger = whiteTiger;
	}
	
	/**
	 * get 守护徽章牌数
	 * @return 
	 */
	public int getSuzaku(){
		return suzaku;
	}
	
	/**
	 * set 守护徽章牌数
	 */
	public void setSuzaku(int suzaku){
		this.suzaku = suzaku;
	}
	
	/**
	 * get 勇敢徽章牌数
	 * @return 
	 */
	public int getBasaltic(){
		return basaltic;
	}
	
	/**
	 * set 勇敢徽章牌数
	 */
	public void setBasaltic(int basaltic){
		this.basaltic = basaltic;
	}
	
	/**
	 * get 库存钻石
	 * @return 
	 */
	public long getStockGold(){
		return stockGold;
	}
	
	/**
	 * set 库存钻石
	 */
	public void setStockGold(long stockGold){
		this.stockGold = stockGold;
	}
	
	/**
	 * get 友好战盟列表
	 * @return 
	 */
	public List<DiplomaticInfo> getFriendGuildList(){
		return friendGuildList;
	}
	
	/**
	 * set 友好战盟列表
	 */
	public void setFriendGuildList(List<DiplomaticInfo> friendGuildList){
		this.friendGuildList = friendGuildList;
	}
	
	/**
	 * get 敌对战盟列表
	 * @return 
	 */
	public List<DiplomaticInfo> getEnemyGuildList(){
		return enemyGuildList;
	}
	
	/**
	 * set 敌对战盟列表
	 */
	public void setEnemyGuildList(List<DiplomaticInfo> enemyGuildList){
		this.enemyGuildList = enemyGuildList;
	}
	
	/**
	 * get 今日活跃值
	 * @return 
	 */
	public byte getActiveValue(){
		return activeValue;
	}
	
	/**
	 * set 今日活跃值
	 */
	public void setActiveValue(byte activeValue){
		this.activeValue = activeValue;
	}
	
	/**
	 * get 解散警告值
	 * @return 
	 */
	public byte getWarningValue(){
		return warningValue;
	}
	
	/**
	 * set 解散警告值
	 */
	public void setWarningValue(byte warningValue){
		this.warningValue = warningValue;
	}
	
	/**
	 * get 自动同意加入战盟的申请
	 * @return 
	 */
	public byte getAutoGuildAgreeAdd(){
		return autoGuildAgreeAdd;
	}
	
	/**
	 * set 自动同意加入战盟的申请
	 */
	public void setAutoGuildAgreeAdd(byte autoGuildAgreeAdd){
		this.autoGuildAgreeAdd = autoGuildAgreeAdd;
	}
	
	/**
	 * get 成员数量
	 * @return 
	 */
	public byte getMemberNum(){
		return memberNum;
	}
	
	/**
	 * set 成员数量
	 */
	public void setMemberNum(byte memberNum){
		this.memberNum = memberNum;
	}
	
	/**
	 * get 成员战斗力之和
	 * @return 
	 */
	public int getMemberFightPower(){
		return memberFightPower;
	}
	
	/**
	 * set 成员战斗力之和
	 */
	public void setMemberFightPower(int memberFightPower){
		this.memberFightPower = memberFightPower;
	}
	
	/**
	 * get 占领地图列表
	 * @return 
	 */
	public List<Integer> getOwnMapList(){
		return ownMapList;
	}
	
	/**
	 * set 占领地图列表
	 */
	public void setOwnMapList(List<Integer> ownMapList){
		this.ownMapList = ownMapList;
	}
	
	/**
	 * get 拥有王城
	 * @return 
	 */
	public byte getOwnKingCity(){
		return ownKingCity;
	}
	
	/**
	 * set 拥有王城
	 */
	public void setOwnKingCity(byte ownKingCity){
		this.ownKingCity = ownKingCity;
	}
	
	/**
	 * get 拥有皇城
	 * @return 
	 */
	public byte getOwnEmperorCity(){
		return ownEmperorCity;
	}
	
	/**
	 * set 拥有皇城
	 */
	public void setOwnEmperorCity(byte ownEmperorCity){
		this.ownEmperorCity = ownEmperorCity;
	}
	
	/**
	 * get 战盟创建时间
	 * @return 
	 */
	public int getCreateTime(){
		return createTime;
	}
	
	/**
	 * set 战盟创建时间
	 */
	public void setCreateTime(int createTime){
		this.createTime = createTime;
	}
	
	/**
	 * get 战盟创建IP
	 * @return 
	 */
	public String getCreateIp(){
		return createIp;
	}
	
	/**
	 * set 战盟创建IP
	 */
	public void setCreateIp(String createIp){
		this.createIp = createIp;
	}
	
	/**
	 * get 战盟公告最后更新时间
	 * @return 
	 */
	public int getLastGuildBulletinTime(){
		return lastGuildBulletinTime;
	}
	
	/**
	 * set 战盟公告最后更新时间
	 */
	public void setLastGuildBulletinTime(int lastGuildBulletinTime){
		this.lastGuildBulletinTime = lastGuildBulletinTime;
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//战盟id
		buf.append("guildId:" + guildId +",");
		//战盟名
		if(this.guildName!=null) buf.append("guildName:" + guildName.toString() +",");
		//战盟旗帜
		if(this.guildBanner!=null) buf.append("guildBanner:" + guildBanner.toString() +",");
		//盟主id
		buf.append("bangZhuid:" + bangZhuid +",");
		//盟主名字
		if(this.bangZhuName!=null) buf.append("bangZhuName:" + bangZhuName.toString() +",");
		//盟主等级
		buf.append("bangZhuLevel:" + bangZhuLevel +",");
		//盟主是否在线
		buf.append("bangZhuOnline:" + bangZhuOnline +",");
		//副盟主id
		buf.append("viceBangZhuid:" + viceBangZhuid +",");
		//副盟主名字
		if(this.viceBangZhuName!=null) buf.append("viceBangZhuName:" + viceBangZhuName.toString() +",");
		//副盟主等级
		buf.append("viceBangZhuLevel:" + viceBangZhuLevel +",");
		//副盟主是否在线
		buf.append("viceBangZhuOnline:" + viceBangZhuOnline +",");
		//旗帜造型
		buf.append("bannerIcon:" + bannerIcon +",");
		//旗帜等级
		buf.append("bannerLevel:" + bannerLevel +",");
		//战盟公告
		if(this.guildBulletin!=null) buf.append("guildBulletin:" + guildBulletin.toString() +",");
		//忠诚徽章牌数
		buf.append("dragon:" + dragon +",");
		//荣誉徽章牌数
		buf.append("whiteTiger:" + whiteTiger +",");
		//守护徽章牌数
		buf.append("suzaku:" + suzaku +",");
		//勇敢徽章牌数
		buf.append("basaltic:" + basaltic +",");
		//库存钻石
		buf.append("stockGold:" + stockGold +",");
		//友好战盟列表
		buf.append("friendGuildList:{");
		for (int i = 0; i < friendGuildList.size(); i++) {
			buf.append(friendGuildList.get(i).toString() +",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		//敌对战盟列表
		buf.append("enemyGuildList:{");
		for (int i = 0; i < enemyGuildList.size(); i++) {
			buf.append(enemyGuildList.get(i).toString() +",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		//今日活跃值
		buf.append("activeValue:" + activeValue +",");
		//解散警告值
		buf.append("warningValue:" + warningValue +",");
		//自动同意加入战盟的申请
		buf.append("autoGuildAgreeAdd:" + autoGuildAgreeAdd +",");
		//成员数量
		buf.append("memberNum:" + memberNum +",");
		//成员战斗力之和
		buf.append("memberFightPower:" + memberFightPower +",");
		//占领地图列表
		buf.append("ownMapList:{");
		for (int i = 0; i < ownMapList.size(); i++) {
			buf.append(ownMapList.get(i) +",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		//拥有王城
		buf.append("ownKingCity:" + ownKingCity +",");
		//拥有皇城
		buf.append("ownEmperorCity:" + ownEmperorCity +",");
		//战盟创建时间
		buf.append("createTime:" + createTime +",");
		//战盟创建IP
		if(this.createIp!=null) buf.append("createIp:" + createIp.toString() +",");
		//战盟公告最后更新时间
		buf.append("lastGuildBulletinTime:" + lastGuildBulletinTime +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}