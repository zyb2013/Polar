//package com.game.zones.script;


package scripts.item;

import com.game.backpack.script.IItemScript;
import com.game.backpack.structs.Item;
import com.game.config.Config;
//import com.game.data.bean.Q_horse_basicBean;
import com.game.horse.struts.Horse;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerAttributeType;
import com.game.prompt.structs.Notifys;
import com.game.structs.Reasons;
import com.game.utils.MessageUtil;

/**坐骑炼骨丹
 * 
 * @author zhangrong
 *
 */
 
public class HorseDuanGu implements IItemScript {
	@Override
	public int getId() {
		return 1009148;	
	}


	
	@Override
	public boolean use(Item item, Player player, String... parameters) {
		String name = item.acqItemModel().getQ_name();
		Horse horse = ManagerPool.horseManager.getHorse(player);
		int lv = horse.getLayer();
		
		if (item.getItemModelId()  != 9148) {
			return false;
		}

		if (lv <= 8 && horse.getMixingbone() >= 25) {
			/*xiaozhuoming: 暂时没有用到
			Q_horse_basicBean horse_data = ManagerPool.dataManager.q_horse_basicContainer.getMap().get(9);
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您当前的坐骑只能使用25颗{1}，提升至{2}后上限可使用40颗"),name,horse_data.getQ_name());
			*/
			return false;
		}
			
		if (lv <= 9 && horse.getMixingbone() >= 40) {
			/*xiaozhuoming: 暂时没有用到
			Q_horse_basicBean horse_data = ManagerPool.dataManager.q_horse_basicContainer.getMap().get(10);
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您当前的坐骑只能使用40颗{1}，提升至{2}后上限可使用50颗"),name,horse_data.getQ_name());
			*/
			return false;
		}
		if (lv <= 10 && horse.getMixingbone() >= 50) {
			/*xiaozhuoming: 暂时没有用到
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您的坐骑龙威已觉醒，已不用继续锻骨"));
			*/
			return false;
		}
		
		if( ManagerPool.backpackManager.removeItem(player, item, 1, Reasons.def14,Config.getId()) ){
			horse.setMixingbone(horse.getMixingbone() + 1);
			ManagerPool.playerAttributeManager.countPlayerAttribute(player, PlayerAttributeType.HORSE);
			MessageUtil.notify_player(player, Notifys.MOUSEPOS, ResManager.getInstance().getString("恭喜您成功使用{1}，获得属性加成。"),name);
			ManagerPool.horseManager.stResHorseInfo(player);
			if ( horse.getMixingbone() >= 50) {
				/*xiaozhuoming: 暂时没有用到
				MessageUtil.notify_All_player(Notifys.CHAT_BULL, ResManager.getInstance().getString("恭喜{1}将九幽龙皇成功锻骨，全新的龙皇造型与天赋技能将傲视群雄！"),player.getName());
				*/
//				ParseUtil parseUtil = new ParseUtil();
//				parseUtil.setValue(String.format(ResManager.getInstance().getString("恭喜【%s】将九幽龙皇成功锻骨，全新的龙皇造型与天赋技能将傲视群雄！{@}"), player.getName()), new ParseUtil.VipParm(VipManager.getInstance().getPlayerVipId(player),GuideType.DUANGU.getValue()));
//				MessageUtil.notify_All_player(Notifys.CHAT_BULL,parseUtil.toString(),new ArrayList<GoodsInfoRes>(),GuideType.DUANGU.getValue());
			}
		}

		return false;
	}

}
