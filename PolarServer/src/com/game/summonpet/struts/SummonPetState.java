package com.game.summonpet.struts;

public enum SummonPetState {
	
	/**
	 * 休息
	 */
	IDEL		(0),
	/**
	 * 战斗
	 */
	FIGHT   	(1),
	/**
	 * 跟随
	 */
	FLLOW		(2),
	
	/**
	 * 死亡
	 */
	DIE			(3),
	
	/**
	 * 不显示
	 */
	UNSHOW		(4),
	
	/**
	 * 追击
	 */
	PURSUIT		(5),

	//死亡中
	DIEING 	(6),
	//死亡等待移除
	DIEWAIT	(7);

	
	private int state;
	SummonPetState(int value){
		this.state=value;
	}
	public int getValue(){
		return state;
	}
}
