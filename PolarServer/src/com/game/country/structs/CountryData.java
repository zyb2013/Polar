package com.game.country.structs;

import java.util.ArrayList;
import java.util.List;

public class CountryData{
	

	//攻城战的时间  yyyy-MM-dd
	private String countryTime;
	
	//参与攻城战的 玩家ID 集合
	private List<Long>  playerList = new ArrayList<Long>();
	
	//获胜方的玩家 ID 集合
	private List<Long>  kingGuildList = new ArrayList<Long>();
		
	//获胜的盟主 ID
	private long kingId;

	//获胜的盟主名
	private String kingName;
	
	//获胜方的 公会ID
	private long kingGuildId;
	
	//获胜的盟主名
	private String kingGuildName;

	public String getCountryTime() {
		return countryTime;
	}

	public void setCountryTime(String countryTime) {
		this.countryTime = countryTime;
	}

	public List<Long> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(List<Long> playerList) {
		this.playerList = playerList;
	}

	public List<Long> getKingGuildList() {
		return kingGuildList;
	}

	public void setKingGuildList(List<Long> kingGuildList) {
		this.kingGuildList = kingGuildList;
	}

	public long getKingId() {
		return kingId;
	}

	public void setKingId(long kingId) {
		this.kingId = kingId;
	}

	public String getKingName() {
		return kingName;
	}

	public void setKingName(String kingName) {
		this.kingName = kingName;
	}

	public long getKingGuildId() {
		return kingGuildId;
	}

	public void setKingGuildId(long kingGuildId) {
		this.kingGuildId = kingGuildId;
	}

	public String getKingGuildName() {
		return kingGuildName;
	}

	public void setKingGuildName(String kingGuildName) {
		this.kingGuildName = kingGuildName;
	}





}
