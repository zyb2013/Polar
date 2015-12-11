package com.game.country.structs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.game.country.manager.CountryManager;
import com.game.data.bean.Q_hold_rewardBean;
import com.game.data.manager.DataManager;
import com.game.player.structs.Player;
import com.game.utils.TimeUtil;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 王城信息
 *
 */
public class KingCity {

	//王城战盟ID
	private long guildid;
	//王城战盟名字
	private String guildname;
	
	//领取俸禄记录标记为天数 今日天数=领取时间，表示已经领取
	private HashMap<String, Integer> salarymap = new HashMap<String, Integer>();
	//秦王数据
	private HashMap<String, KingData> kingdataMap = new HashMap<String, KingData>();

	//圣盟争夺战期间，玩家持有玉玺时间 (秒)（开始时间）
	private transient int  holdtime;
	
	//玉玺持有者ID
	private  long  holdplayerid;
	//玉玺持有者名字 (包括雕像显示)
	private  String  holdplayername;
	//玉玺持有者性别 (雕像显示)
	private  int  holdplayersex;
	
	
	private  long  oldGuildid;
	
	private  long  currytime;
	
	

	
	


	public long getCurrytime() {
		return currytime;
	}

	public void setCurrytime(long currytime) {
		this.currytime = currytime;
	}

	public long getOldGuildid() {
		return oldGuildid;
	}

	public void setOldGuildid(long oldGuildid) {
		this.oldGuildid = oldGuildid;
	}



	//-----------------------成员函数-----------------------//
	/**
	 * 判断是否圣盟
	 *
	 * @return true 是 false 不是
	 */
	public boolean checkKingCity(Player player) {
		return (player.getGuildId() == getGuildid() && getGuildid() != 0 )? true : false;
	}

	/**
	 * 判断是否圣盟
	 *
	 * @return true 是 false 不是
	 */
	public boolean checkKingCity(long guildid) {
		return (guildid == getGuildid() && getGuildid() != 0)  ? true : false;
	}
	/////////////////////////////////////////////////////////

	public int gKingDataKey() {
		return gKingTerm() + 1;
	}

	public int gKingTerm() {
		return getKingdataMap().size();
	}

	public KingData gCurKingData() {
		return getKingdataMap().get(""+gKingTerm());
	}

	public KingData gKingData(int kingterm) {
		return getKingdataMap().get(""+kingterm);
	}

	public void putKingData(KingData kingData) {
		getKingdataMap().put(""+kingData.getTerm(), kingData);
	}

	public Collection<KingData> gKingDataValue() {
		return getKingdataMap().values();
	}

	public Iterator<KingData> gKingDataIterator() {
		return gKingDataValue().iterator();
	}
	//////////////////////////////////////////////////////////

	private int gSalary(int powerLv,long playerId) {
		
		if (getSalarymap().containsKey(playerId+"")) {
			return getSalarymap().get(playerId+"");
		}
		return 0;
	}

	private void sSalary(int powerLv, int curDay,long playerId) {
		getSalarymap().put(playerId+"", curDay);
	}

	public void sCurdayToSalary(int powerLv,long playerId) {
		long curday = TimeUtil.GetCurTimeInMin(4);
		sSalary(powerLv, (int) curday,playerId);
	}

	/**
	 * 判断是否可以领取薪水
	 *
	 * @return true 可以 false 不可以
	 */
	public boolean checkSalary(int powerLv ,long playerId) {
		long curday = TimeUtil.GetCurTimeInMin(4);
		
		
		
		
		if (gSalary(powerLv,playerId) < curday) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否可以领取薪水
	 *
	 * @return true 可以 false 不可以
	 */
	public boolean checkSalary(Player player) {
		
		return checkSalary(player.getMemberInfo().getGuildPowerLevel(),player.getId());
		/*每个玩家都能领
//		if (player.getMemberInfo().getGuildPowerLevel() > 0 && player.getMemberInfo().getGuildPowerLevel() < 6) {
			
//		}
//		return false;*/
	}
	
	
//	public boolean checkAngle(Player player) {
//		// 只有领主 才能拿
//		KingCity city = CountryManager.getInstance().getKingcity();
//		if(player.getGuildId()==city.getGuildid()){
//		
//			//满足领取条件
//			//获得连续占领的天数
//			
//			//公会占领天数
//			int day = (int)city.getCurrytime();
//			
//			//公会已经领取的天数
//			int getday = 0;
//			if(getSalarymap().containsKey("getAngleDay")){
//				getday = getSalarymap().get("getAngleDay");
//			}else{
//				getSalarymap().put("getAngleDay", 0);
//			}
//			
//			List<Q_hold_rewardBean> li =DataManager.getInstance().q_hold_rewardContainer.getList();
//			
//			//可领取的 最接近的天数
//			int carrday = 0;
//			
//			for (Q_hold_rewardBean bean :li) {
//				if(day>=bean.getQ_day()){
//					if( bean.getQ_day()>=carrday){
//						carrday = bean.getQ_day();
//					}
//				}
//			}
//			//可以去领取
//			if(carrday>getday){
//				return true;
//			}
//		}
//		return false;
//	}
	//-----------------------成员函数-----------------------//

	public long getGuildid() {
		return guildid;
	}

	public void setGuildid(long guildid) {
		this.guildid = guildid;
	}

	public HashMap<String, KingData> getKingdataMap() {
		return kingdataMap;
	}

	public void setKingdataMap(HashMap<String, KingData> kingdataMap) {
		this.kingdataMap = kingdataMap;
	}

	public HashMap<String, Integer> getSalarymap() {
		return salarymap;
	}

	public void setSalarymap(HashMap<String, Integer> salarymap) {
		this.salarymap = salarymap;
	}

	public int getHoldtime() {
		return holdtime;
	}

	public void setHoldtime(int holdtime) {
		this.holdtime = holdtime;
	}

	public long getHoldplayerid() {
		return holdplayerid;
	}

	public void setHoldplayerid(long holdplayerid) {
		this.holdplayerid = holdplayerid;
	}

	public String getGuildname() {
		return guildname;
	}

	public void setGuildname(String guildname) {
		this.guildname = guildname;
	}

	public String getHoldplayername() {
		return holdplayername;
	}

	public void setHoldplayername(String holdplayername) {
		this.holdplayername = holdplayername;
	}

	public int getHoldplayersex() {
		return holdplayersex;
	}

	public void setHoldplayersex(int holdplayersex) {
		this.holdplayersex = holdplayersex;
	}

}
