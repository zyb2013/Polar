package com.game.backpack.structs;

import org.apache.log4j.Logger;

public class HorseEquip extends Equip {
	private Logger log = Logger.getLogger(HorseEquip.class);

	private static final long serialVersionUID = 3440260000258031580L;

	
	
	
	/**返回正确位置
	 * 
	 * @param pos
	 * @return
	 */
	public int kind(int pos){
		int xpos = -1;
		if (pos == 20) {
			xpos = 0;
		}else if(pos == 21){
			xpos = 1;
		}else if(pos == 22){
			xpos = 2;
		}else if(pos == 23){
			xpos = 3;
		}
		return xpos;
	}
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
}
