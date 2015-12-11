package com.game.backpack.structs;


/**
 * 道具解析类
 * @author hongxiao.z
 * @date   2014-2-15  下午6:13:30
 */
public class AnalysisItemInfo 
{
//	道具ID_道具数量_有效时长_强化等级_追加等级_卓越类型|值;卓越类型|值_会心一击率_无视一击率
	private int itemId;
	private int num;
	private int duration;
	private int strongLv;
	private int addLv;
	private String zhuoyue;
	private int huixin;
	private int wushi;
	
	//领取需要的背包格子数
	private int size;
	
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getStrongLv() {
		return strongLv;
	}
	public void setStrongLv(int strongLv) {
		this.strongLv = strongLv;
	}
	public int getAddLv() {
		return addLv;
	}
	public void setAddLv(int addLv) {
		this.addLv = addLv;
	}
	
	public String getZhuoyue() {
		return zhuoyue;
	}
	public void setZhuoyue(String zhuoyue) {
		this.zhuoyue = zhuoyue;
	}
	public int getHuixin() {
		return huixin;
	}
	public void setHuixin(int huixin) {
		this.huixin = huixin;
	}
	public int getWushi() {
		return wushi;
	}
	public void setWushi(int wushi) {
		this.wushi = wushi;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	
}
