package com.game.toplist.bean;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Bean;
import com.game.guild.bean.GuildInfo;
import java.util.List;
import java.util.ArrayList;
import com.game.equip.bean.EquipInfo;
/**
  * 排行信息
 */
public class TopInfo extends Bean{

	//玩家id
	private long playerid;
	//平台VIP
	private int webvip;
	//玩家名
	private String playername;
	//地图模板id
	private int mapModelId;
	//排行类型 1等级 2坐骑 3武功 4龙元 5 连斩 6 侍宠 7 战斗力
	private byte toptype;
	//名次
	private int topidx;
	//等级
	private int level;
	//性别
	private byte sex;
	//角色经验
	private long exp;
	//头象ID
	private int avatar;
	//VIPid
	private int vipid;
	//游戏币
	private int money;
	//战斗力
	private int fightPower;
	//历史最大连击数
	private int maxEventcut;
	//坐骑最高阶层
	private int horselayer;
	//坐骑最高阶的当前等级
	private int horselevel;
	//战盟信息
	private GuildInfo guildinfo;
	//战盟信息
	private List<EquipInfo> itemlist = new ArrayList<EquipInfo>();
	
	//职业
	private byte job;
	
	public byte getJob() {
		return job;
	}

	public void setJob(byte job) {
		this.job = job;
	}

	/**
 	 *set 玩家id
	 *@return
	 */
	public void setPlayerid(long playerid){
		this.playerid = playerid;
	}

	/**
 	 *get 玩家id
	 *@return
	 */
	public long getPlayerid(){
		return this.playerid;
	}

	/**
 	 *set 平台VIP
	 *@return
	 */
	public void setWebvip(int webvip){
		this.webvip = webvip;
	}

	/**
 	 *get 平台VIP
	 *@return
	 */
	public int getWebvip(){
		return this.webvip;
	}

	/**
 	 *set 玩家名
	 *@return
	 */
	public void setPlayername(String playername){
		this.playername = playername;
	}

	/**
 	 *get 玩家名
	 *@return
	 */
	public String getPlayername(){
		return this.playername;
	}

	/**
 	 *set 地图模板id
	 *@return
	 */
	public void setMapModelId(int mapModelId){
		this.mapModelId = mapModelId;
	}

	/**
 	 *get 地图模板id
	 *@return
	 */
	public int getMapModelId(){
		return this.mapModelId;
	}

	/**
 	 *set 排行类型 1等级 2坐骑 3武功 4龙元 5 连斩 6 侍宠 7 战斗力
	 *@return
	 */
	public void setToptype(byte toptype){
		this.toptype = toptype;
	}

	/**
 	 *get 排行类型 1等级 2坐骑 3武功 4龙元 5 连斩 6 侍宠 7 战斗力
	 *@return
	 */
	public byte getToptype(){
		return this.toptype;
	}

	/**
 	 *set 名次
	 *@return
	 */
	public void setTopidx(int topidx){
		this.topidx = topidx;
	}

	/**
 	 *get 名次
	 *@return
	 */
	public int getTopidx(){
		return this.topidx;
	}

	/**
 	 *set 等级
	 *@return
	 */
	public void setLevel(int level){
		this.level = level;
	}

	/**
 	 *get 等级
	 *@return
	 */
	public int getLevel(){
		return this.level;
	}

	/**
 	 *set 性别
	 *@return
	 */
	public void setSex(byte sex){
		this.sex = sex;
	}

	/**
 	 *get 性别
	 *@return
	 */
	public byte getSex(){
		return this.sex;
	}

	/**
 	 *set 角色经验
	 *@return
	 */
	public void setExp(long exp){
		this.exp = exp;
	}

	/**
 	 *get 角色经验
	 *@return
	 */
	public long getExp(){
		return this.exp;
	}

	/**
 	 *set 头象ID
	 *@return
	 */
	public void setAvatar(int avatar){
		this.avatar = avatar;
	}

	/**
 	 *get 头象ID
	 *@return
	 */
	public int getAvatar(){
		return this.avatar;
	}

	/**
 	 *set VIPid
	 *@return
	 */
	public void setVipid(int vipid){
		this.vipid = vipid;
	}

	/**
 	 *get VIPid
	 *@return
	 */
	public int getVipid(){
		return this.vipid;
	}

	/**
 	 *set 游戏币
	 *@return
	 */
	public void setMoney(int money){
		this.money = money;
	}

	/**
 	 *get 游戏币
	 *@return
	 */
	public int getMoney(){
		return this.money;
	}

	/**
 	 *set 战斗力
	 *@return
	 */
	public void setFightPower(int fightPower){
		this.fightPower = fightPower;
	}

	/**
 	 *get 战斗力
	 *@return
	 */
	public int getFightPower(){
		return this.fightPower;
	}

	/**
 	 *set 历史最大连击数
	 *@return
	 */
	public void setMaxEventcut(int maxEventcut){
		this.maxEventcut = maxEventcut;
	}

	/**
 	 *get 历史最大连击数
	 *@return
	 */
	public int getMaxEventcut(){
		return this.maxEventcut;
	}

	/**
 	 *set 坐骑最高阶层
	 *@return
	 */
	public void setHorselayer(int horselayer){
		this.horselayer = horselayer;
	}

