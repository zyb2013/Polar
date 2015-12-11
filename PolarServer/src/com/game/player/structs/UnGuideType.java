package com.game.player.structs;

import com.game.player.message.RespopuGuideMessage;
import com.game.utils.MessageUtil;


/**通用引导标记
 * 
 * @author zhangrong
 *
 */
public class UnGuideType {

	
	/**打坐凝丹
	 * 
	 */
	public static int dazuo_ningdan = 1;
	
	
	
	
	
	
	
	
	
	/**检测是否已经引导过
	 * false = 已经引导过， true=没有引导
	 * @param player
	 * @return
	 */
	public static boolean checkGuideType(Player player ,int type ){
		if (player.getGuidestatusmap().containsKey("GUIDE"+ type)) {
			return true;
		}else {
			return false;
		}
	}
	
	
	
	
	/**设置成功表示首次引导  返回true
	 * 
	 * @param player
	 * @param type
	 * @return
	 */
	public static boolean setGuideType(Player player ,int type){
		if (!checkGuideType(player,type)) {
			player.getGuidestatusmap().put("GUIDE"+ type,type);
			RespopuGuideMessage msg = new RespopuGuideMessage();
			msg.setType(type);
			MessageUtil.tell_player_message(player, msg);
			return true;
		}
		return false;
	}
	
	
	
	
	
}
