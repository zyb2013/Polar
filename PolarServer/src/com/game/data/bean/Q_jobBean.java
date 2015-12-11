package com.game.data.bean;

/**
 * 玩家职业实体类
 * @author 周江华
 *
 */
public class Q_jobBean {
	private int id;
	private int level;//初生等级
	private int curExp;//初生经验
	private int surplusOfPoint;//初生属性点
	private int strength;//初生力量
	private int vitality;//初生体力
	private int agile;//初生敏捷
	private int intelligence;//初生智力
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getCurExp() {
		return curExp;
	}
	public void setCurExp(int curExp) {
		this.curExp = curExp;
	}
	public int getSurplusOfPoint() {
		return surplusOfPoint;
	}
	public void setSurplusOfPoint(int surplusOfPoint) {
		this.surplusOfPoint = surplusOfPoint;
	}
	public int getStrength() {
		return strength;
	}
	public void setStrength(int strength) {
		this.strength = strength;
	}
	public int getVitality() {
		return vitality;
	}
	public void setVitality(int vitality) {
		this.vitality = vitality;
	}
	public int getAgile() {
		return agile;
	}
	public void setAgile(int agile) {
		this.agile = agile;
	}
	public int getIntelligence() {
		return intelligence;
	}
	public void setIntelligence(int intelligence) {
		this.intelligence = intelligence;
	}
	

}