	/**
 	 *get 坐骑最高阶层
	 *@return
	 */
	public int getHorselayer(){
		return this.horselayer;
	}

	/**
 	 *set 坐骑最高阶的当前等级
	 *@return
	 */
	public void setHorselevel(int horselevel){
		this.horselevel = horselevel;
	}

	/**
 	 *get 坐骑最高阶的当前等级
	 *@return
	 */
	public int getHorselevel(){
		return this.horselevel;
	}

	/**
 	 *set 战盟信息
	 *@return
	 */
	public void setGuildinfo(GuildInfo guildinfo){
		this.guildinfo = guildinfo;
	}

	/**
 	 *get 战盟信息
	 *@return
	 */
	public GuildInfo getGuildinfo(){
		return this.guildinfo;
	}

	/**
 	 *set 战盟信息
	 *@return
	 */
	public void setItemlist(List<EquipInfo> itemlist){
		this.itemlist = itemlist;
	}

	/**
 	 *get 战盟信息
	 *@return
	 */
	public List<EquipInfo> getItemlist(){
		return this.itemlist;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//玩家id
		writeLong(buf,this.playerid);
		//平台VIP
		writeInt(buf,this.webvip);
		//玩家名
		writeString(buf,this.playername);
		//地图模板id
		writeInt(buf,this.mapModelId);
		//排行类型 1等级 2坐骑 3武功 4龙元 5 连斩 6 侍宠 7 战斗力
		writeByte(buf,this.toptype);
		//名次
		writeInt(buf,this.topidx);
		//等级
		writeInt(buf,this.level);
		//性别
		writeByte(buf,this.sex);
		//角色经验
		writeLong(buf,this.exp);
		//头象ID
		writeInt(buf,this.avatar);
		//VIPid
		writeInt(buf,this.vipid);
		//游戏币
		writeInt(buf,this.money);
		//战斗力
		writeInt(buf,this.fightPower);
		//历史最大连击数
		writeInt(buf,this.maxEventcut);
		//坐骑最高阶层
		writeInt(buf,this.horselayer);
		//坐骑最高阶的当前等级
		writeInt(buf,this.horselevel);
		//战盟信息
		writeBean(buf,this.guildinfo);
		//战盟信息
		writeShort(buf,itemlist.size());
		for(int i = 0;i < itemlist.size();i++){
			writeBean(buf,itemlist.get(i));
		}
		//职业
		writeByte(buf,this.job);
		return true;
	}

	/**
 	 *读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//玩家id
		this.playerid = readLong(buf);
		//平台VIP
		this.webvip = readInt(buf);
		//玩家名
		this.playername = readString(buf);
		//地图模板id
		this.mapModelId = readInt(buf);
		//排行类型 1等级 2坐骑 3武功 4龙元 5 连斩 6 侍宠 7 战斗力
		this.toptype = readByte(buf);
		//名次
		this.topidx = readInt(buf);
		//等级
		this.level = readInt(buf);
		//性别
		this.sex = readByte(buf);
		//角色经验
		this.exp = readLong(buf);
		//头象ID
		this.avatar = readInt(buf);
		//VIPid
		this.vipid = readInt(buf);
		//游戏币
		this.money = readInt(buf);
		//战斗力
		this.fightPower = readInt(buf);
		//历史最大连击数
		this.maxEventcut = readInt(buf);
		//坐骑最高阶层
		this.horselayer = readInt(buf);
		//坐骑最高阶的当前等级
		this.horselevel = readInt(buf);
		//战盟信息
		this.guildinfo = (GuildInfo)readBean(buf,GuildInfo.class);
		//战盟信息
		int itemlist_length = readShort(buf);
		for(int i = 0;i < itemlist_length;i++){
			itemlist.add((EquipInfo)readBean(buf,EquipInfo.class));
		}
		this.job = readByte(buf);
		return true;
	}

	
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//玩家id
		buf.append("playerid:"+playerid+",");
		//平台VIP
		buf.append("webvip:"+webvip+",");
		//玩家名
		buf.append("playername:"+playername+",");
		//地图模板id
		buf.append("mapModelId:"+mapModelId+",");
		//排行类型 1等级 2坐骑 3武功 4龙元 5 连斩 6 侍宠 7 战斗力
		buf.append("toptype:"+toptype+",");
		//名次
		buf.append("topidx:"+topidx+",");
		//等级
		buf.append("level:"+level+",");
		//性别
		buf.append("sex:"+sex+",");
		//角色经验
		buf.append("exp:"+exp+",");
		//头象ID
		buf.append("avatar:"+avatar+",");
		//VIPid
		buf.append("vipid:"+vipid+",");
		//游戏币
		buf.append("money:"+money+",");
		//战斗力
		buf.append("fightPower:"+fightPower+",");
		//历史最大连击数
		buf.append("maxEventcut:"+maxEventcut+",");
		//坐骑最高阶层
		buf.append("horselayer:"+horselayer+",");
		//坐骑最高阶的当前等级
		buf.append("horselevel:"+horselevel+",");
		//战盟信息
		if(this.guildinfo!=null) buf.append("guildinfo:"+guildinfo.toString()+",");
		//战盟信息
		buf.append("itemlist:{");
		for(int i=0;i<itemlist.size();i++){
			buf.append(itemlist.get(i).toString()+",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		buf.append("job:"+job+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}