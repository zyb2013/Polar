/**
 * 
 */
package com.game.player.log;

import com.game.dblog.TableCheckStepEnum;
import com.game.dblog.base.Log;
import com.game.dblog.bean.BaseLogBean;

/**
 * @author luminghua
 *
 * @date   2014年1月10日 下午2:49:11
 * 
 * 一级属性变化日志（力量、敏捷、智力、体力）
 */
public class RoleAttributeChangeLog extends BaseLogBean {

	private int type;//改变方式：0 面板加点，1 道具使用（果实、洗点）
	
	private int preStrength;//改变前力量
	private int preVitality;//改变前体力
	private int preAgile;//改变前敏捷
	private int preIntelligence;//改变前智力
	private int preRemain;//改变前剩余点数
	
	private int afStrength;//改变后力量
	private int afVitality;//改变后体力
	private int afAgile;//改变后敏捷
	private int afIntelligence;//改变后智力
	private int afRemain;//改变后剩余点数
	
	
	@Override
	public TableCheckStepEnum getRollingStep() {
		return TableCheckStepEnum.DAY;
	}

	@Log(logField="type",fieldType="int")
	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}

	@Log(logField="preStrength",fieldType="int")
	public int getPreStrength() {
		return preStrength;
	}


	public void setPreStrength(int preStrength) {
		this.preStrength = preStrength;
	}

	@Log(logField="preVitality",fieldType="int")
	public int getPreVitality() {
		return preVitality;
	}


	public void setPreVitality(int preVitality) {
		this.preVitality = preVitality;
	}

	@Log(logField="preAgile",fieldType="int")
	public int getPreAgile() {
		return preAgile;
	}


	public void setPreAgile(int preAgile) {
		this.preAgile = preAgile;
	}

	@Log(logField="preIntelligence",fieldType="int")
	public int getPreIntelligence() {
		return preIntelligence;
	}


	public void setPreIntelligence(int preIntelligence) {
		this.preIntelligence = preIntelligence;
	}

	@Log(logField="preRemain",fieldType="int")
	public int getPreRemain() {
		return preRemain;
	}


	public void setPreRemain(int preRemain) {
		this.preRemain = preRemain;
	}

	@Log(logField="afStrength",fieldType="int")
	public int getAfStrength() {
		return afStrength;
	}


	public void setAfStrength(int afStrength) {
		this.afStrength = afStrength;
	}

	@Log(logField="afVitality",fieldType="int")
	public int getAfVitality() {
		return afVitality;
	}


	public void setAfVitality(int afVitality) {
		this.afVitality = afVitality;
	}

	@Log(logField="afAgile",fieldType="int")
	public int getAfAgile() {
		return afAgile;
	}


	public void setAfAgile(int afAgile) {
		this.afAgile = afAgile;
	}

	@Log(logField="afIntelligence",fieldType="int")
	public int getAfIntelligence() {
		return afIntelligence;
	}


	public void setAfIntelligence(int afIntelligence) {
		this.afIntelligence = afIntelligence;
	}

	@Log(logField="afRemain",fieldType="int")
	public int getAfRemain() {
		return afRemain;
	}


	public void setAfRemain(int afRemain) {
		this.afRemain = afRemain;
	}


	@Override
	public void logToFile() {
		logger.error(buildSql());
	}

}
