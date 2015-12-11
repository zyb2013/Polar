/**
 * 
 */
package com.game.backpack.structs;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.game.backpack.manager.BackpackManager;
import com.game.backpack.message.ResUseItemSuccessMessage;
import com.game.config.Config;
import com.game.data.bean.Q_itemBean;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.structs.Reasons;
import com.game.utils.MessageUtil;

/**
 * @author luminghua
 *
 * @date   2014年1月10日 下午4:16:48
 * 
 * 货币，使用药效字段：101=钻石，102=绑钻，103=金币
 */
public class Currency extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7218313512298853937L;

	private Logger logger = Logger.getLogger(Currency.class);
	
	@Override
	public void use(Player player, String... parameters) {
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(this.getItemModelId());
		if(model == null) return;
		if (!StringUtils.isBlank(model.getQ_effict_type())) {
			// 101=钻石，102=绑钻，103=金币
			JSONArray typeArray = JSONArray.fromObject(model.getQ_effict_type());
			// 对应值
			JSONArray valueArray = JSONArray.fromObject(model.parseEffict_value());
			if (typeArray.size() != valueArray.size() || typeArray.size() >1) {
				logger.error("货币道具配置错误： modelId[" + model.getQ_id() + "]," + typeArray.toString() + "," + valueArray.toString());
				return;
			}
			int num = 1;
			if(parameters!=null && parameters.length >= 1){
				num = Integer.parseInt(parameters[0]);
				if(num > getNum()){
					return;
				}
			}
			if(num <= 0) {
				return;
			}
			long configId = Config.getId();
			int useType = typeArray.getInt(0);
			int useEffect = valueArray.getInt(0) * num;
			boolean change = true;
			switch(useType) {
				case 101:
					if(useEffect > 0)
						change = BackpackManager.getInstance().addGold(player, useEffect, Reasons.GOODUSE, configId);
					else
						change = BackpackManager.getInstance().changeGold(player, useEffect, Reasons.GOODUSE, configId);
					break;
				case 102:
					change = BackpackManager.getInstance().changeBindGold(player, useEffect,Reasons.GOODUSE, configId);
					break;
				case 103:
					change = BackpackManager.getInstance().changeMoney(player, useEffect,Reasons.GOODUSE, configId);
					break;
				default:
						break;
			}
			if(change) {
				if(!BackpackManager.getInstance().removeItem(player, this, num, Reasons.GOODUSE, Config.getId())) {
					logger.error("扣除物品失败："+player.getId()+","+this.getItemModelId()+","+num);
				}else{
					//! xuliang 物品使用成功
					ResUseItemSuccessMessage msg = new ResUseItemSuccessMessage();
					msg.setItemId(model.getQ_id());
					MessageUtil.tell_player_message(player, msg);
				}
			}else {
				logger.error("使用货币失败："+player.getId()+","+this.getItemModelId()+","+num);
			}
		}else {
			logger.error("货币配置使用效果为空"+this.getItemModelId());
		}
		
	}

	@Override
	public void unuse(Player player, String... parameters) {

	}

}
